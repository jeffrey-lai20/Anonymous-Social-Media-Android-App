package au.edu.sydney.comp5216.project.ui.moment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import au.edu.sydney.comp5216.project.R;
import au.edu.sydney.comp5216.project.ui.post.Post;

public class MomentFragment extends Fragment{

    private MomentViewModel momentViewModel;
    private RecyclerView mRecyclerView;
    private ListViewAdaptor mAdapter;
    private List<Post> mDataList = new ArrayList<>();
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
        getpost();
        mAdapter = new ListViewAdaptor(mDataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        return root;
    }

    public void prepareList(){
        Post post = new Post(10000000,"Price1");
        mDataList.add(post);
        post = new Post(20000000,"Price2");
        mDataList.add(post);
        post = new Post(30000000,"Price3");
        mDataList.add(post);
    }

    public void getpost(){
        db.collection("posts")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                post = new Post(document.getLong("id").intValue(),document.getString("content"),document.getString("image_path"));
                                mDataList.add(post);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
}
