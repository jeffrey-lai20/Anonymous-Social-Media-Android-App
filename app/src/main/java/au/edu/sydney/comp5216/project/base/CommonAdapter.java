package au.edu.sydney.comp5216.project.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {
	
	protected List<T> mData;
	protected final int mItemLayoutId;
	
	public CommonAdapter(int itemLayoutId, List<T> datas){
		this.mItemLayoutId = itemLayoutId;
		this.mData = datas ;
	}
	
	public void refreshView(List<T> data)
	{
		this.mData = data;  
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData==null ? 0 :mData.size();
	}

	@Override
	public T getItem(int position) {
		// TODO Auto-generated method stub
		if(mData != null && position< mData.size()){
			return mData.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public abstract void convert(CommonViewHolder holder, T item, int position);

	@Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final CommonViewHolder viewHolder = CommonViewHolder.get(convertView, parent, mItemLayoutId);
        convert(viewHolder, getItem(position),position);  
        return viewHolder.getConvertView();    
    }
	
}
