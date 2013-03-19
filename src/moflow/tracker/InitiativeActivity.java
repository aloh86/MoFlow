package moflow.tracker;

import java.util.ArrayList;
import java.util.ListIterator;

import moflow.adapters.InitiativeAdapter;
import moflow.database.MoFlowDB;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
/*
===============================================================================
InitiativeActivity.java

Keeps track of initiative.
===============================================================================
*/

public class InitiativeActivity extends ListActivity 
implements OnClickListener, OnItemClickListener, OnItemLongClickListener, android.content.DialogInterface.OnClickListener, OnFocusChangeListener {
	
	private TextView roundsText;
	
	private Button prevButton;
	private Button nextButton;
	
	private AlertDialog surpriseDialog;
	private AlertDialog itemEditDialog;
	private AlertDialog graveDialog;
	private AlertDialog itemDialog;
	private AlertDialog addOptionsDialog;
	private AlertDialog partyListDialog;
	private AlertDialog encounterListDialog;
	private AlertDialog catalogListDialog;
	private AlertDialog friendFoeDialog;
	private AlertDialog removeOptionsDialog;
	private AlertDialog waitListDialog;
	
	private ArrayList< Moflow_Creature > initList;
	private ArrayList< Moflow_Creature > waitList;
	
	private InitiativeAdapter adapter;
	
	private Moflow_Creature newCreature;
	
	private int creatureType; // 0 for Friendly, 1 for Hostile
	private int selectedItemPosition;
	
	private final String RETAIN_LIST_KEY = "listKey";
	
	String [] menuList; // used for lists opened by menu options
	
	MoFlowDB db;
	
	// stuff for new item layout
	private View itemView;
	
	private EditText nameField;
	private EditText initField;
	private EditText acField;
	private EditText hpField;
	
	//////////////////////////////////////////////////////////////////////////
	// ACTIVITY OVERRIDES
	//////////////////////////////////////////////////////////////////////////
	
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.init_layout );
		
		initializeLayout();
		initializeDialogs();
		
		db = new MoFlowDB( this );
		
		try {
			db.open();
		} catch ( SQLiteException e ) {
		}
		
		loadListFromDB();
	}
	
