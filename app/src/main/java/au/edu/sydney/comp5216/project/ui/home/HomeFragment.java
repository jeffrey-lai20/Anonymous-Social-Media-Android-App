package au.edu.sydney.comp5216.project.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import au.edu.sydney.comp5216.project.GridAdapter;
import au.edu.sydney.comp5216.project.R;

public class HomeFragment extends Fragment {

    //private HomeViewModel homeViewModel;
    private GridView gridView;
    private String[] rooms={"I just want to find someone to talk","Room2","Room3","Room4","Room5","Room6","Room7"};
    private String[] peopleNumOfRooms={"1","2","3","4","5","6","7"};


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        gridView = (GridView) root.findViewById(R.id.gridView);
        gridView.setAdapter(new GridAdapter(getActivity(), rooms, peopleNumOfRooms));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //....
            }
        });
        return root;
    }

    public void addNewRoomClicked(View v){}

    public void searchRoomClicked(View v){}

}