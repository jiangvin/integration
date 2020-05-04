package com.integration.socket.model.dto;

import com.integration.socket.model.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/5/4
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto {
    private String roomId;
    private String creator;
    private String mapId;
    private RoomType roomType;
    private int userCount;
}
