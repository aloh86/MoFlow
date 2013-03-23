package moflow.tracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.Random;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
/*
===============================================================================
InitiativeActivity.java

Keeps track of initiative.
===============================================================================
*/
import android.widget.Toast;

public class InitiativeActivity extends ListActivity 
implements OnClickListener, OnItemClickListener, OnItemLongClickListener, android.content.DialogInterface.OnClickListener, OnFocusChangeListener {
	
	private TextView roundsText;
	
	private Button prevButton;
	private Button nextButton;
	
	private AlertDialog itemEditDialog;
	private AlertDialog itemDialog;
	private AlertDialog addOptionsDialog;
	private AlertDialog partyListDialog;
	private AlertDialog encounterListDialog;
	private AlertDialog catalogListDialog;
	private AlertDialog friendFoeDialog;
	private AlertDialog removeOptionsDialog;
	private AlertDialog waitListDialog;
	private AlertDialog initiativeOptionsDialog;
	
	private ArrayList< Moflow_Creature > initList;
	private ArrayList< Moflow_Creature > waitList;
	
	private InitiativeAdapter adapter;
	
	private Moflow_Creature newCreature;
	
	private int creatureType; // 0 for Friendly, 1 for Hostile
	private int selectedItemPosition;
	
	private final String RETAIN_LIST_KEY = "listKey";
	
	String [] menuList; // used for lists opened by menu options
	
	MoFlowDB db;
	
	private int roundCount;
	
	// stuff for new item layout
	private View itemView;
	
	private EditText nameField;
	private EditText initField;
	private EditText acField;
	private EditText hpField;
	
	// stuff for edit item layout
	private View editView;
	
	private TextView nameLabel;
	
	private EditText initiativeEditText;
	private EditText curHPEditText;
	private EditText maxHPEditText;
	private EditText armorClassEditText;
	
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
		
		// initialize layout for editView
		editView = inflater.inflate( R.layout.init_item_edit, null );
		
		initiativeEditText = ( EditText ) editView.findViewById( R.id.initiativeEditText );
		curHPEditText = ( EditText ) editView.findViewById( R.id.curHPEditText );
		maxHPEditText = ( EditText ) editView.findViewById( R.id.maxHPEditText );
		armorClassEditText = ( EditText ) editView.findViewById( R.id.armorClassEditText );
		
		nameLabel = ( TextView ) editView.findViewById( R.id.nameLabel );
	}
	
	private void initializeDialogs() {
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		
		builder = new AlertDialog.Builder( this );
		builder.setView( itemView );
		builder.setPositiveButton( "OK", this );
		builder.setNegativeButton( "Cancel", this );
		itemDialog = builder.create();
		
		builder = new AlertDialog.Builder( this );
		builder.setView( editView );
		builder.setPositiveButton( "OK", this );
		itemEditDialog = builder.create();
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
		ListIterator< Moflow_Creature > waitLitr = waitList.listIterator();
		while( litr.hasNext() ) {
			if ( !litr.next().isCreature() ) {
				litr.remove();
			}
		}
		while( waitLitr.hasNext() ) {
			if ( !waitLitr.next().isCreature() ) {
				waitLitr.remove();
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	private void removeAllMonsters() {
		ListIterator< Moflow_Creature > litr = initList.listIterator();
		ListIterator< Moflow_Creature > waitLitr = waitList.listIterator();
		while( litr.hasNext() ) {
			if ( litr.next().isCreature() ) {
				litr.remove();
			}
		}
		while( waitLitr.hasNext() ) {
			if ( waitLitr.next().isCreature() ) {
				waitLitr.remove();
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	private void removeAll() {
		initList.clear();
		waitList.clear();
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
	
	private void moveItemUp() {
		if ( selectedItemPosition > 0 && selectedItemPosition < initList.size() ) {
			initList.add( selectedItemPosition - 1, initList.get( selectedItemPosition ) );
			initList.remove( selectedItemPosition + 1 );
		}
		adapter.notifyDataSetChanged();
	}
	
	private void moveItemDown() {
		if ( selectedItemPosition >= 0 && selectedItemPosition < initList.size() - 1 ) {
			initList.add( selectedItemPosition, initList.get( selectedItemPosition + 1 ) );
			initList.remove( selectedItemPosition + 2 );
		}
		adapter.notifyDataSetChanged();
	}
	
	private void removeItem() {
		initList.remove( selectedItemPosition );
		adapter.notifyDataSetChanged();
	}
	
	private void prepareItemEditDialog() {
		Moflow_Creature creature = initList.get( selectedItemPosition );
		nameLabel.setText( creature.creatureName );
		initiativeEditText.setText( String.valueOf( creature.initiative ) );
		curHPEditText.setText( String.valueOf( creature.currentHP ) );
		maxHPEditText.setText( String.valueOf( creature.hitPoints ) );
		armorClassEditText.setText( String.valueOf( creature.armorClass ) );
	}
	
	private void setNewStats() {
		Moflow_Creature creature = initList.get( selectedItemPosition );
		if ( initiativeEditText.getText().toString().equals( "" ) )
			initiativeEditText.setText( String.valueOf( creature.initiative ) );
		if ( curHPEditText.getText().toString().equals( "" ) )
			curHPEditText.setText( String.valueOf( creature.currentHP ) );
		if ( maxHPEditText.getText().toString().equals( "" ) )
			maxHPEditText.setText( String.valueOf( creature.hitPoints ) );
		if ( armorClassEditText.getText().toString().equals( "" ) )
			armorClassEditText.setText( String.valueOf( creature.armorClass ) );
		
		creature.initiative = Integer.valueOf( initiativeEditText.getText().toString() );
		creature.currentHP  = Integer.valueOf( curHPEditText.getText().toString() );
		creature.hitPoints  = Integer.valueOf( maxHPEditText.getText().toString() );
		creature.armorClass = Integer.valueOf( armorClassEditText.getText().toString() );
	}
	
	private void startInitMenu() {
		String [] menu = { "Restart", "Sort Ascending", "Sort Descending", "Auto Roll Players", "Auto Roll Monsters", "Auto Roll All" };
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		builder.setTitle( "Initiative" );
		builder.setItems( menu, this );
		initiativeOptionsDialog = builder.create();
		initiativeOptionsDialog.show();
	}
	
	private void autoRollPlayers() {
		Random rand = new Random();
		for ( int i = 0; i < initList.size(); i++ ) {
			if ( !initList.get( i ).isMonster ) {
				Moflow_Creature critter = initList.get( i );
				critter.initiative = rand.nextInt( 20 ) + critter.initMod + 1;
			}
		}
		sortList( true );
	}
	
	private void autoRollMonsters() {
		Random rand = new Random();
		for ( int i = 0; i < initList.size(); i++ ) {
			if ( initList.get( i ).isMonster ) {
				Moflow_Creature critter = initList.get( i );
				critter.initiative = rand.nextInt( 20 ) + critter.initMod + 1;
			}
		}
		sortList( true );
	}
	
	private void autoRollAll() {
		Random rand = new Random();
		for ( int i = 0; i < initList.size(); i++ ) {
			Moflow_Creature critter = initList.get( i );
			critter.initiative = rand.nextInt( 20 ) + critter.initMod + 1;
		}
		sortList( true );
	}
	
	private void sortList( boolean descending ) {
		Comparator< Moflow_Creature > comparator;
		if ( descending ) {
			comparator = Collections.reverseOrder();
			Collections.sort( initList, comparator );
		} else {
			Collections.sort( initList );
		}
	}
	
	private void startInitiative() {
		roundCount = 1;
		roundsText.setText( String.valueOf( roundCount ) );
		initList.get( 0 ).setHasInit( true );
	}
	
	private void addItemToWaitList() {
		if ( initList.get( selectedItemPosition ).hasInit ) {
			if ( selectedItemPosition >= 0 && selectedItemPosition < initList.size() - 1 ) {
				initList.get( selectedItemPosition ).hasInit = false;
				initList.get( selectedItemPosition + 1 ).hasInit = true;
			}
			else if ( selectedItemPosition == initList.size() - 1 ) {
				initList.get( selectedItemPosition ).hasInit = false;
				initList.get( 0 ).hasInit = true;
			}
			waitList.add( initList.get( selectedItemPosition ) );
	        initList.remove( selectedItemPosition );
	        adapter.notifyDataSetChanged();
		}
		else
			Toast.makeText( this, "Must have initiative to wait", Toast.LENGTH_LONG ).show();
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
		for ( int i = 0; i < waitList.size(); i++ ) {
			db.insertItemFromInitiative( waitList.get( i ) );
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
		int index = getWhoHasInitiative();
		
		if ( index == -1 && !initList.isEmpty() ) {
			initList.get( 0 ).hasInit = true;
			roundCount = 1;
			index = 0;
			roundsText.setText( String.valueOf( roundCount ) );
		}
		
		else if ( view == nextButton && !initList.isEmpty() ) {
			initList.get( index ).hasInit = false;
			if ( index == initList.size() - 1 ) {
				initList.get( 0 ).hasInit = true;
				roundCount += 1;
			}
			else
				initList.get( index + 1 ).hasInit = true;
			
			if ( index != initList.size() - 1 )
				this.getListView().smoothScrollToPosition( index + 1 );
			else
				this.getListView().smoothScrollToPosition( 0 );
			
			roundsText.setText( String.valueOf( roundCount ) );
		}
		
		else if ( view == prevButton && !initList.isEmpty() ) {
			index = getWhoHasInitiative();
			initList.get( index ).hasInit = false;
			if ( index == 0 ) {
				if ( roundCount <= 1 ) {
					roundCount = 1;
					initList.get( 0 ).hasInit = true;
				} else {
					initList.get( initList.size() - 1 ).hasInit = true;
					roundCount -= 1;
				}
			}
			else
				initList.get( index - 1 ).hasInit = true;
			
			if ( index != 0 )
				this.getListView().smoothScrollToPosition( index - 1 );
			else
				this.getListView().smoothScrollToPosition( initList.size() - 1 );
			
			roundsText.setText( String.valueOf( roundCount ) );
		}
		
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		selectedItemPosition = position;
		itemEditDialog.show();
		prepareItemEditDialog();
	}

	@Override
	public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ) {
		selectedItemPosition = position;
		return false;
	}

	@Override
	public void onClick( DialogInterface dialog, int which ) {
		final int NEW, PARTY, ENCOUNTER, CATALOG;
		final int REMOVE_PC, REMOVE_MONSTER, REMOVE_ALL;
		final int START, SORT_ASC, SORT_DESC, AUTO_PCS, AUTO_MONSTERS, AUTO_ALL;
		
		NEW = 0;
		PARTY = 1;
		ENCOUNTER = 2;
		CATALOG = 3;
		
		REMOVE_PC = 0;
		REMOVE_MONSTER = 1;
		REMOVE_ALL = 2;
		
		START = 0;
		SORT_ASC = 1;
		SORT_DESC = 2;
		AUTO_PCS = 3;
		AUTO_MONSTERS = 4;
		AUTO_ALL = 5;
		
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
			if ( which == REMOVE_PC )
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
			
			if ( index >= 1 && index < initList.size() ) {
				initList.add( index, waitList.get( which ) );
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
		
		else if ( dialog == itemEditDialog ) {
			setNewStats();
			adapter.notifyDataSetChanged();
		}
		
		else if ( dialog == initiativeOptionsDialog ) {
			int index = getWhoHasInitiative();
			
			if ( which == START ) {
				initList.get( index ).hasInit = false;
				startInitiative();
			}
			else if ( which == SORT_ASC )
				sortList( false );
			else if ( which == SORT_DESC )
				sortList( true );
			else if ( which == AUTO_PCS )
				autoRollPlayers();
			else if ( which == AUTO_MONSTERS )
				autoRollMonsters();
			else if ( which == AUTO_ALL )
				autoRollAll();
			
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
	    // Handle item selection
	    switch ( item.getItemId() ) {
	    	case R.id.menu_add:
	    		startAddMenu();
	    		return true;
	    	case R.id.menu_sub:
	    		startRemoveMenu();
	    		return true;
	    	case R.id.menu_wait:
	    		startWaitList();
	    		return true;
	    	case R.id.menu_initiative:
	    		startInitMenu();
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
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
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.menu_moveUp:
	            moveItemUp();
	            return true;
	        case R.id.menu_itemWait:
	        	addItemToWaitList();
	            return true;
	        case R.id.menu_itemRemove:
	        	removeItem();
	        	return true;
	        case R.id.menu_moveDown:
	        	moveItemDown();
	        	return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
}
