package au.edu.sydney.comp5216.project.RoomChat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class RoomMessage {

    private String message;
    private String userId;
    private String key;
    private String roomId;
    private String messageCreatedTime;

    public RoomMessage(){}

    public RoomMessage(String message, String userId, String roomId) {
        this.message = message;
        this.userId = userId;
        this.roomId = roomId;
        this.messageCreatedTime = getCurrentDateAndTimeString();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRoomId() { return roomId; }

    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getMessageCreatedTime() { return messageCreatedTime; }

    public void setMessageCreatedTime(String messageCreatedTime) { this.messageCreatedTime = messageCreatedTime; }

    @Override
    public String toString() {
        return "RoomMessage{" +
                "message='" + message + '\'' +
                ", userId='" + userId + '\'' +
                ", key='" + key + '\'' +
                ", roomId='" + roomId + '\'' +
                ", messageCreatedTime='" + messageCreatedTime + '\'' +
                '}';
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
