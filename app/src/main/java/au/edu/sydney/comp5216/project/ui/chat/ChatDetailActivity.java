package au.edu.sydney.comp5216.project.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.sydney.comp5216.project.MessageService;
import au.edu.sydney.comp5216.project.R;
import au.edu.sydney.comp5216.project.adapter.ChatDetailAdapter;
import au.edu.sydney.comp5216.project.common.Constants;
import au.edu.sydney.comp5216.project.entity.ChatListInfo;
import au.edu.sydney.comp5216.project.entity.MessageInfo;
import au.edu.sydney.comp5216.project.utils.DBHelper;
import au.edu.sydney.comp5216.project.utils.HelpUtils;
import au.edu.sydney.comp5216.project.utils.PreferencesUtils;

public class ChatDetailActivity extends AppCompatActivity {
    public final static int TYPE_RECEIVER_MSG = 0x21;
    public final static int TYPE_SENDER_MSG = 0x22;
    public final static int TYPE_TIME_STAMP = 0x23;
    private int profileId = R.drawable.hdimg_5;
    private Button sendBtn;
    private EditText msgEt;
    private List<MessageInfo> mChatMessages = new ArrayList<>();
    private ChatDetailAdapter mAdapter;
    private String mRoomId;
    private String mPeerUid;
    private String mPeerEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPeerUid = getIntent().getStringExtra("uid");
        mPeerEmail= getIntent().getStringExtra("email");
        setTitle(mPeerUid);
        sendBtn = findViewById(R.id.sendBtn);
        msgEt = findViewById(R.id.msgEt);
        String from = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        mRoomId = HelpUtils.getRoomId(from,mPeerUid);
        setListener();
        initDatas();
        MessageService.setIfNeedNotify(false);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String key = HelpUtils.getRoomId(uid,mPeerUid);
        PreferencesUtils.putBoolean(this, Constants.PrefKey.HAS_MESSAGE+key,false);
    }


    private void setListener()
    {
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String from = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            Log.d("sss","name="+from);
            String msg = msgEt.getText().toString();
            MessageInfo msgInfo = new MessageInfo();
            msgInfo.msg = msg;
            msgInfo.timeStamp = System.currentTimeMillis();
            msgInfo.creator = from;
            DBHelper.getInstance().sendMessage(msgInfo,mRoomId);
            msgEt.setText("");

            ChatListInfo chatListInfo = new ChatListInfo();
            chatListInfo.email = mPeerEmail;
            chatListInfo.userId = mPeerUid;
            chatListInfo.lastMessage = msg;
            chatListInfo.timeStamp = String.valueOf(System.currentTimeMillis());
            DBHelper.getInstance().updateChatList(chatListInfo,from,mPeerUid);
            }
        });
    }


    private void initDatas() {
        RecyclerView rv = findViewById(R.id.messageList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ChatDetailAdapter(this, mChatMessages);
        rv.setAdapter(mAdapter);
        DBHelper.getInstance().getDatabase().getReference(Constants.Reference.Messages)
            .child(mRoomId)
            .addValueEventListener(mMessageListener);
    }

    /**
     *Listen to the message callback and display to the chat list
     */
    private ValueEventListener mMessageListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            GenericTypeIndicator<HashMap<String, MessageInfo>> type = new GenericTypeIndicator<HashMap<String, MessageInfo>>() {};
            HashMap<String, MessageInfo> messages = snapshot.getValue(type);
            if(messages != null) {
                mChatMessages.clear();
                String from = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                for (Map.Entry<String, MessageInfo> entry : messages.entrySet()) {
                    MessageInfo info = entry.getValue();
                    if (from.equals(info.creator)) {
                        //If it is a sent message, display on the right
                        info.msgType = TYPE_SENDER_MSG;
                        info.avatarRes = profileId;
                    } else {
                        //If it is a receive message, display on the left
                        info.msgType = TYPE_RECEIVER_MSG;
                        info.avatarRes = R.drawable.hdimg_4;
                    }
                    mChatMessages.add(info);
                }
                Collections.sort(mChatMessages,mssageComparator);
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    private Comparator<MessageInfo> mssageComparator = new Comparator<MessageInfo>() {
        @Override
        public int compare(MessageInfo o1, MessageInfo o2) {
            if(o1.timeStamp > o2.timeStamp){
                return 1;
            }else if(o1.timeStamp < o2.timeStamp){
                return -1;
            }
            return 0;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageService.setIfNeedNotify(true);
        DBHelper.getInstance().getDatabase()
            .getReference(Constants.Reference.Messages)
            .child(mRoomId)
            .removeEventListener(mMessageListener);
    }
}