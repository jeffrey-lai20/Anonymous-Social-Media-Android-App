package au.edu.sydney.comp5216.project.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import au.edu.sydney.comp5216.project.R;
import au.edu.sydney.comp5216.project.base.CommonAdapter;
import au.edu.sydney.comp5216.project.base.CommonViewHolder;
import au.edu.sydney.comp5216.project.common.Constants;
import au.edu.sydney.comp5216.project.entity.ChatListInfo;
import au.edu.sydney.comp5216.project.utils.HelpUtils;
import au.edu.sydney.comp5216.project.utils.PreferencesUtils;

public class ChatFragment extends Fragment {

    private ChatViewModel chatViewModel;
    private CommonAdapter<ChatListInfo> mAdapter;
    private ListView chatLv;
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tipsTv;
    private ProgressBar progressBar;
    private CharSequence query;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        View root = inflater.inflate(R.layout.fragment_chat, container, false);
        setHasOptionsMenu(true);
        chatLv = root.findViewById(R.id.chatLv);
        searchView = root.findViewById(R.id.searchView);
        tipsTv = root.findViewById(R.id.tipsTv);
        progressBar = root.findViewById(R.id.progressBar);

        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
//        searchView.findViewById(androidx.appcompat.R.id.search_plate).setBackground(null);
//        searchView.findViewById(androidx.appcompat.R.id.submit_area).setBackground(null);
        chatViewModel.getData().observe(getViewLifecycleOwner(), new Observer<List<ChatListInfo>>() {
            @Override
            public void onChanged(List<ChatListInfo> data) {
                if(data.size()>0) {
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    tipsTv.setVisibility(View.GONE);
                    mAdapter.refreshView(data);
                }else{
                    swipeRefreshLayout.setVisibility(View.GONE);
                    tipsTv.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        mAdapter = new CommonAdapter<ChatListInfo>(R.layout.item_chat_view,chatViewModel.getData().getValue()) {
            @Override
            public void convert(CommonViewHolder holder, ChatListInfo item, int position) {
                RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
                ImageView imageView = holder.getImageView(R.id.avatarIv);
                Glide.with(getActivity())
                    .load(R.drawable.hdimg_4)
                    .apply(requestOptions)
                    .into(imageView);
                holder.setTvText(R.id.idTv,item.userId);
                holder.setTvText(R.id.messageTv,item.lastMessage);
                holder.setTvText(R.id.timeTv, HelpUtils.formatDate(item.timeStamp));
                if(hasMessage(item)) {
                    holder.setVisibility(R.id.redView, View.VISIBLE);
                }else{
                    holder.setVisibility(R.id.redView, View.GONE);
                }
            }
        };

        chatLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChatListInfo chatListInfo = chatViewModel.getUserInfo(i);
                if(chatListInfo != null) {
                    Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
                    intent.putExtra("uid", chatListInfo.userId);
                    intent.putExtra("email", chatListInfo.email);
                    startActivity(intent);
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    String key = HelpUtils.getRoomId(uid,chatListInfo.userId);
                    PreferencesUtils.putBoolean(getActivity(), Constants.PrefKey.HAS_MESSAGE+key,false);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        searchView = root.findViewById(R.id.searchView); // inititate a search view
        query = searchView.getQuery(); // get the query string currently in the text field
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                    }
                }, 600); // 600ms delay before the timer executes the „run“ method from TimerTask
                System.out.println("Change view to display searched users");
                chatViewModel.getData().observe(getViewLifecycleOwner(), new Observer<List<ChatListInfo>>() {
                    @Override
                    public void onChanged(List<ChatListInfo> data) {
                        if(data.size()>0) {
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            tipsTv.setVisibility(View.GONE);
                            mAdapter.refreshView(data);
                        }else{
                            swipeRefreshLayout.setVisibility(View.GONE);
                            tipsTv.setVisibility(View.VISIBLE);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });

                mAdapter = new CommonAdapter<ChatListInfo>(R.layout.item_chat_view,chatViewModel.getData().getValue()) {
                    @Override
                    public void convert(CommonViewHolder holder, ChatListInfo item, int position) {
                        if (!item.userId.contains(newText)) {
                            System.out.println("No results found");
                            holder.setVisibility(R.id.idTv, View.GONE);
                            holder.setVisibility(R.id.messageTv, View.GONE);
                            holder.setVisibility(R.id.timeTv, View.GONE);
                            holder.setVisibility(R.id.redView, View.GONE);
                            return;
                        }
                        RequestOptions requestOptions = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                        ImageView imageView = holder.getImageView(R.id.avatarIv);


                        Glide.with(getActivity())
                                .load(R.drawable.hdimg_4)
                                .apply(requestOptions)
                                    .into(imageView);

                        holder.setTvText(R.id.idTv,item.userId);
                        holder.setTvText(R.id.messageTv,item.lastMessage);
                        holder.setTvText(R.id.timeTv, HelpUtils.formatDate(item.timeStamp));
                        if(hasMessage(item)) {
                            holder.setVisibility(R.id.redView, View.VISIBLE);
                        }else{
                            holder.setVisibility(R.id.redView, View.GONE);
                        }


                    }
                };

                chatLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        ChatListInfo chatListInfo = chatViewModel.getUserInfo(i);
                        if(chatListInfo != null) {
                            if (chatListInfo.userId.contains(newText)) {
                                Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
                                intent.putExtra("uid", chatListInfo.userId);
                                intent.putExtra("email", chatListInfo.email);
                                startActivity(intent);
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                String key = HelpUtils.getRoomId(uid, chatListInfo.userId);
                                PreferencesUtils.putBoolean(getActivity(), Constants.PrefKey.HAS_MESSAGE + key, false);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                return;
                            }
                        }
                    }
                });
                chatLv.setAdapter(mAdapter);
                chatViewModel.onCreate();
                return true;
            }
        });


        chatLv.setAdapter(mAdapter);
        chatViewModel.onCreate();
        return root;
    }


    private boolean hasMessage(ChatListInfo chatListInfo)
    {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String key = HelpUtils.getRoomId(uid,chatListInfo.userId);
        return PreferencesUtils.getBoolean(getActivity(), Constants.PrefKey.HAS_MESSAGE+key,false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chatViewModel.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.chat_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_friend) {
            startActivity(new Intent(getActivity(), AddFriendsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
