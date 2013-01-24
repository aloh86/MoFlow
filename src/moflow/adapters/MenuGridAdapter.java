package moflow.adapters;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuGridAdapter extends BaseAdapter {
	private Context mContext;
	private int [] iconResIDs;
	private String [] icoText = {
			"PC Manager", "Encounter Manager", "Creature Catalog",
			"Initiative", "Options", "Manual"
	};
	
	public MenuGridAdapter( Context c ) {
		mContext = c;
	}

	@Override
	public int getCount() {
		return icoText.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		View v = convertView;
		
		if( v == null ) {
			LayoutInflater inflater = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			v = inflater.inflate( moflow.tracker.R.layout.grid_icon, null );
		}
		
		ImageView iconImg = ( ImageView ) v.findViewById( moflow.tracker.R.id.icon_image );
		iconImg.setImageResource( moflow.tracker.R.drawable.icon );
		
		TextView iconText = ( TextView ) v.findViewById( moflow.tracker.R.id.icon_text );
		iconText.setText( icoText[ position ] );
		
		return v;
	}
	
}
