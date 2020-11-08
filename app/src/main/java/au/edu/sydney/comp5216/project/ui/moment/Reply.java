package au.edu.sydney.comp5216.project.ui.moment;

public class Reply {
    public Integer id;
    public String content;
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

}
