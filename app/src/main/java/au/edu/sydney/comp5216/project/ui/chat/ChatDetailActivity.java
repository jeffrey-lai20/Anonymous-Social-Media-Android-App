package au.edu.sydney.comp5216.project.ui.chat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import au.edu.sydney.comp5216.project.R;
import au.edu.sydney.comp5216.project.adapter.ChatDetailAdapter;
import au.edu.sydney.comp5216.project.base.CommonAdapter;
import au.edu.sydney.comp5216.project.entity.ChatInfo;
import au.edu.sydney.comp5216.project.entity.MsgData;
import au.edu.sydney.comp5216.project.utils.HelpUtils;

public class ChatDetailActivity extends AppCompatActivity {
    public final static int TYPE_RECEIVER_MSG = 0x21;
    public final static int TYPE_SENDER_MSG = 0x22;
    public final static int TYPE_TIME_STAMP = 0x23;
    private int profileId = R.drawable.hdimg_5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("1232131");
        initComponents();
    }

    private void initComponents() {
        RecyclerView rv = findViewById(R.id.messageList);
        rv.setLayoutManager(new LinearLayoutManager(this));

        String[] msgs = {"hi", "hello", "how are you doing?", "not bad,how are you?", "i'm fine", "what's your job?", "i'm a teacher."
                , "great job!", "what's yours?", "i'm a lawyer", "sounds great!", "what's your favorite movie?", "let's me see,maybe that",
                "what?", "you know that", "know what? i don't know", "haha...", "witch one？",
                "fly", "fly? ???? ！！！", "oh~~~"};
        List<MsgData> data = new ArrayList<>();

        for (int i = 0; i < msgs.length; i++) {
            MsgData msgData = new MsgData(msgs[i], HelpUtils.getCurrentMillisTime(), i % 2 == 0 ? profileId : R.drawable.hdimg_4
                    , i % 2 == 0 ? TYPE_RECEIVER_MSG : TYPE_SENDER_MSG);
            data.add(i, msgData);
        }

        ChatDetailAdapter adapter = new ChatDetailAdapter(this, data);
        rv.setAdapter(adapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}