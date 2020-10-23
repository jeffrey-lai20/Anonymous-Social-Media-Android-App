package au.edu.sydney.comp5216.project;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import org.w3c.dom.Text;

import java.util.List;

import au.edu.sydney.comp5216.project.ui.home.HomeFragment;

public class GridAdapter extends BaseAdapter {

    private Context context;
    private String[] rooms;
    private String[] peopleNumOfRooms;
    LayoutInflater inflater;

    /**
     * The constructor of grid view adapter class
     *
     * @param context adapter context
     * @param rooms   room list
     * @param peopleNumOfRooms   people number list of each room
     */
    public GridAdapter(Context context, String[] rooms, String[] peopleNumOfRooms) {
        this.context = context;
        this.rooms = rooms;
        this.peopleNumOfRooms = peopleNumOfRooms;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Get item list size
     *
     * @return int of list size
     */
    @Override
    public int getCount() {
        return rooms.length;
    }

    /**
     * Get item in specified position
     *
     * @param position item's position in list
     * @return item object
     */
    @Override
    public Object getItem(int position) {
        return rooms[position];
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
        roomName.setText(rooms[position]);
        roomPeople.setText(peopleNumOfRooms[position]);
        //Bitmap image = items.get(position).getImage();

        // if (image != null){
        // imageView.setImageBitmap(image);
        //}
        return convertView;
    }
}