package moflow.activities;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.view.*;

import android.os.Bundle;
import android.widget.*;
import moflow.dialogs.NameDialogFragment;
import moflow.dialogs.SimpleDialogListener;
import moflow.utility.Key;
import moflow.utility.DBTransaction;
import moflow.utility.NameModifier;

import java.util.ArrayList;
import java.util.Collections;

/*
===============================================================================
GroupListActivity.java

Activity for listing the existing parties/encounters. The user can create a new
party/encounter and edit or delete an existing party/encounter.
===============================================================================
*/
public class GroupListActivity extends ListActivity implements AdapterView.OnItemClickListener, SimpleDialogListener, AbsListView.MultiChoiceModeListener
{
    private DBTransaction dbTransaction;
    private boolean editMode;
    private boolean deleteMode;
    private ArrayList< String > groupList;
    private ArrayAdapter< String > listAdapter;
    private ArrayList< String > deleteList;
    private int indexOfItemToEdit;
    private NameDialogFragment renameDialog;
    private NameDialogFragment newGroupDialog;
    private String groupType;

	@Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        try {
            groupType = getIntent().getExtras().getString( Key.GROUP_TYPE);
        } catch ( NullPointerException npe ) {
            Toast.makeText( this, "onCreate: groupType Extra could not be found.", Toast.LENGTH_LONG );
        }

        this.setTitle( groupType.equals( Key.Val.PARTY) ? "Parties" : "Encounters" );

        dbTransaction = new DBTransaction( this );

        groupList = dbTransaction.getGroupList( groupType );

        // fill the list with parties from database
        listAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                groupList);
        setListAdapter( listAdapter );

        getListView().setOnItemClickListener(this);
        getListView().setChoiceMode( ListView.CHOICE_MODE_MULTIPLE_MODAL );
        getListView().setMultiChoiceModeListener( this );
  
        editMode = false;
        deleteMode = false;

        deleteList = new ArrayList<String>();

        indexOfItemToEdit = -1;

        renameDialog = new NameDialogFragment( "Rename" );
        newGroupDialog = new NameDialogFragment( "Name" );
    }

    /**
     * Creates action bar menu.
     */
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.actionbar_add, menu );
        return super.onCreateOptionsMenu( menu );
    }

    /**
     * Action bar item handling.
     */
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle presses on the action bar items
        switch ( item.getItemId() ) {
            case R.id.action_new:
                newGroupDialog.show(getFragmentManager(), "newGroupDialog");
                break;
            case R.id.action_help:
                Toast.makeText( this, "Long-click to edit or delete an item. Tap an item to open.", Toast.LENGTH_LONG ).show();
                break;
            default:
                return super.onOptionsItemSelected( item );
        }
        return false;
    }

    /**
     * Handle list item clicks
     * @param listView
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick( AdapterView<?> listView, View view, int position, long id ) {
            Intent intent = new Intent( "moflow.activities.EditGroupActivity" );
            intent.putExtra( Key.GROUP_TYPE, groupType );
            intent.putExtra( Key.GROUP_NAME, listAdapter.getItem( position ) );
            startActivityForResult( intent, 1 );
    }

    /**
     * Handler for positive dialog click
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick( DialogFragment dialog ) {
        if ( dialog == renameDialog ) {
            renameParty( dialog );
        }

        else if ( dialog == newGroupDialog ) {
            createNewGroup( dialog );
        }
    }

    /**
     * Handler for negative dialog click
     * @param dialog
     */
    @Override
    public void onDialogNegativeClick( DialogFragment dialog ) {
        indexOfItemToEdit = -1;
    }

    /**
     * Renames party and saves new name to database
     * @param dialog
     */
    private void renameParty( DialogFragment dialog ) {
        EditText et = ( EditText ) dialog.getDialog().findViewById( R.id.nameField );
        String uniqueName = et.getText().toString().trim();
        String oldName = listAdapter.getItem( indexOfItemToEdit );

        if ( uniqueName.equals( oldName ) )
            return;

        if ( !uniqueName.isEmpty() ) {
            uniqueName = NameModifier.makeNameUnique(groupList, uniqueName);

            dbTransaction.renameGroup(uniqueName, groupList.get(indexOfItemToEdit), groupType);
            groupList.set(indexOfItemToEdit, uniqueName);
            Collections.sort( groupList );
            listAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Creates new group and saves new party to database
     * @param dialog
     */
    private void createNewGroup( DialogFragment dialog ) {
        EditText et = ( EditText ) dialog.getDialog().findViewById( R.id.nameField );
        String uniqueName = et.getText().toString().trim();

        if ( !uniqueName.isEmpty() ) {
            uniqueName = NameModifier.makeNameUnique(groupList, uniqueName );
            dbTransaction.insertNewGroup( uniqueName, groupType );
            groupList.add( uniqueName );
            Collections.sort( groupList );
            listAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, groupType + " name required.", Toast.LENGTH_LONG).show();
        }
     }

    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
        if ( checked )
            deleteList.add( listAdapter.getItem( position ) );
        else
            deleteList.remove( listAdapter.getItem( position ) );

        MenuItem item = actionMode.getMenu().findItem( R.id.action_edit );

        if ( deleteList.size() > 1 ) {
            indexOfItemToEdit = -1;
            item.setEnabled(false);
            item.getIcon().setAlpha( 128 );
        } else if ( deleteList.size() == 1 ) {
            indexOfItemToEdit = position;
            item.setEnabled( true );
            item.getIcon().setAlpha( 255 );
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        listAdapter = null;
        listAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_checked,
                groupList);
        setListAdapter( listAdapter );
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate( R.menu.context_actionbar_delete, menu );
        menu.findItem( R.id.action_edit ).setVisible( true );

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch ( menuItem.getItemId() ) {
            case R.id.action_discard:
                deleteSelectedItems();
                listAdapter.notifyDataSetChanged();
                actionMode.finish();
                return true;
            case R.id.action_edit:
                renameDialog.show( getFragmentManager(), "renameDialog" );
                actionMode.finish();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        deleteList.clear();
    }

    private void deleteSelectedItems() {
        dbTransaction.deleteGroup(deleteList, groupType);

        for ( String s : deleteList ) {
            listAdapter.remove( s );
        }
    }
}
