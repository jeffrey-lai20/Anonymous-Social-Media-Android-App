package au.edu.sydney.comp5216.project.ui.moment;

public class Reply {
    public Integer id;
    public String content;

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
}
