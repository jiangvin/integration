package com.integration.socket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/23
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

    @NonNull
    private String message;

    private MessageType messageType = MessageType.USER_MESSAGE;

    private String sendTo;

    public MessageDto(String message) {
        this.message = message;
    }

    public MessageDto(String message, MessageType messageType) {
        this.message = message;
        this.messageType = messageType;
    }
}