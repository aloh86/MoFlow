package moflow.tracker;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
public class PCM_EditParty extends ListActivity implements OnClickListener, android.content.DialogInterface.OnClickListener
{
	Button addPCBtn;
	Button saveBtn;
	
	EditText partyNameField;
	
	ArrayList<Moflow_PC> pc_arrayList;
	ArrayAdapter<Moflow_PC> adapter;
	
	AlertDialog createPCDialog;
	AlertDialog editPCDialog;
	AlertDialog deletePCDialog;
	
	Dialog addDialog;
	Dialog editDialog;
	
	Moflow_Party party = null;
	Moflow_PC character = null;
	
	View dialogView;
	
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
		
		loadParty();
	}
	
	private void initDialogs()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		LayoutInflater inflater = this.getLayoutInflater();
		dialogView = inflater.inflate( R.layout.groupitem, null );
		builder.setView( dialogView );
		builder.setPositiveButton( "OK", this );
		builder.setNegativeButton( "Cancel", this );
		createPCDialog = builder.create();
	}
	
	/**-----------------------------------------------------------------------
	 * Event handler for buttons.
	 */
	@Override
	public void onClick( View view ) {
		if ( view == addPCBtn )
			createPCDialog.show();
		if ( view == saveBtn ) {
			onSaveButtonClick();
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
				//dialog = addPCDialog();
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
	 * Initializes the Edit PC dialog.
	 */
	private Dialog editPCDialog() {
		
		editDialog = new Dialog( this );
		
		editDialog.setContentView( R.layout.groupitem );
		editDialog.setTitle( "Edit PC" );
		
		Button createDoneBtn = 
			( Button ) editDialog.findViewById( 1 );
		Button cancelCreateBtn = 
			( Button ) editDialog.findViewById( 1 );
		
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
		
		textField = ( EditText ) dialogView.findViewById( R.id.nameEditText );
		character.setName( textField.getText().toString().trim() );
		
		textField = ( EditText ) dialogView.findViewById( R.id.acEditText );
		character.setArmorClass( Integer.parseInt( textField.getText().toString() ) );
		
		textField = ( EditText ) dialogView.findViewById( R.id.initEditText );
		character.setInitMod( Integer.parseInt( textField.getText().toString() ) );
		
		textField = ( EditText ) dialogView.findViewById( R.id.hpEditText );
		character.setHitPoints( Integer.parseInt( textField.getText().toString() ) );
	}
	
	/**-----------------------------------------------------------------------
	 * Sets all numerical value EditText fields to 0 so that Integer.parseInt
	 * in setPCStats() does not throw an invalid value exception.
	 */
	private void setFieldsToZero( Dialog dialog )
	{
		ArrayList< EditText > field = new ArrayList< EditText >();

		field.add( ( EditText ) dialogView.findViewById( R.id.acEditText ) );
		field.add( ( EditText ) dialogView.findViewById( R.id.initEditText ) );
		field.add( ( EditText ) dialogView.findViewById( R.id.hpEditText ) );
		
		for ( int i = 0; i < field.size(); i++ )
		{
			if ( field.get( i ).getText().toString().trim().equals( "" ) )
				field.get( i ).setText( "0" );
		}
		
		EditText nameField = 
			( EditText ) dialogView.findViewById( R.id.nameEditText );
		
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
			setPCStats( false, addDialog );
			party.addMember( character );
			adapter.add( character );	
			adapter.notifyDataSetChanged();
			character = null;
			dialog.dismiss();
		}
		else if ( button == DialogInterface.BUTTON_NEGATIVE ) {
			character = null;
			dialog.dismiss();
		}
	}
}