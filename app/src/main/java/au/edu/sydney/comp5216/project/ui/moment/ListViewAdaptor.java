package au.edu.sydney.comp5216.project.ui.moment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import au.edu.sydney.comp5216.project.R;
import au.edu.sydney.comp5216.project.ui.post.Post;

public class ListViewAdaptor extends RecyclerView.Adapter<ListViewAdaptor.MyViewHolder>{
    private List<Post> mDataList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView id,content;
        public ImageView imageView;


        public MyViewHolder(View view){
            super(view);
            id = (TextView) view.findViewById(R.id.user_id);
            content= (TextView) view.findViewById(R.id.post_content);
            imageView = (ImageView) view.findViewById(R.id.image_post);

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
        Post post = mDataList.get(position);
        holder.id.setText(Integer.toString(post.getid()));
        holder.content.setText(post.getcontent());
        if (TextUtils.isEmpty(post.getimagepath())) {
            return;
        }else{
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference pathReference = storageReference.child(post.getimagepath());
            Glide.with(context)
                    .load(pathReference)
                    .apply(new RequestOptions().override(300, 300))
                    .into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}

