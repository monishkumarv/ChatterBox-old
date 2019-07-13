package com.example.chatterbox;

public class Messages{

    public String message;
    public Boolean IS_MSG_FROM_YOU;

    public Messages(){}

    public Messages(Boolean IS_MSG_FROM_YOU, String message) {
        this.message = message;
        this.IS_MSG_FROM_YOU = IS_MSG_FROM_YOU;
    }
}
