package moflow.tracker;

import java.util.ArrayList;

import moflow.database.MoFlowDB;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EncounterListActivity extends ListActivity 
implements OnClickListener, OnItemLongClickListener, android.content.DialogInterface.OnClickListener, OnItemClickListener {

	private Button newEncounterButton;
	
	private EditText encNameField;
	private EditText renameField;
	
	private AlertDialog newEncounterDialog;
	private AlertDialog renameDialog;
	private AlertDialog deleteDialog;
	
	private View groupNameView;
	private View renameView;
	
	private ArrayList< String > encList;
	
	private ArrayAdapter< String > adapter;
	
	private final String RETAIN_LIST_KEY = "listKey";
	private final String BUNDLE_KEY = "encounter";
	
	private final int REQC_EDITENC = 1;
	
	private int selectedItemPosition;
	
	private MoFlowDB database;
	
	//////////////////////////////////////////////////////////////////////////
	// ACTIVITY OVERRIDES
	//////////////////////////////////////////////////////////////////////////
	
	public void onCreate( Bundle savedInstanceState ) {
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
		
		loadEncountersFromDB();
	}
	
	public void onDestroy() {
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
	
	//////////////////////////////////////////////////////////////////////////
	// PRIVATE HELPERS
	//////////////////////////////////////////////////////////////////////////
	private void initializeLayout()
	{
		newEncounterButton = ( Button ) findViewById( R.id.addGroupButton );
		newEncounterButton.setText( "Create New Encounter" );
		newEncounterButton.setOnClickListener( this );
		
		encList = new ArrayList< String >();
		adapter = new ArrayAdapter< String >( this, R.layout.list_item, encList );
		this.setListAdapter( adapter );
		
		this.registerForContextMenu( this.getListView() );
		this.getListView().setOnItemLongClickListener( this );
		this.getListView().setOnItemClickListener( this );
	}
	
	private void initializeDialogs()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		LayoutInflater inflater = this.getLayoutInflater();
		
		// setup the dialog for creating a new group
		groupNameView = inflater.inflate( R.layout.groupname, null );
		builder.setView( groupNameView );
		builder.setMessage( "New Encounter" );
		builder.setPositiveButton( "OK", this );
		builder.setNegativeButton( "Cancel", this );
		newEncounterDialog = builder.create();
		
		// setup the dialog for renaming an existing group
		renameView = inflater.inflate( R.layout.groupname, null );
		builder.setView( renameView );
		builder.setMessage( "Rename Encounter" );
		builder.setPositiveButton( "OK", this );
		builder.setNegativeButton( "Cancel", this );
		renameDialog = builder.create();
		
		encNameField = ( EditText ) groupNameView.findViewById( R.id.groupNameField );
		encNameField.setHint( "New Encounter Name" );
		
		renameField = ( EditText ) renameView.findViewById( R.id.groupNameField );
		renameField.setHint( "New Encounter Name" );
		
		// setup the delete dialog
		builder = new AlertDialog.Builder( this );
		builder.setMessage( "Delete Encounter?" );
		builder.setPositiveButton( "Yes", this );
		builder.setNegativeButton( "No", this );
		deleteDialog = builder.create();
	}
	
	/**
	 * Assimilate the name...to be unique? lol.
	 * @param name name to make unique
	 * @return the unique-ized name
	 */
	private String assimilateName( String name )
	{
		if ( encList.size() == 0 )
			return name;
		
		int count = 1;
		String original = name;
		boolean unique = false;
		
		while ( !unique )
		{
			for ( int i = 0; i < encList.size(); i++ )
			{
				if ( encList.get( i ).equals( name ) )
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
	
	//////////////////////////////////////////////////////////////////////////
	// DATABASE
	//////////////////////////////////////////////////////////////////////////
	private void loadEncountersFromDB() {
		Cursor cur;
		
		cur = database.getAllEncounters();
		
		while ( cur.moveToNext() ) {
			// there is only 1 column in the encounters table, hence 0
			// for getString
			encList.add( cur.getString( 0 ) );
		}
		cur.close();
	}
	
	private void saveEncounterToDB( String name ) {
		database.insertEncounter( name );
	}
	
	private void updateEncounterDBRecord( String newName, String oldName ) {
		database.updateEncounterRecord( newName, oldName );
		database.updateEncountersForCreatures( newName, oldName );
	}
	
	private void deleteEncounterFromDB( String encounterToRemove ) {
		database.deleteEncounter( encounterToRemove );
		database.deleteEncounterCreatures( encounterToRemove );
	}
	
	private void loadMonstersIntoEncounter( Moflow_Party party ) {
		Cursor cur;
		
		cur = database.getCreaturesForEncounter( party.getPartyName() );
		
		while ( cur.moveToNext() ) {
			Moflow_Creature creature = new Moflow_Creature();
			
			for ( int i = 0; i < cur.getColumnCount(); i++ ) {
				if ( i == 0 ) // name column
					creature.setName( cur.getString( i ) );
				else if ( i == 1 ) // init column
					creature.setInitMod( cur.getInt( i ) );
				else if ( i == 2 ) // ac column
					creature.setArmorClass( cur.getInt( i ) );
				else if ( i == 3 ) // hp column
					creature.setHitPoints( cur.getInt( i ) );
			}
			party.addMember( creature );
		}
		cur.close();
	}
	
	//////////////////////////////////////////////////////////////////////////
	// VIEW HELPERS : Helpers for buttons and other GUI elements
	//////////////////////////////////////////////////////////////////////////
	private void handleNewEncounter( int button ) {
		if ( button == DialogInterface.BUTTON_POSITIVE )
		{
			// get the encounter name
			String groupName = encNameField.getText().toString().trim();
			if ( groupName.equals( "" ) )
				groupName = "Unnamed Encounter";
			
			// add the encounter name to the list
			String uniqueName = assimilateName( groupName );
			encList.add( uniqueName );
			adapter.notifyDataSetChanged();
			
			saveEncounterToDB( uniqueName );
		}
		encNameField.setText( "" );
	}
	
	private void handleEncounterRename( int button ) {
		String oldName = encList.get( selectedItemPosition );
		
		if ( button == DialogInterface.BUTTON_POSITIVE )
		{
			String groupName = renameField.getText().toString().trim();
			if ( groupName.equals( "" ) )
				groupName = "Unnamed Encounter";
			
			if ( !oldName.equals( groupName ) ) 
			{
				String uniqueName = assimilateName( groupName );
				encList.set( selectedItemPosition, uniqueName );
				updateEncounterDBRecord( uniqueName, oldName );
			}
			else // they make no changes to the name but press OK anyways
				encList.set( selectedItemPosition, oldName );
			
			adapter.notifyDataSetChanged();
		}
		renameField.setText( "" );
	}
	
	private void handleEncounterDeletion( int button ) {
		String encounterToRemove = encList.get( selectedItemPosition );
		
		if ( button == DialogInterface.BUTTON_POSITIVE ) {
			encList.remove( selectedItemPosition );
			adapter.notifyDataSetChanged();
			deleteEncounterFromDB( encounterToRemove );
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////
	// LISTENERS
	//////////////////////////////////////////////////////////////////////////
	@Override
	public void onClick( View view ) {
		if ( view == newEncounterButton ) {
			newEncounterDialog.show();
		}
	}

	@Override
	public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ) {
		selectedItemPosition = position;
		return false;
	}

	@Override
	public void onClick( DialogInterface dialog, int which ) {
		if ( dialog == newEncounterDialog )
			handleNewEncounter( which );
		else if ( dialog == renameDialog )
			handleEncounterRename( which );
		else if ( dialog == deleteDialog )
			handleEncounterDeletion( which );
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
	public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		Moflow_Party monsterParty = new Moflow_Party();
		monsterParty.setPartyName( encList.get( position ) );
		loadMonstersIntoEncounter( monsterParty );
		
		Intent i = new Intent( "moflow.tracker.EditEncounterActivity" );
		Bundle extras = new Bundle();
		extras.putParcelable( BUNDLE_KEY, monsterParty );
		i.putExtras( extras );
		
		// start the activity
		startActivityForResult( i, REQC_EDITENC );
	}
}
