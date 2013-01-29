package moflow.tracker;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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
public class PCM_EditParty extends ListActivity
{
	Button addPCBtn;
	Button editPCBtn;
	Button deletePCBtn;
	Button editDoneBtn;
	
	EditText partyNameField;
	
	ArrayList<Moflow_PC> pc_arrayList;
	ArrayAdapter<Moflow_PC> adapter;
	
	Dialog addDialog;
	Dialog editDialog;
	
	Moflow_Party party = null;
	Moflow_PC character = null;
	
	final int DIALOG_CREATEPC = 0;	// for onCreateDialog switch statement
	final int DIALOG_EDITPC = 1;	// for onCreateDialog switch statement
	final int DIALOG_DELETEPC = 2;	// for onCreateDialog switch statement
	
	final int RC_DONE = 1;	// result code returned when "Done" clicked
	final int RC_EXISTING_EDIT = 2;	// result code for editing existing party
	
	int checkedItemPosition = -1;
	
	boolean existingParty = false;
	boolean itemChecked = false;
	
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
		getListView().setChoiceMode( ListView.CHOICE_MODE_SINGLE );
		
		partyNameField = ( EditText ) findViewById( R.id.groupNameEditText );
		partyNameField.setHint( "Group Name" );
		
		addPCBtn = ( Button ) findViewById( R.id.addBtn );
		addPCBtn.setText( "Add PC" );
		editPCBtn = ( Button ) findViewById( R.id.editBtn );
		editPCBtn.setText( "Edit PC" );
		deletePCBtn = ( Button ) findViewById( R.id.delBtn );
		deletePCBtn.setText( "Delete PC" );
		editDoneBtn = ( Button ) findViewById( R.id.doneBtn );
		editDoneBtn.setText( "Done" );
		
