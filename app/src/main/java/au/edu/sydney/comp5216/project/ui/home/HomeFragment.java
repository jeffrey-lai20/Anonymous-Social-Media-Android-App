package au.edu.sydney.comp5216.project.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import au.edu.sydney.comp5216.project.GridAdapter;
import au.edu.sydney.comp5216.project.R;
import au.edu.sydney.comp5216.project.RoomItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private GridView gridView;
    private GridAdapter gridAdapter;
    private ArrayList<RoomItem> rooms;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore fireStore;

    private String userId;
    private String roomName = "";

    private ImageButton addNewRoomButton;
    private ImageButton searchRoomButton;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        fireStore = FirebaseFirestore.getInstance();

        if(firebaseUser != null){
            userId = firebaseUser.getDisplayName();
            Toast.makeText(getActivity(),
                    "User ID: "+ userId+"!", Toast.LENGTH_SHORT).show();
            rooms = new ArrayList<RoomItem>();
            rooms.add(new RoomItem(userId,"Welcome to 0204!",""));
        } else {
            Toast.makeText(getActivity(),
                    "User is not exist!", Toast.LENGTH_SHORT).show();
        }

        gridView = (GridView) root.findViewById(R.id.gridView);
        gridAdapter = new GridAdapter(getActivity(), rooms);
        gridView.setAdapter(gridAdapter);

        //set grid view on item click
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //....
            }
        });

        //set up click listener for add new room button
        addNewRoomButton = (ImageButton) root.findViewById(R.id.btn_add_new_room);
        addNewRoomButton.setOnClickListener(this);
        //set up click listener for search room button
        searchRoomButton = (ImageButton) root.findViewById(R.id.btn_search_room);
        searchRoomButton.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_add_new_room:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Room Name");
                builder.setMessage("Please enter your room name here:");

                //set up the input
                final EditText roomNameInput = new EditText(getActivity());
                builder.setView(roomNameInput);

                //set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputName = roomNameInput.getText().toString();
                        boolean checkBoolean = inputCheck(inputName);
                        if(checkBoolean){
                            //add new room to grid view
                            RoomItem roomItem = new RoomItem(userId, inputName,"1");
                            rooms.add(roomItem);
                            saveRoomToDB(roomItem);
                            gridAdapter.notifyDataSetChanged();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                break;

            case R.id.btn_search_room:

                break;
            default:
                break;
        }
    }

    private boolean inputCheck(String input){
        if(input.toCharArray().length == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.room_name_dialog_empty_title)
                    .setMessage(R.string.room_name_dialog_empty_msg)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //check whether current work is fast
                            dialogInterface.cancel();
                        }
                    });
            builder.create().show();
            return false;
        } else if(input.toCharArray().length > 50){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.room_name_dialog_long_title)
                    .setMessage(R.string.room_name_dialog_long_msg)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //check whether current work is fast
                            dialogInterface.cancel();
                        }
                    });
            builder.create().show();
            return false;
        } else{
            roomName = input;
            Toast.makeText(getActivity(),
                    "Room Added: "+ roomName, Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private void saveRoomToDB(RoomItem roomItem){
        CollectionReference rooms = fireStore.collection("rooms");
        // get all room info
        Map<String, Object> room = new HashMap<>();
        room.put("room_id", roomItem.getRoomId());
        room.put("owner_id", roomItem.getOwnerId());
        room.put("room_name", roomItem.getRoomName());
        room.put("joined_num", roomItem.getJoinedUserNum());
        room.put("joined_user_list", roomItem.getJoinedUserIDs());

        rooms.document(roomItem.getRoomId()).set(room);
    }
}