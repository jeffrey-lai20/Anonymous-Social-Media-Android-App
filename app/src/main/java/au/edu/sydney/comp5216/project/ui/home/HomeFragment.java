package au.edu.sydney.comp5216.project.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import au.edu.sydney.comp5216.project.GridAdapter;
import au.edu.sydney.comp5216.project.R;
import au.edu.sydney.comp5216.project.RoomChat.AllMethods;
import au.edu.sydney.comp5216.project.RoomChat.RoomChatActivity;
import au.edu.sydney.comp5216.project.RoomChat.RoomMessage;
import au.edu.sydney.comp5216.project.RoomChat.RoomMsgAdapter;
import au.edu.sydney.comp5216.project.RoomItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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


import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


public class HomeFragment extends Fragment implements View.OnClickListener {

    private GridView gridView;
    private GridAdapter gridAdapter;
    static private ArrayList<RoomItem> rooms = null;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore fireStore;
    FirebaseDatabase database;
    DatabaseReference roomdb;

    private String userId;
    private String roomName = "";

    private ImageButton addNewRoomButton;

    private SearchView searchView;
    private CharSequence query;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        fireStore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();

        rooms = new ArrayList<RoomItem>();

        userId = firebaseUser.getDisplayName();

        rooms.add(getDedaultRoom());

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
                    Toast.makeText(getActivity(),
                            clickedRoom.getRoomId(), Toast.LENGTH_SHORT).show();
                    updateRoomData(clickedRoom.getRoomId());
                    updateFireStore(clickedRoom);
                    Intent i = new Intent(getActivity(), RoomChatActivity.class);
                    i.putExtra("room_id", clickedRoom.getRoomId());
                    i.putExtra("room_name", clickedRoom.getRoomName());
                    i.putExtra("owner_id", clickedRoom.getOwnerId());
                    startActivity(i);
                }
            }
        });

        //set up click listener for add new room button
        addNewRoomButton = (ImageButton) root.findViewById(R.id.btn_add_new_room);
        addNewRoomButton.setOnClickListener(this);

        searchView = (SearchView) root.findViewById(R.id.searchView); // inititate a search view
        query = searchView.getQuery(); // get the query string currently in the text field
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                    }
                }, 600); // 600ms delay before the timer executes the „run“ method from TimerTask
                getRoomFromDB(newText);
                return true;
            }
        });

        return root;
    }

    private RoomItem getDedaultRoom() {
        RoomItem defaultRoom = new RoomItem(userId, "Welcome to 0204!");
        defaultRoom.setRoomCreatedTime("2020-1-1 00:00:01");
        defaultRoom.setKey("default key");

        return defaultRoom;
    }

    @Override
    public void onStart() {
        super.onStart();

        roomdb = database.getReference("rooms");
        roomdb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                RoomItem room = (RoomItem) snapshot.getValue(RoomItem.class);
                room.setKey(snapshot.getKey());

                if (!rooms.contains(room)) {
                    rooms.add(room);
                }

                removeDuplicates(rooms);
                displayRooms(rooms);

                Toast.makeText(getActivity(),
                        "Add Success!" + room.getRoomId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                RoomItem room = (RoomItem) snapshot.getValue(RoomItem.class);
                room.setKey(snapshot.getKey());

                ArrayList<RoomItem> newRooms = new ArrayList<RoomItem>();

                for (RoomItem r : rooms) {
                    if (r.getKey().equals(room.getKey())) {
                        newRooms.add(room);
                    } else {
                        newRooms.add(r);
                    }
                }

                rooms = newRooms;
                removeDuplicates(rooms);
                displayRooms(rooms);

                Toast.makeText(getActivity(),
                        "Change Success!" + room.getRoomId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                RoomItem room = (RoomItem) snapshot.getValue(RoomItem.class);
                room.setKey(snapshot.getKey());

                ArrayList<RoomItem> newRooms = new ArrayList<RoomItem>();

                for (RoomItem r : rooms) {
                    if (!r.getKey().equals(room.getKey())) {
                        newRooms.add(r);
                    }
                }

                rooms = newRooms;
                removeDuplicates(rooms);
                displayRooms(rooms);

                Toast.makeText(getActivity(),
                        "Remove Success!" + room.getRoomId(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayRooms(ArrayList<RoomItem> rooms) {
        if (getActivity()!=null){
            gridAdapter = new GridAdapter(getActivity(), rooms);
            gridView.setAdapter(gridAdapter);
        }
    }

    private void updateRoomData(final String clickedRoomID) {
        Toast.makeText(getActivity(),
                "Updating!" + clickedRoomID, Toast.LENGTH_SHORT).show();
        roomdb = database.getReference("rooms");
        roomdb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    RoomItem r = (RoomItem) s.getValue(RoomItem.class);
                    //check room ID
                    if (r.getRoomId().equals(clickedRoomID)) {
                        String numBefore = r.getJoinedUserNum();
                        ArrayList<String> joinedUserIDs = r.getJoinedUserIDs();
                        //to update in realtime database
                        int joinedUserNum = Integer.parseInt(numBefore);
                        if (joinedUserIDs == null) {
                            String numAfter = Integer.toString(++joinedUserNum);
                            ArrayList<String> newJoinedUserIDs = new ArrayList<String>();
                            newJoinedUserIDs.add(userId);
                            roomdb.child(s.getKey()).child("joinedUserNum").setValue(numAfter);
                            roomdb.child(s.getKey()).child("joinedUserIDs").setValue(newJoinedUserIDs);

                            Toast.makeText(getActivity(),
                                    "Update room success!", Toast.LENGTH_SHORT).show();
                        } else if (joinedUserIDs != null && !joinedUserIDs.contains(userId)) {
                            String numAfter = Integer.toString(++joinedUserNum);
                            joinedUserIDs.add(userId);
                            roomdb.child(s.getKey()).child("joinedUserNum").setValue(numAfter);
                            roomdb.child(s.getKey()).child("joinedUserIDs").setValue(joinedUserIDs);
                            Toast.makeText(getActivity(),
                                    "Update room success!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateFireStore(RoomItem clickedRoom) {
        //update room joined number and joined user
        int joinedUserNum = Integer.parseInt(clickedRoom.getJoinedUserNum());
        ArrayList<String> joinedUserIDs = clickedRoom.getJoinedUserIDs();
        if (joinedUserIDs == null) {
            String num = Integer.toString(++joinedUserNum);
            ArrayList<String> newjoinedUserIDs = new ArrayList<String>();
            newjoinedUserIDs.add(userId);

            Map<String, Object> room = new HashMap<>();
            room.put("room_id", clickedRoom.getRoomId());
            room.put("owner_id", clickedRoom.getOwnerId());
            room.put("room_name", clickedRoom.getRoomName());
            room.put("joined_num", num);
            room.put("joined_user_list", newjoinedUserIDs);
            room.put("room_created_time", clickedRoom.getRoomCreatedTime());

            fireStore.collection("rooms").document(clickedRoom.getRoomId()).set(room);
            Toast.makeText(getActivity(),
                    "Update room success!", Toast.LENGTH_SHORT).show();

        } else if (!joinedUserIDs.contains(userId) && joinedUserIDs != null) {
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

                View dialogV = getLayoutInflater().inflate(R.layout.dialog_room_create, null);
                //set up the input
                final EditText roomNameInput = (EditText) dialogV.findViewById(R.id.et_room_name);

                Button btn_cancel = (Button) dialogV.findViewById(R.id.btn_cancel);
                Button btn_ok = (Button) dialogV.findViewById(R.id.btn_ok);
                builder.setView(dialogV);

                final AlertDialog alertDialog = builder.create();

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (alertDialog.isShowing())
                            alertDialog.dismiss();
                    }
                });

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                            saveRoomToDB(roomItem);
                            Intent i = new Intent(getActivity(), RoomChatActivity.class);
                            i.putExtra("room_id", roomItem.getRoomId());
                            i.putExtra("room_name", roomItem.getRoomName());
                            i.putExtra("owner_id", roomItem.getOwnerId());
                            startActivity(i);
                            gridAdapter.notifyDataSetChanged();
                            alertDialog.dismiss();
                        }
                    }
                });
                alertDialog.show();
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
        } else if (input.toCharArray().length > 38) {
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
        //save to realtime db
        roomdb = database.getReference("rooms");
        roomdb.push().setValue(roomItem);

        //save to firestore
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
        rooms.clear();
        rooms.add(getDedaultRoom());
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
                    }
                    removeDuplicates(rooms);
                    sortItemsByDate();
                    gridAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(),
                            "Get Documents Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void removeDuplicates(ArrayList<RoomItem> list) {
        // Create a new ArrayList
        ArrayList<RoomItem> newList = new ArrayList<RoomItem>();
        ArrayList<String> idList = new ArrayList<String>();

        // Traverse through the first list
        for (RoomItem e : list) {
            // If this element is not present in newList
            // then add it
            if (!idList.contains(e.getRoomId())) {
                newList.add(e);
                idList.add(e.getRoomId());
            }
        }
        // return the new list
        rooms = newList;
    }


    private void getRoomFromDB(final String query) {
        if (query.equals("")) {
            rooms.clear();
            RoomItem defaultRoom = new RoomItem(userId, "Welcome to 0204!");
            defaultRoom.setRoomCreatedTime("2020-1-1 00:00:01");
            rooms.add(defaultRoom);
            getRoomFromDB();
            return;
        }
        rooms.clear();

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
                        if (roomName.contains(query)) {
                            rooms.add(roomItem);
                        }
                        sortItemsByDate();
                        removeDuplicates(rooms);
                        gridAdapter.notifyDataSetChanged();
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
