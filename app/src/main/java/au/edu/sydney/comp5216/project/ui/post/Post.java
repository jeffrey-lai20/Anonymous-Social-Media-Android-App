package au.edu.sydney.comp5216.project.ui.post;


import android.net.Uri;

import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Post {
    public Integer id;
    public String content;
    public String imagepath;
    public Integer likes;
    public String pid;
    public String user_picture_path;
    public String gender;
    public boolean like = false;
    public boolean expanded = false;

    public Post() {
    }

    public Post(Integer id, String content) {
        this.content = content;
        this.id = id;
    }

    public Post(Integer id, String content,String imagepath, Integer likes, String pid, String gender) {
        this.content = content;
        this.id = id;
        this.imagepath = imagepath;
        this.likes = likes;
        this.pid = pid;
        this.gender = gender;
    }

    public Integer getid(){
        return this.id;
    }

    public String getcontent(){
        return this.content;
    }

    public String getimagepath(){
        return this.imagepath;
    }

    public Integer getlikes(){
        return this.likes;
    }

    public String getpid(){
        return this.pid;
    }

    public String getgender(){
        return this.gender;
    }

    public String getUser_picture_path(){
        return this.user_picture_path;
    }

    public void setlikes_account(Integer likes){
        this.likes = likes;
    }

    public void setlike(boolean like){
        this.like = like;
    }

    public boolean isExpanded(){
        return this.expanded;
    }

    public void setUser_picture_path(String path){
        this.user_picture_path = path;
    }
}
