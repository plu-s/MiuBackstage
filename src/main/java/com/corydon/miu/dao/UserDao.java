package com.corydon.miu.dao;

import com.corydon.miu.bean.User;
import com.corydon.miu.web.Configures;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDao extends Dao<User> {
    private static final String SQL_ADD_USER="INSERT INTO t_user(name,mail,passwords,state,pic_url)" +
            " VALUES (?,?,?,?,?)";
    private static final String SQL_UPDATE_USER="UPDATE t_user %s %s";
    private static final String SQL_FIND_USERS="SELECT * FROM t_user %s";
    private static String SQL_MATCH_EXIST_USER_COUNT="select count(*) from t_user where Mail=? and passwords=?";
    private JdbcTemplate jdbcTemplate;
    private Logger logger=Configures.logger;

    public User findByMail(String mail){
        Map<String,String> params=new HashMap<>();
        params.put("mail",mail);
        List<User> userList=find(params);
        if(userList.size()>0)
            return userList.get(0);
        return null;
    }

    public void updateUser(String mail,String name,String passwords){
        Map<String,String> tp=new HashMap<>();
        Map<String,String> cp=new HashMap<>();
        tp.put("name",name);
        tp.put("passwords",passwords);
        cp.put("mail",mail);
        update(tp,cp);
    }

    public void updatePicUrl(String mail,String picUrl){
        Map<String,String> tp=new HashMap<>();
        Map<String,String> cp=new HashMap<>();
        tp.put("pic_url",picUrl);
        cp.put("mail",mail);
        update(tp,cp);
    }

    public void updateUserState(User user){
        Map<String,String> tp=new HashMap<>();
        Map<String,String> cp=new HashMap<>();
        tp.put("state",user.getStateString());
        cp.put("mail",user.getMail());
        update(tp,cp);
    }

    @Override
    public User add(User user) {
        Object[] args={user.getName(),user.getMail(),user.getPasswords(),user.getStateString(),user.getPicUrl()};
        jdbcTemplate.update(SQL_ADD_USER,args);
        return user;
    }

    @Override
    public void update(Map<String, String> tp, Map<String, String> cp) {
        String target=generateTarget(tp);
        String condition=generateCondition(cp);
        jdbcTemplate.update(String.format(SQL_UPDATE_USER,target,condition));
    }

    @Override
    public void delete(Map<String, String> params) {

    }

    @Override
    public List<User> find(Map<String, String> params) {
        List<User> userList;
        String condition=generateCondition(params);
        userList=jdbcTemplate.query(String.format(SQL_FIND_USERS, condition), new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user=new User();
                user.setMail(resultSet.getString("mail"));
                user.setName(resultSet.getString("name"));
                user.setPasswords(resultSet.getString("passwords"));
                user.setState(resultSet.getString("state"));
                user.setPicUrl(resultSet.getString("pic_url"));
                return user;
            }
        });
        return userList;
    }
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}