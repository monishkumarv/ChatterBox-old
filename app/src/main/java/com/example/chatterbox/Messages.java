package com.example.chatterbox;

public class Messages{

    public String message;
    public Boolean IS_MSG_FROM_YOU;
    public String status;

    public Messages(){}

    public Messages(Boolean IS_MSG_FROM_YOU, String message, String status) {
        this.message = message;
        this.IS_MSG_FROM_YOU = IS_MSG_FROM_YOU;
        this.status = status;
    }
}
