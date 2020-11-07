package au.edu.sydney.comp5216.project.ui.moment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

import java.io.File;
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
    public Integer mExpandedPosition = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView id,content, likes_count;
        public ImageView imageView,picture;
        public ToggleButton btn_like;
        public RecyclerView child_view;
        public ImageButton btn_reply;
        public TextInputLayout text_reply;
        public TextInputEditText editText_reply;


        public MyViewHolder(View view){
            super(view);
            id = (TextView) view.findViewById(R.id.user_id);
            content= (TextView) view.findViewById(R.id.post_content);
            imageView = (ImageView) view.findViewById(R.id.image_post);
            likes_count = (TextView) view.findViewById(R.id.likes_count);
            btn_like = (ToggleButton) view.findViewById(R.id.btn_likes);
            child_view = (RecyclerView) view.findViewById(R.id.childrecyclerview);
            btn_reply = (ImageButton) view.findViewById(R.id.btn_reply);
            text_reply = (TextInputLayout) view.findViewById(R.id.text_reply);
            picture = (ImageView)view.findViewById(R.id.image_user);
            editText_reply = (TextInputEditText) view.findViewById(R.id.edittext_reply);
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
        final boolean isExpanded = position==mExpandedPosition;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        switch(post.getgender()){
            case "male":
                holder.picture.setImageResource(R.drawable.male);
            case "Female":
                holder.picture.setImageResource(R.drawable.female);
            case "other":
                holder.picture.setImageResource(R.drawable.anonymous);
        }
        holder.child_view.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.btn_reply.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.text_reply.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        // Adpater for nested recyclerview
        mAdapter = new ChildViewAdaptor(replies);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        holder.child_view.setLayoutManager(mLayoutManager);
        holder.child_view.setItemAnimator(new DefaultItemAnimator());
        holder.child_view.setHasFixedSize(true);
        holder.child_view.setAdapter(mAdapter);

        holder.itemView.setActivated(isExpanded);
        holder.id.setText(Integer.toString(post.getid()));
        holder.content.setText(post.getcontent());
        holder.likes_count.setText(Integer.toString(post.getlikes()));
        holder.btn_like.setOnCheckedChangeListener(null);
        holder.btn_like.setChecked(post.like);
        holder.imageView.setImageResource(android.R.color.transparent);

        // Show image if the post contains image
        if (TextUtils.isEmpty(post.getimagepath())) {
            holder.imageView.getLayoutParams().height = 0;
        }else{
            holder.imageView.getLayoutParams().height = 600;
            final StorageReference pathReference = storageReference.child(post.getimagepath());
            Glide.with(context)
                    .load(pathReference)
                    .centerCrop()
                    .into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.image_viewer);
                    ImageView image = (ImageView) dialog.findViewById(R.id.image_view);
                    image.setScaleType(ImageView.ScaleType.FIT_XY);
                    Glide.with(context)
                            .load(pathReference)
                            .into(image);
                    dialog.getWindow().setBackgroundDrawable(null);
                    dialog.show();
                }
            });
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
                mExpandedPosition = isExpanded ? -1:position;
                notifyItemChanged(position);
                if(isExpanded == false){
                    replies.clear();
                    mAdapter.notifyItemChanged(position);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getreply(post, holder, position);
                        }
                    }, 300);
                }
            }
        });

        // Save reply to db
        holder.btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String value = holder.text_reply.getEditText().getText().toString();
                holder.editText_reply.setText("");
                if(TextUtils.isEmpty(value)){
                    Toast.makeText(context,"Please type something",Toast.LENGTH_SHORT).show();
                }else{
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    getgender(Integer.parseInt(user.getDisplayName()),value,post.getpid(),position);
                    mExpandedPosition = isExpanded ? -1:position;
                    notifyItemChanged(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    // Update like counts from database
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
    public void getreply(Post post, final MyViewHolder holder, Integer position){
        final MyViewHolder holdera = holder;
        final Integer myposition = position;
        Query reply = db.collection("replies").whereEqualTo("reply_to_id",post.getpid());
        reply.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Reply reply = new Reply(document.getLong("id").intValue(),document.getString("content"),document.getString("gender"));
                        replies.add(reply);
                    }
                    if(replies.size() != 0){
                        mAdapter.notifyDataSetChanged();
                        notifyItemChanged(myposition);
                    }else{
                        //notifyItemChanged(myposition);
                    }
                } else {
                }
            }
        });
    }

    // Save replies to database
    private void writetodb(Integer id, String content, String pid, final Integer position, String gender){
        // Create reply
        Map<String, Object> post = new HashMap<>();
        post.put("id", id);
        post.put("content", content);
        post.put("reply_to_id", pid);
        post.put("gender", gender);
        post.put("created_at", FieldValue.serverTimestamp());

        // Save reply to database
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

    // Get the gender of users of posts
    public void getgender(final Integer id, final String content, final String pid, final Integer position){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docref = db.collection("users").document(user.getDisplayName());
        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        writetodb(id,content,pid,position,document.getString("gender"));
                    } else {
                    }
                } else {
                    Toast.makeText(context,"Failed. Please check your network connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


