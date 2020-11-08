package au.edu.sydney.comp5216.project.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.edu.sydney.comp5216.project.R;
import au.edu.sydney.comp5216.project.entity.MessageInfo;
import au.edu.sydney.comp5216.project.ui.chat.ChatDetailActivity;


public class ChatDetailAdapter extends RecyclerView.Adapter<ChatDetailAdapter.MsgViewHolder> {

    private List<MessageInfo> listData;
    private Context context;

    /**
     * Constructor of custom personal message adapter
     * @param context
     * @param listData
     */
    public ChatDetailAdapter(Context context, List<MessageInfo> listData) {
        this.context = context;
        this.listData = listData;
    }

    /**
     * custom personal message adapter view holder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_msg_list, parent, false);
        return new MsgViewHolder(view);
    }

    /**
     * Handle bind view to display and hide in one to one chat
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MsgViewHolder holder, int position) {
        MessageInfo currentMsgData = listData.get(position);
        MessageInfo preMsgData = null;
        if (position >= 1)
            preMsgData = listData.get(position - 1);
        switch (currentMsgData.msgType) {
            case ChatDetailActivity.TYPE_RECEIVER_MSG:
                initTimeStamp(holder, currentMsgData, preMsgData);
                holder.senderLayout.setVisibility(View.GONE);
                holder.receiverLayout.setVisibility(View.VISIBLE);
                holder.receiveMsg.setText(currentMsgData.msg);
                holder.receiver_profile.setImageResource(currentMsgData.avatarRes);
                break;

            case ChatDetailActivity.TYPE_SENDER_MSG:
                initTimeStamp(holder, currentMsgData, preMsgData);
                holder.senderLayout.setVisibility(View.VISIBLE);
                holder.receiverLayout.setVisibility(View.GONE);
                holder.sendMsg.setText(currentMsgData.msg);
                holder.send_profile.setImageResource(currentMsgData.avatarRes);
                break;
        }
    }

    private void initTimeStamp(MsgViewHolder holder, MessageInfo currentMsgData, MessageInfo preMsgData) {
        holder.timeStamp.setVisibility(View.GONE);
    }

    /**
     * get message list size
     * @return
     */
    @Override
    public int getItemCount() {
        return listData.size();
    }

    /**
     * Custom personal message adapter view holder class
     */
    class MsgViewHolder extends RecyclerView.ViewHolder {

        ImageView receiver_profile, send_profile;
        TextView timeStamp, receiveMsg, sendMsg;
        RelativeLayout senderLayout;
        LinearLayout receiverLayout;

        /**
         * Constructor of custom personal message adapter view holder
         * @param itemView
         */
        public MsgViewHolder(View itemView) {
            super(itemView);
            receiver_profile =  itemView.findViewById(R.id.item_wechat_msg_iv_receiver_profile);
            send_profile =  itemView.findViewById(R.id.item_wechat_msg_iv_sender_profile);
            timeStamp =  itemView.findViewById(R.id.item_wechat_msg_iv_time_stamp);
            receiveMsg =  itemView.findViewById(R.id.item_wechat_msg_tv_receiver_msg);
            sendMsg =  itemView.findViewById(R.id.item_wechat_msg_tv_sender_msg);
            senderLayout =  itemView.findViewById(R.id.item_wechat_msg_layout_sender);
            receiverLayout =  itemView.findViewById(R.id.item_wechat_msg_layout_receiver);
        }
    }
}
