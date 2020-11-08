package au.edu.sydney.comp5216.project.RoomChat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import au.edu.sydney.comp5216.project.R;

public class RoomMsgAdapter extends RecyclerView.Adapter<RoomMsgAdapter.RoomMessageAdapterViewHolder> {

    Context context;
    List<RoomMessage> messageList;
    DatabaseReference messagedb;

    /**
     * Constructor of custome room message adapter
     * @param context Context type of handle context
     * @param messageList List type of message list
     * @param messagedb DatabaseReference of message database reference
     */
    public RoomMsgAdapter(Context context,
                          List<RoomMessage> messageList, DatabaseReference messagedb) {
        this.context = context;
        this.messageList = messageList;
        this.messagedb = messagedb;
    }

    /**
     *  Custom room message adapter view holder
     * @param parent ViewGroup type
     * @param viewType View type
     * @return RoomMessageAdapterViewHolder type, custom view holder
     */
    @NonNull
    @Override
    public RoomMessageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_msg, parent, false);
        return new RoomMessageAdapterViewHolder(view);
    }

    /**
     * Handle bind view holder and room messages
     * @param holder RoomMessageAdapterViewHolder type
     * @param position int type, view position
     */
    @Override
    public void onBindViewHolder(@NonNull RoomMsgAdapter.RoomMessageAdapterViewHolder holder, int position) {
        RoomMessage message = messageList.get(position);

        if (message.getUserId().equals(AllMethods.userid)) {
            holder.tv_room_msg_right.setText(message.getMessage());
            holder.ll_left.setVisibility(View.GONE);
            holder.rl_right.setVisibility(View.VISIBLE);
        } else {
            holder.tv_room_id_receiver.setText("ID:" + message.getUserId());
            holder.tv_room_msg_left.setText(message.getMessage());
            holder.rl_right.setVisibility(View.GONE);
            holder.ll_left.setVisibility(View.VISIBLE);
        }
    }

    /**
     * get message list size
     * @return int type of list size
     */
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    /**
     * Custom room message adapter view holder class
     */
    public class RoomMessageAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView tv_room_id_receiver, tv_room_msg_left, tv_room_msg_right;
        ImageView iv_room_left, iv_room_right;
        LinearLayout ll_left;
        RelativeLayout rl_right;

        /**
         * Constructor of custom room message adapter view holder
         * @param itemView view type of current view
         */
        public RoomMessageAdapterViewHolder(View itemView) {
            super(itemView);

            tv_room_id_receiver = (TextView) itemView.findViewById(R.id.tv_room_id_left);
            tv_room_msg_left = (TextView) itemView.findViewById(R.id.tv_room_msg_left);
            tv_room_msg_right = (TextView) itemView.findViewById(R.id.tv_room_msg_right);
            iv_room_left = (ImageView) itemView.findViewById(R.id.iv_room_avatar_left);
            iv_room_right = (ImageView) itemView.findViewById(R.id.iv_room_avatar_right);
            ll_left = (LinearLayout) itemView.findViewById(R.id.ll_room_msg_layout_receiver);
            rl_right = (RelativeLayout) itemView.findViewById(R.id.rl_room_msg_layout_sender);
        }
    }
}
