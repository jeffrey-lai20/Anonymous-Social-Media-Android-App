package au.edu.sydney.comp5216.project.ui.post;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

import au.edu.sydney.comp5216.project.R;

public class PostFragment extends Fragment {

    private PostViewModel PostViewModel;
    private Button postbtn;
    private Button addimagebtn;
    private ImageButton removebtn;
    private ImageView iPreview;
    private EditText text;
    private Bitmap selectedImage;
    private Uri photoUri;
    private DatabaseReference mDatabase;
    public static final int PICK_IMAGE = 1;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PostViewModel =
                ViewModelProviders.of(this).get(PostViewModel.class);
        View root = inflater.inflate(R.layout.fragment_post, container, false);
        text = root.findViewById(R.id.post_text);
        iPreview = (ImageView) root.findViewById(R.id.image_preview);
        iPreview.setVisibility(View.GONE);
        removebtn =  root.findViewById(R.id.btn_remove_image);
        removebtn.setVisibility(View.GONE);
        removebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iPreview.setVisibility(View.GONE);
                removebtn.setVisibility(View.GONE);
            }
        });
        postbtn =  root.findViewById(R.id.btn_post);
        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
        addimagebtn = root.findViewById(R.id.btn_add_image);
        addimagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addimage();
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();

        return root;
    }

    private void post(){
        final String value = text.getText().toString();
        if(value != ""){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                final String email = user.getEmail();
                // if user added image
                if(iPreview.getVisibility() == View.VISIBLE){
                    StorageReference postRef = storageRef.child("images/"+photoUri.getLastPathSegment());
                    UploadTask uploadTask = postRef.putFile(photoUri);
                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getActivity(),"Failed. Please check your network connection",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String path = "images/"+photoUri.getLastPathSegment();
                            Post newpost = new Post(email,value,ServerValue.TIMESTAMP,path);
                            writetodb(newpost);
                        }
                    });
                }else{
                    Post newpost = new Post(email,value,ServerValue.TIMESTAMP);
                    writetodb(newpost);
                }
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

    private void addimage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE && resultCode != 0) {
            photoUri = data.getData();
            iPreview.setVisibility(View.GONE);
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(
                        getActivity().getContentResolver(), photoUri);

                // Load the selected image into a preview
                iPreview.setImageBitmap(selectedImage);
                iPreview.setVisibility(View.VISIBLE);
                removebtn.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}