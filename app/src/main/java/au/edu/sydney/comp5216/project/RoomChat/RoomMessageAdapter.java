package au.edu.sydney.comp5216.project.RoomChat;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import au.edu.sydney.comp5216.project.R;

public class RoomMessageAdapter extends RecyclerView.Adapter<RoomMessageAdapter.RoomMessageAdapterViewHolder> {

    Context context;
    List<RoomMessage> messageList;
    DatabaseReference messagedb;

    public RoomMessageAdapter(Context context,
                              List<RoomMessage> messageList, DatabaseReference messagedb) {
        this.context = context;
        this.messageList = messageList;
        this.messagedb = messagedb;
    }

    @NonNull
    @Override
    public RoomMessageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_message, parent,false);
        return new RoomMessageAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomMessageAdapter.RoomMessageAdapterViewHolder holder, int position) {
        RoomMessage message = messageList.get(position);

        if(message.getUserId().equals(AllMethods.userid)){
            holder.textView.setText("You: " + message.getMessage());
            holder.textView.setGravity(Gravity.END);
            holder.ll.setBackgroundColor(Color.parseColor("#EF9E73"));
        }else{
            holder.textView.setText(message.getUserId() + ": "+ message.getMessage());
            holder.textView.setGravity(Gravity.START);
            holder.ll.setBackgroundColor(Color.parseColor("#f7f7f7"));
            holder.imageButtonDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class RoomMessageAdapterViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageButton imageButtonDelete;
        LinearLayout ll;

        public RoomMessageAdapterViewHolder(View itemView){
           super(itemView);
           textView = (TextView)itemView.findViewById(R.id.tv_single_room_message);
           imageButtonDelete = (ImageButton)itemView.findViewById(R.id.imagebtn_message_delete);
           ll = (LinearLayout) itemView.findViewById(R.id.single_room_message);

           imageButtonDelete.setOnClickListener(new View.OnClickListener(){
               @Override
               public void onClick(View v) {
                   messagedb.child(messageList.get(getAdapterPosition()).getKey()).removeValue();
               }
           });
       }
   }

}
