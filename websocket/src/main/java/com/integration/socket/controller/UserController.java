package com.integration.socket.controller;

import com.integration.socket.model.RoomType;
import com.integration.socket.model.dto.RoomDto;
import com.integration.socket.service.OnlineUserService;
import com.integration.util.model.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/5/1
 */

@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

    private final OnlineUserService onlineUserService;

    public UserController(OnlineUserService onlineUserService) {
        this.onlineUserService = onlineUserService;
    }

    @GetMapping("/getUsers")
    public List<String> getUsers() {
        return onlineUserService.getUserList();
    }

    @GetMapping("/checkName")
    public boolean checkName(@RequestParam(value = "name") String name) {
        if (onlineUserService.exists(name)) {
            throw new CustomException("输入的名字重复: " + name);
        }
        return true;
    }

    @GetMapping("/getRooms")
    public List<RoomDto> getRooms(@RequestParam(value = "start", defaultValue = "0") int start,
                                  @RequestParam(value = "start", defaultValue = "5") int limit) {
        List<RoomDto> rooms = new ArrayList<>();
        rooms.add(new RoomDto("房间1", "创建者1", RoomType.PVP, 1));
        rooms.add(new RoomDto("房间2", "创建者2", RoomType.PVE, 2));
        rooms.add(new RoomDto("房间3", "创建者3", RoomType.EVE, 3));
        return rooms;
    }
}