		addPCBtn.setOnClickListener( 
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onAddButtonClick();
					}
				});
		editPCBtn.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick( View v ) {
						onEditButtonClick();
					}
				});
		deletePCBtn.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onDeleteButtonClick();
					}
				});
		editDoneBtn.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if ( onDoneButtonClick() )
							finish();
					}
				});
		
		loadParty();
	}
	
	/**-----------------------------------------------------------------------
	 * Shows check button when character in list is clicked.
	 */
	@Override
	public void onListItemClick( ListView parent, View v, int position, long id ) {
		character = (Moflow_PC) parent.getItemAtPosition( position );
		
		if ( checkedItemPosition == position && itemChecked == true ) {
			parent.setItemChecked( position, false );
			itemChecked = false;
			character = null;
		} else {
			parent.setItemChecked( position, true );
			itemChecked = true;
		}
			
		checkedItemPosition = position;
	}
	
	
	/**-----------------------------------------------------------------------
	 * Handles request code returned from PCM_CreatePC activity.
	 * @param requestCode request code to identify which activity result is
	 * being returned from.
	 * @param resultCode The integer result code returned by the child activity 
	 * through its setResult().
	 * @param data the data returned.
	 */
	public void onActivityResult( int requestCode, int resultCode, Intent data )
	{
	}
	
	/**-----------------------------------------------------------------------
	 * Prepare dialog
	 */
	protected void onPrepareDialog( int id, Dialog dialog, Bundle args ) {
		
		switch ( id ) {
			case DIALOG_CREATEPC:
				prepAddPCDialog( dialog );
				break;
			case DIALOG_EDITPC:
				prepEditPCDialog( dialog );
				break;
		}
	}
	
	/**-----------------------------------------------------------------------
	 * Prepare Add PC dialog
	 */
	private void prepAddPCDialog( Dialog dialog ) {
		EditText field;
		
		field = ( EditText ) dialog.findViewById( R.id.acEditText );
		field.setText( "0" );
		field = ( EditText ) dialog.findViewById( R.id.initEditText );
		field.setText( "0" );
		field = ( EditText ) dialog.findViewById( R.id.hpEditText );
		field.setText( "0" );
		field = ( EditText ) dialog.findViewById( R.id.nameEditText );
		field.setHint( "I need a name!" );
		field.requestFocus();
	}
	
	/**-----------------------------------------------------------------------
	 * Prepare Edit PC dialog
	 */
	private void prepEditPCDialog( Dialog dialog )
	{
		EditText field;
		
		field = ( EditText ) dialog.findViewById( R.id.acEditText );
		field.setText( String.valueOf( character.getAC() ) );
		field = ( EditText ) dialog.findViewById( R.id.initEditText );
		field.setText( String.valueOf( character.getInitMod() ) );
		field = ( EditText ) dialog.findViewById( R.id.hpEditText );
		field.setText( String.valueOf( character.getMaxHitPoints() ) );
		field = ( EditText ) dialog.findViewById( R.id.nameEditText );
		field.setText( character.getCharName() );
		field.requestFocus();
	}
	
	/**-----------------------------------------------------------------------
	 * Show dialog specified by the parameter value
	 */
	@Override
	protected Dialog onCreateDialog( int id ) {
		Dialog dialog = null;
		switch( id ) {
			case DIALOG_CREATEPC:
				dialog = addPCDialog();
				break;
			case DIALOG_EDITPC:
				dialog = editPCDialog();
				break;
			case DIALOG_DELETEPC:
				dialog = deletePCDialog();
				break;
			default:
				dialog = null;
		}
		return dialog;
	}
	
	/**-----------------------------------------------------------------------
	 * Initializes the Add PC dialog.
	 */
	private Dialog addPCDialog() {
		
		addDialog = new Dialog( this );
		
		addDialog.setContentView( R.layout.groupitem );
		addDialog.setTitle( "Add PC" );
		
		Button createDoneBtn = 
			( Button ) addDialog.findViewById( R.id.doneGroupItemButton );
		Button cancelCreateBtn = 
			( Button ) addDialog.findViewById( R.id.cancelGroupItemButton );
		
		createDoneBtn.setOnClickListener(
				new View.OnClickListener() {	
					@Override
					public void onClick(View v) {
						setPCStats( false, addDialog );
						
						party.addMember( character );
						adapter.add( character );
						
						addDialog.dismiss();
						adapter.notifyDataSetChanged();
						
						character = null;
					}
				});
		
		cancelCreateBtn.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) { 
						addDialog.dismiss();
						character = null;
					}
				});
		return addDialog;
	}
	
	/**-----------------------------------------------------------------------
	 * Initializes the Edit PC dialog.
	 */
	private Dialog editPCDialog() {
		
		editDialog = new Dialog( this );
		
		editDialog.setContentView( R.layout.groupitem );
		editDialog.setTitle( "Edit PC" );
		
		Button createDoneBtn = 
			( Button ) editDialog.findViewById( R.id.doneGroupItemButton );
		Button cancelCreateBtn = 
			( Button ) editDialog.findViewById( R.id.cancelGroupItemButton );
		
		createDoneBtn.setOnClickListener(
				new View.OnClickListener() {	
					@Override
					public void onClick(View v) {
						setPCStats( true, editDialog );
						
						// remove the edited PC from the list and reinsert it
						// back in with the changed values.
						party.RemovePC( character );
						party.addMember( character );
						
						editDialog.dismiss();
						adapter.notifyDataSetChanged();
						
						character = null;
					}
				});
		
		cancelCreateBtn.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) { 
						editDialog.dismiss();
						character = null;
					}
				});
		return editDialog;
	}
	
	/**-----------------------------------------------------------------------
	 * Confirm deletion of a PC from the list view
	 */
	private AlertDialog deletePCDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		builder.setMessage( "Delete this character?" );
		builder.setCancelable( false );
		builder.setPositiveButton( "Yes", 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						adapter.remove( character );
						party.RemovePC( character );
					}
				});
		
		builder.setNegativeButton( "No", 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						character = null;
					}
				});
		
		return builder.create();
	}
	
	/**-----------------------------------------------------------------------
	 * Set all the stats of the PC. This function should only be called
	 * when the user clicks the "Done" button in the Add PC dialog.
	 */
	private void setPCStats( boolean editExistingPC, Dialog diag )
	{
		if ( !editExistingPC )
			character = new Moflow_PC();
		
		setFieldsToZero( diag );
		
		EditText textField;
		
		textField = ( EditText ) diag.findViewById( R.id.nameEditText );
		character.setName( textField.getText().toString().trim() );
		
		textField = ( EditText ) diag.findViewById( R.id.acEditText );
		character.setArmorClass( Integer.parseInt( textField.getText().toString() ) );
		
		textField = ( EditText ) diag.findViewById( R.id.initEditText );
		character.setInitMod( Integer.parseInt( textField.getText().toString() ) );
		
		textField = ( EditText ) diag.findViewById( R.id.hpEditText );
		character.setHitPoints( Integer.parseInt( textField.getText().toString() ) );
	}
	
	/**-----------------------------------------------------------------------
	 * Sets all numerical value EditText fields to 0 so that Integer.parseInt
	 * in setPCStats() does not throw an invalid value exception.
	 */
	private void setFieldsToZero( Dialog dialog )
	{
		ArrayList< EditText > field = new ArrayList< EditText >();

		field.add( ( EditText ) dialog.findViewById( R.id.acEditText ) );
		field.add( ( EditText ) dialog.findViewById( R.id.initEditText ) );
		field.add( ( EditText ) dialog.findViewById( R.id.hpEditText ) );
		
		for ( int i = 0; i < field.size(); i++ )
		{
			if ( field.get( i ).getText().toString().trim().equals( "" ) )
				field.get( i ).setText( "0" );
		}
		
		EditText nameField = 
			( EditText ) dialog.findViewById( R.id.nameEditText );
		
		if ( nameField.getText().toString().trim().equals( "" ) )
			nameField.setText( "Nameless One" );
	}
	
	/**-----------------------------------------------------------------------
	 * Prepares an intent object with bundle when "Done" button is pressed
	 */
	private boolean onDoneButtonClick()
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
	 * Brings up the Delete Character dialog.
	 */
	private void onDeleteButtonClick() {
		if ( character != null ) { 
			showDialog( DIALOG_DELETEPC );
			getListView().setItemChecked( checkedItemPosition, false );
			itemChecked = false;
		}
		else
			Toast.makeText( PCM_EditParty.this, "Select a PC!", 
					Toast.LENGTH_SHORT ).show();
	}
	
	
	/**-----------------------------------------------------------------------
	 * Brings up dialog to edit a character.
	 */
	private void onEditButtonClick() {
		if ( character != null ) {
			showDialog( DIALOG_EDITPC );
			getListView().setItemChecked( checkedItemPosition, false );
			itemChecked = false;
		}
		else
			Toast.makeText( PCM_EditParty.this, "Select a PC!", 
					Toast.LENGTH_SHORT ).show();
	}
	
	
	/**-----------------------------------------------------------------------
	 * Brings up the dialog to create a character.
	 */
	private void onAddButtonClick() {
		showDialog( DIALOG_CREATEPC );
		
		getListView().setItemChecked( checkedItemPosition, false );
		itemChecked = false;
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
}