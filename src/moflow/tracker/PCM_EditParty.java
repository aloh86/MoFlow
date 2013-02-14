package moflow.tracker;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
/*
===============================================================================
PCM_EditParty.java
Alex Oh

Activity shows the current list of parties.
===============================================================================
*/
public class PCM_EditParty extends ListActivity 
implements OnClickListener, android.content.DialogInterface.OnClickListener, OnItemLongClickListener, TextWatcher
{
	private Button addPCBtn;
	private Button saveBtn;
	
	private EditText partyNameField;
	private EditText itemNameField;
	
	private ArrayList<Moflow_PC> pc_arrayList;
	private ArrayAdapter<Moflow_PC> adapter;
	
	private AlertDialog itemDialog;
	private AlertDialog deletePCDialog;
	
	private Moflow_Party party = null;
	private Moflow_PC character = null;
	
	private View itemView;
	
	private final int RC_DONE = 1;	// result code returned when "Done" clicked
	private final int RC_EXISTING_EDIT = 2;	// result code for editing existing party
	
	private final String RETAIN_PARTY_KEY = "retainedParty";
	
	private boolean existingParty = false;
	private boolean editingItem = false;
	private boolean addingNewItem = false;
	
	/**-----------------------------------------------------------------------
	 * Initializes View and View properties.
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.new_group );
		
		party = new Moflow_Party();
		
		// setup adapter for list view
		pc_arrayList = new ArrayList<Moflow_PC>();
		adapter = new ArrayAdapter<Moflow_PC>( this, R.layout.list_item, pc_arrayList );
		setListAdapter( adapter );
		
		// setup group name field
		partyNameField = ( EditText ) findViewById( R.id.groupNameEditText );
		partyNameField.setHint( "Group Name" );
		
		// setup "Add PC" button
		addPCBtn = ( Button ) findViewById( R.id.addBtn );
		addPCBtn.setText( "Add PC" );
		addPCBtn.setOnClickListener( this );
		
		// setup "Save" button
		saveBtn = ( Button ) findViewById( R.id.saveBtn );
		saveBtn.setText( "Save" );
		saveBtn.setOnClickListener( this );
		
		this.getListView().setOnItemLongClickListener( this );
		
		loadParty();
		initDialogsMenus();
	}
	
	/**-----------------------------------------------------------------------
	 * Save party for orientation changes
	 */
	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		outState.putParcelable( RETAIN_PARTY_KEY, party );
	}
	
	/**-----------------------------------------------------------------------
	 * Reload party on orientation change
	 */
	@Override
	public void onRestoreInstanceState( Bundle savedInstanceState ) {
		super.onRestoreInstanceState( savedInstanceState );
		party = savedInstanceState.getParcelable( RETAIN_PARTY_KEY );
		
		if ( party != null ) {
			for ( int i = 0; i < party.getPartySize(); i++ ) {
				adapter.add( party.getMember( i ) );
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	/**-----------------------------------------------------------------------
	 * Creation of context menu
	 */
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo ) {
		super.onCreateContextMenu( menu, v, menuInfo );
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.edit_del_prompt, menu );
	}
	
	/**-----------------------------------------------------------------------
	 * Initialize dialog menus.
	 */
	private void initDialogsMenus()
	{	
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		LayoutInflater inflater = this.getLayoutInflater();
		
		// setup the dialog for adding PCs to a group
		itemView = inflater.inflate( R.layout.groupitem, null );
		builder.setView( itemView );
		builder.setMessage( "Create Party Member" );
		builder.setPositiveButton( "OK", this );
		builder.setNegativeButton( "Cancel", this );
		itemDialog = builder.create();
		
		// setup Views for addPCView dialog
		itemNameField = ( EditText ) itemView.findViewById( R.id.nameEditText );
		itemNameField.addTextChangedListener( this );
		
		// setup the dialog for PC deletion
		builder = new AlertDialog.Builder( this );
		builder.setMessage( "Delete this PC?" );
		builder.setPositiveButton( "Yes", this );
		builder.setNegativeButton( "No", this );
		deletePCDialog = builder.create();
		
		// setup the edit-delete context menu
		this.registerForContextMenu( this.getListView() );
	}
	
	
	/**-----------------------------------------------------------------------
	 * Event handler for buttons.
	 */
	@Override
	public void onClick( View view ) {
		if ( view == addPCBtn ) {
			prepAddPCDialog();
			addingNewItem = true;
			itemDialog.show();
			//itemDialog.getButton( AlertDialog.BUTTON1 ).setEnabled( false );
		}
		if ( view == saveBtn ) {
			if ( onSaveButtonClick() )
				finish();
		}
	}

	/**-----------------------------------------------------------------------
	 * Event handler for dialog buttons.
	 */
	@Override
	public void onClick( DialogInterface dialog, int which ) {
		if ( editingItem )
			editPCChoice( dialog, which );
		else if ( addingNewItem )
			createPCChoice( dialog, which );
		else if ( dialog == deletePCDialog )
			deletePCChoice( dialog, which );
	}
	
	/**-----------------------------------------------------------------------
	 * Event handler for long clicks
	 */
	@Override
	public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ) {
		character = adapter.getItem( position );
		return false;
	}
	
	/**-----------------------------------------------------------------------
	 * Handle context menu items
	 */
	@Override
	public boolean onContextItemSelected( MenuItem item ) {
		switch( item.getItemId() ) {
			case R.id.cmenuEdit:
				prepEditPCDialog();
				editingItem = true;
				itemDialog.show();
				break;
			case R.id.cmenuDelete:
				deletePCDialog.show();
				break;
			default:
				return super.onContextItemSelected( item );
		}
		return false;
	}
	
	/**-----------------------------------------------------------------------
	 * TextWatcher methods
	 */
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		boolean unique = true;
		
	}
	
	
	/**-----------------------------------------------------------------------
	 * Prepare Add PC dialog
	 */
	private void prepAddPCDialog( ) {
		EditText field;
		
		field = ( EditText ) itemView.findViewById( R.id.acEditText );
		field.setText( "0" );
		field = ( EditText ) itemView.findViewById( R.id.initEditText );
		field.setText( "0" );
		field = ( EditText ) itemView.findViewById( R.id.hpEditText );
		field.setText( "0" );
		field = ( EditText ) itemView.findViewById( R.id.nameEditText );
		field.setText( "" );
		field.setHint( "I need a unique name!" );
		field.requestFocus();
	}
	
	/**-----------------------------------------------------------------------
	 * Prepare Edit PC dialog
	 */
	private void prepEditPCDialog( )
	{
		EditText field;
		
		field = ( EditText ) itemView.findViewById( R.id.acEditText );
		field.setText( String.valueOf( character.getAC() ) );
		field = ( EditText ) itemView.findViewById( R.id.initEditText );
		field.setText( String.valueOf( character.getInitMod() ) );
		field = ( EditText ) itemView.findViewById( R.id.hpEditText );
		field.setText( String.valueOf( character.getMaxHitPoints() ) );
		field = ( EditText ) itemView.findViewById( R.id.nameEditText );
		field.setText( character.getCharName() );
		field.requestFocus();
	}
	
	/**-----------------------------------------------------------------------
	 * Set all the stats of the PC. This function should only be called
	 * when the user clicks the "Done" button in the Add PC dialog.
	 */
	private void setPCStats( boolean editExistingPC )
	{
		if ( !editExistingPC )
			character = new Moflow_PC();
		
		setFieldsToZero( itemView );
		
		EditText textField;
		
		textField = ( EditText ) itemView.findViewById( R.id.nameEditText );
		character.setName( textField.getText().toString().trim() );
		
		textField = ( EditText ) itemView.findViewById( R.id.acEditText );
		character.setArmorClass( Integer.parseInt( textField.getText().toString() ) );
		
		textField = ( EditText ) itemView.findViewById( R.id.initEditText );
		character.setInitMod( Integer.parseInt( textField.getText().toString() ) );
		
		textField = ( EditText ) itemView.findViewById( R.id.hpEditText );
		character.setHitPoints( Integer.parseInt( textField.getText().toString() ) );
	}
	
	/**-----------------------------------------------------------------------
	 * Sets all numerical value EditText fields to 0 so that Integer.parseInt
	 * in setPCStats() does not throw an invalid value exception.
	 */
	private void setFieldsToZero( View view )
	{
		ArrayList< EditText > field = new ArrayList< EditText >();

		field.add( ( EditText ) view.findViewById( R.id.acEditText ) );
		field.add( ( EditText ) view.findViewById( R.id.initEditText ) );
		field.add( ( EditText ) view.findViewById( R.id.hpEditText ) );
		
		for ( int i = 0; i < field.size(); i++ )
		{
			if ( field.get( i ).getText().toString().trim().equals( "" ) )
				field.get( i ).setText( "0" );
		}
		
		EditText nameField = 
			( EditText ) view.findViewById( R.id.nameEditText );
		
		if ( nameField.getText().toString().trim().equals( "" ) )
			nameField.setText( "Nameless One" );
	}
	
	/**-----------------------------------------------------------------------
	 * Prepares an intent object with bundle when "Save" button is pressed
	 */
	private boolean onSaveButtonClick()
	{
		// prompt the user for a party name if none is provided
		if ( !partyNameField.getText().toString().trim().equals("") )
			partyNameField.setText( partyNameField.getText().toString() );
		else
		{
			Toast.makeText( PCM_EditParty.this, "Unique Party Name Needed", 
					Toast.LENGTH_SHORT ).show();
			
			return false;
		}
		
		// if there are no members in the party, prompt user to add a member
		if ( party.getPartySize() == 0 ) {
			Toast.makeText( PCM_EditParty.this, 
					"You need at least 1 party member", 
					Toast.LENGTH_SHORT ).show();
			
			return false;
		}
		
		// package the party into bundle
		party.setPartyName( partyNameField.getText().toString().trim() );
		Intent i = new Intent();
		Bundle extras = new Bundle();
		extras.putParcelable( "partyData", party );
		i.putExtras( extras );
		
		if ( !existingParty )
			setResult( RC_DONE, i );
		else
			setResult( RC_EXISTING_EDIT, i );
		
		return true;
	}
	
	/**-----------------------------------------------------------------------
	 * List party members if intent was sent to this activity
	 */
	private void loadParty() {
		Bundle extras = getIntent().getExtras();
		
		if ( extras != null ) {
			party = extras.getParcelable( "party" );
			
			partyNameField.setText( party.getPartyName() );
			
			for ( int i = 0; i < party.getPartySize(); i++ )
				adapter.add( party.getMember( i ) );
			
			adapter.notifyDataSetChanged();
			existingParty = true;
		}
	}
	
	/**-----------------------------------------------------------------------
	 * Ensure the uniqueness of PC names.
	 * @param view the add or edit pc dialog view
	 * @return true if name is unique or party is empty, false otherwise or if empty
	 */
	private boolean pcNameIsUnique( View view ) {
		Toast.makeText( this, "inOnKey", Toast.LENGTH_SHORT ).show();
		if ( party.getPartySize() == 0 )
			return true;
		
		EditText nameField = ( EditText )view.findViewById( R.id.nameEditText );
		String charName = nameField.getText().toString();
		
		if ( charName.equals( "" ) )
			return false;
		
		boolean unique = true;
		
		// go through the party list and check if there are name conflicts
		for ( int i = 0; i < party.getPartySize(); i++ ) {
			String memberName = party.getMember( i ).getCharName();
			
			if ( charName.equals( memberName ) ) {
				unique = false;
				break;
			}
		}
		return unique;
	}
	
	/**-----------------------------------------------------------------------
	 * 
	 */
	private void createPCChoice( DialogInterface dialog, int button ) {
		if ( button == DialogInterface.BUTTON_POSITIVE ) {
			setPCStats( false );
			party.addMember( character );
			adapter.add( character );	
			adapter.notifyDataSetChanged();
		}
		character = null;
		addingNewItem = false;
	}
	
	/**-----------------------------------------------------------------------
	 * 
	 */
	private void editPCChoice( DialogInterface dialog, int button ) {
		if ( button == DialogInterface.BUTTON_POSITIVE ) {	
			setPCStats( true );
			party.RemovePC( character );
			party.addMember( character );
			adapter.notifyDataSetChanged();
		}
		character = null;
		editingItem = false;
	}
	
	/**-----------------------------------------------------------------------
	 * 
	 */
	private void deletePCChoice( DialogInterface dialog, int button ) {
		if ( button == DialogInterface.BUTTON_POSITIVE ) {
			adapter.remove( character );
			party.RemovePC( character );
		}
		else if ( button == DialogInterface.BUTTON_NEGATIVE )
			dialog.dismiss();
	}
}