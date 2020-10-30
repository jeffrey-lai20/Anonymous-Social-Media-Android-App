package au.edu.sydney.comp5216.project.ui.moment;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.sydney.comp5216.project.R;
import au.edu.sydney.comp5216.project.ui.post.Post;

public class ListViewAdaptor extends RecyclerView.Adapter<ListViewAdaptor.MyViewHolder>{
    private List<Post> mDataList;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ChildViewAdaptor mAdapter;
    public List<Reply> replies = new ArrayList<>();
    public List<ChildViewAdaptor> adapters = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView id,content, likes_count;
        public ImageView imageView;
        public ToggleButton btn_like;
        public RecyclerView child_view;
        public ImageButton btn_reply;
        public EditText text_reply;


        public MyViewHolder(View view){
            super(view);
            id = (TextView) view.findViewById(R.id.user_id);
            content= (TextView) view.findViewById(R.id.post_content);
            imageView = (ImageView) view.findViewById(R.id.image_post);
            likes_count = (TextView) view.findViewById(R.id.likes_count);
            btn_like = (ToggleButton) view.findViewById(R.id.btn_likes);
            child_view = (RecyclerView) view.findViewById(R.id.childrecyclerview);
            btn_reply = (ImageButton) view.findViewById(R.id.btn_reply);
            text_reply = (EditText) view.findViewById(R.id.text_reply);
        }
    }

    public ListViewAdaptor(List<Post> dataList){
        this.mDataList = dataList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_moment, parent, false);
        context = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Post post = mDataList.get(position);

        holder.id.setText(Integer.toString(post.getid()));
        holder.content.setText(post.getcontent());
        holder.likes_count.setText(Integer.toString(post.getlikes()));
        holder.btn_like.setOnCheckedChangeListener(null);
        holder.btn_like.setChecked(post.like);
        // Show image if the post contains image
        if (TextUtils.isEmpty(post.getimagepath())) {

        }else{
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference pathReference = storageReference.child(post.getimagepath());
            Glide.with(context)
                    .load(pathReference)
                    .apply(new RequestOptions().override(300, 300))
                    .into(holder.imageView);
        }

        // Likes function
        holder.btn_like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DocumentReference dbref = db.collection("users").document(user.getDisplayName());
                if (isChecked) {
                    post.setlike(true);
                    post.setlikes_account(post.getlikes()+1);
                    updateLikeCount(post);
                    dbref.update("postLikes", FieldValue.arrayUnion(post.getpid()));
                } else {
                    post.setlike(false);
                    post.setlikes_account(post.getlikes()-1);
                    updateLikeCount(post);
                    dbref.update("postLikes", FieldValue.arrayRemove(post.getpid()));
                }
            }
        });

        // Click item to expand the list
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getreply(post, holder);
            }
        });

        // Save reply to db
        holder.btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String value = holder.text_reply.getText().toString();
                if(TextUtils.isEmpty(value)){
                    Toast.makeText(context,"Please type something",Toast.LENGTH_SHORT).show();
                }else{
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    writetodb(Integer.parseInt(user.getDisplayName()),value,post.getpid());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void updateLikeCount(Post post){
        DocumentReference dbref = db.collection("posts").document(post.getpid());
        dbref.update("likes",post.getlikes()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                notifyDataSetChanged();
            }
        });
    }

    // Get replies on db
    public void getreply(Post post,MyViewHolder holder){
        final MyViewHolder holdera = holder;
        replies = new ArrayList<>();
        Query reply = db.collection("replies").whereEqualTo("reply_to_id",post.getpid());
        reply.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Reply reply = new Reply(document.getLong("id").intValue(),document.getString("content"));
                        replies.add(reply);
                    }
                    mAdapter = new ChildViewAdaptor(replies);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
                    holdera.child_view.setLayoutManager(mLayoutManager);
                    holdera.child_view.setItemAnimator(new DefaultItemAnimator());
                    holdera.child_view.setHasFixedSize(true);
                    holdera.child_view.setAdapter(mAdapter);
                } else {

                }
            }
        });
    }

    private void writetodb(Integer id, String content, String pid){
        // Create post
        Map<String, Object> post = new HashMap<>();
        post.put("id", id);
        post.put("content", content);
        post.put("reply_to_id", pid);
        post.put("created_at", FieldValue.serverTimestamp());

        // Save post to database
        db.collection("replies")
                .add(post)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(context,"Successful!",Toast.LENGTH_SHORT).show();
                        Log.d("Post","Successful!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Failed. Please check your network connection",Toast.LENGTH_SHORT).show();
                        Log.d("Post","Failed. Please check your network connection");
                    }
                });
    }
}


