package com.integration.socket.controller;

import com.integration.socket.model.RoomType;
import com.integration.socket.model.dto.RoomDto;
import com.integration.socket.model.dto.RoomListDto;
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
    public RoomListDto getRooms(@RequestParam(value = "start", defaultValue = "0") int start,
                                @RequestParam(value = "limit", defaultValue = "5") int limit) {
        List<RoomDto> rooms = new ArrayList<>();
        rooms.add(new RoomDto("房间1", "创建者1", "地图1", RoomType.PVP, 1));
        rooms.add(new RoomDto("房间2", "创建者2", "地图2", RoomType.PVE, 2));
        rooms.add(new RoomDto("房间3", "创建者3", "地图3", RoomType.EVE, 3));
        rooms.add(new RoomDto("房间4", "创建者4", "地图4", RoomType.EVE, 4));
        rooms.add(new RoomDto("房间5", "创建者5", "地图5", RoomType.EVE, 5));
        rooms.add(new RoomDto("房间6", "创建者6", "地图6", RoomType.EVE, 6));
        rooms.add(new RoomDto("房间7", "创建者7", "地图7", RoomType.EVE, 7));
        rooms.add(new RoomDto("房间8", "创建者8", "地图8", RoomType.EVE, 8));
        rooms.add(new RoomDto("房间9", "创建者9", "地图9", RoomType.EVE, 9));
        rooms.add(new RoomDto("房间10", "创建者10", "地图10", RoomType.EVE, 10));
        rooms.add(new RoomDto("房间11", "创建者11", "地图11", RoomType.PVP, 11));


        return new RoomListDto(rooms.subList(start, Math.min(start + limit, rooms.size())), rooms.size());
    }
}
