package moflow.activities;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.view.*;

import android.os.Bundle;
import android.widget.*;
import moflow.dialogs.NameDialogFragment;
import moflow.dialogs.SimpleDialogListener;
import moflow.utility.CommonKey;
import moflow.utility.DBTransaction;
import moflow.utility.NameModfier;

import java.util.ArrayList;

/*
===============================================================================
GroupListActivity.java

Activity for listing the existing parties/encounters. The user can create a new
party/encounter and edit or delete an existing party/encounter.
===============================================================================
*/
public class GroupListActivity extends ListActivity implements AdapterView.OnItemClickListener, SimpleDialogListener
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
            groupType = getIntent().getExtras().getString( CommonKey.KEY_GROUP_TYPE );
        } catch ( NullPointerException npe ) {
            Toast.makeText( this, "onCreate: groupType Extra could not be found.", Toast.LENGTH_LONG );
        }

        dbTransaction = new DBTransaction( this );

        groupList = dbTransaction.getGroupList( groupType );

        // fill the list with parties from database
        listAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                groupList);
        setListAdapter( listAdapter );

        getListView().setOnItemClickListener(this);
  
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
        inflater.inflate( R.menu.abm_common_actions, menu );
        return super.onCreateOptionsMenu( menu );
    }

    /**
     * Show the appropriate action bar item depending on the mode.
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu( Menu menu ) {
        try {
            if ( editMode || deleteMode ) {
                menu.findItem( R.id.action_new  ).setVisible( false );
                menu.findItem( R.id.action_edit  ).setVisible( false );
                menu.findItem( R.id.action_discard  ).setVisible( false );
                menu.findItem( R.id.action_confirm  ).setVisible( true );
                menu.findItem( R.id.action_cancel  ).setVisible( true );
            } else {
                menu.findItem( R.id.action_new  ).setVisible( true );
                menu.findItem( R.id.action_edit  ).setVisible( true );
                menu.findItem( R.id.action_discard  ).setVisible( true );
                menu.findItem( R.id.action_confirm  ).setVisible( false );
                menu.findItem( R.id.action_cancel  ).setVisible( false );
            }
        } catch ( NullPointerException npe ) {
            Toast.makeText( this, "onPrepareOptionsMenu: view ID not found.", Toast.LENGTH_LONG );
        }
        return super.onPrepareOptionsMenu( menu );
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
            case R.id.action_edit:
                editPrep();
                break;
            case R.id.action_discard:
                discardPrep();
                break;
            case R.id.action_confirm:
                editOrDeleteItems();
                break;
            case R.id.action_cancel:
                restoreCommonMenu();
                indexOfItemToEdit = -1;
                break;
            default:
                return super.onOptionsItemSelected( item );
        }
        return true;
    }

    private void editPrep() {
        if ( listAdapter.getCount() > 0 ) {
            editMode = true;
            listAdapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_checked,
                    groupList);
            setListAdapter( listAdapter );
            getListView().setChoiceMode( ListView.CHOICE_MODE_SINGLE );
            invalidateOptionsMenu();
        }
    }

    private void discardPrep() {
        if ( listAdapter.getCount() > 0 ) {
            deleteMode = true;
            listAdapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_checked,
                    groupList);
            setListAdapter( listAdapter );
            getListView().setChoiceMode( ListView.CHOICE_MODE_MULTIPLE );
            invalidateOptionsMenu();
        }
    }

    private void restoreCommonMenu() {
        editMode = false;
        deleteMode = false;
        deleteList.clear();

        listAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                groupList);
        setListAdapter( listAdapter );

        getListView().setChoiceMode( ListView.CHOICE_MODE_SINGLE );
        invalidateOptionsMenu();
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
        if ( editMode || deleteMode ) {
            getListView().setItemChecked( position, getListView().isItemChecked( position ) );

            if ( editMode ) {
                indexOfItemToEdit = position;
            }
            else { // delete mode
                if ( getListView().isItemChecked( position) )
                    deleteList.add( groupList.get( position ) );
                else
                    deleteList.remove(groupList.get(position));
            }
        } else {
            Intent intent = new Intent( "moflow.activities.EditGroupActivity" );
            intent.putExtra( CommonKey.KEY_GROUP_TYPE, groupType );
            intent.putExtra( CommonKey.KEY_GROUP_NAME, listAdapter.getItem( position ) );
            startActivityForResult( intent, 1 );
        }
    }

    /**
     * Handles confirmation action bar button
     */
    public void editOrDeleteItems() {
        if ( editMode ) {
            renameDialog.show( getFragmentManager(), "renameDialog" );
        }
        else if ( deleteMode && !deleteList.isEmpty() ) {
            dbTransaction.deleteGroup(deleteList, groupType);

            for ( String name : deleteList ) {
                listAdapter.remove( name );
            }
        }
        listAdapter.notifyDataSetChanged();
        restoreCommonMenu();
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

        if ( !uniqueName.isEmpty() ) {
            uniqueName = NameModfier.makeNameUnique(groupList, uniqueName );

            dbTransaction.renameGroup(uniqueName, groupList.get(indexOfItemToEdit), groupType);

            groupList.set(indexOfItemToEdit, uniqueName);
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
            uniqueName = NameModfier.makeNameUnique(groupList, uniqueName );
            dbTransaction.insertNewGroup( uniqueName, groupType );
            groupList.add( uniqueName );
            listAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText( this, "Party name required.", Toast.LENGTH_LONG ).show();
        }
     }
}
