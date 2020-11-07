package au.edu.sydney.comp5216.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<RoomItem> rooms;
    LayoutInflater inflater;

    /**
     * The constructor of grid view adapter class
     *
     * @param context adapter context
     * @param rooms   room list
     */
    public GridAdapter(Context context, ArrayList<RoomItem> rooms) {
        this.context = context;
        this.rooms = rooms;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Get item list size
     *
     * @return int of list size
     */
    @Override
    public int getCount() {
        return rooms.size();
    }

    /**
     * Get item in specified position
     *
     * @param position item's position in list
     * @return item object
     */
    @Override
    public Object getItem(int position) {
        return rooms.get(position);
    }

    /**
     * Get item position
     *
     * @param position item position
     * @return long type of position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Override getView to custom the grid view item in grid view
     *
     * @param position    grid view item position
     * @param convertView grid view
     * @param parent      parent of this
     * @return View of new grid item view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_view_item, null);
        }

        TextView roomName = (TextView) convertView.findViewById(R.id.gv_textView_name);
        TextView roomPeople = (TextView) convertView.findViewById(R.id.gv_textView_num);
        roomName.setText(rooms.get(position).getRoomName());
        roomPeople.setText(rooms.get(position).getJoinedUserNum());

        return convertView;
    }
}