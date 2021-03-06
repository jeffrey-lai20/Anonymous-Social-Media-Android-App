package au.edu.sydney.comp5216.project.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import au.edu.sydney.comp5216.project.common.Constants;
import au.edu.sydney.comp5216.project.entity.ChatListInfo;
import au.edu.sydney.comp5216.project.entity.MessageInfo;
import au.edu.sydney.comp5216.project.entity.UserInfo;

public class DBHelper {
    private static DBHelper sDBHelper;
    private final FirebaseDatabase database;

    private DBHelper() {
        database = FirebaseDatabase.getInstance();
    }

    public static DBHelper getInstance() {
        if (sDBHelper == null) {
            sDBHelper = new DBHelper();
        }
        return sDBHelper;
    }

    public FirebaseDatabase getDatabase()
    {
        return database;
    }

    /**
     * peer to peer way
     * send message
     * @param message the message to send
     * @param roomId room id of chat
     * */
    public void sendMessage(MessageInfo message, String roomId) {
        DatabaseReference messageRef = database.getReference(Constants.Reference.Messages).child(roomId);
        messageRef.push().setValue(message);
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("sss", "onDataChange");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("sss", "onCancelled");
            }
        });
    }

    public void updateUserInfo()
    {
        UserInfo info = new UserInfo();
        info.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("sss","user id ==>"+info.id);
        info.uid = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        Log.d("sss","user uid ==>"+info.uid);
        info.email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.d("sss","user email ==>"+info.email);
        database.getReference(Constants.Reference.Users)
                .child(info.id).setValue(info);
    }

    /**
     * update chat list
     * @param chatListInfo
     * @param from
     * @param to
     * @return
     */
    public Task<Void> updateChatList(ChatListInfo chatListInfo, String from, String to)
    {
        String roomId = HelpUtils.getRoomId(from,to);
        database.getReference(Constants.Reference.ChatList)
                .child(from)
                .child(roomId)
                .setValue(chatListInfo);
        chatListInfo.userId = from;
        chatListInfo.email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        return database.getReference(Constants.Reference.ChatList)
                .child(to)
                .child(roomId)
                .setValue(chatListInfo);
    }

}
