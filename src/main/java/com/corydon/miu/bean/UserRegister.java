package com.corydon.miu.bean;

public class UserRegister {
    private String mail;
    private String token;

    public UserRegister(){

    }
    public UserRegister(String mail,String token){
        this.mail=mail;
        this.token=token;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
