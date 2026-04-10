package com.baolo.study_room_rservation_system.Exception;

import lombok.Data;

@Data
public class CustomizeException extends RuntimeException{

    private Integer code;
    private String message;


    public CustomizeException(Integer code, String message) {
        super( message);
        this.code = code;
        this.message = message;
    }

}
