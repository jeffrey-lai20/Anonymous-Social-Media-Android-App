package au.edu.sydney.comp5216.project.ui.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.sydney.comp5216.project.R;
import au.edu.sydney.comp5216.project.base.CommonAdapter;
import au.edu.sydney.comp5216.project.base.CommonViewHolder;
import au.edu.sydney.comp5216.project.common.Constants;
import au.edu.sydney.comp5216.project.entity.ChatListInfo;
import au.edu.sydney.comp5216.project.entity.UserInfo;
import au.edu.sydney.comp5216.project.utils.DBHelper;
import au.edu.sydney.comp5216.project.utils.HelpUtils;


public class AddFriendsActivity extends AppCompatActivity {

    private CommonAdapter<UserInfo> mAdapter;
    private List<UserInfo> mFriends = new ArrayList();
    private List<String> mRelations = new ArrayList();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("0204");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_add_friends);
        ListView list = findViewById(R.id.friendsList);
        progressBar = findViewById(R.id.progressBar);
        mAdapter = new CommonAdapter<UserInfo>(R.layout.item_friends,mFriends) {
            @Override
            public void convert(CommonViewHolder holder, final UserInfo item, int position) {
                holder.setTvText(R.id.nameTv,item.uid);
                holder.getView(R.id.addBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addFriend(item);
                    }
                });
            }
        };
        list.setAdapter(mAdapter);

        initData();
    }


    private void initData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        DBHelper.getInstance().getDatabase().getReference(Constants.Reference.ChatList)
                .child(uid).addValueEventListener(mChatListValueLister);
    }

    private ValueEventListener mChatListValueLister = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            GenericTypeIndicator<HashMap<String, ChatListInfo>> type = new GenericTypeIndicator<HashMap<String, ChatListInfo>>() {};
            HashMap<String, ChatListInfo> chatLists = snapshot.getValue(type);
            if(chatLists != null) {
                mRelations.clear();
                for(Map.Entry<String, ChatListInfo> entry:chatLists.entrySet()){
                    mRelations.add(entry.getKey());
                }
            }
            DBHelper.getInstance().getDatabase().getReference(Constants.Reference.Users)
                    .addValueEventListener(mValueListener);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private ValueEventListener mValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            GenericTypeIndicator<HashMap<String, UserInfo>> type = new GenericTypeIndicator<HashMap<String, UserInfo>>() {};
            HashMap<String, UserInfo> users = snapshot.getValue(type);
            if(users != null) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                mFriends.clear();
                for (Map.Entry<String, UserInfo> entry : users.entrySet()) {
                    UserInfo info = entry.getValue();
                    if(!TextUtils.isEmpty(info.uid)) {
                        if (!uid.equals(info.uid) && !isFriends(info.uid)) {
                            mFriends.add(info);
                        }
                    }
                }
            }
            mAdapter.refreshView(mFriends);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private boolean isFriends(String uid)
    {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String roomId = HelpUtils.getRoomId(myUid,uid);
        return mRelations.contains(roomId);
    }

    /**
     * add friends
     * @param user
     */
    private void addFriend(UserInfo user)
    {
        ChatListInfo chatListInfo = new ChatListInfo();
        chatListInfo.timeStamp = String.valueOf(System.currentTimeMillis());
        chatListInfo.lastMessage = "You are friends now!";
        chatListInfo.userId = user.uid;
        chatListInfo.email = user.email;

        String from = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        DBHelper.getInstance().updateChatList(chatListInfo,from,user.uid).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AddFriendsActivity.this,
                        "add success",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBHelper.getInstance().getDatabase()
            .getReference(Constants.Reference.Users)
            .removeEventListener(mValueListener);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        DBHelper.getInstance().getDatabase().getReference(Constants.Reference.ChatList)
                .child(uid).removeEventListener(mChatListValueLister);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}