package com.integration.socket.service;

import com.integration.socket.model.ActionType;
import com.integration.socket.model.bo.TankBo;
import com.integration.socket.model.dto.TankDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/5/1
 */

@Service
@Slf4j
public class TankService {
    private ConcurrentHashMap<String, TankBo> tankMap = new ConcurrentHashMap<>();

    /**
     * 刷新频率17毫秒，模拟1秒60帧刷新模式
     */
    @Scheduled(fixedDelay = 17)
    public void update() {
        for (Map.Entry<String, TankBo> kv : tankMap.entrySet()) {
            TankBo tankBo = kv.getValue();
            if (tankBo.getActionType() == ActionType.RUN) {
                switch (tankBo.getOrientationType()) {
                    case UP:
                        tankBo.setY(tankBo.getY() - tankBo.getSpeed());
                        break;
                    case DOWN:
                        tankBo.setY(tankBo.getY() + tankBo.getSpeed());
                        break;
                    case LEFT:
                        tankBo.setX(tankBo.getX() - tankBo.getSpeed());
                        break;
                    case RIGHT:
                        tankBo.setX(tankBo.getX() + tankBo.getSpeed());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public boolean addTank(TankDto tankDto) {
        TankBo tankBo = TankBo.convert(tankDto);
        if (tankMap.containsKey(tankBo.getTankId())) {
            return false;
        }
        tankMap.put(tankBo.getTankId(), tankBo);
        return true;
    }

    public List<TankDto> getTankList() {
        List<TankDto> tankDtoList = new ArrayList<>();
        for (Map.Entry<String, TankBo> kv : tankMap.entrySet()) {
            tankDtoList.add(TankDto.convert(kv.getValue()));
        }
        return tankDtoList;
    }
}
