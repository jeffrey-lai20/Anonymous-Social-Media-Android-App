package au.edu.sydney.comp5216.project.ui.moment;

public class Reply {
    public Integer id;
    public String content;
    public String user_picture_path;

    Reply(){

    }

    Reply(Integer id,String content){
        this.id = id;
        this.content = content;
    }

    public Integer getid(){
        return this.id;
    }

    public String getcontent(){
        return this.content;
    }

    public void setUser_picture_path(String path){
        this.user_picture_path = path;
    }

    public String getUser_picture_path(){
        return this.user_picture_path;
    }
}
