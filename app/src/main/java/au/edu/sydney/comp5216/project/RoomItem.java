package au.edu.sydney.comp5216.project;

import java.util.ArrayList;
import java.util.UUID;

public class RoomItem {
    private String roomId;
    private String ownerId;
    private String roomName;
    private String joinedUserNum;
    private ArrayList<String> joinedUserIDs;

    public RoomItem(String ownerId, String roomName, String joinedUserNum) {
        this.roomId = UUID.randomUUID().toString();
        this.ownerId = ownerId;
        this.roomName = roomName;
        this.joinedUserNum = joinedUserNum;
        this.joinedUserIDs = new ArrayList<String>();
        joinedUserIDs.add(ownerId);
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getJoinedUserNum() {
        return joinedUserNum;
    }

    public void setJoinedUserNum(String joinedUserNum) {
        this.joinedUserNum = joinedUserNum;
    }

    public ArrayList<String> getJoinedUserIDs() {
        return joinedUserIDs;
    }

    public void setJoinedUserIDs(ArrayList<String> joinedUserIDs) {
        this.joinedUserIDs = joinedUserIDs;
    }

}
