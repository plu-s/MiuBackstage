package com.corydon.miu.dao;

import com.corydon.miu.bean.DiscussComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DiscussCommentDao extends Dao<DiscussComment> {
    private static final String SQL_ADD_DISCUSS_COMMENT="INSERT INTO t_discuss_comment(discuss_id,user_mail,content,create_date) VALUES (?,?,?,?);";
    private static final String SQL_FIND_DISCUSS_COMMENT ="SELECT * FROM t_discuss_comment %s";
    private static final String SQL_DELETE_DISCUSS_COMMENT ="DELETE FROM t_discuss_comment %s";
    private JdbcTemplate jdbcTemplate;


    public int findCommentCount(String discussId){
        Map<String,String> params=new HashMap<>();
        params.put("discuss_id",discussId);
        List<DiscussComment> discussCommentList=find(params);
        return discussCommentList.size();
    }

    public List<DiscussComment> findByUserMail(String userMail){
        Map<String,String> params=new HashMap<>();
        params.put("user_mail",userMail);
        List<DiscussComment> discussCommentList=find(params);
        return discussCommentList;
    }

    public List<DiscussComment> findByDiscussId(String discussId){
        Map<String,String> params=new HashMap<>();
        params.put("discuss_id",discussId);
        List<DiscussComment> discussCommentList=find(params);
        return discussCommentList;
    }
    public void deleteAll(){
        delete(null);
    }
    @Override
    public DiscussComment add(DiscussComment discussComment) {
        Object[] args={discussComment.getDiscussId(),discussComment.getUserMail(),discussComment.getContent(),
                new Timestamp(new Date().getTime())};
        jdbcTemplate.update(SQL_ADD_DISCUSS_COMMENT,args);
        return discussComment;
    }

    @Override
    public void update(Map<String, String> tp, Map<String, String> cp) {

    }

    @Override
    public void delete(Map<String, String> params) {
        String condition=generateCondition(params);
        jdbcTemplate.update(String.format(SQL_DELETE_DISCUSS_COMMENT,condition));
    }

    @Override
    public List<DiscussComment> find(Map<String, String> params) {
        String condition=generateCondition(params);
        List<DiscussComment> discussCommentList=jdbcTemplate.query(String.format(SQL_FIND_DISCUSS_COMMENT, condition),
                new RowMapper<DiscussComment>() {
                    @Override
                    public DiscussComment mapRow(ResultSet resultSet, int i) throws SQLException {
                        DiscussComment discussComment=new DiscussComment();
                        discussComment.setDiscussId(resultSet.getString("discuss_id"));
                        discussComment.setUserMail(resultSet.getString("user_mail"));
                        discussComment.setContent(resultSet.getString("content"));
                        discussComment.setCreateDate(new Date(resultSet.getTimestamp("create_date").getTime()));
                        return discussComment;
                    }
                });
        return discussCommentList;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}