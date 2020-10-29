package au.edu.sydney.comp5216.project.ui.moment;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
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
import java.util.List;

import au.edu.sydney.comp5216.project.R;
import au.edu.sydney.comp5216.project.ui.post.Post;

public class ListViewAdaptor extends RecyclerView.Adapter<ListViewAdaptor.MyViewHolder>{
    private List<Post> mDataList;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView id,content, likes_count;
        public ImageView imageView;
        public ToggleButton btn_like;
        public ArrayList<String> list_like;


        public MyViewHolder(View view){
            super(view);
            id = (TextView) view.findViewById(R.id.user_id);
            content= (TextView) view.findViewById(R.id.post_content);
            imageView = (ImageView) view.findViewById(R.id.image_post);
            likes_count = (TextView) view.findViewById(R.id.likes_count);
            btn_like = (ToggleButton) view.findViewById(R.id.btn_likes);
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
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Post post = mDataList.get(position);
        holder.id.setText(Integer.toString(post.getid()));
        holder.content.setText(post.getcontent());
        holder.likes_count.setText(Integer.toString(post.getlikes()));
        holder.btn_like.setOnCheckedChangeListener(null);
        holder.btn_like.setChecked(post.like);
        if (TextUtils.isEmpty(post.getimagepath())) {

        }else{
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference pathReference = storageReference.child(post.getimagepath());
            Glide.with(context)
                    .load(pathReference)
                    .apply(new RequestOptions().override(300, 300))
                    .into(holder.imageView);
        }
        holder.btn_like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DocumentReference dbref = db.collection("users").document(user.getDisplayName());
                if (isChecked) {
                    post.setlike(true);
                    post.setlikes_account(post.getlikes()+1);
                    updatelike(post);
                    dbref.update("likes", FieldValue.arrayUnion(post.getpid()));
                } else {
                    post.setlike(false);
                    post.setlikes_account(post.getlikes()-1);
                    updatelike(post);
                    dbref.update("likes", FieldValue.arrayRemove(post.getpid()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void updatelike(Post post){
        DocumentReference dbref = db.collection("posts").document(post.getpid());
        dbref.update("likes",post.getlikes()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                notifyDataSetChanged();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
}


