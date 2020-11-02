package au.edu.sydney.comp5216.project.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import au.edu.sydney.comp5216.project.RoomChat.RoomChatActivity;
import au.edu.sydney.comp5216.project.RoomItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener {

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

        rooms = new ArrayList<RoomItem>();

        userId = firebaseUser.getDisplayName();
        Toast.makeText(getActivity(),
                "User ID: " + userId + "!", Toast.LENGTH_SHORT).show();

        RoomItem defaultRoom = new RoomItem(userId, "Welcome to 0204!");
        defaultRoom.setRoomCreatedTime("2020-1-1 00:00:01");
        rooms.add(defaultRoom);
        getRoomFromDB();

        gridView = (GridView) root.findViewById(R.id.gridView);
        gridAdapter = new GridAdapter(getActivity(), rooms);
        gridView.setAdapter(gridAdapter);

        //set grid view on item click
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //The default room can not be changed
                if (position != 0) {
                    //get room item
                    RoomItem clickedRoom = (RoomItem) gridView.getItemAtPosition(position);
                    //update room data
                    updateRoomData(clickedRoom);
                    Intent i = new Intent(getActivity(), RoomChatActivity.class);
                    i.putExtra("room_id", clickedRoom.getRoomId());
                    i.putExtra("room_name", clickedRoom.getRoomName());
                    i.putExtra("owner_id",clickedRoom.getOwnerId());
                    startActivity(i);
                }
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

    private void updateRoomData(RoomItem clickedRoom) {
        //update room joined number and joined user
        int joinedUserNum = Integer.parseInt(clickedRoom.getJoinedUserNum());
        ArrayList<String> joinedUserIDs = clickedRoom.getJoinedUserIDs();
        if (!joinedUserIDs.contains(userId)) {
            String num = Integer.toString(++joinedUserNum);
            joinedUserIDs.add(userId);

            Map<String, Object> room = new HashMap<>();
            room.put("room_id", clickedRoom.getRoomId());
            room.put("owner_id", clickedRoom.getOwnerId());
            room.put("room_name", clickedRoom.getRoomName());
            room.put("joined_num", num);
            room.put("joined_user_list", joinedUserIDs);
            room.put("room_created_time", clickedRoom.getRoomCreatedTime());

            fireStore.collection("rooms").document(clickedRoom.getRoomId()).set(room);
            Toast.makeText(getActivity(),
                    "Update room success!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(),
                    "Update room failed! This user already in the list", Toast.LENGTH_SHORT).show();
        }
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
                        if (checkBoolean) {
                            //add new room to grid view
                            RoomItem roomItem = new RoomItem(userId, inputName);
                            ArrayList<String> joinedUserIDs = new ArrayList<String>();
                            joinedUserIDs.add(userId);
                            roomItem.setOwnerId(userId);
                            roomItem.setJoinedUserNum("1");
                            roomItem.setJoinedUserIDs(joinedUserIDs);
                            rooms.add(roomItem);
                            saveRoomToDB(roomItem);
                            gridAdapter.notifyDataSetChanged();
                            Intent i = new Intent(getActivity(), RoomChatActivity.class);
                            i.putExtra("room_id", roomItem.getRoomId());
                            i.putExtra("room_name", roomItem.getRoomName());
                            i.putExtra("owner_id", roomItem.getOwnerId());
                            startActivity(i);
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

    private boolean inputCheck(String input) {
        if (input.toCharArray().length == 0) {
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
        } else if (input.toCharArray().length > 50) {
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
        } else {
            roomName = input;
            Toast.makeText(getActivity(),
                    "Room Added: " + roomName, Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private void saveRoomToDB(RoomItem roomItem) {
        CollectionReference rooms = fireStore.collection("rooms");
        // get all room info
        Map<String, Object> room = new HashMap<>();
        room.put("room_id", roomItem.getRoomId());
        room.put("owner_id", roomItem.getOwnerId());
        room.put("room_name", roomItem.getRoomName());
        room.put("joined_num", roomItem.getJoinedUserNum());
        room.put("joined_user_list", roomItem.getJoinedUserIDs());
        room.put("room_created_time", roomItem.getRoomCreatedTime());

        rooms.document(roomItem.getRoomId()).set(room);
    }

    private void getRoomFromDB() {
        /*Toast.makeText(getActivity(),
                "Start Get Documents!", Toast.LENGTH_SHORT).show();*/
        CollectionReference collectionRef = fireStore.collection("rooms");
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                    for (DocumentSnapshot document : myListOfDocuments) {
                        Map<String, Object> room = document.getData();
                        String roomId = room.get("room_id").toString();
                        String ownerId = room.get("owner_id").toString();
                        String roomName = room.get("room_name").toString();
                        String joinedUserNum = room.get("joined_num").toString();
                        ArrayList<String> joinedUserIDs
                                = (ArrayList<String>) document.get("joined_user_list");
                        String roomCreatedTime = room.get("room_created_time").toString();
                        RoomItem roomItem = new RoomItem(ownerId, roomName);
                        roomItem.setRoomId(roomId);
                        roomItem.setJoinedUserNum(joinedUserNum);
                        roomItem.setJoinedUserIDs(joinedUserIDs);
                        roomItem.setRoomCreatedTime(roomCreatedTime);
                        rooms.add(roomItem);
                        sortItemsByDate();
                        gridAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(),
                                "Get Documents Success!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(),
                            "Get Documents Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Sort all exist items by date when items' size greater than 1,
     * the latest edit/add item at the first.
     */
    private void sortItemsByDate() {
        if (rooms.size() > 1) {
            Collections.sort(rooms, new Comparator<RoomItem>() {
                public int compare(RoomItem r1, RoomItem r2) {
                    return r2.compareTo(r1);
                }
            });
        }
    }
}