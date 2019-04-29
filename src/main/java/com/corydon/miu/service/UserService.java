package com.corydon.miu.service;

import com.corydon.miu.bean.User;
import com.corydon.miu.bean.UserNotFoundException;
import com.corydon.miu.bean.UserRegister;
import com.corydon.miu.dao.UserDao;
import com.corydon.miu.dao.UserRegisterDao;
import com.corydon.miu.mail.LMailer;
import com.corydon.miu.mail.Mail;
import com.corydon.miu.web.Configures;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    private UserDao userDao;
    private UserRegisterDao userRegisterDao;
    Logger logger=Configures.logger;
    private static final String MAIL_SENDER_ACCOUNT="m13543390435@163.com";
    private static final String MAIL_SENDER_PASSWORDS="163smtp";    // 授权码

    public void updateUser(String mail,String name,String passwords){
        userDao.updateUser(mail, name, passwords);
    }

    public void updateUserPic(User user,String picPath){
        userDao.updatePicUrl(user.getMail(),picPath);
    }
    public String signUser(User user){
        String token=UUID.randomUUID().toString().replace("-","");
        userDao.add(user);
        UserRegister userRegister=new UserRegister(user.getMail(),token);
        userRegisterDao.add(userRegister);
        return token;
    }

    public void sendActiveMail(String to,String token){
        String url=Configures.HOST+"/"+Configures.PROJECT_NAME+Configures.MODULE_USER+"/activeUser?token="+token;
        Mail mail=new Mail(MAIL_SENDER_ACCOUNT,to,"激活账户",url,MAIL_SENDER_PASSWORDS);
        LMailer.sendEmail(mail);
    }

    public boolean activeUser(String token) throws UserNotFoundException{
        UserRegister userRegister=userRegisterDao.findByToken(token);
        if(userRegister==null)
            return false;
        User user=userDao.findByMail(userRegister.getMail());
        if(user==null) {
            throw new UserNotFoundException("the user who's Mail is " + userRegister.getMail() + " is not exist!");
        }
        user.setState(User.State.REGISTERED);
        userDao.updateUserState(user);
        userRegisterDao.deleteByMail(userRegister.getMail());
        return true;
    }

    public boolean hasUserExist(String mail){
        Map<String,String> params=new HashMap<>();
        params.put("mail", mail);
        List<User> userList=userDao.find(params);
        return userList.size()>0;
    }

    public boolean hasUserAvailable(String mail, String passwords){
        Map<String,String> params=new HashMap<>();
        params.put("mail", mail);
        params.put("passwords",passwords);
        List<User> userList=userDao.find(params);
        if(userList.size()>0&&userList.get(0).getStateString().equalsIgnoreCase("registered"))
            return true;
        return false;
    }

    public User findUser(String mail, String passwords){
        Map<String,String> params=new HashMap<>();
        params.put("mail", mail);
        params.put("passwords",passwords);
        List<User> userList=userDao.find(params);
        if(userList.size()>0){
            return userList.get(0);
        }
        else
            return null;
    }

    public User findUser(String mail){
        Map<String,String> params=new HashMap<>();
        params.put("mail", mail);
        List<User> userList=userDao.find(params);
        if(userList.size()>0){
            User user=userList.get(0);
            user.setPasswords("");
            return user;
        }
        else
            return null;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setUserRegisterDao(UserRegisterDao userRegisterDao) {
        this.userRegisterDao = userRegisterDao;
    }
}