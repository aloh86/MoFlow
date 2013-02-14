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
		
		initDatabase();
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
		parent.setItemChecked(position, parent.isItemChecked(position));
		party = adapter.getItem( position );
		checkedItemPosition = position;
		
		gatherParty();
		
		Intent i = new Intent( "moflow.tracker.PCM_EditParty" );
		Bundle extras = new Bundle();
		
		extras.putParcelable( "party", party );
		i.putExtras( extras );
		startActivityForResult( i, REQC_EDITPARTY );
	}

	/**
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
	
	private void gatherParty() {
		Cursor cur;
		
		try {
			database = new MoFlowDB( this );
			database.open();
		} catch ( SQLException e ) {
		}
		
		cur = database.getPCForGroup( party.getPartyName() );
		
		// if the database is empty, just return
		if ( cur.getCount() == 0 ) {
			cur.close();
			database.close();
			return;
		}
		
		// else, load the PCs and insert into party
		if ( cur.moveToFirst() ) {
			for ( int i = 1; i < cur.getColumnCount(); i++ ) {
				Moflow_PC pc = new Moflow_PC();
				if ( i == 1 )
					pc.setName( cur.getString( i ) );
				else if ( i == 2 )
					pc.setInitMod( cur.getInt( i ) );
				else if ( i == 3 )
					pc.setArmorClass( cur.getInt( i ) );
				else if ( i == 4 )
					pc.setHitPoints( cur.getInt( i ) );
			}
			cur.moveToNext();
		}
		adapter.notifyDataSetChanged();
		cur.close();
		database.close();
	}
	
	/**-----------------------------------------------------------------------
	 * 
	 */
	private void initDatabase() {
		PartyListRunnable runnable = new PartyListRunnable( 
				this.getBaseContext(),
				database,
				adapter );
		Thread t = new Thread( runnable );
		t.start();
	}
}
