package moflow.tracker;

import java.util.ArrayList;

import moflow.database.MoFlowDB;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/*
===============================================================================
EditEncounterActivity.java

Allows the user to add, edit, or remove an item from an encounter. User
can add items to encounter from the creature catalog.
===============================================================================
*/
public class EditEncounterActivity extends ListActivity 
implements OnClickListener, OnFocusChangeListener, OnItemLongClickListener, OnItemClickListener, android.content.DialogInterface.OnClickListener {
	// Members for the Activity
	private Button addButton;
	
	private TextView encounterNameTV;
	
	private ArrayAdapter< Moflow_Creature > adapter;
	private ArrayAdapter< String > catalogAdapter;
	
	private ArrayList< Moflow_Creature > creatureList;
	private ArrayList< String > catalogList;
	
	private AlertDialog itemDialog;
	private AlertDialog deleteDialog;
	private AlertDialog catalogDialog;
	
	private Moflow_Party creatureParty;
	
	private Moflow_Creature creature;
	
	private boolean editingItem;
	
	private MoFlowDB database;
	
	// Members for the add PC dialog
	private View itemView;
	
	private EditText itemNameField;
	private EditText initField;
	private EditText acField;
	private EditText hpField;
	
	private int selectedItemPosition;
	
	//////////////////////////////////////////////////////////////////////////
	// ACTIVITY OVERRIDES
	//////////////////////////////////////////////////////////////////////////
	
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.group );
		
		// initialize and open the database first before all the next three function calls
		database = new MoFlowDB( this );
		
		try 
		{
			database.open();
		} catch ( SQLException e ) {
			Toast.makeText( this, "Error: database could not be opened", Toast.LENGTH_LONG ).show();
		}
		
		// always call the following three functions in order because 
		// loadCreatures() and initializeDialogs() depends on some variables
		// initialized in initializeLayout().
		initializeLayout();
		loadCreatures();
		initializeDialogs();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		database.close();
	}
	
	//////////////////////////////////////////////////////////////////////////
	// PRIVATE HELPERS
	//////////////////////////////////////////////////////////////////////////
	
	private void initializeLayout() {
		addButton = ( Button ) findViewById( R.id.addBtn );
		addButton.setText( "Add Creature" );
		addButton.setOnClickListener( this );
		
		encounterNameTV = ( TextView ) findViewById( R.id.partyNameTV );
		
		creatureList = new ArrayList< Moflow_Creature >();
		adapter = new ArrayAdapter< Moflow_Creature >( this, R.layout.list_item, creatureList );
		this.setListAdapter( adapter );
		
		// initialize views for the item dialogs
		LayoutInflater inflater = this.getLayoutInflater();
		itemView = inflater.inflate( R.layout.groupitem, null );
		
		itemNameField = ( EditText ) itemView.findViewById( R.id.nameEditText );
		itemNameField.setHint( "Creature Name" );
		
		initField = ( EditText ) itemView.findViewById( R.id.initEditText );
		initField.setOnFocusChangeListener( this );
		acField = ( EditText ) itemView.findViewById( R.id.acEditText );
		acField.setOnFocusChangeListener( this );
		hpField = ( EditText ) itemView.findViewById( R.id.hpEditText );
		hpField.setOnFocusChangeListener( this );
		
		this.getListView().setOnItemLongClickListener( this );
		this.getListView().setOnItemClickListener( this );
		
		// initialize adapter for the catalog dialog
		initializeCatalogList();
		catalogAdapter = new ArrayAdapter< String >( this, R.layout.list_item, catalogList );
	}
	
	private void initializeDialogs() {
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		
		// setup i tem dialog
		builder.setView( itemView );
		builder.setPositiveButton( "OK", this );
		builder.setNegativeButton( "Cancel", this );
		itemDialog = builder.create();
		
		// setup the delete dialog
		builder = new AlertDialog.Builder( this );
		builder.setMessage( "Delete Party?" );
		builder.setPositiveButton( "Yes", this );
		builder.setNegativeButton( "No", this );
		deleteDialog = builder.create();
		
		// setup the catalog dialog
		builder = new AlertDialog.Builder( this );
		builder.setTitle( "Creature Catalog" );
		builder.setAdapter( catalogAdapter, this );
		catalogDialog = builder.create();
	}
	
	private void loadCreatures() {
		Bundle extras = this.getIntent().getExtras();
		
		if ( extras != null ) 
		{
			creatureParty = extras.getParcelable( "encounter" );
			
			encounterNameTV.setText( creatureParty.getPartyName() );
			
			for ( int i = 0; i < creatureParty.getPartySize(); i++ )
				creatureList.add( creatureParty.getMember( i ) );
			
			adapter.notifyDataSetChanged();
		}
	}
	
	private String makeNameUnique( String name ) {
		if ( creatureList.size() == 0 )
			return name;
		
		int count = 1;
		String original = name;
		boolean unique = false;
		
		while ( !unique )
		{
			for ( int i = 0; i < creatureList.size(); i++ )
			{
				if ( creatureList.get( i ).getCharName().equals( name ) )
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
	
	private String setCreatureStats() {
		String oldName = creature.getCharName();
		String currentNameInField = itemNameField.getText().toString().trim();
		
		if ( !creature.getCharName().equals( currentNameInField ) ) {
			String uniqueName = makeNameUnique( itemNameField.getText().toString().trim() );
			creature.setName( uniqueName );
		}
		creature.setInitMod( Integer.parseInt( initField.getText().toString().trim() ) );
		creature.setArmorClass( Integer.parseInt( acField.getText().toString().trim() ) );
		creature.setHitPoints( Integer.parseInt( hpField.getText().toString().trim() ) );
		
		return oldName;
	}
	
	//////////////////////////////////////////////////////////////////////////
	// DATABASES
	//////////////////////////////////////////////////////////////////////////
	private void initializeCatalogList() {
		Cursor cur;
		
		cur = database.getCatalog();
		catalogList = new ArrayList< String >();
		
		while( cur.moveToNext() ) {
			catalogList.add( cur.getString( 0 ) ); // 0 is column of the name
		}
		cur.close();
	}
	
	private void setActiveCreature( String name ) {
		Cursor cur;
		
		cur = database.getCreatureFromCatalog( name );
		creature = new Moflow_Creature();
		
		while ( cur.moveToNext() ) {
			// always returns 4 columns:
			// 0 = name, 1 = init, 2 = ac, 3 = hp
			creature.setName( cur.getString( 0 ) );
			creature.setInitMod( cur.getInt( 1 ) );
			creature.setArmorClass( cur.getInt( 2 ) );
			creature.setHitPoints( cur.getInt( 3 ) );
		}
		cur.close();
	}
	
	private void saveItemToCreaturesDB() {
		String encounterName = encounterNameTV.getText().toString();
		String creatureName = creature.getCharName();
		int init = creature.getInitMod();
		int ac = creature.getAC();
		int hp = creature.getMaxHitPoints();
		
		database.insertCreature( encounterName, creatureName, init, ac, hp ); 
	}
	
	private void updateCreatureInDB( Moflow_Creature updated, String oldName, String encounterName ) {
		String name = updated.getCharName();
		int init = updated.getInitMod();
		int ac = updated.getAC();
		int hp = updated.getMaxHitPoints();
		
		database.updateCreatureRecord( name, oldName, encounterName, init, ac, hp );
	}
	
	//////////////////////////////////////////////////////////////////////////
	// VIEW/LISTENER HELPERS
	//////////////////////////////////////////////////////////////////////////
	private void prepItemDialog() {
		itemNameField.setText( creature.getCharName() );
		initField.setText( String.valueOf( creature.getInitMod() ) );
		acField.setText( String.valueOf( creature.getAC() ) );
		hpField.setText( String.valueOf( creature.getMaxHitPoints() ) );
	}
	
	private void addNewItemToList( int position ) {
		String creatureName = catalogList.get( position );
		setActiveCreature( creatureName );
		
		// make sure the name is unique before adding
		String uniqueName = makeNameUnique( creature.getCharName() );
		creature.setName( uniqueName );
		
		creatureList.add( creature );
		adapter.notifyDataSetChanged();
		saveItemToCreaturesDB();
	}
	
	private void handleEditedItem( final int which ) {
		if ( which == Dialog.BUTTON_POSITIVE ) {
			String oldName = setCreatureStats();
			adapter.notifyDataSetChanged();
			updateCreatureInDB( creature, oldName, creatureParty.getPartyName() );
		}
	}
	
	private void handleItemDelete( final int which ) {
		if ( which == Dialog.BUTTON_POSITIVE ) {
			creatureList.remove( selectedItemPosition );
			adapter.notifyDataSetChanged();
			database.deleteCreature( creatureParty.getPartyName(), creature.getCharName() );
		}
	}
	
	//////////////////////////////////////////////////////////////////////////
	// LISTENERS
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onClick( View view ) {
		if ( view == addButton ) {
			catalogDialog.show();
		}
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
	public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ) {
		creature = creatureList.get( position );
		selectedItemPosition = position;
		deleteDialog.show();
		return false;
	}

	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		creature = creatureList.get( position );
		prepItemDialog();
		itemDialog.show();
	}

	@Override
	public void onClick( DialogInterface dialog, int which ) {
		if ( dialog == catalogDialog )
			addNewItemToList( which );
		else if ( dialog == itemDialog )
			handleEditedItem( which );
		else if ( dialog == deleteDialog )
			handleItemDelete( which );
	}
}
