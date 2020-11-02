package au.edu.sydney.comp5216.project.ui.chat;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.sydney.comp5216.project.common.Constants;
import au.edu.sydney.comp5216.project.entity.ChatListInfo;
import au.edu.sydney.comp5216.project.utils.DBHelper;

public class ChatViewModel extends ViewModel {

    private MutableLiveData<List<ChatListInfo>> mItemData ;

    public ChatViewModel() {
        mItemData = new MutableLiveData<>();
    }

    public LiveData<List<ChatListInfo>> getData()
    {
        return mItemData;
    }


    public void onCreate()
    {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if(!TextUtils.isEmpty(uid)) {
            DBHelper.getInstance().getDatabase().getReference(Constants.Reference.ChatList).child(uid)
                    .addValueEventListener(mValueListener);
        }
    }

    private ValueEventListener mValueListener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            GenericTypeIndicator<HashMap<String, ChatListInfo>> type = new GenericTypeIndicator<HashMap<String, ChatListInfo>>() {};
            HashMap<String, ChatListInfo> chatLists = snapshot.getValue(type);
            List<ChatListInfo> result = new ArrayList<>();
            if(chatLists != null) {
                for(Map.Entry<String, ChatListInfo> entry:chatLists.entrySet()){
                    result.add(entry.getValue());
                }
            }
            mItemData.setValue(result);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public ChatListInfo getUserInfo(int pos)
    {
        List<ChatListInfo> list = mItemData.getValue();
        if(list != null){
            return list.get(pos);
        }
        return null;
    }

    public void onDestroy()
    {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(!TextUtils.isEmpty(uid)) {
            DBHelper.getInstance().getDatabase()
                    .getReference(Constants.Reference.ChatList)
                    .child(uid)
                    .removeEventListener(mValueListener);
        }
    }
}
