package com.corydon.miu.dao;

import com.corydon.miu.bean.UserRegister;
import com.corydon.miu.web.Configures;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRegisterDao extends Dao<UserRegister> {
    private static final String SQL_ADD_REGISTER_USER="INSERT INTO t_user_register(mail,token) VALUES (?,?)";
    private static final String SQL_DELETE_REGISTER_USER="DELETE FROM t_user_register %s";
    private static final String SQL_FIND_REGISTER_USERS="SELECT * FROM t_user_register %s";
    JdbcTemplate jdbcTemplate;
    private Logger logger=Configures.logger;


    public void deleteByMail(String mail){
        Map<String,String> params=new HashMap<>();
        params.put("mail",mail);
        delete(params);
    }

    public UserRegister findByToken(String token){
        Map<String,String> params=new HashMap<>();
        params.put("token",token);
        List<UserRegister> userRegisterList=find(params);
        if(userRegisterList.size()>0)
            return userRegisterList.get(0);
        return null;
    }

    @Override
    public UserRegister add(UserRegister userRegister) {
        Object[] args={userRegister.getMail(),userRegister.getToken()};
        jdbcTemplate.update(SQL_ADD_REGISTER_USER,args);
        return userRegister;
    }

    @Override
    public void update(Map<String, String> tp, Map<String, String> cp) {

    }

    @Override
    public void delete(Map<String, String> params) {
        String condition=generateCondition(params);
        jdbcTemplate.update(String.format(SQL_DELETE_REGISTER_USER,condition));
    }

    @Override
    public List<UserRegister> find(Map<String, String> params) {
        List<UserRegister> userRegisterList=new ArrayList<>();
        String condition=generateCondition(params);
        logger.debug(String.format(SQL_FIND_REGISTER_USERS,condition));
        userRegisterList=jdbcTemplate.query(String.format(SQL_FIND_REGISTER_USERS,condition) , new RowMapper<UserRegister>() {
            @Override
            public UserRegister mapRow(ResultSet resultSet, int i) throws SQLException {
                UserRegister userRegister=new UserRegister();
                userRegister.setMail(resultSet.getString("Mail"));
                userRegister.setToken(resultSet.getString("token"));
                return userRegister;
            }
        });
        return userRegisterList;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
