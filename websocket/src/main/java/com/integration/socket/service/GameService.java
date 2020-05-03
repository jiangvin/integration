package com.integration.socket.service;

import com.integration.socket.model.bo.UserBo;
import com.integration.socket.model.dto.MessageDto;
import com.integration.socket.stage.BaseStage;
import com.integration.socket.stage.StageMenu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description 游戏的主体函数
 * @date 2020/5/3
 */

@Service
@Slf4j
public class GameService {

    /**
     * 用户管理
     */
    @Autowired
    private OnlineUserService onlineUserService;

    /**
     * 消息发送，接收管理
     */
    @Autowired
    private MessageService messageService;

    /**
     * 菜单管理
     */
    private List<BaseStage> stageList;
    private int stageIndex;

    public void addUser(UserBo userBo) {
        onlineUserService.add(userBo);
        messageService.sendUserStatusAndMessage(onlineUserService.getUserList(), userBo.getUsername(), false);
    }

    public void removeUser(String username) {
        //用户离开
        if (!onlineUserService.remove(username)) {
            return;
        }
        messageService.sendUserStatusAndMessage(onlineUserService.getUserList(), username, true);
        currentStage().remove(username);
    }

    public void receiveMessage(MessageDto messageDto, String sendFrom) {
        if (!userCheck(messageDto, sendFrom)) {
            return;
        }

        log.info("receive:{} from user:{}", messageDto.toString(), sendFrom);

        switch (messageDto.getMessageType()) {
            case USER_MESSAGE:
                messageService.processUserMessage(messageDto, sendFrom);
                break;
            default:
                currentStage().processMessage(messageDto, sendFrom);
                break;
        }
    }

    @PostConstruct
    private void init() {
        //初始化舞台
        initStage();
    }

    private void initStage() {
        this.stageIndex = 0;
        this.stageList = new ArrayList<>();

        //初始化菜单
        this.stageList.add(new StageMenu(messageService));
    }

    @Scheduled(fixedDelay = 17)
    public void update() {
        currentStage().update();
    }

    private BaseStage currentStage() {
        return this.stageList.get(stageIndex);
    }

    private boolean userCheck(MessageDto messageDto, String sendFrom) {
        //检查发送方
        if (!onlineUserService.exists(sendFrom)) {
            return false;
        }

        //检查接收方
        if (StringUtils.isEmpty(messageDto.getSendTo())) {
            return true;
        }

        return onlineUserService.exists(messageDto.getSendTo());
    }
}
