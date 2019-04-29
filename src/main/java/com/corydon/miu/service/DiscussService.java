package com.corydon.miu.service;

import com.corydon.miu.bean.*;
import com.corydon.miu.dao.DiscussCommentDao;
import com.corydon.miu.dao.DiscussDao;
import com.corydon.miu.dao.DiscussImageDao;
import com.corydon.miu.dao.DiscussLikeDao;
import com.corydon.miu.web.Configures;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class DiscussService {
    DiscussDao discussDao;
    DiscussLikeDao discussLikeDao;
    DiscussCommentDao discussCommentDao;
    DiscussImageDao discussImageDao;

    public void deleteAll(){
        discussImageDao.deleteAll();
        discussLikeDao.deleteAll();
        discussCommentDao.deleteAll();
        discussDao.deleteAll();
    }

    public Discuss findDiscussById(String discussId){
        Discuss discuss=discussDao.findById(discussId);
        return discuss;
    }

    public List<Discuss> findLikedDiscuss(String mail){
        List<String> discussIdList=discussLikeDao.findDiscussIdByUserMail(mail);
        List<Discuss> discussList=new ArrayList<>();
        for(String discussId:discussIdList){
            discussList.add(discussDao.findById(discussId));
        }
        return discussList;
    }

    public List<DiscussComment> findAllCommentByDiscussId(String discussId){
        List<DiscussComment> discussCommentList=discussCommentDao.findByDiscussId(discussId);
        return discussCommentList;
    }

    public List<DiscussComment> findAllCommentByUserMail(String userMail){
        List<DiscussComment> discussCommentList=discussCommentDao.findByUserMail(userMail);
        return discussCommentList;
    }

    public List<String> findUserDoLikeList(String userMail){
        return discussLikeDao.findDiscussIdByUserMail(userMail);
    }

    public Discuss saveDiscuss(Discuss discuss){
        return discussDao.add(discuss);
    }

    public void comment(String discussId,String userId,String content){
        DiscussComment discussComment=new DiscussComment(discussId,userId,content,new Date());
        discussCommentDao.add(discussComment);
        discussDao.addCommentCount(discussId);
    }

    public boolean hasUserDoLike(String discussId,String userId){
        List<String> userIdList=discussLikeDao.findUserMailByDiscussId(discussId);
        if(userIdList.contains(userId)){
            return true;
        }
        return false;
    }

    public void replaceRealDiscussImage(Discuss discuss, List<File> fileList){
        Document document=Jsoup.parse(discuss.getContent());
        Elements imgs=document.getElementsByTag("img");
        for(int i=0;i<imgs.size();i++){
            Element img=imgs.get(i);
            String url=Configures.HOST+"/"+Configures.PROJECT_NAME+Configures.MODULE_DISCUSS
                    +"/image/"+discuss.getId()+"/"+fileList.get(i).getName();
            img.attr("src",url);
            discussImageDao.add(new DiscussImage(discuss.getId(),url));
        }
        discuss.setContent(document.html());
        discussDao.updateContent(discuss.getId(),discuss.getContent());
    }

    public void doLike(String discussId,String userId){
        DiscussLike discussLike=new DiscussLike(discussId,userId);
        discussDao.addLikeCount(discussId);
        discussLikeDao.add(discussLike);
    }

    public List<Discuss> pushDiscussByLikeCount(){
        List<Discuss> discussList=discussDao.find(null);
        for(Discuss discuss:discussList){
            List<DiscussImage> discussImageList=discussImageDao.findByDiscussId(discuss.getId());
            int commentCount=discussCommentDao.findCommentCount(discuss.getId());
            discuss.setCommentCount(commentCount);
            discuss.setDiscussImageList(discussImageList);
        }
        discussList.sort(new Comparator<Discuss>() {
            @Override
            public int compare(Discuss o1, Discuss o2) {
                if(o1.getLikeCount()>=o2.getLikeCount())
                    return 1;
                else
                    return -1;
            }
        }.reversed());
        return discussList;
    }

    public boolean hasDiscussExist(String discussId){
        Discuss discuss=discussDao.findById(discussId);
        if(discuss==null)
            return false;
        return true;
    }

    @Autowired
    public void setDiscussDao(DiscussDao discussDao) {
        this.discussDao = discussDao;
    }

    @Autowired
    public void setDiscussLikeDao(DiscussLikeDao discussLikeDao) {
        this.discussLikeDao = discussLikeDao;
    }

    @Autowired
    public void setDiscussCommentDao(DiscussCommentDao discussCommentDao) {
        this.discussCommentDao = discussCommentDao;
    }

    @Autowired
    public void setDiscussImageDao(DiscussImageDao discussImageDao) {
        this.discussImageDao = discussImageDao;
    }
}