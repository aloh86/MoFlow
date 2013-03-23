package moflow.tracker;

import java.util.ArrayList;
import java.util.Locale;

import moflow.adapters.CatalogListAdapter;
import moflow.database.MoFlowDB;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreatureCatalogActivity extends ListActivity 
implements OnClickListener, android.content.DialogInterface.OnClickListener, OnItemClickListener, OnItemLongClickListener, OnFocusChangeListener {
	
	private Button addButton;
	
	private TextView activityNameTV;
	
	private AlertDialog entryDialog;
	private AlertDialog deleteDialog;
	
	private MoFlowDB database;
	
	// dialog views
	private EditText nameField;
	private EditText initField;
	private EditText acField;
	private EditText hpField;
	
	private View entryView;
	
	private ArrayList< CatalogItem > creatureList;
	private CatalogListAdapter adapter;
	
	private CatalogItem item;
	
	private Moflow_Creature creature;
	
	private boolean editingItem;
	
	private int selectedItemPosition;
	
	//////////////////////////////////////////////////////////////////////////
	// ACTIVITY OVERRIDES
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.grouplist );
		
		addButton = ( Button ) findViewById( R.id.addGroupButton );
		addButton.setText( "Add New Creature" );
		addButton.setOnClickListener( this );
		
		activityNameTV = ( TextView ) findViewById( R.id.activityNameTV );
		activityNameTV.setText( "Creature Catalog" );
		
		initializeDialogs();
		
		creatureList = new ArrayList< CatalogItem >();
		
		database = new MoFlowDB( this );
		
		try {
			database.open();
		} catch ( SQLiteException e ) {
			Toast.makeText( this, "Error: database could not be opened", Toast.LENGTH_LONG ).show();
		}
		
		loadCreaturesFromDB();
		
		// setup activity's list adapter - loadCreaturesFromDB() must be called first
		adapter = new CatalogListAdapter( this, android.R.layout.simple_list_item_1, creatureList );
		this.getListView().setAdapter( adapter );
		this.getListView().setOnItemClickListener( this );
		this.getListView().setOnItemLongClickListener( this );
		this.getListView().setFastScrollEnabled( true );
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		database.close();
	}
	
	//////////////////////////////////////////////////////////////////////////
	// PRIVATE HELPERS
	//////////////////////////////////////////////////////////////////////////
	
	private void initializeDialogs() {
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		LayoutInflater inflater = this.getLayoutInflater();
		entryView = inflater.inflate( R.layout.groupitem, null );
		
		builder.setView( entryView );
		builder.setPositiveButton( "OK", this );
		builder.setNegativeButton( "Cancel", this );
		entryDialog = builder.create();
		
		nameField = ( EditText ) entryView.findViewById( R.id.nameEditText );
		nameField.setHint( "Creature Name" );
		initField = ( EditText ) entryView.findViewById( R.id.initEditText );
		acField = ( EditText ) entryView.findViewById( R.id.acEditText );
		hpField = ( EditText ) entryView.findViewById( R.id.hpEditText );
		
		initField.setOnFocusChangeListener( this );
		acField.setOnFocusChangeListener( this );
		hpField.setOnFocusChangeListener( this );
		
		builder = new AlertDialog.Builder( this );
		builder.setMessage( "Delete Creature?" );
		builder.setPositiveButton( "Yes", this );
		builder.setNegativeButton( "No", this );
		deleteDialog = builder.create();
	}
	
	private void prepNewEntryDialog() {
		nameField.setText( "" );
		initField.setText( "0" );
		acField.setText( "0" );
		hpField.setText( "0" );
	}
	
	private void prepEditEntryDialog( Moflow_Creature critter ) {
		nameField.setText( critter.getCharName() );
		initField.setText( String.valueOf( critter.getInitMod() ) );
		acField.setText( String.valueOf( critter.getAC() ) );
		hpField.setText( String.valueOf( critter.getMaxHitPoints() ) );
	}
	
	private Moflow_Creature setCreatureStats( boolean editing ) {
		Moflow_Creature oldCrit = null;
		
		if ( !editing )
			creature = new Moflow_Creature();
		else
			oldCrit = creature.clone();
		
		if ( nameField.getText().toString().trim().equals( "" ) )
			nameField.setText( "Nameless One" );
		
		String currentNameInField = nameField.getText().toString().trim();
		
		if ( !creature.getCharName().equals( currentNameInField ) ) {
			String uniqueName = makeNameUnique( nameField.getText().toString().trim() );
			creature.setName( uniqueName );
		}

		creature.setInitMod( Integer.valueOf( initField.getText().toString().trim() ) );
		creature.setArmorClass( Integer.valueOf( acField.getText().toString().trim() ) );
		creature.setHitPoints( Integer.valueOf( hpField.getText().toString().trim() ) );
		
		return oldCrit;
	}
	
	/**
	 * Ensures that every name in the catalog is unique. An inefficient algorithm O(n),
	 * but this way avoids creating duplicate catalog lists. First, the starting index
	 * is found, and then each value is checked from the starting index until the next
	 * header is reached or it is the end of the list. This range is looped through
	 * until the name is unique.
	 * @param name The name to make unique.
	 * @return a unique name
	 */
	private String makeNameUnique( String name ) {
		// if the catalog is empty, yes it's unique
		if ( creatureList.size() == 0 )
			return name;
		
		int startIndex = 0;
		// grab the first letter of the name to find the appropriate section header
		String startSection = String.valueOf( name.toUpperCase( Locale.getDefault() ).charAt( 0 ) );
		
		// move startIndex up to the section header
		for ( int i = 0; i < creatureList.size(); i++ ) {
			// if it went to end of the list it's unique
			if ( i == creatureList.size() - 1 )
				return name;
			
			if ( startSection.equalsIgnoreCase( creatureList.get( i ).name ) && creatureList.get( i ).header ) {
				startIndex = i + 1;
				break;
			}
		}
		
		if ( creatureList.get( startIndex ).header )
			return name;
		
		String original = name;
		boolean unique = false;
		int count = 1;
		
		while ( !unique )
		{
			for ( int i = startIndex; i < creatureList.size() && !creatureList.get( i ).header; i++ )
			{
				if ( creatureList.get( i ).name.equals( name ) )
				{
					name = original + " " + String.valueOf( count );
					count++;
					i = startIndex;	// take i back to the start to re-check
				}
				else
					unique = true;
			}
		}
		
		return name;
	}
	
	private boolean createNewHeader( String curSection, String newSection ) {
		boolean createHeader = false;
		
		if ( !( curSection.equalsIgnoreCase( newSection ) ) )
			createHeader = true;
		
		return createHeader;
	}
	
	//////////////////////////////////////////////////////////////////////////
	// VIEW HELPERS
	//////////////////////////////////////////////////////////////////////////
	
	private void handleAddNewCreature( final int button ) {
		if ( button == DialogInterface.BUTTON_POSITIVE ) {
			setCreatureStats( false );
			saveNewCreatureToDB();
			creatureList.clear();
			loadCreaturesFromDB();
			adapter.notifyDataSetChanged();
		}
	}
	
	private void handleCreatureEdit( final int button ) {
		if ( button == DialogInterface.BUTTON_POSITIVE ) {
			Moflow_Creature oldCritter = setCreatureStats( true );
			saveEditedCreatureToDB( creature, oldCritter.getCharName() );
			creatureList.clear();
			loadCreaturesFromDB();
			adapter.notifyDataSetChanged();
		}
	}
	
	private void handleDeleteCatalogItem( final int button ) {
		if ( button == DialogInterface.BUTTON_POSITIVE ) {
			deleteCatalogCreatureFromDB( item.name );
			creatureList.remove( selectedItemPosition );
//			creatureList.clear();
//			loadCreaturesFromDB();
			adapter.notifyDataSetChanged();
		}
	}
	
	//////////////////////////////////////////////////////////////////////////
	// DATABASE
	//////////////////////////////////////////////////////////////////////////
	private void saveNewCreatureToDB() {
		String name = creature.getCharName();
		int init = creature.getInitMod();
		int ac = creature.getAC();
		int hp = creature.getMaxHitPoints();
		
		database.insertCreatureInCatalog( name, init, ac, hp );
	}
	
	
	private void loadCreaturesFromDB() {
		Cursor cur;
		
		cur = database.getCatalog();
		
		final int COL_POSITION = 0;
		char currentSection = '$';
		
		while( cur.moveToNext() ) {
			String name = cur.getString( COL_POSITION );
			
			if ( createNewHeader( String.valueOf( currentSection ), String.valueOf( name.charAt( 0 ) ) ) ) {
				creatureList.add( new CatalogItem( String.valueOf( name.toUpperCase( Locale.getDefault() ).charAt( 0 ) ), true ) );
			}	
			creatureList.add( new CatalogItem( name, false ) );
			currentSection = name.charAt( 0 );
		}
		cur.close();
	}
	
	private Moflow_Creature getCreatureFromDB( String name ) {
		Cursor cur;
		
		cur = database.getCreatureFromCatalog( name );
		
		creature = new Moflow_Creature();
		
		while ( cur.moveToNext() ) {
			for ( int i = 0; i < cur.getColumnCount(); i++ ) {
				if ( i == 0 ) // name column
					creature.setName( cur.getString( i ) );
				else if ( i == 1 ) // init column
					creature.setInitMod( cur.getInt( i ) );
				else if ( i == 2 ) // AC column
					creature.setArmorClass( cur.getInt( i ) );
				else if ( i == 3 ) // HP column
					creature.setHitPoints( cur.getInt( i ) );
			}
		}
		cur.close();
		return creature;
	}
	
	private void saveEditedCreatureToDB( Moflow_Creature edited, String oldCreatureName ) {
		database.updateCreatureInCatalog( edited, oldCreatureName );
	}
	
	private void deleteCatalogCreatureFromDB( String name ) {
		database.deleteCreatureFromCatalog( name );
	}
	
	//////////////////////////////////////////////////////////////////////////
	// LISTENERS
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onClick( View view ) {
		if ( view == addButton ) {
			editingItem = false;
			entryDialog.show();
			prepNewEntryDialog();
		}
	}

	@Override
	public void onClick( DialogInterface dialog, int button ) {
		if ( dialog == entryDialog && !editingItem )
			handleAddNewCreature( button );
		else if ( dialog == entryDialog && editingItem )
			handleCreatureEdit( button );
		else if ( dialog == deleteDialog )
			handleDeleteCatalogItem( button );
	}

	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		editingItem = true;
		item = creatureList.get( position );
		creature = getCreatureFromDB( item.name );
		entryDialog.show();
		prepEditEntryDialog( creature );
	}

	@Override
	public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ) {
		selectedItemPosition = position;
		item = creatureList.get( position );
		deleteDialog.show();
		
		return false;
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
}
