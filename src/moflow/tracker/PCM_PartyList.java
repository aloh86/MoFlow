package moflow.tracker;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import moflow.database.MoFlowDB;
import moflow.runnables.PartyListRunnable;
/*
===============================================================================
PCM_PartyList.java

Activity shows the current list of parties.
===============================================================================
*/
public class PCM_PartyList extends ListActivity 
implements OnClickListener, OnItemLongClickListener, android.content.DialogInterface.OnClickListener
{
	private Button addPartyBtn;
	
	private final int REQC_EDITPARTY = 1; // request code for Edit Party activity
	private final int RC_EDITPARTYDONE = 1; // if user clicks Done btn in Edit Party
	private final int RC_EDITEXISTING = 2;
	
	private Moflow_Party party;
	
	private MoFlowDB database;
	
	private AlertDialog deletePCDialog;
	
	private ArrayList<Moflow_Party> partyList;
	private ArrayAdapter<Moflow_Party> adapter;
	
	private int checkedItemPosition = 0;
	
	
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
		
		addPartyBtn = ( Button ) findViewById( R.id.addGroupButton );
		addPartyBtn.setText( "Create New Party" );
		addPartyBtn.setOnClickListener( this );
		
		// setup list
		partyList = new ArrayList<Moflow_Party>();
		adapter = new ArrayAdapter<Moflow_Party>( this, R.layout.list_item, 
				partyList );
		setListAdapter( adapter );
		getListView().setChoiceMode( ListView.CHOICE_MODE_SINGLE );
		
		this.getListView().setOnItemLongClickListener( this );
		
		initList();
	}

	
	/**-----------------------------------------------------------------------
	 * 
	 */
	@Override
	public void onClick(View v) 
	{
		if ( v == addPartyBtn )
			startActivityForResult( 
					new Intent( "moflow.tracker.PCM_EditPartyActivity" ), 
					REQC_EDITPARTY );
	}
	
	/**-----------------------------------------------------------------------
	 * Buttons for dialogs. For this activity, there is only a delete dialog.
	 */
	@Override
	public void onClick(DialogInterface dialog, int which ) {
		if ( which == DialogInterface.BUTTON_POSITIVE ) {
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
		party = adapter.getItem( position );
		checkedItemPosition = position;
		
		gatherParty();
		
		Intent i = new Intent( "moflow.tracker.PCM_EditParty" );
		Bundle extras = new Bundle();
		
		extras.putParcelable( "party", party );
		i.putExtras( extras );
		startActivityForResult( i, REQC_EDITPARTY );
	}

	/**-----------------------------------------------------------------------
	 * Prompts the user if they want to delete the party in response to a
	 * long-click.
	 */
	@Override
	public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ) {
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		
		// setup the dialog for PC deletion
		builder = new AlertDialog.Builder( this );
		builder.setMessage( "Delete this Party?" );
		builder.setPositiveButton( "Yes", this );
		builder.setNegativeButton( "No", this );
		deletePCDialog = builder.create();
		deletePCDialog.show();
		return false;
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
				savePartyToDB( party );
				party.clearMembers();
				adapter.add( party );
				adapter.notifyDataSetChanged();
			}
			
			if ( resultCode == RC_EDITEXISTING )
			{
				Bundle bundle = data.getExtras();
				party = bundle.getParcelable( "partyData" );
				adapter.remove( 
						adapter.getItem( checkedItemPosition ) );
				savePartyToDB( party );
				party.clearMembers();
				adapter.add( party );
				adapter.notifyDataSetChanged();
			}
			
			party.clearMembers();
			party = null;
		}
	}
	
	/**-----------------------------------------------------------------------
	 * 
	 */
	private void savePartyToDB( Moflow_Party party ) {		
		try {
			database = new MoFlowDB( this );
			database.open();
		} catch ( SQLException e ) {
			Toast.makeText( this, "Database could not be opened!", Toast.LENGTH_LONG ).show();
		}
		
		database.insertGroup( party.getPartyName() );
		
		for ( int i = 0; i < party.getPartySize(); i++ ) {
			Moflow_PC pc = party.getMember( i );
			database.insertPlayer( 
					party.getPartyName(), 
					pc.getCharName(), 
					pc.getInitMod(), 
					pc.getAC(),
					pc.getMaxHitPoints() );
		}
		
		database.close();
	}
	
	/**-----------------------------------------------------------------------
	 * 
	 */
	private void gatherParty() {
		Cursor cur;
		
		try {
			database = new MoFlowDB( this );
			database.open();
		} catch ( SQLException e ) {
			Toast.makeText( this, "Database could not be opened!", Toast.LENGTH_LONG ).show();
		}
		
		cur = database.getPCForGroup( party.getPartyName() );
		
		// if the database is empty, just return
		if ( cur.getCount() == 0 ) {
			cur.close();
			database.close();
			return;
		}
		
		// else, load the PCs and insert into party
		Moflow_PC pc = new Moflow_PC();
		while ( cur.moveToNext() ) {
			for ( int i = 1; i < cur.getColumnCount(); i++ ) {
				if ( i == 1 ) {
					String test = cur.getString( i );
					pc.setName( test );
				}
				else if ( i == 2 )
					pc.setInitMod( cur.getInt( i ) );
				else if ( i == 3 )
					pc.setArmorClass( cur.getInt( i ) );
				else if ( i == 4 )
					pc.setHitPoints( cur.getInt( i ) );
			}
			party.addMember( pc );
			pc = new Moflow_PC();
		}
		
		cur.close();
		database.close();
	}
	
	/**-----------------------------------------------------------------------
	 * 
	 */
	private void initList() {
		Cursor cur;
		
		try {
			database = new MoFlowDB( this );
			database.open();
		} catch ( SQLException e ) {
			Toast.makeText( this, "Database could not be opened!", Toast.LENGTH_LONG ).show();
		}
		
		cur = database.getAllParties();
		
		// if the database is empty, just return
		if ( cur.getCount() == 0 ) {
			cur.close();
			database.close();
			return;
		}
		
		// else, load the party names
		Moflow_Party party = null;
		int x = cur.getColumnCount();
		while( cur.moveToNext() ) {
			for ( int i = 0; i < cur.getColumnCount(); i++ ) {
				String colValue = cur.getString( i );
				party = new Moflow_Party();
				party.setPartyName( colValue );
			}
			adapter.add( party );
		}
		
		adapter.notifyDataSetChanged();
		cur.close();
		database.close();
	}
}
