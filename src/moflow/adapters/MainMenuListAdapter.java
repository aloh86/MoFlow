package moflow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import moflow.activities.R;

public class MainMenuListAdapter extends BaseAdapter
{
	private Context mContext;
	private int [] iconResIDs = { 
			moflow.activities.R.drawable.knight,
			moflow.activities.R.drawable.skull,
			moflow.activities.R.drawable.gazer,
			moflow.activities.R.drawable.init_icon,
            R.drawable.manual
	};

	private String [] icoText = {
			"Create Player Characters", "Create Encounters", "Creature Catalog",
			"Initiative", "Options"
	};
	
	public MainMenuListAdapter(Context c) {
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
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if(v == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(moflow.activities.R.layout.inflate_mainmenu, null);
			
			TextView iconText = (TextView) v.findViewById(moflow.activities.R.id.mainMenuItemText);
			iconText.setText(icoText[position]);
			iconText.setCompoundDrawablesWithIntrinsicBounds(iconResIDs[position], 0, 0, 0);
            iconText.setCompoundDrawablePadding(10);
		}
		return v;
	}
	
}
