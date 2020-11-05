package au.edu.sydney.comp5216.project.ui.moment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import au.edu.sydney.comp5216.project.R;
import au.edu.sydney.comp5216.project.ui.post.Post;

public class MomentFragment extends Fragment{

    private MomentViewModel momentViewModel;
    private RecyclerView mRecyclerView;
    private ListViewAdaptor mAdapter;
    private List<Post> mDataList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "Post";
    private Post post;
    private CountDownLatch doneSignal;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        momentViewModel =
                ViewModelProviders.of(this).get(MomentViewModel.class);
        View root = inflater.inflate(R.layout.fragment_moment, container, false);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.list_moment);
        mDataList = new ArrayList<>();
        getlist_like();
        mAdapter = new ListViewAdaptor(mDataList);
        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        return root;
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
                                post = new Post(document.getLong("id").intValue(),document.getString("content"),document.getString("image_path"),document.getLong("likes").intValue(),document.getId(),document.getString("gender"));
                                if(group.contains(post.getpid())){
                                    post.setlike(true);
                                }
                                mDataList.add(post);
                                //getprofile_picture(post,task.getResult().size());
                            }
                            getpicture();
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


    public void getpicture(){
        for(final Post post:mDataList){
            DocumentReference docref = db.collection("users").document(post.getid().toString());
            docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            post.setUser_picture_path(document.getString("photo"));
                        } else {

                        }
                    } else {
                        Toast.makeText(getActivity(),"Failed. Please check your network connection",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        mAdapter.notifyDataSetChanged();
    }

}







