package moflow.tracker;

import java.util.ArrayList;
import moflow.database.MoFlowDB;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.SQLException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/*
===============================================================================
PCM_EditPartyActivity.java

Activity for editing members of a party. Members can be added, edited, or 
removed. All changes are saved in the Players table of the database.
===============================================================================
*/
public class PCM_EditPartyActivity extends ListActivity 
implements OnClickListener, android.content.DialogInterface.OnClickListener, OnFocusChangeListener, OnItemLongClickListener, OnItemClickListener
{
	// Members for the Activity
	private Button addButton;
	
	private TextView partyNameTV;
	
	private ArrayAdapter<Moflow_PC> adapter;
	
	private ArrayList< Moflow_PC > partyList;
	
	private AlertDialog itemDialog;
	private AlertDialog deleteDialog;
	
	private View itemView;
	
	private Moflow_Party party;
	
	private Moflow_PC character;
	
	private boolean editingItem;
	
	private MoFlowDB database;
	
	// Members for the add PC dialog
	private EditText itemNameField;
	private EditText initField;
	private EditText acField;
	private EditText hpField;
	
	
	//////////////////////////////////////////////////////////////////////////
	// ACTIVITY OVERRIDES
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onCreate( Bundle savedInstanceState ) 
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.group );
		
		initializeLayout();	// always call this first!
		loadParty();		// load the party sent from the calling activity
		initializeDialogs();
		
		database = new MoFlowDB( this );
		
		try 
		{
			database.open();
		} catch ( SQLException e ) {
			Toast.makeText( this, "Error: database could not be opened", Toast.LENGTH_LONG ).show();
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		database.close();
	}

	
	//////////////////////////////////////////////////////////////////////////
	// LISTENERS
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onClick( View view ) 
	{
		if ( view == addButton ) {
			editingItem = false;
			itemDialog.show();
			prepareItemDialogForNewPC();
		}
	}
	
	@Override
	public void onClick(DialogInterface dialog, int buttonChoice ) {
		if ( dialog == itemDialog && !editingItem )
			handleNewItem( buttonChoice );
		else if ( dialog == itemDialog && editingItem )
			handleEditItem( buttonChoice );
		else if ( dialog == deleteDialog )
			handleDeleteItem( buttonChoice );
		
	}
	
	@Override
	public void onFocusChange( View view, boolean hasFocus ) {
		if ( view == initField && !hasFocus && initField.getText().toString().trim().equals("") )
			initField.setText( "0" );
		else if ( view == acField && !hasFocus && acField.getText().toString().trim().equals("") )
			acField.setText( "0" );
		else if ( view == hpField && !hasFocus && hpField.getText().toString().trim().equals("") )
			hpField.setText( "0" );
	}
	
	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		editingItem = true;
		character = partyList.get( position );
		itemDialog.show();
		prepareItemDialogForEditPC();
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id ) {
		character = partyList.get( position );
		deleteDialog.show();
		
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////
	// VIEW HELPERS : Helpers for buttons and other GUI elements
	//////////////////////////////////////////////////////////////////////////
	
	private void handleNewItem( int button ) {
		if ( button == DialogInterface.BUTTON_POSITIVE ) {
			setPCStats( false );
			partyList.add( character );
			adapter.notifyDataSetChanged();
			saveNewMemberToDB();
		}
	}
	
	private void handleEditItem( int button ) {
		if ( button == DialogInterface.BUTTON_POSITIVE ) {
			Moflow_PC oldCharacter = setPCStats( true );
			adapter.notifyDataSetChanged();
			updateMemberInDB( character, party.getPartyName(), oldCharacter.getCharName() );
		}
	}
	
	private void handleDeleteItem( int button ) {
		if ( button == DialogInterface.BUTTON_POSITIVE ) {
			partyList.remove( character );
			adapter.notifyDataSetChanged();
			removeCharacterFromDB();
		}
	}
	
	//////////////////////////////////////////////////////////////////////////
	// PRIVATE HELPERS
	//////////////////////////////////////////////////////////////////////////
	
	/**
	 * Setup the layout
	 */
	private void initializeLayout() 
	{
		addButton = ( Button ) findViewById( R.id.addBtn );
		addButton.setText( "Add PC" );
		addButton.setOnClickListener( this );
		
		partyNameTV = ( TextView ) findViewById( R.id.partyNameTV );
		
		partyList = new ArrayList< Moflow_PC >();
		adapter = new ArrayAdapter< Moflow_PC >( this, R.layout.list_item, partyList );
		this.setListAdapter( adapter );
		
		// initialize views for the item dialogs
		LayoutInflater inflater = this.getLayoutInflater();
		itemView = inflater.inflate( R.layout.groupitem, null );
		
		itemNameField = ( EditText ) itemView.findViewById( R.id.nameEditText );
		itemNameField.setHint( "Character Name" );
		
		initField = ( EditText ) itemView.findViewById( R.id.initEditText );
		initField.setOnFocusChangeListener( this );
		acField = ( EditText ) itemView.findViewById( R.id.acEditText );
		acField.setOnFocusChangeListener( this );
		hpField = ( EditText ) itemView.findViewById( R.id.hpEditText );
		hpField.setOnFocusChangeListener( this );
		
		this.getListView().setOnItemLongClickListener( this );
		this.getListView().setOnItemClickListener( this );
	}
	
	/**
	 * Load the party that was sent from the calling Activity
	 */
	private void loadParty() 
	{
		Bundle extras = this.getIntent().getExtras();
		
		if ( extras != null ) 
		{
			party = extras.getParcelable( "party" );
			
			partyNameTV.setText( party.getPartyName() );
			
			for ( int i = 0; i < party.getPartySize(); i++ )
				partyList.add( (Moflow_PC) party.getMember( i ) );
			
			adapter.notifyDataSetChanged();
		}
	}
	
	private void initializeDialogs() {
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		
		builder.setView( itemView );
		builder.setPositiveButton( "OK", this );
		builder.setNegativeButton( "Cancel", this );
		itemDialog = builder.create();
		
		// setup the delete dialog
		builder = new AlertDialog.Builder( this );
		builder.setMessage( "Delete Character?" );
		builder.setPositiveButton( "Yes", this );
		builder.setNegativeButton( "No", this );
		deleteDialog = builder.create();
	}
	
	private void prepareItemDialogForNewPC() {
		itemNameField.setText( "" );
		itemNameField.requestFocus();
		initField.setText( "0" );
		acField.setText( "0" );
		hpField.setText( "0" );
	}
	
	private void prepareItemDialogForEditPC() {
		itemNameField.setText( character.getCharName() );
		itemNameField.requestFocus();
		initField.setText( String.valueOf( character.getInitMod() ) );
		acField.setText( String.valueOf( character.getAC() ) );
		hpField.setText( String.valueOf ( character.getMaxHitPoints() ) );
	}
	
	private Moflow_PC setPCStats( boolean editing ) {	
		Moflow_PC oldCharacter = null;
		
		if ( !editing ) {
			character = new Moflow_PC();
		} else {
			oldCharacter = ( Moflow_PC ) character.clone();
		}
		
		if ( itemNameField.getText().toString().trim().equals( "" ) )
			itemNameField.setText( "Nameless One" );
		
		String currentNameInField = itemNameField.getText().toString().trim();
		
		if ( !character.getCharName().equals( currentNameInField ) ) {
			String uniqueName = assimilateName( itemNameField.getText().toString().trim() );
			character.setName( uniqueName );
		}
		character.setInitMod( Integer.parseInt( initField.getText().toString().trim() ) );
		character.setArmorClass( Integer.parseInt( acField.getText().toString().trim() ) );
		character.setHitPoints( Integer.parseInt( hpField.getText().toString().trim() ) );
		
		return oldCharacter;
	}
	
	private String assimilateName( String name ) {
		if ( partyList.size() == 0 )
			return name;
		
		int count = 1;
		String original = name;
		boolean unique = false;
		
		while ( !unique )
		{
			for ( int i = 0; i < partyList.size(); i++ )
			{
				if ( partyList.get( i ).getCharName().equals( name ) )
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
	// DATBASE
	//////////////////////////////////////////////////////////////////////////
	
	/**
	 * Saves a new PC to the database.
	 */
	private void saveNewMemberToDB() {
		String partyName = party.getPartyName();
		String pcName = character.getCharName();
		int init = character.getInitMod();
		int AC = character.getAC();
		int hp = character.getMaxHitPoints();

		database.insertPlayer( partyName, pcName, init, AC, hp );
	}
	
	/**
	 * Removes a character from the database.
	 */
	private void removeCharacterFromDB() {
		database.deletePC( party.getPartyName(), character.getCharName() );
	}
	
	/**
	 * Updates a member in the database. This is called when a PC is edited and the
	 * previous records needs to be updated.
	 * @param updated the edited character
	 * @param partyName party that the old character belongs to
	 * @param oldName the name of the character before it was edited
	 */
	private void updateMemberInDB( Moflow_PC updated, String partyName, String oldName ) {
		database.updatePlayerRecord( updated, partyName, oldName );
	}
}
