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
import java.util.List;

import au.edu.sydney.comp5216.project.R;
import au.edu.sydney.comp5216.project.base.CommonAdapter;
import au.edu.sydney.comp5216.project.base.CommonViewHolder;
import au.edu.sydney.comp5216.project.entity.ChatInfo;

public class ChatFragment extends Fragment {

    private ChatViewModel chatViewModel;
    private CommonAdapter<ChatInfo> mAdapter;
    private ListView chatLv;
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        View root = inflater.inflate(R.layout.fragment_chat, container, false);
        setHasOptionsMenu(true);
        chatLv = root.findViewById(R.id.chatLv);
        searchView = root.findViewById(R.id.searchView);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        searchView.findViewById(androidx.appcompat.R.id.search_plate).setBackground(null);
        searchView.findViewById(androidx.appcompat.R.id.submit_area).setBackground(null);
        chatViewModel.getData().observe(getViewLifecycleOwner(), new Observer<List<ChatInfo>>() {
            @Override
            public void onChanged(List<ChatInfo> data) {
                mAdapter.refreshView(data);
            }
        });

        mAdapter = new CommonAdapter<ChatInfo>(R.layout.item_chat_view,chatViewModel.getData().getValue()) {
            @Override
            public void convert(CommonViewHolder holder, ChatInfo item, int position) {
                RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
                ImageView imageView = holder.getImageView(R.id.avatarIv);
                Glide.with(getActivity())
                    .load(R.mipmap.ic_launcher)
                    .apply(requestOptions)
                    .into(imageView);
                holder.setTvText(R.id.idTv,item.userId);
                holder.setTvText(R.id.messageTv,item.lastMessage);
                holder.setTvText(R.id.timeTv,item.time);
            }
        };

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        chatLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(getActivity(),ChatDetailActivity.class));
            }
        });

        chatLv.setAdapter(mAdapter);
        return root;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.chat_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_friend:
                Toast.makeText(getActivity(),"add friend",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
