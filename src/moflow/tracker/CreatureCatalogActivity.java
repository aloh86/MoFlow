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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreatureCatalogActivity extends ListActivity 
implements OnClickListener, android.content.DialogInterface.OnClickListener, OnItemClickListener {
	
	Button addButton;
	
	AlertDialog entryDialog;
	
	MoFlowDB database;
	
	// dialog views
	EditText nameField;
	EditText initField;
	EditText acField;
	EditText hpField;
	
	View entryView;
	
	ArrayList< CatalogItem > creatureList;
	CatalogListAdapter adapter;
	
	Moflow_Creature creature;
	
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
	}
	
	private void prepNewEntryDialog() {
		nameField.setText( "" );
		initField.setText( "0" );
		acField.setText( "0" );
		hpField.setText( "0" );
	}
	
	private Moflow_Creature setCreatureStats( boolean editing ) {
		Moflow_Creature oldCrit = null;
		
		if ( !editing )
			creature = new Moflow_Creature();
		else
			oldCrit = creature.clone();
		
		String uniqueName = makeNameUnique( nameField.getText().toString().trim() );
		creature.setName( uniqueName );
		creature.setInitMod( Integer.valueOf( initField.getText().toString().trim() ) );
		creature.setArmorClass( Integer.valueOf( acField.getText().toString().trim() ) );
		creature.setHitPoints( Integer.valueOf( hpField.getText().toString().trim() ) );
		
		return oldCrit;
	}
	
	private String makeNameUnique( String name ) {
		// if the catalog is empty, yes it's unique
		if ( creatureList.size() == 0 )
			return name;
		
		int startIndex = 0;
		String startSection = String.valueOf( name.toUpperCase( Locale.getDefault() ).charAt( 0 ) );
		
		for ( int i = 0; i < creatureList.size(); i++ ) {
			if ( startSection.equalsIgnoreCase( creatureList.get( i ).name ) && creatureList.get( i ).header ) {
				startIndex = i + 1;
				break;
			}
			// if it went to end of the list it's unique
			if ( i == creatureList.size() - 1 )
				return name;
		}
		
		String original = name;
		boolean unique = false;
		int count = 1;
		
		while ( !unique )
		{
			for ( int i = startIndex; i < creatureList.size() && !creatureList.get( i ).header; i++ )
			{
				if ( creatureList.get( i ).name.equalsIgnoreCase( name ) )
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
	
	//////////////////////////////////////////////////////////////////////////
	// VIEW HELPERS
	//////////////////////////////////////////////////////////////////////////
	
	private void handleAddNewCreature( int button ) {
		if ( button == DialogInterface.BUTTON_POSITIVE ) {
			setCreatureStats( false );
			saveNewCreatureToDB();
			creatureList.clear();
			loadCreaturesFromDB();
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
	
	private boolean createNewHeader( String curSection, String newSection ) {
		boolean createHeader = false;
		
		if ( !( curSection.equalsIgnoreCase( newSection ) ) )
			createHeader = true;
		
		return createHeader;
	}
	
	//////////////////////////////////////////////////////////////////////////
	// LISTENERS
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onClick( View view ) {
		if ( view == addButton ) {
			entryDialog.show();
			prepNewEntryDialog();
		}
	}

	@Override
	public void onClick( DialogInterface dialog, int button ) {
		if ( dialog == entryDialog ) {
			handleAddNewCreature( button );
		}
	}

	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		
	}
}
