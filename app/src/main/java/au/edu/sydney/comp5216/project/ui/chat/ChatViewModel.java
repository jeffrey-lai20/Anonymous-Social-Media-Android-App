package au.edu.sydney.comp5216.project.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import au.edu.sydney.comp5216.project.entity.ChatInfo;

public class ChatViewModel extends ViewModel {

    private MutableLiveData<List<ChatInfo>> mItemData ;

    public ChatViewModel() {
        mItemData = new MutableLiveData<>();
        List<ChatInfo> infos = new ArrayList<>();
        ChatInfo info = new ChatInfo();
        info.userId = "1232131";
        info.lastMessage = "what's your name? would you like something to eat?how are you?how are you?how are you?";
        info.time = "Sep 2020 Fri";
        infos.add(info);
        infos.add(info);
        infos.add(info);
        infos.add(info);
        mItemData.setValue(infos);
    }

    public LiveData<List<ChatInfo>> getData()
    {
        return mItemData;
    }


}
