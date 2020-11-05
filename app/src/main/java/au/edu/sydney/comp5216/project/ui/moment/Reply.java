package au.edu.sydney.comp5216.project.ui.moment;

public class Reply {
    public Integer id;
    public String content;
    public String user_picture_path;
    public String gender;

    Reply(){

    }

    Reply(Integer id,String content, String gender){
        this.id = id;
        this.content = content;
        this.gender = gender;
    }

    public Integer getid(){
        return this.id;
    }

    public String getcontent(){
        return this.content;
    }

    public String getgender(){
        return this.gender;
    }

    public void setUser_picture_path(String path){
        this.user_picture_path = path;
    }

    public String getUser_picture_path(){
        return this.user_picture_path;
    }
}
