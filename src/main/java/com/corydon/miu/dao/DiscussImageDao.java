package com.corydon.miu.dao;

import com.corydon.miu.bean.DiscussImage;
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
public class DiscussImageDao extends Dao<DiscussImage> {
    private static final String SQL_ADD_DISCUSS_IMAGE="INSERT INTO t_discuss_image(discuss_id,image_url) VALUES (?,?)";
    private static final String SQL_FIND_DISCUSS_IMAGE="SELECT * FROM t_discuss_image %s";
    private static final String SQL_DELETE_DISCUSS_IMAGE="DELETE FROM t_discuss_image %s";
    private JdbcTemplate jdbcTemplate;
    @Override
    public DiscussImage add(DiscussImage discussImage) {
        Object[] args={discussImage.getDiscussId(),discussImage.getImageUrl()};
        jdbcTemplate.update(SQL_ADD_DISCUSS_IMAGE,args);
        return discussImage;
    }

    public List<DiscussImage> findByDiscussId(String discussId){
        Map<String,String> params=new HashMap<>();
        List<DiscussImage> discussImageList=new ArrayList<>();
        params.put("discuss_id",discussId);
        discussImageList.addAll(find(params));
        return discussImageList;
    }

    public void deleteAll(){
        delete(null);
    }


    @Override
    public void update(Map<String, String> tp, Map<String, String> cp) {

    }

    @Override
    public void delete(Map<String, String> params) {
        String condition=generateCondition(params);
        jdbcTemplate.update(String.format(SQL_DELETE_DISCUSS_IMAGE,condition));
    }

    @Override
    public List<DiscussImage> find(Map<String, String> params) {
        List<DiscussImage> discussPictureList;
        String condition=generateCondition(params);
        discussPictureList=jdbcTemplate.query(String.format(SQL_FIND_DISCUSS_IMAGE, condition), new RowMapper<DiscussImage>() {
            @Override
            public DiscussImage mapRow(ResultSet rs, int rowNum) throws SQLException {
                DiscussImage discussPicture=new DiscussImage();
                discussPicture.setDiscussId(rs.getString("discuss_id"));
                discussPicture.setImageUrl(rs.getString("image_url"));
                return discussPicture;
            }
        });
        return discussPictureList;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
