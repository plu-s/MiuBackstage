package com.corydon.miu.bean;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(){
        super();
    }

    public UserNotFoundException(String message){
        super(message);
    }
}
