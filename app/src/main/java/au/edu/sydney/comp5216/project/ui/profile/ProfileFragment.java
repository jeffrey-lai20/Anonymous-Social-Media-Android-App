package au.edu.sydney.comp5216.project.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import au.edu.sydney.comp5216.project.LoginActivity;
import au.edu.sydney.comp5216.project.MainActivity;
import au.edu.sydney.comp5216.project.R;
import au.edu.sydney.comp5216.project.ui.moment.ListViewAdaptor;
import au.edu.sydney.comp5216.project.ui.moment.MomentViewModel;
import au.edu.sydney.comp5216.project.ui.post.Post;
import au.edu.sydney.comp5216.project.MarshmallowPermission;


//public class ProfileFragment extends Fragment{
//
//    private ProfileViewModel profileViewModel;
//    private FirebaseAuth.AuthStateListener authListener;
//    private FirebaseAuth auth;
//    private Button signOut;
//
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        profileViewModel =
//                ViewModelProviders.of(this).get(ProfileViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_setting, container, false);
//        final TextView textView = root.findViewById(R.id.text_profile);
//        signOut = (Button) root.findViewById(R.id.sign_out);
//
//        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
//        //get firebase auth instance
//        auth = FirebaseAuth.getInstance();
//
//        //get current user
//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        authListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user == null) {
//                    // user auth state is changed - user is null
//                    // launch login activity
//                    Intent intent = new Intent(getActivity(), LoginActivity.class);
//
//                    startActivity(intent);
//                }
//            }
//        };
//
//        signOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("Signed out");
//                signOut();
//            }
//        });
//        return root;
//    }
//
//
//    //sign out method
//    public void signOut() {
//        auth.signOut();
//        Intent intent = new Intent(getActivity(), LoginActivity.class);
//        startActivity(intent);
//    }
//
//}
public class ProfileFragment extends Fragment{

    private ProfileViewModel profileViewModel;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FloatingActionButton button;
    private MomentViewModel momentViewModel;
    private RecyclerView mRecyclerView;
    private ListViewAdaptor mAdapter;
    private List<Post> mDataList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "Post";
    private Post post;
    private CountDownLatch doneSignal;
    private String userId;
    private FirebaseUser firebaseUser;
    private Button  changePicture, loadPhoto;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    public String photoFileName = "photo.jpg";
    private File file;
    private ImageView ivPreview;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHOTOS = 102;

//    MarshmallowPermission marshmallowPermission = new MarshmallowPermission(this);


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        button = (FloatingActionButton)root.findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Setting.class);
                startActivity(intent);
            }
        });

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
//        changePicture = (Button) root.findViewById(R.id.changePicture);
//        loadPhoto = (Button) root.findViewById(R.id.loadphoto);
        ivPreview = (ImageView) root.findViewById(R.id.photopreview);

        Uri uri = auth.getCurrentUser().getPhotoUrl();
        System.out.println(uri);
        Glide.with(ivPreview.getContext())
                .load(uri)
                .into(ivPreview);

//        loadPhoto.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
////                if (!marshmallowPermission.checkPermissionForReadfiles()) {
////                    marshmallowPermission.requestPermissionForReadfiles();
////                } else {
//                    // Create intent for picking a photo from the gallery
//                    Intent intent = new Intent(Intent.ACTION_PICK,
//                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//                    // Bring up gallery to select a photo
//                    startActivityForResult(intent, MY_PERMISSIONS_REQUEST_READ_PHOTOS);
////                 }
//            }
//        });


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getDisplayName();
        mRecyclerView = (RecyclerView) root.findViewById(R.id.list_profile);
        getlist_like();
        mAdapter = new ListViewAdaptor(mDataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        return root;
    }

    public void onLoadPhotoClick(View view) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHOTOS) {
            if (resultCode == -1) {
                System.out.println("YEAH");
                Uri photoUri = data.getData();
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(photoUri)
                        .build();

                auth.getCurrentUser().updateProfile(profileUpdates);

                firebaseUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("TAG", "User profile updated.");
                                }
                            }
                        });

                Glide.with(ivPreview.getContext())
                        .load(photoUri)
                        .into(ivPreview);
            }
        }
    }

    public void getpost(final ArrayList<String> group){
        db.collection("posts")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                post = new Post(document.getLong("id").intValue(), document.getString("content"), document.getString("image_path"), document.getLong("likes").intValue(), document.getId());
                                if (group.contains(post.getpid())) {
                                    post.setlike(true);
                                }
                                if (post.getid().toString().equals(userId)){
                                    mDataList.add(post);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void getlist_like(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docref = db.collection("users").document(user.getDisplayName());
        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> group = (ArrayList<String>) document.get("postLikes");
                        getpost(group);
                    } else {

                    }
                } else {
                    Toast.makeText(getActivity(),"Failed. Please check your network connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}