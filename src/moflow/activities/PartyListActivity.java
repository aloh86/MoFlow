package moflow.activities;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.view.*;

import android.os.Bundle;
import android.widget.*;
import moflow.dialogs.NameDialogFragment;
import moflow.dialogs.SimpleDialogListener;
import moflow.utility.DBTransaction;
import moflow.utility.NameModfier;

import java.util.ArrayList;

/*
===============================================================================
PartyListActivity.java

Activity for listing the existing parties in the Parties table in the database.
The user can create a new party and edit or delete an existing party. When
the user 'taps' on a party, PCM_EditPartyActivity is called, where the party
members can be added, edited, or removed.
===============================================================================
*/
public class PartyListActivity extends ListActivity implements AdapterView.OnItemClickListener, SimpleDialogListener
{
    private DBTransaction dbTransaction;
    private boolean editMode;
    private boolean deleteMode;
    private ArrayList< String > groupList;
    private ArrayAdapter< String > listAdapter;
    private ArrayList< String > deleteList;
    private int indexOfItemToEdit;
    private NameDialogFragment renameDialog;
    private NameDialogFragment newPartyDialog;

	@Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        dbTransaction = new DBTransaction( this );
        groupList = dbTransaction.getAllParties();

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

        renameDialog = new NameDialogFragment( "Rename" );
        newPartyDialog = new NameDialogFragment( "Party Name" );
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

    @Override
    public boolean onPrepareOptionsMenu( Menu menu ) {
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
                break;
            case R.id.action_edit:
                setEditFlags();
                break;
            case R.id.action_discard:
                setDiscardFlags();
                break;
            case R.id.action_confirm:
                editOrDeleteItems();
                break;
            case R.id.action_cancel:
                restoreCommonMenu();
                break;
            default:
                return super.onOptionsItemSelected( item );
        }
        return true;
    }

    private void setEditFlags() {
        editMode = true;
        listAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_checked,
                groupList);
        setListAdapter( listAdapter );
        getListView().setChoiceMode( ListView.CHOICE_MODE_SINGLE );
        invalidateOptionsMenu();
    }

    private void setDiscardFlags() {
        deleteMode = true;

        listAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_checked,
                groupList);
        setListAdapter( listAdapter );
        getListView().setChoiceMode( ListView.CHOICE_MODE_MULTIPLE );
        invalidateOptionsMenu();
    }

    private void restoreCommonMenu() {
        editMode = false;
        deleteMode = false;

        listAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                groupList);
        setListAdapter( listAdapter );

        getListView().setChoiceMode( ListView.CHOICE_MODE_SINGLE );
        invalidateOptionsMenu();
    }

    @Override
    public void onItemClick( AdapterView<?> listView, View view, int position, long id ) {
        if ( editMode || deleteMode ) {
            // isItemChecked gets state of click immediately. So, if it is checked,
            // and user clicks item, it is now unchecked. Visually, if you step through with
            // the debugger, you'll see that the item is still checked, but it's actual state
            // is unchecked.
            getListView().setItemChecked( position, getListView().isItemChecked( position ) );

            if ( editMode ) {
                indexOfItemToEdit = position;
            }
            else { // delete mode
                if ( true == getListView().isItemChecked( position) )
                    deleteList.add( groupList.get( position ) );
                else
                    deleteList.remove(groupList.get(position));
            }
        }
    }

    public void editOrDeleteItems() {
        if ( editMode ) {
            renameDialog.show( getFragmentManager(), "renameDialog" );
        }
        else if ( deleteMode ) {
            dbTransaction.deleteParties( deleteList );

            for ( int i = 0; i < deleteList.size(); i++ ) {
                listAdapter.remove( deleteList.get( i ) );
            }
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogPositiveClick( DialogFragment dialog ) {
        if ( dialog == renameDialog ) {
            renameParty( dialog );
            restoreCommonMenu();
        }
    }

    @Override
    public void onDialogNegativeClick( DialogFragment dialog ) {
        restoreCommonMenu();
    }

    private void renameParty( DialogFragment dialog ) {
        EditText et = ( EditText ) dialog.getDialog().findViewById( R.id.nameField );
        String uniqueName = et.getText().toString();

        if ( !uniqueName.isEmpty() ) {
            uniqueName = NameModfier.makeNameUnique(groupList, uniqueName );
            dbTransaction.renameParty(uniqueName, groupList.get(indexOfItemToEdit));
            groupList.set(indexOfItemToEdit, uniqueName);
            listAdapter.notifyDataSetChanged();
        }
    }
}
