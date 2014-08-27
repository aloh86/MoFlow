package moflow.adapters;

import java.util.List;

import moflow.wolfpup.Creature;
import moflow.activities.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class InitiativeAdapter extends ArrayAdapter<Creature> {
	private List<Creature> initList;
	Context mContext;
	LayoutInflater inflater;

	public InitiativeAdapter(Context context, int textViewResourceId, List<Creature> objects) {
		super( context, textViewResourceId, objects );
		mContext = context;
		initList = objects;
		inflater = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	}
	/*
	public View getView( int position, View convertView, ViewGroup parent ) {
		ViewHolder holder = null;
		
		if ( convertView == null ) {
			holder = new ViewHolder();
			
			holder.initItemName = ( TextView )convertView.findViewById( R.id.initItemName );
			holder.initValue = ( TextView ) convertView.findViewById( R.id.initValue );
			holder.initHPCurrent = ( TextView ) convertView.findViewById( R.id.initHPCurrent );
			holder.initHPMax = ( TextView ) convertView.findViewById( R.id.initHPMax );
			holder.initItemAC = ( TextView ) convertView.findViewById( R.id.initItemAC );
			
			convertView.setTag( holder );
		} else {
			holder = ( ViewHolder ) convertView.getTag();
		}
		
		Creature creature = getItem( position );
		holder.initItemName.setText( creature.getCreatureName() );
		holder.initValue.setText( String.valueOf( creature.getInitiative() ) );
		holder.initHPCurrent.setText( String.valueOf( creature.getCurrentHitPoints() ) );
		holder.initHPMax.setText( String.valueOf( creature.getMaxHitPoints() ) );
		holder.initItemAC.setText( String.valueOf( creature.getArmorClass() ) );
		
		if ( creature.isMonster() )
			holder.initItemName.setTextColor( Color.RED );
		else
			holder.initItemName.setTextColor( Color.GREEN );
		
		setCurrentHPTextColor( creature.getCurrentHitPoints(), creature.getMaxHitPoints(), holder.initHPCurrent );
		holder.initHPMax.setTextColor( Color.GREEN );
		
		if ( creature.hasInit() )
			convertView.setBackgroundColor( Color.parseColor( "#694489" ) );
		else
			convertView.setBackgroundColor( Color.BLACK );
			
		return convertView;
	}
	
	private void setCurrentHPTextColor( float curHP, float maxHP, TextView hpTV ) {
		if ( maxHP > 0 ) {
			float percent = ( curHP / maxHP ) * 100;
			int flatScore = (int) percent;
			
			if ( flatScore >= 90 )
				hpTV.setTextColor( Color.GREEN );
			else if ( flatScore >=80 && percent < 90 )
				hpTV.setTextColor( Color.YELLOW );
			else if ( flatScore >= 70 && percent < 80 )
				hpTV.setTextColor( Color.parseColor( "#FFA000" ) ); // orange
			else
				hpTV.setTextColor( Color.parseColor( "#FC1501" ) ); // gummi red
		} else
			hpTV.setTextColor( Color.GREEN );
	}

	private static class ViewHolder {
		public TextView initItemName;
		public TextView initValue;
		public TextView initHPCurrent;
		public TextView initHPMax;
		public TextView initItemAC;
	}
	*/
}
