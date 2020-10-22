package au.edu.sydney.comp5216.project.ui.post;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import au.edu.sydney.comp5216.project.MainActivity;
import au.edu.sydney.comp5216.project.R;

public class PostFragment extends Fragment {

    private PostViewModel PostViewModel;
    private Button post;
    private EditText text;
    private DatabaseReference mDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PostViewModel =
                ViewModelProviders.of(this).get(PostViewModel.class);
        View root = inflater.inflate(R.layout.fragment_post, container, false);
        text = root.findViewById(R.id.post_text);
        post =  root.findViewById(R.id.btn_post);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return root;
    }

    private void post(){
        String value = text.getText().toString();
        if(value != ""){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String email = user.getEmail();
                Post newpost = new Post(email,value,ServerValue.TIMESTAMP);
                writetodb(newpost);
            }
        }else{
            System.out.println("Please type something");
        }
    }

    private void writetodb(Post post){
        mDatabase.child("posts").push().setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(),"Successful!",Toast.LENGTH_SHORT).show();
                Log.d("Post","Successful!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"Failed. Please check your network connection",Toast.LENGTH_SHORT).show();
                        Log.d("Post","Failed. Please check your network connection");
                    }
                });
    }
}