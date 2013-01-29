package moflow.tracker;

import java.util.ArrayList;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
/*
===============================================================================
PCM_PartyList.java
Alex Oh

Activity shows the current list of parties.
===============================================================================
*/
public class PCM_PartyList extends ListActivity implements OnClickListener
{
	Button addPartyBtn;
	Button editPartyBtn;
	Button deletePartyBtn;
	
	final int REQC_EDITPARTY = 1; // request code for Edit Party activity
	final int RC_EDITPARTYDONE = 1; // if user clicks Done btn in Edit Party
	final int RC_EDITEXISTING = 2;
	
	Moflow_Party party;
	
	ArrayList<Moflow_Party> partyList;
	ArrayAdapter<Moflow_Party> adapter;
	
	int checkedItemPosition = 0;
	
	
	/**-----------------------------------------------------------------------
	 * 
	 */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.grouplist );
		
		party = null;
		
		addPartyBtn = ( Button ) findViewById( R.id.leftBtn );
		addPartyBtn.setText( "Add" );
		addPartyBtn.setOnClickListener( this );
		
		editPartyBtn = ( Button ) findViewById( R.id.middleBtn );
		editPartyBtn.setText( "Edit" );
		editPartyBtn.setOnClickListener( this );

		deletePartyBtn = ( Button ) findViewById( R.id.rightBtn );
		deletePartyBtn.setText( "Delete" );
		deletePartyBtn.setOnClickListener( this );
		
		// setup list
		partyList = new ArrayList<Moflow_Party>();
		adapter = new ArrayAdapter<Moflow_Party>( this, R.layout.list_item, 
				partyList );
		setListAdapter( adapter );
		getListView().setChoiceMode( ListView.CHOICE_MODE_SINGLE );
	}

	
	/**-----------------------------------------------------------------------
	 * 
	 */
	@Override
	public void onClick(View v) 
	{
		if ( v == addPartyBtn )
			startActivityForResult( 
					new Intent( "moflow.tracker.PCM_EditParty" ), 
					REQC_EDITPARTY );
		
		if ( v == editPartyBtn && party != null ) 
		{
			Intent i = new Intent( "moflow.tracker.PCM_EditParty" );
			Bundle extras = new Bundle();
			
			extras.putParcelable( "party", party );
			i.putExtras( extras );
			startActivityForResult( i, REQC_EDITPARTY );
		}
		
		if ( v == deletePartyBtn && party != null ) 
		{
			adapter.remove( adapter.getItem( checkedItemPosition ) );
			adapter.notifyDataSetChanged();
			party = null;
		}
	}
	
	/**-----------------------------------------------------------------------
	 * Shows check button when character in list is clicked.
	 */
	@Override
	public void onListItemClick( ListView parent, View v, int position, long id)
	{
		parent.setItemChecked(position, parent.isItemChecked(position));
		party = adapter.getItem( position );
		checkedItemPosition = position;
	}
	
	
	/**-----------------------------------------------------------------------
	 * 
	 */
	public void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		if ( requestCode == REQC_EDITPARTY )
		{
			if ( resultCode == RC_EDITPARTYDONE )
			{
				Bundle bundle = data.getExtras();
				party = bundle.getParcelable( "partyData" );
				adapter.add( party );
				adapter.notifyDataSetChanged();
			}
			
			if ( resultCode == RC_EDITEXISTING )
			{
				Bundle bundle = data.getExtras();
				party = bundle.getParcelable( "partyData" );
				adapter.remove( 
						adapter.getItem( checkedItemPosition ) );
				adapter.add( party );
				adapter.notifyDataSetChanged();
			}
			
			// party set to null to force user to click on item
			// so that check mark appears. Else, they can just click on
			// edit or delete without knowing which party is being
			// edited or deleted.
			party = null;
			
			getListView().setItemChecked( checkedItemPosition, false );
		}
	}
}
