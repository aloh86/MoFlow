package moflow.tracker;

import java.util.ArrayList;

import moflow.database.MoFlowDB;
import android.app.AlertDialog;
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
can add items to encounter from scratch or from the creature catalog. Items
added from scratch 
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
	
	// Members for the catalog dialog
	private View catalogView;
	
	private EditText amountField;
	private ListView catalogListView;
	
	//////////////////////////////////////////////////////////////////////////
	// ACTIVITY OVERRIDES
	//////////////////////////////////////////////////////////////////////////
	
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.group );
		
		// always call the following three functions in order because 
		// loadCreatures() and initializeDialogs() depends on some variables
		// initialized in initializeLayout().
		initializeLayout();
		loadCreatures();
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
		
		// initialize views for the catalog dialog
		catalogView = inflater.inflate( R.layout.catalog_dialog, null );
		
		amountField = ( EditText ) catalogView.findViewById( R.id.creatureAmountEditText );
		amountField.setText( "1" );
		
		initializeCatalogList();
		catalogAdapter = new ArrayAdapter< String >( this, R.layout.catalog_dialog, catalogList );
		catalogListView = ( ListView ) catalogView.findViewById( R.id.catalogList );
		catalogListView.setAdapter( catalogAdapter );
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
		builder.setView( catalogView );
		builder.setTitle( "Creature Catalog" );
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
	
	
	//////////////////////////////////////////////////////////////////////////
	// VIEW/LISTENER HELPERS
	//////////////////////////////////////////////////////////////////////////
	
	//////////////////////////////////////////////////////////////////////////
	// LISTENERS
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onClick( View view ) {
		if ( view == addButton ) {
			
		}
	}

	@Override
	public void onFocusChange( View v, boolean hasFocus ) {
		
	}

	@Override
	public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ) {
		
		return false;
	}

	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		
	}

	@Override
	public void onClick( DialogInterface dialog, int button ) {
		
	}
}
