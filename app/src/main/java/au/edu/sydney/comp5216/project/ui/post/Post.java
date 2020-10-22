package au.edu.sydney.comp5216.project.ui.post;

import java.util.HashMap;
import java.util.Map;

public class Post {
    public String email;
    public String content;
    public Map<String,String> timestamp;

    public Post() {
    }

    public Post(String email, String content, Map<String,String> timestamp) {
        this.content = content;
        this.email = email;
        this.timestamp = timestamp;
    }
}
