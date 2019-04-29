package com.corydon.miu.dao;

import com.corydon.miu.Util;
import com.corydon.miu.bean.Discuss;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DiscussDao extends Dao<Discuss> {
    private static final String SQL_ADD_DISCUSS="INSERT INTO t_discuss(id,author_mail,title,content,create_date) VALUES(?,?,?,?,?)";
    private static final String SQL_UPDATE_DISCUSS="UPDATE t_discuss %s %s";
    private static final String SQL_FIND_DISCUSSES="SELECT * FROM t_discuss %s";
    private static final String SQL_DELETE_DISCUSSE="DELETE FROM t_discuss %s";
    private JdbcTemplate jdbcTemplate;
    private LobHandler lobHandler;

    public Discuss findById(String discussId){
        Map<String,String> params=new HashMap<>();
        params.put("id",discussId);
        List<Discuss> discussList=find(params);
        if(discussList.size()>0)
            return discussList.get(0);
        return null;
    }


    public void addLikeCount(String discussId){
        Discuss discuss=findById(discussId);
        updateLikeCount(discussId,discuss.getLikeCount()+1);
    }

    public void addCommentCount(String discussId){
        Discuss discuss=findById(discussId);
        updateCommentCount(discussId,discuss.getCommentCount()+1);
    }

    public void updateLikeCount(String discussId,int newLikeCount){
        Map<String,String> tp=new HashMap<>();
        Map<String,String> cp=new HashMap<>();
        tp.put("like_count",newLikeCount+"");
        cp.put("id",discussId+"");
        update(tp,cp);
    }

    public void updateCommentCount(String discussId,int newCommentCount){
        Map<String,String> tp=new HashMap<>();
        Map<String,String> cp=new HashMap<>();
        tp.put("comment_count",newCommentCount+"");
        cp.put("id",discussId+"");
        update(tp,cp);
    }

    public void updateContent(String discussId,String content){
        Map<String,String> tp=new HashMap<>();
        Map<String,String> cp=new HashMap<>();
        tp.put("content",content);
        cp.put("id",discussId+"");
        update(tp,cp);
    }

    public void deleteAll(){
        delete(null);
    }

    @Override
    public Discuss add(Discuss discuss) {
        String id=Util.getUUID();
        discuss.setId(id);
        jdbcTemplate.execute(SQL_ADD_DISCUSS, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
            @Override
            protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException, DataAccessException {
                ps.setString(1,discuss.getId());
                ps.setString(2,discuss.getAuthorMail());
                ps.setString(3,discuss.getTitle());
                lobCreator.setClobAsString(ps,4,discuss.getContent());
                ps.setTimestamp(5,new Timestamp(discuss.getCreateDate().getTime()));
            }
        });
        return discuss;
    }

    @Override
    public void update(Map<String, String> tp, Map<String, String> cp) {
        String target=generateTarget(tp);
        String condition=generateCondition(cp);
        jdbcTemplate.update(String.format(SQL_UPDATE_DISCUSS,target,condition));
    }

    @Override
    public void delete(Map<String, String> params) {
        String condition=generateCondition(params);
        jdbcTemplate.update(String.format(SQL_DELETE_DISCUSSE,condition));
    }

    @Override
    public List<Discuss> find(Map<String, String> params) {
        List<Discuss> discussList=new ArrayList<>();
        String condition=generateCondition(params);
        discussList=jdbcTemplate.query(String.format(SQL_FIND_DISCUSSES, condition.toString()), new RowMapper<Discuss>() {
            @Override
            public Discuss mapRow(ResultSet resultSet, int i) throws SQLException {
                Discuss discuss=new Discuss();
                discuss.setId(resultSet.getString("id"));
                discuss.setTitle(resultSet.getString("title"));
                discuss.setAuthorMail(resultSet.getString("author_mail"));
                discuss.setContent(lobHandler.getClobAsString(resultSet,"content"));
                discuss.setCreateDate(new java.util.Date(resultSet.getTimestamp("create_date").getTime()));
                discuss.setLikeCount(resultSet.getInt("like_count"));
                return discuss;
            }
        });
        return discussList;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setLobHandler(LobHandler lobHandler) {
        this.lobHandler = lobHandler;
    }
}
