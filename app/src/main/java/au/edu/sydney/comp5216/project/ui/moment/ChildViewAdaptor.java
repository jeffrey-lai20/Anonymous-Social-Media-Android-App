package au.edu.sydney.comp5216.project.ui.moment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import au.edu.sydney.comp5216.project.R;
import au.edu.sydney.comp5216.project.ui.post.Post;

public class ChildViewAdaptor extends RecyclerView.Adapter<ChildViewAdaptor.ChildViewHolder>{
    private List<Reply> mDataList;
    private Context context;
    public class ChildViewHolder extends RecyclerView.ViewHolder{
        public TextView id,content;
        public ImageView picture;



        public ChildViewHolder(View view){
            super(view);
            id = (TextView) view.findViewById(R.id.user_id);
            content= (TextView) view.findViewById(R.id.post_content);
            picture = (ImageView)view.findViewById(R.id.image_user);
        }
    }

    public ChildViewAdaptor(List<Reply> dataList){
        this.mDataList = dataList;
    }

    @Override
    public ChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_childview, parent, false);
        context = parent.getContext();
        return new ChildViewAdaptor.ChildViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChildViewAdaptor.ChildViewHolder holder, int position) {
        final Reply reply = mDataList.get(position);
        holder.id.setText(Integer.toString(reply.getid()));
        holder.content.setText(reply.getcontent());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference pathpicture = storageReference.child(reply.getgender() + ".jpg");
        Glide.with(context)
                .load(pathpicture)
                .apply(new RequestOptions().override(50, 50))
                .into(holder.picture);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