//	@Override
//	public void onSaveInstanceState( Bundle outState ) {
//		super.onSaveInstanceState( outState );
//		outState.putParcelableArrayList( RETAIN_LIST_KEY, initList );
//	}
//	
//	@Override
//	public void onRestoreInstanceState( Bundle savedInstanceState ) {
//		super.onRestoreInstanceState( savedInstanceState );
//		initList = savedInstanceState.getParcelableArrayList( RETAIN_LIST_KEY );
//		adapter = new InitiativeAdapter( this, R.layout.init_layout, initList );
//		this.setListAdapter( adapter );
//		adapter.notifyDataSetChanged();
//	}
	
	public void onStop() {
		db.deleteInitListAll();
		saveInitToDB();
		super.onStop();
	}
	
	public void onDestroy() {
		db.close();
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.init_menu, menu );
	    return true;
	}
	
	public void onBackPressed() {
		db.deleteInitListAll();
		saveInitToDB();
		super.onBackPressed();
	}
	
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo ) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.init_item_menu, menu );
	}
	
	//////////////////////////////////////////////////////////////////////////
	// PRIVATE HELPERS
	//////////////////////////////////////////////////////////////////////////
	
	private void initializeLayout() {
		roundsText = ( TextView ) findViewById( R.id.roundsText );
		
		prevButton = ( Button ) findViewById( R.id.prevButton );
		prevButton.setOnClickListener( this );
		
		nextButton = ( Button ) findViewById( R.id.nextButton );
		nextButton.setOnClickListener( this );
		
		initList = new ArrayList< Moflow_Creature >();
		adapter = new InitiativeAdapter( this, R.layout.init_layout, initList );
		this.setListAdapter( adapter );
		
		waitList = new ArrayList< Moflow_Creature >();
		
		this.getListView().setOnItemClickListener( this );
		this.getListView().setOnItemLongClickListener( this );
		this.registerForContextMenu( this.getListView() );
		
		// initialize layout for itemView
		LayoutInflater inflater = this.getLayoutInflater();
		itemView = inflater.inflate( R.layout.groupitem, null );
		
		nameField = ( EditText ) itemView.findViewById( R.id.nameEditText );
		nameField.setHint( "Name" );
		
		initField = ( EditText ) itemView.findViewById( R.id.initEditText );
		initField.setOnFocusChangeListener( this );
		acField = ( EditText ) itemView.findViewById( R.id.acEditText );
		acField.setOnFocusChangeListener( this );
		hpField = ( EditText ) itemView.findViewById( R.id.hpEditText );
		hpField.setOnFocusChangeListener( this );
	}
	
	private void initializeDialogs() {
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		
		// setup the dialog for creating a new group
		builder.setMessage( "Start with surprise round?" );
		builder.setPositiveButton( "OK", this );
		builder.setNegativeButton( "Cancel", this );
		surpriseDialog = builder.create();
		
		builder = new AlertDialog.Builder( this );
		builder.setMessage( "Remove from list?" );
		builder.setPositiveButton( "OK", this );
		builder.setNegativeButton( "Cancel", this );
		graveDialog = builder.create();
		
		builder = new AlertDialog.Builder( this );
		builder.setView( itemView );
		builder.setPositiveButton( "OK", this );
		builder.setNegativeButton( "Cancel", this );
		itemDialog = builder.create();
	}
	
	private void prepareItemDialogForNewPC() {
		nameField.setText( "" );
		nameField.requestFocus();
		initField.setText( "0" );
		acField.setText( "0" );
		hpField.setText( "0" );
	}
	
	private void addNewItemToList( int button ) {
		if ( button == Dialog.BUTTON_POSITIVE ) {
			if ( creatureType == 0 ) // if a PC
				newCreature = new Moflow_PC();
			else
				newCreature = new Moflow_Creature();
			
			if ( nameField.getText().toString().trim().equals( "" ) )
				newCreature.setName( "Nameless One" );
			else
				newCreature.setName( nameField.getText().toString().trim() );
			newCreature.setInitMod( Integer.parseInt( initField.getText().toString().trim() ) );
			newCreature.setArmorClass( Integer.parseInt( acField.getText().toString().trim() ) );
			newCreature.setHitPoints( Integer.parseInt( hpField.getText().toString().trim() ) );
			newCreature.setCurrentHP( Integer.parseInt( hpField.getText().toString().trim() ) );
			
			initList.add( newCreature );
		}
	}
	
	private void startFriendOrFoeDialog() {
		String [] choices = { "PC", "Monster" };
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		builder.setItems( choices, this );
		friendFoeDialog = builder.create();
		friendFoeDialog.show();
	}
	
	private void startAddMenu() {
		String [] addItems = { "New", "Party", "Encounter", "Catalog" };
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		builder.setTitle( "Add To List" );
		builder.setItems( addItems, this );
		addOptionsDialog = builder.create();
		addOptionsDialog.show();
	}
	
	private void loadPartyList() {
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		builder.setTitle( "Parties" );
		menuList = getPartiesFromDB();
		builder.setItems( menuList, this );
		partyListDialog = builder.create();
		partyListDialog.show();
	}
	
	private void loadEncounterList() {
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		builder.setTitle( "Encounters" );
		menuList = getEncountersFromDB();
		builder.setItems( menuList, this );
		encounterListDialog = builder.create();
		encounterListDialog.show();
	}
	
	private void loadCatalogList() {
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		builder.setTitle( "Creature Catalog" );
		menuList = getCatalogFromDB();
		builder.setItems( menuList, this );
		catalogListDialog = builder.create();
		catalogListDialog.show();
	}
	
	private void startRemoveMenu() {
		String [] removeMenu = { "Players", "Monsters", "All" };
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		builder.setTitle( "Remove from List" );
		builder.setItems( removeMenu, this );
		removeOptionsDialog = builder.create();
		removeOptionsDialog.show();
	}
	
	private void removeAllPCs() {
		ListIterator< Moflow_Creature > litr = initList.listIterator();
		while( litr.hasNext() ) {
			if ( !litr.next().isCreature() ) {
				litr.remove();
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	private void removeAllMonsters() {
		ListIterator< Moflow_Creature > litr = initList.listIterator();
		while( litr.hasNext() ) {
			if ( litr.next().isCreature() ) {
				litr.remove();
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	private void removeAll() {
		initList.clear();
		adapter.notifyDataSetChanged();
	}
	
	private void startWaitList() {
		menuList = new String[ waitList.size() ];
		for ( int i = 0; i < waitList.size(); i++ ) {
			menuList[ i ] = waitList.get( i ).getCharName();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		builder.setTitle( "Wait List" );
		builder.setItems( menuList, this );
		waitListDialog = builder.create();
		waitListDialog.show();
	}
	
	private int getWhoHasInitiative() {
		int index = -1;
		for ( int i = 0; i < initList.size(); i++ ) {
			if ( initList.get( i ).hasInit ) {
				index = i;
			}
		}
		return index;
	}
	
	//////////////////////////////////////////////////////////////////////////
	// DATABASE
	//////////////////////////////////////////////////////////////////////////
	
	private String [] getPartiesFromDB() {
		Cursor cur;
		
		cur = db.getAllParties();
		
		String [] list = new String[ cur.getCount() ];
		int count = 0;
		while ( cur.moveToNext() ) {
			list[ count ] = cur.getString( 0 ); // there is only 1 column, so 0.
			count++;
		}
		cur.close();
		return list;
	}
	
	private void loadPartyMembersFromDB( String partyName ) {
		Cursor cur;
		
		cur = db.getPCForGroup( partyName );
		
		Moflow_PC critter;
		while ( cur.moveToNext() ) {
			critter = new Moflow_PC();
			critter.setName( cur.getString( 0 ) ); // get name column
			critter.setInitMod( cur.getInt( 1 ) ); // get init column
			critter.setArmorClass( cur.getInt( 2 ) ); // get ac column
			critter.setHitPoints( cur.getInt( 3 ) ); // get hp column
			critter.setCurrentHP( cur.getInt( 3 ) ); // set current hp same to max hp
			initList.add( critter );
		}
		cur.close();
	}
	
	private String [] getEncountersFromDB() {
		Cursor cur;
		
		cur = db.getAllEncounters();
		
		String [] list = new String[ cur.getCount() ];
		int count = 0;
		while ( cur.moveToNext() ) {
			list[ count ] = cur.getString( 0 ); // there is only 1 column, so 0.
			count++;
		}
		cur.close();
		return list;
	}
	
	private void loadEncounterMembersFromDB( String encName ) {
		Cursor cur;
		
		cur = db.getCreaturesForEncounter( encName );
		
		Moflow_Creature critter;
		while ( cur.moveToNext() ) {
			critter = new Moflow_Creature();
			critter.setName( cur.getString( 0 ) ); // get name column
			critter.setInitMod( cur.getInt( 1 ) ); // get init column
			critter.setArmorClass( cur.getInt( 2 ) ); // get ac column
			critter.setHitPoints( cur.getInt( 3 ) ); // get hp column
			critter.setCurrentHP( cur.getInt( 3 ) ); // set current hp same to max hp
			initList.add( critter );
		}
		cur.close();
	}
	
	private String [] getCatalogFromDB() {
		Cursor cur;
		
		cur = db.getCatalog();
		
		String [] list = new String[ cur.getCount() ];
		int count = 0;
		while( cur.moveToNext() ) {
			list[ count ] = cur.getString( 0 );
			count++;
		}
		cur.close();
		return list;
	}
	
	// load from catalog
	private void loadCreatureFromDB( String creatureName ) {
		Cursor cur;
		
		cur = db.getCreatureFromCatalog( creatureName );
		
		Moflow_Creature critter = null;
		while ( cur.moveToNext() ) {
			critter = new Moflow_Creature();
			critter.setName( cur.getString( 0 ) ); // get name column
			critter.setInitMod( cur.getInt( 1 ) ); // get init column
			critter.setArmorClass( cur.getInt( 2 ) ); // get ac column
			critter.setHitPoints( cur.getInt( 3 ) ); // get hp column
			critter.setCurrentHP( cur.getInt( 3 ) ); // set current hp same to max hp
			initList.add( critter );
		}
		cur.close();
	}
	
	private void saveInitToDB() {
		for ( int i = 0; i < initList.size(); i++ ) {
			db.insertItemFromInitiative( initList.get( i ) );
		}
	}
	
	private void loadListFromDB() {
		Cursor cur;
		final int INIT = 1;
		final int NAME = 2;
		final int INITBONUS = 3;
		final int AC = 4;
		final int CURHP = 5;
		final int MAXHP = 6;
		final int TYPE = 7;
		
		cur = db.getInitListFromDB();
		
		while ( cur.moveToNext() ) {
			Moflow_Creature creature = new Moflow_Creature();
			creature.setInitiative( cur.getInt( INIT ) );
			creature.setName( cur.getString( NAME ) );
			creature.setInitMod( cur.getInt( INITBONUS ) );
			creature.setArmorClass( cur.getInt( AC ) );
			creature.setCurrentHP( cur.getInt( CURHP ) );
			creature.setHitPoints( cur.getInt( MAXHP ) );
			creature.setAsMonster( cur.getInt( TYPE ) );
			
			initList.add( creature );
		}
		cur.close();
		adapter.notifyDataSetChanged();
	}

	
	//////////////////////////////////////////////////////////////////////////
	// LISTENERS
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onClick( View view ) {
		
	}

	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		
	}

	@Override
	public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ) {
		selectedItemPosition = position;
		return false;
	}

	@Override
	public void onClick( DialogInterface dialog, int which ) {
		final int NEW, SELECTION;
		final int PARTY, REMOVE_PC;
		final int ENCOUNTER, REMOVE_MONSTER;
		final int CATALOG, REMOVE_ALL;
		
		NEW = SELECTION = 0;
		PARTY = REMOVE_PC = 1;
		ENCOUNTER = REMOVE_MONSTER = 2;
		CATALOG = REMOVE_ALL = 3;
		
		if ( dialog == addOptionsDialog ) {
			if ( which == NEW )
				startFriendOrFoeDialog();
			else if ( which == PARTY )
				loadPartyList();
			else if ( which == ENCOUNTER )
				loadEncounterList();
			else if ( which == CATALOG )
				loadCatalogList();
		}
		
		else if ( dialog == removeOptionsDialog ) {
			if ( which == SELECTION )
				;
			else if ( which == REMOVE_PC )
				removeAllPCs();
			else if ( which == REMOVE_MONSTER )
				removeAllMonsters();
			else if ( which == REMOVE_ALL )
				removeAll();
		}
		
		else if ( dialog == partyListDialog ) {
			loadPartyMembersFromDB( menuList[ which ] );
			adapter.notifyDataSetChanged();
		}
		
		else if ( dialog == encounterListDialog ) {
			loadEncounterMembersFromDB( menuList[ which ] );
			adapter.notifyDataSetChanged();
		}
		
		else if ( dialog == catalogListDialog ) {
			loadCreatureFromDB( menuList[ which ] );
			adapter.notifyDataSetChanged();
		}
		else if ( dialog == itemDialog ) {
			addNewItemToList( which );
			adapter.notifyDataSetChanged();
		}
		else if ( dialog == friendFoeDialog ) {
			creatureType = which;
			itemDialog.show();
			prepareItemDialogForNewPC();
		}
		else if ( dialog == waitListDialog ) {
			int index = getWhoHasInitiative();
			
			if ( index > 1 && index < initList.size() ) {
				initList.add( index - 1, waitList.get( which ) );
				waitList.remove( which );
			}
			else if ( index == 0 ) {
				initList.add( 0, waitList.get( which ) );
				waitList.remove( which );
			}
			else if ( index == -1 ) {
				initList.add( waitList.get( which ) );
				waitList.remove( which );	
			}
			adapter.notifyDataSetChanged();
		}
		
	}
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
	    // Handle item selection
	    switch ( item.getItemId() ) {
	    	case R.id.menu_add:
	    		startAddMenu();
	    		break;
	    	case R.id.menu_sub:
	    		startRemoveMenu();
	    		break;
	    	case R.id.menu_wait:
	    		startWaitList();
	    		break;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
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
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.menu_moveUp:
	            
	            return true;
	        case R.id.menu_itemWait:
	            waitList.add( initList.get( selectedItemPosition ) );
	            initList.remove( selectedItemPosition );
	            adapter.notifyDataSetChanged();
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
}