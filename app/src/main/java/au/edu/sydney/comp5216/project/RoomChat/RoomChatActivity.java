package au.edu.sydney.comp5216.project.RoomChat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import au.edu.sydney.comp5216.project.MainActivity;
import au.edu.sydney.comp5216.project.R;
import au.edu.sydney.comp5216.project.RoomItem;

public class RoomChatActivity extends AppCompatActivity implements View.OnClickListener {

    private String userId;
    private String room_id;
    private String room_name;
    private String owner_id;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference messagedb;
    private DatabaseReference roomdb;

    RoomMsgAdapter messageAdapter;
    List<RoomMessage> messages;

    RecyclerView rvMessage;
    EditText etMessage;
    Button imageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_chat);

        init();

        rvMessage = (RecyclerView) findViewById(R.id.rv_massge);
        etMessage = (EditText) findViewById(R.id.et_room_message);
        imageButton = (Button) findViewById(R.id.btn_room_send);
        imageButton.setOnClickListener(this);

        messages = new ArrayList<>();
        rvMessage.setLayoutManager(new LinearLayoutManager(RoomChatActivity.this));
        messageAdapter = new RoomMsgAdapter(RoomChatActivity.this, messages, messagedb);
        rvMessage.setAdapter(messageAdapter);

        //Custom back button function
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                updateRoomData(room_id);
                finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * initialize all needed parameter
     */
    private void init() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        firebaseUser = auth.getCurrentUser();

        room_id = getIntent().getStringExtra("room_id");
        room_name = getIntent().getStringExtra("room_name");
        owner_id = getIntent().getStringExtra("owner_id");
        setTitle(room_name);
    }

    /**
     * set up on click method for user send message
     * @param v current room chat view
     */
    @Override
    public void onClick(View v) {
        if (!TextUtils.isEmpty(etMessage.getText().toString())) {
            RoomMessage message = new RoomMessage(etMessage.getText().toString(), userId, room_id);
            etMessage.setText("");
            messagedb = database.getReference("room_messages").child(room_id);
            messagedb.push().setValue(message);
            Log.d("MESSAGE", "Message sent!");
        } else {
            Toast.makeText(getApplicationContext(), "You cannot send empty message!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * set up room chat menu
     * @param menu room chat activity menu
     * @return boolean type of menu inflate result
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.room_chat_menu, menu);
        return true;
    }

    /**
     * handle three menus: change room name, delte room and quit room
     * @param item menu item
     * @return boolean type of menu event result
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_quit) {
            updateRoomData(room_id);
            finish();
        } else if (item.getItemId() == R.id.menu_delete) {
            deleteRoom();
        } else if (item.getItemId() == R.id.menu_change_room_name) {
            changeRoomName();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Update room data when user quite current room
     * @param currentRoomID String type of current room id
     */
    private void updateRoomData(final String currentRoomID) {
        Log.d("ROOM CHAT UPDATING", currentRoomID);
        roomdb = database.getReference("rooms");
        roomdb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    RoomItem r = (RoomItem) s.getValue(RoomItem.class);
                    //check room ID
                    if (r.getRoomId().equals(currentRoomID)) {
                        String numBefore = r.getJoinedUserNum();
                        ArrayList<String> joinedUserIDs = r.getJoinedUserIDs();
                        //to update in realtime database
                        int joinedUserNum = Integer.parseInt(numBefore);
                        if (joinedUserIDs != null && joinedUserIDs.contains(userId)) {
                            String numAfter = Integer.toString(--joinedUserNum);
                            joinedUserIDs.remove(userId);
                            roomdb.child(s.getKey()).child("joinedUserNum").setValue(numAfter);
                            roomdb.child(s.getKey()).child("joinedUserIDs").setValue(joinedUserIDs);
                            Log.d("UPDATE", "Update Success!");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Handle room deletion when user click delete menu
     */
    private void deleteRoom() {
        //check authority of deletion
        if (owner_id.equals(userId)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RoomChatActivity.this);
            builder.setTitle("Delete Room!")
                    .setMessage("Are you sure you want to delete this room?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //delete room messages
                            messageDelete();
                            //delete room
                            roomDelete();
                            startActivity(new Intent(RoomChatActivity.this, MainActivity.class));
                            finish();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    //nothing
                }
            });
            builder.create().show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(RoomChatActivity.this);
            builder.setTitle("Delete Failed!")
                    .setMessage("You do not have authority! Only the owner can delete room.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //nothing
                        }
                    });
            builder.create().show();
        }
    }

    /**
     * Handle change room name when user click change name menu
     */
    private void changeRoomName() {
        //check authority of changing
        if (owner_id.equals(userId)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RoomChatActivity.this);
            //set up custom room name input dialog
            View dialogV = getLayoutInflater().inflate(R.layout.dialog_room_create, null);
            //set up the input
            final EditText roomNameInput = (EditText) dialogV.findViewById(R.id.et_room_name);
            roomNameInput.setText(getTitle());

            Button btn_cancel = (Button) dialogV.findViewById(R.id.btn_cancel);
            Button btn_ok = (Button) dialogV.findViewById(R.id.btn_ok);
            builder.setView(dialogV);

            final AlertDialog alertDialog = builder.create();
            //set up negative button
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (alertDialog.isShowing())
                        alertDialog.dismiss();
                }
            });
            //set up positive button, confirm room name change
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String inputName = roomNameInput.getText().toString();
                    boolean checkBoolean = inputCheck(inputName);
                    if (checkBoolean) {
                        //update realtime database
                        roomdb = database.getReference("rooms");
                        roomdb.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot s : snapshot.getChildren()) {
                                    RoomItem r = (RoomItem) s.getValue(RoomItem.class);
                                    //check room ID
                                    if (r.getRoomId().equals(room_id)) {
                                        String name = inputName;
                                        //to update in realtime database
                                        roomdb.child(s.getKey()).child("roomName").setValue(name);
                                        room_name = inputName;
                                        setTitle(inputName);
                                        alertDialog.dismiss();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }
            });
            alertDialog.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(RoomChatActivity.this);
            builder.setTitle("Change Failed!")
                    .setMessage("You do not have authority! Only the owner can change name.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //nothing
                        }
                    });
            builder.create().show();
        }
    }

    /**
     * Check the room name input whether is valid
     * @param input room name input by user
     * @return boolean room name input check result
     */
    private boolean inputCheck(String input) {
        if (input.toCharArray().length == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RoomChatActivity.this);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(RoomChatActivity.this);
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
            Log.d("ROOM NAME CHANGE", "Room Name Changed Success!");
            return true;
        }
    }

    /**
     * Handle room messages deletion when user delete room
     */
    private void messageDelete() {
        messagedb = database.getReference("room_messages");
        messagedb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().child(room_id).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DELETE MESSAGES", "onCancelled", error.toException());
            }
        });
    }

    /**
     * Handle room detele when user confirm delelte room
     */
    private void roomDelete() {
        //delete database
        roomdb = database.getReference("rooms");
        roomdb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    RoomItem r = (RoomItem) s.getValue(RoomItem.class);
                    //check room ID
                    if (r.getRoomId().equals(room_id)) {
                        roomdb.child(s.getKey()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        userId = firebaseUser.getDisplayName();
        AllMethods.userid = userId;

        messagedb = database.getReference("room_messages").child(room_id);
        messagedb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                RoomMessage message = snapshot.getValue(RoomMessage.class);
                message.setKey(snapshot.getKey());

                messages.add(message);
                displayMessages(messages);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                RoomMessage message = snapshot.getValue(RoomMessage.class);
                message.setKey(snapshot.getKey());

                List<RoomMessage> newMessages = new ArrayList<RoomMessage>();

                for (RoomMessage m : messages) {
                    if (m.getKey().equals(message.getKey())) {
                        newMessages.add(message);
                    } else {
                        newMessages.add(m);
                    }
                }

                messages = newMessages;
                displayMessages(messages);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                RoomMessage message = snapshot.getValue(RoomMessage.class);
                message.setKey(snapshot.getKey());

                List<RoomMessage> newMessages = new ArrayList<RoomMessage>();

                for (RoomMessage m : messages) {
                    if (!m.getKey().equals(message.getKey())) {
                        newMessages.add(m);
                    }
                }

                messages = newMessages;
                displayMessages(messages);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        messages = new ArrayList<>();
    }


    private void displayMessages(List<RoomMessage> messages) {
        rvMessage.setLayoutManager(new LinearLayoutManager(RoomChatActivity.this));
        messageAdapter = new RoomMsgAdapter(RoomChatActivity.this, messages, messagedb);
        rvMessage.setAdapter(messageAdapter);
    }

    /**
     * convert date and time string in item to date type
     *
     * @param cuTime date and time string in format "yyyy-MM-dd HH:mm:ss"
     * @return return date type which corresponds to it's string
     */
    private Date convertStringToDate(String cuTime) {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = format.parse(cuTime);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * Get current date and time when item be edited or added
     *
     * @return current date and time in format "yyyy-MM-dd HH:mm:ss"
     */
    private String getCurrentDateAndTimeString() {
        //get current data and time string
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
        String currentTime = sdf.format(currentDate);
        return currentTime;
    }
}