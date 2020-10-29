package au.edu.sydney.comp5216.project.ui.post;


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
    public boolean like = false;

    public Post() {
    }

    public Post(Integer id, String content) {
        this.content = content;
        this.id = id;
    }

    public Post(Integer id, String content,String imagepath, Integer likes, String pid) {
        this.content = content;
        this.id = id;
        this.imagepath = imagepath;
        this.likes = likes;
        this.pid = pid;
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

    public void setlikes_account(Integer likes){
        this.likes = likes;
    }

    public void setlike(boolean like){
        this.like = like;
    }
}
