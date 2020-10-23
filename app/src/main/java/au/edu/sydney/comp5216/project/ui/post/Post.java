package au.edu.sydney.comp5216.project.ui.post;


import com.google.firebase.firestore.FieldValue;

public class Post {
    public Integer id;
    public String content;
    public String imagepath;

    public Post() {
    }

    public Post(Integer id, String content) {
        this.content = content;
        this.id = id;
    }

    public Post(Integer id, String content,String imagepath) {
        this.content = content;
        this.id = id;
        this.imagepath = imagepath;
    }
}
