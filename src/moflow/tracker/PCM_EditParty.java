package moflow.tracker;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/*
===============================================================================
PCM_EditParty.java
Alex Oh

Activity shows the current list of parties.
===============================================================================
*/
public class PCM_EditParty extends ListActivity implements OnClickListener, android.content.DialogInterface.OnClickListener, OnItemLongClickListener
{
	Button addPCBtn;
	Button saveBtn;
	
	EditText partyNameField;
	
	ListView editDelList;
	
	ArrayList<Moflow_PC> pc_arrayList;
	ArrayAdapter<Moflow_PC> adapter;
	
	AlertDialog createPCDialog;
	AlertDialog editPCDialog;
	AlertDialog deletePCDialog;
	
	Moflow_Party party = null;
	Moflow_PC character = null;
	
	View addPCView;
	View editPCView;
	
	final int DIALOG_CREATEPC = 0;	// for onCreateDialog switch statement
	final int DIALOG_EDITPC = 1;	// for onCreateDialog switch statement
	final int DIALOG_DELETEPC = 2;	// for onCreateDialog switch statement
	
	final int RC_DONE = 1;	// result code returned when "Done" clicked
	final int RC_EXISTING_EDIT = 2;	// result code for editing existing party
	
	boolean existingParty = false;
	
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
	
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo ) {
		super.onCreateContextMenu( menu, v, menuInfo );
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.edit_del_prompt, menu );
	}
	
	private void initDialogsMenus()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		LayoutInflater inflater = this.getLayoutInflater();
		
		// setup the dialog for adding PCs to a group
		addPCView = inflater.inflate( R.layout.groupitem, null );
		builder.setView( addPCView );
		builder.setPositiveButton( "OK", this );
		builder.setNegativeButton( "Cancel", this );
		createPCDialog = builder.create();
		
		// setup the dialog for editing PCs.
		builder = new AlertDialog.Builder( this );
		editPCView = inflater.inflate( R.layout.groupitem, null );
		builder.setView( editPCView );
		builder.setPositiveButton( "OK", this );
		builder.setNegativeButton( "Cancel", this );
		editPCDialog = builder.create();
		
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
			createPCDialog.show();
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
		if ( dialog == createPCDialog )
			createPCChoice( dialog, which );
		else if ( dialog == editPCDialog )
			editPCChoice( dialog, which );
		else if ( dialog == deletePCDialog )
			deletePCChoice( dialog, which );
	}
	
	/**-----------------------------------------------------------------------
	 * Event handler for long clicks
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id ) {
		character = adapter.getItem( position );
		return false;
	}
	
	@Override
	public boolean onContextItemSelected( MenuItem item ) {
		AdapterContextMenuInfo info = ( AdapterContextMenuInfo ) item.getMenuInfo();
		switch( item.getItemId() ) {
			case R.id.cmenuEdit:
				prepEditPCDialog();
				editPCDialog.show();
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
	 * Prepare dialog
	 */
	protected void onPrepareDialog( int id, Dialog dialog, Bundle args ) {
		
		switch ( id ) {
			case DIALOG_CREATEPC:
				prepAddPCDialog(  );
				break;
			case DIALOG_EDITPC:
				prepEditPCDialog();
				break;
		}
	}
	
	/**-----------------------------------------------------------------------
	 * Prepare Add PC dialog
	 */
	private void prepAddPCDialog( ) {
		EditText field;
		
		field = ( EditText ) addPCView.findViewById( R.id.acEditText );
		field.setText( "0" );
		field = ( EditText ) addPCView.findViewById( R.id.initEditText );
		field.setText( "0" );
		field = ( EditText ) addPCView.findViewById( R.id.hpEditText );
		field.setText( "0" );
		field = ( EditText ) addPCView.findViewById( R.id.nameEditText );
		field.setText( "" );
		field.setHint( "I need a name!" );
		field.requestFocus();
	}
	
	/**-----------------------------------------------------------------------
	 * Prepare Edit PC dialog
	 */
	private void prepEditPCDialog( )
	{
		EditText field;
		
		field = ( EditText ) editPCView.findViewById( R.id.acEditText );
		field.setText( String.valueOf( character.getAC() ) );
		field = ( EditText ) editPCView.findViewById( R.id.initEditText );
		field.setText( String.valueOf( character.getInitMod() ) );
		field = ( EditText ) editPCView.findViewById( R.id.hpEditText );
		field.setText( String.valueOf( character.getMaxHitPoints() ) );
		field = ( EditText ) editPCView.findViewById( R.id.nameEditText );
		field.setText( character.getCharName() );
		field.requestFocus();
	}
	
	/**-----------------------------------------------------------------------
	 * Set all the stats of the PC. This function should only be called
	 * when the user clicks the "Done" button in the Add PC dialog.
	 */
	private void setPCStats( boolean editExistingPC, View view )
	{
		if ( !editExistingPC )
			character = new Moflow_PC();
		
		setFieldsToZero( view );
		
		EditText textField;
		
		textField = ( EditText ) view.findViewById( R.id.nameEditText );
		character.setName( textField.getText().toString().trim() );
		
		textField = ( EditText ) view.findViewById( R.id.acEditText );
		character.setArmorClass( Integer.parseInt( textField.getText().toString() ) );
		
		textField = ( EditText ) view.findViewById( R.id.initEditText );
		character.setInitMod( Integer.parseInt( textField.getText().toString() ) );
		
		textField = ( EditText ) view.findViewById( R.id.hpEditText );
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
		if ( !partyNameField.getText().toString().trim().equals("") )
			partyNameField.setText( partyNameField.getText().toString() );
		else
		{
			Toast.makeText( PCM_EditParty.this, "Unique Party Name Needed", 
					Toast.LENGTH_SHORT ).show();
			
			return false;
		}
		
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
	
	private void createPCChoice( DialogInterface dialog, int button ) {
		if ( button == DialogInterface.BUTTON_POSITIVE ) {
			setPCStats( false, addPCView );
			party.addMember( character );
			adapter.add( character );	
			adapter.notifyDataSetChanged();
			dialog.dismiss();
		}
		else if ( button == DialogInterface.BUTTON_NEGATIVE )
			dialog.dismiss();
		
		character = null;
	}
	
	private void editPCChoice( DialogInterface dialog, int button ) {
		if ( button == DialogInterface.BUTTON_POSITIVE ) {
			setPCStats( true, editPCView );
			party.RemovePC( character );
			party.addMember( character );
			dialog.dismiss();
			adapter.notifyDataSetChanged();
		}
		else if ( button == DialogInterface.BUTTON_NEGATIVE )
			dialog.dismiss();
		
		character = null;
	}
	
	private void deletePCChoice( DialogInterface dialog, int button ) {
		if ( button == DialogInterface.BUTTON_POSITIVE ) {
			adapter.remove( character );
			party.RemovePC( character );
		}
		else if ( button == DialogInterface.BUTTON_NEGATIVE )
			dialog.dismiss();
	}
}