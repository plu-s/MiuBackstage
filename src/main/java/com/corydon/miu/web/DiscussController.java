package com.corydon.miu.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.corydon.miu.Util;
import com.corydon.miu.bean.Discuss;
import com.corydon.miu.bean.DiscussComment;
import com.corydon.miu.bean.User;
import com.corydon.miu.service.DiscussService;
import com.corydon.miu.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.corydon.miu.web.Configures.*;

@Controller
@RequestMapping(value = Configures.MODULE_DISCUSS)
public class DiscussController {

    private UserService userService;
    private DiscussService discussService;
    private Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private Logger logger=Configures.logger;



    @RequestMapping(value = "/push",method = RequestMethod.POST)
    @ResponseBody
    public String push(@RequestParam(name = "mail")String mail,
                       @RequestParam(name = "passwords")String passwords){
        JsonObject response=new JsonObject();
        List<User> authorList=new ArrayList<>();
        List<String> likedDiscussId=new ArrayList<>();
        if(!userService.hasUserAvailable(mail,passwords)){
            response.addProperty("result",RESULT_ACCESS_DENIED);
            response.addProperty("data","拒绝访问");
            return response.toString();
        }
        List<Discuss> discussList=discussService.pushDiscussByLikeCount();
        for(Discuss discuss:discussList){
            authorList.add(userService.findUser(discuss.getAuthorMail()));
        }
        likedDiscussId.addAll(discussService.findUserDoLikeList(mail));
        JsonObject data=new JsonObject();
        data.addProperty("discussList",gson.toJson(discussList));
        data.addProperty("authorList",gson.toJson(authorList));
        data.addProperty("likedDiscussIdList",gson.toJson(likedDiscussId));
        response.addProperty("result",RESULT_OK);
        response.add("data",data);
        return response.toString();
    }

    @RequestMapping(value = "/getDiscussById",method = RequestMethod.POST)
    @ResponseBody
    public String getDiscussById(@RequestParam(name = "mail")String mail,
                                 @RequestParam(name = "passwords")String passwords,
                                 @RequestParam(name = "discussId")String discussId){
        JsonObject response=new JsonObject();
        if(!userService.hasUserAvailable(mail,passwords)){
            response.addProperty("result",RESULT_ACCESS_DENIED);
            response.addProperty("data","拒绝访问");
            return response.toString();
        }
        Discuss discuss=discussService.findDiscussById(discussId);
        response.addProperty("result",RESULT_OK);
        response.addProperty("data",gson.toJson(discuss, Discuss.class));
        return response.toString();
    }

