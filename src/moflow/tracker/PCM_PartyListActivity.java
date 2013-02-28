package moflow.tracker;

import java.util.ArrayList;

import moflow.database.MoFlowDB;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
/*
===============================================================================
PCM_PartyListActivity.java

Activity for listing the existing parties in the Parties table in the database.
The user can create a new party and edit or delete an existing party. When
the user 'taps' on a party, PCM_EditPartyActivity is called, where the party
members can be added, edited, or removed.
===============================================================================
*/
public class PCM_PartyListActivity extends ListActivity 
implements OnClickListener, android.view.View.OnClickListener, OnItemLongClickListener
{
	private Button newPartyButton;
	
	private EditText groupNameField;
	private EditText renameField;
	
	private AlertDialog newGroupDialog;
	private AlertDialog renameDialog;
	private AlertDialog deleteDialog;
	
	private View groupNameView;
	private View renameView;
	
	private ArrayList< String > partyList;
	
	private ArrayAdapter< String > adapter;
	
	private final String RETAIN_LIST_KEY = "listKey";
	private final String BUNDLE_KEY = "party";
	
	private final int REQC_EDITPARTY = 1;
	
	private int selectedItemPosition;
	
	private MoFlowDB database;
	
	//////////////////////////////////////////////////////////////////////////
	// ACTIVITY OVERRIDES
	//////////////////////////////////////////////////////////////////////////
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.grouplist );
		
		initializeLayout();
		initializeDialogs();
		
		database = new MoFlowDB( this );
		
		try 
		{
			database.open();
		} catch ( SQLException e ) {
			Toast.makeText( this, "Error: database could not be opened", Toast.LENGTH_LONG ).show();
		}
		
		getPartiesFromDB();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		database.close();
	}
	
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo )
	{
		super.onCreateContextMenu( menu, v, menuInfo );
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.rename_del_prompt, menu );
	}
	
	/**-----------------------------------------------------------------------
	 * Save party for orientation changes
	 */
	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		outState.putStringArrayList( RETAIN_LIST_KEY, partyList );
	}
	
	/**-----------------------------------------------------------------------
	 * Reload party on orientation change
	 */
	@Override
	public void onRestoreInstanceState( Bundle savedInstanceState ) {
		super.onRestoreInstanceState( savedInstanceState );
		partyList = savedInstanceState.getStringArrayList( RETAIN_LIST_KEY );
		adapter = new ArrayAdapter< String >( this, R.layout.list_item, partyList );
		this.setListAdapter( adapter );
		adapter.notifyDataSetChanged();
	}
	
	//////////////////////////////////////////////////////////////////////////
	// LISTENERS
	//////////////////////////////////////////////////////////////////////////
	@Override
	public void onClick( View v ) 
	{
		if ( v == newPartyButton ) {
			newGroupDialog.show();
		}
	}
	
	@Override
	public void onClick( DialogInterface dialog, int button ) 
	{
		if ( dialog == newGroupDialog )
			handleNewGroupChoices( button );
		else if ( dialog == renameDialog )
			handleGroupRename( button );
		else if ( dialog == deleteDialog )
			handleGroupDelete( button );
	}
	
	@Override
	public void onListItemClick( ListView parent, View v, int position, long id) 
	{		
		// set the party name
		Moflow_Party party = new Moflow_Party();
		party.setPartyName( partyList.get( position ) );
		
		// open the database and load the party
		loadPartyMembersFromDB( party );
		
		// bundle the activity
		Intent i = new Intent( "moflow.tracker.PCM_EditPartyActivity" );
		Bundle extras = new Bundle();
		extras.putParcelable( BUNDLE_KEY, party );
		i.putExtras( extras );
		
		// start the activity
		startActivityForResult( i, REQC_EDITPARTY );
	}
	
	@Override
	public boolean onContextItemSelected( MenuItem item )
	{
		switch ( item.getItemId() ) {
			case R.id.cmenuRename:
				renameDialog.show();
				break;
			case R.id.cmenuDelete_rename:
				deleteDialog.show();
				break;
			default:
				return super.onContextItemSelected( item );
		}
		return false;
	}
	
	@Override
	public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ) {
		selectedItemPosition = position;
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////
	// VIEW HELPERS : Helpers for buttons and other GUI elements
	//////////////////////////////////////////////////////////////////////////
	/**
	 * Handle OK and CANCEL choices for the new party dialog.
	 */
	private void handleNewGroupChoices( int button )
	{
		if ( button == DialogInterface.BUTTON_POSITIVE )
		{
			// get the party name
			String groupName = groupNameField.getText().toString().trim();
			if ( groupName.equals( "" ) )
				groupName = "Unnamed Party";
			
			// add the party name to the list
			String uniqueName = assimilateName( groupName );
			partyList.add( uniqueName );
			adapter.notifyDataSetChanged();
			
			savePartyToDB( uniqueName );
		}
		groupNameField.setText( "" );
	}
	
	/**
	 * Handle OK and CANCEL choices for the rename party dialog
	 */
	private void handleGroupRename( int button )
	{
		String oldName = partyList.get( selectedItemPosition );
		
		if ( button == DialogInterface.BUTTON_POSITIVE )
		{
			String groupName = renameField.getText().toString().trim();
			if ( groupName.equals( "" ) )
				groupName = "Unnamed Party";
			
			if ( !oldName.equals( groupName ) ) 
			{
				String uniqueName = assimilateName( groupName );
				partyList.set( selectedItemPosition, uniqueName );
				updatePartyDBRecord( uniqueName, oldName );
			}
			else
				partyList.set( selectedItemPosition, oldName );
			
			adapter.notifyDataSetChanged();
		}
		renameField.setText( "" );
	}
	
	private void handleGroupDelete( int button )
	{
		String partyToRemove = partyList.get( selectedItemPosition );
		
		if ( button == DialogInterface.BUTTON_POSITIVE ) {
			partyList.remove( selectedItemPosition );
			adapter.notifyDataSetChanged();
			deletePartyFromDB( partyToRemove );
		}
	}
	
	//////////////////////////////////////////////////////////////////////////
	// PRIVATE HELPERS
	//////////////////////////////////////////////////////////////////////////
	
	/**
	 * Assimilate the name...to be unique? lol.
	 * @param name name to make unique
	 * @return the unique-ized name
	 */
	private String assimilateName( String name )
	{
		if ( partyList.size() == 0 )
			return name;
		
		int count = 1;
		String original = name;
		boolean unique = false;
		
		while ( !unique )
		{
			for ( int i = 0; i < partyList.size(); i++ )
			{
				if ( partyList.get( i ).equals( name ) )
				{
					name = original + " " + String.valueOf( count );
					count++;
					i = 0;	// take i back to the start to re-check
				}
				else
					unique = true;
			}
		}
		return name;
	}
	
	private void initializeLayout()
	{
		newPartyButton = ( Button ) findViewById( R.id.addGroupButton );
		newPartyButton.setText( "Create New Party" );
		newPartyButton.setOnClickListener( this );
		
		partyList = new ArrayList< String >();
		adapter = new ArrayAdapter< String >( this, R.layout.list_item, partyList );
		this.setListAdapter( adapter );
		
		this.registerForContextMenu( this.getListView() );
		this.getListView().setOnItemLongClickListener( this );
	}
	
	private void initializeDialogs()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		LayoutInflater inflater = this.getLayoutInflater();
		
		// setup the dialog for creating a new group
		groupNameView = inflater.inflate( R.layout.groupname, null );
		builder.setView( groupNameView );
		builder.setMessage( "New Party" );
		builder.setPositiveButton( "OK", this );
		builder.setNegativeButton( "Cancel", this );
		newGroupDialog = builder.create();
		
		// setup the dialog for renaming an existing group
		renameView = inflater.inflate( R.layout.groupname, null );
		builder.setView( renameView );
		builder.setMessage( "Rename Party" );
		builder.setPositiveButton( "OK", this );
		builder.setNegativeButton( "Cancel", this );
		renameDialog = builder.create();
		
		groupNameField = ( EditText ) groupNameView.findViewById( R.id.groupNameField );
		groupNameField.setHint( "New Party Name" );
		
		renameField = ( EditText ) renameView.findViewById( R.id.groupNameField );
		renameField.setHint( "New Party Name" );
		
		// setup the delete dialog
		builder = new AlertDialog.Builder( this );
		builder.setMessage( "Delete Party?" );
		builder.setPositiveButton( "Yes", this );
		builder.setNegativeButton( "No", this );
		deleteDialog = builder.create();
	}
	
	//////////////////////////////////////////////////////////////////////////
	// DATABASE ACCESS
	//////////////////////////////////////////////////////////////////////////
	private void savePartyToDB( String partyName )
	{
		database.insertGroup( partyName );
	}
	
	private void updatePartyDBRecord( String newPartyName, String oldPartyName )
	{
		database.updatePartyRecord( newPartyName, oldPartyName );
		database.updateMembersForParty( newPartyName, oldPartyName );
	}
	
	private void getPartiesFromDB()
	{
		Cursor cur;
		
		cur = database.getAllParties();
		
		while( cur.moveToNext() )
		{
			for ( int i = 0; i < cur.getColumnCount(); i++ )
			{
				String colValue = cur.getString( i );
				partyList.add( colValue );
			}
		}
		cur.close();
		adapter.notifyDataSetChanged();
	}
	
	private void loadPartyMembersFromDB( Moflow_Party party )
	{
		Cursor cur;
		
		cur = database.getPCForGroup( party.getPartyName() );
		
		Moflow_PC pc;
		while ( cur.moveToNext() )
		{
			pc = new Moflow_PC();
			for ( int i = 1; i < cur.getColumnCount(); i++ )
			{
				if ( i == 1 )
					pc.setName( cur.getString( i ) );
				else if ( i == 2 )
					pc.setInitMod( cur.getInt( i ) );
				else if ( i == 3 )
					pc.setArmorClass( cur.getInt( i ) );
				else if ( i == 4 )
					pc.setHitPoints( cur.getInt( i ) );
			}
			party.addMember( pc );
		}
		cur.close();
	}
	
	private void deletePartyFromDB( String partyToDelete )
	{
		database.deletePartyRecord( partyToDelete );
		database.deletePartyMembers( partyToDelete );
	}
}
