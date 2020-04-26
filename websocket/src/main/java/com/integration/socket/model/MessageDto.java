package com.integration.socket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/23
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private String message;

    private MessageType messageType = MessageType.USER_MESSAGE;

    public MessageDto(String message) {
        this.message = message;
    }
}