    @RequestMapping(value = "/getComments",method = RequestMethod.POST)
    @ResponseBody
    public String getComments(@RequestParam(name = "mail")String mail,
                              @RequestParam(name = "passwords")String passwords,
                              @RequestParam(name = "discussId")String discussId){
        JsonObject response=new JsonObject();
        if(!userService.hasUserAvailable(mail,passwords)){
            response.addProperty("result",RESULT_ACCESS_DENIED);
            response.addProperty("data","拒绝访问");
            return response.toString();
        }
        List<DiscussComment> discussCommentList=discussService.findAllCommentByDiscussId(discussId);
        for(DiscussComment discussComment:discussCommentList){
            User user=userService.findUser(discussComment.getUserMail());
            discussComment.setUserName(user.getName());
            discussComment.setUserPic(user.getPicUrl());
        }
        response.addProperty("result",RESULT_OK);
        response.addProperty("data",gson.toJson(discussCommentList));
        return response.toString();
    }

    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam(name = "authorMail")String authorMail,
                         @RequestParam(name = "passwords")String passwords,
                         @RequestParam(name = "title")String title,
                         @RequestParam(name = "content")String content,
                         @RequestParam(name = "images",required = false)MultipartFile[] images,
                         HttpServletRequest request) throws IOException {
        JsonObject response=new JsonObject();
        List<File> fileList=new ArrayList<>();
        String pathRoot = request.getSession().getServletContext().getRealPath("");
        if(!userService.hasUserAvailable(authorMail,passwords)){
            response.addProperty("result",RESULT_ACCESS_DENIED);
            response.addProperty("data","拒绝访问");
            return response.toString();
        }
        Discuss discuss=new Discuss(authorMail,title,content,new Date());
        discuss=discussService.saveDiscuss(discuss);
        for(MultipartFile image:images){
            if(!image.isEmpty()){
                String fileName=image.getOriginalFilename();
                String path="/static/images/"+discuss.getId()+"/"+fileName;
                File file=new File(Util.getRealFilePath(pathRoot+path));
                file.mkdirs();
                image.transferTo(file);
                fileList.add(file);
            }
        }
        discussService.replaceRealDiscussImage(discuss,fileList);
        response.addProperty("result",RESULT_OK);
        response.addProperty("data","上传成功");
        return response.toString();
    }

    @RequestMapping(value = "/doLike",method = RequestMethod.POST)
    @ResponseBody
    public String doLike(@RequestParam(name = "discussId")String discussId,
                         @RequestParam(name = "userMail")String userMail,
                         @RequestParam(name = "passwords") String passwords){
        JsonObject response=new JsonObject();
        if(!userService.hasUserAvailable(userMail,passwords)){
            response.addProperty("result",RESULT_ACCESS_DENIED);
            response.addProperty("data","访问拒绝");
            return response.toString();
        }
        if(discussService.hasUserDoLike(discussId, userMail)){
            response.addProperty("result",RESULT_REPEAT_DO_LIKE);
            response.addProperty("data","已经点过赞了");
            return response.toString();
        }
        else{
            discussService.doLike(discussId, userMail);
            response.addProperty("result",RESULT_OK);
            response.addProperty("data","点赞成功");
            return response.toString();
        }
    }

    @RequestMapping(value = "/deleteAll",method = RequestMethod.POST)
    @ResponseBody
    public String deleteAll(@RequestParam(name = "mail") String mail,
                            @RequestParam(name = "passwords") String passwords,
                            HttpServletRequest request){
        User user=userService.findUser(mail, passwords);
        JsonObject response=new JsonObject();
        if(user!=null&&user.getMail().equals("m13543390435@163.com")){
            String pathRoot = request.getSession().getServletContext().getRealPath("");
            String path="/static/images/";
            File file=new File(Util.getRealFilePath(pathRoot+path));
            if(file.exists())
                Util.deleteDirectoryAllFile(file.getAbsolutePath());
            discussService.deleteAll();
            response.addProperty("result",RESULT_OK);
            response.addProperty("data","删除成功");
            return response.toString();
        }
        else{
            response.addProperty("result",RESULT_ACCESS_DENIED);
            response.addProperty("data","拒绝访问");
            return response.toString();
        }
    }

    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    @ResponseBody
    public String comment(@RequestParam(name = "discussId")String discussId,
                          @RequestParam(name = "mail")String mail,
                          @RequestParam(name = "passwords")String passwords,
                          @RequestParam(name = "content")String content){
        JsonObject response=new JsonObject();
        if(!userService.hasUserAvailable(mail,passwords)){
            response.addProperty("result",RESULT_ACCESS_DENIED);
            response.addProperty("data","拒绝访问");
            return response.toString();
        }
        if(!discussService.hasDiscussExist(discussId)){
            response.addProperty("result",RESULT_DISCUSS_NON_EXISTENT);
            response.addProperty("data","帖子不存在");
            return response.toString();
        }
        discussService.comment(discussId, mail, content);
        response.addProperty("result",RESULT_OK);
        response.addProperty("data","评论成功");
        return response.toString();
    }

    @Autowired
    public void setDiscussService(DiscussService discussService) {
        this.discussService = discussService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

}