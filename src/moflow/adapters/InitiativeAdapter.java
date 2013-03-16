package moflow.adapters;

import java.util.List;

import moflow.adapters.CatalogListAdapter.ViewHolder;
import moflow.tracker.CatalogItem;
import moflow.tracker.Moflow_Creature;
import moflow.tracker.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class InitiativeAdapter extends ArrayAdapter< Moflow_Creature > {
	private List< Moflow_Creature > initList;
	Context mContext;
	LayoutInflater inflater;
	
	public InitiativeAdapter(Context context, int textViewResourceId, List< Moflow_Creature > objects) {
		super( context, textViewResourceId, objects );
		mContext = context;
		initList = objects;
		inflater = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	}
	
	public View getView( int position, View convertView, ViewGroup parent ) {
		ViewHolder holder = null;
		int type = getItemViewType( position );
		
		if ( convertView == null ) {
			holder = new ViewHolder();
			convertView = inflater.inflate( R.layout.init_item, null );
			
			holder.initItemName = ( TextView )convertView.findViewById( R.id.initItemName );
			holder.initValue = ( TextView ) convertView.findViewById( R.id.initValue );
			holder.initHPCurrent = ( TextView ) convertView.findViewById( R.id.initHPCurrent );
			holder.initHPMax = ( TextView ) convertView.findViewById( R.id.initHPMax );
			holder.initItemAC = ( TextView ) convertView.findViewById( R.id.initItemAC );
			holder.initItemConditions = ( TextView ) convertView.findViewById( R.id.initItemConditions );
			
			convertView.setTag( holder );
		} else {
			holder = ( ViewHolder ) convertView.getTag();
		}
		
		Moflow_Creature creature = getItem( position );
		holder.initItemName.setText( creature.getCharName() );
		holder.initValue.setText( String.valueOf( creature.getInitiative() ) );
		holder.initHPCurrent.setText( String.valueOf( creature.getCurrentHP() ) );
		holder.initHPMax.setText( String.valueOf( creature.getMaxHitPoints() ) );
		holder.initItemAC.setText( String.valueOf( creature.getAC() ) );
		//holder.initItemConditions.setText( creature.getConditionString() );
		
		if ( creature.isCreature() )
			convertView.setBackgroundColor( Color.RED );
		else
			convertView.setBackgroundColor( Color.GREEN );
		
		return convertView;
	}

	public static class ViewHolder {
		public TextView initItemName;
		public TextView initValue;
		public TextView initHPCurrent;
		public TextView initHPMax;
		public TextView initItemAC;
		public TextView initItemConditions;
	}
}
