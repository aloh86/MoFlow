package moflow.activities;

import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import moflow.adapters.DisplayItemAdapter;
import moflow.dialogs.CreatureEditDialog;
import moflow.dialogs.SimpleDialogListener;
import moflow.utility.CommonKey;
import moflow.utility.DBTransaction;
import moflow.utility.NameModifier;
import moflow.wolfpup.Creature;

import java.util.ArrayList;

/**
 * Created by Alex on 8/2/14.
 */
public class EditGroupActivity extends ListActivity
        implements AdapterView.OnItemClickListener, SimpleDialogListener, DialogInterface.OnClickListener {

    private DBTransaction dbTransaction;
    private boolean editMode;
    private boolean deleteMode;
    private ArrayList< Creature > groupList;
    private ArrayAdapter< Creature > listAdapter;
    private ArrayList< Creature > deleteList;
    private int indexOfItemToEdit;
    private CreatureEditDialog newCreatureDialog;
    private CreatureEditDialog editCreatureDialog;
    private String groupType;
    private String groupName;
    private Dialog newCreatureChoiceDialog;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        try {
            groupType = getIntent().getExtras().getString( CommonKey.KEY_GROUP_TYPE );
            groupName = getIntent().getExtras().getString( CommonKey.KEY_GROUP_NAME );
        } catch ( NullPointerException npe ) {
            Toast.makeText(this, "onCreate: intent extras could not be extracted.", Toast.LENGTH_LONG).show();
        }

        dbTransaction = new DBTransaction( this );

        groupList = dbTransaction.getGroupItemList( groupType, groupName );

        // fill the list with parties from database
        listAdapter = new DisplayItemAdapter(
                this,
                R.layout.groupitemdisplay,
                groupList,
                false );
        setListAdapter( listAdapter );

        getListView().setOnItemClickListener( this );

        editMode = false;
        deleteMode = false;

        deleteList = new ArrayList<Creature>();

        indexOfItemToEdit = -1;

        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle( "New Creature" )
                .setItems( R.array.newCreatureDialogChoices, this );
        newCreatureChoiceDialog = builder.create();
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
            Toast.makeText( this, "onPrepareOptionsMenu: view ID not found.", Toast.LENGTH_LONG ).show();
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
                chooseNewCreatureOptions();
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
            listAdapter = new ArrayAdapter<Creature>(
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
            listAdapter = new ArrayAdapter<Creature>(
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

        listAdapter = new DisplayItemAdapter(
                this,
                R.layout.groupitemdisplay,
                groupList,
                false);
        setListAdapter( listAdapter );

        getListView().setChoiceMode( ListView.CHOICE_MODE_SINGLE );
        invalidateOptionsMenu();
    }

    /**
     * Handles confirmation action bar button
     */
    public void editOrDeleteItems() {
        if ( editMode ) {
            editCreatureDialog = new CreatureEditDialog( "Edit", groupList.get( indexOfItemToEdit ) );
            editCreatureDialog.show( getFragmentManager(), "renameDialog" );
        }
        else if ( deleteMode && !deleteList.isEmpty() ) {
            dbTransaction.deleteCreatureFromGroup( deleteList, groupName, groupType );

            for ( Creature thing : deleteList ) {
                listAdapter.remove( thing );
            }
        }
        listAdapter.notifyDataSetChanged();
        restoreCommonMenu();
    }

    private void chooseNewCreatureOptions() {
        if ( groupType.equals( CommonKey.VAL_ENC ) )
            newCreatureChoiceDialog.show();
        else
            newCreatureFromScratch();
    }


    private void newCreatureFromScratch() {

        newCreatureDialog = new CreatureEditDialog( "Rename" );
        newCreatureDialog.show(getFragmentManager(), "editCreatureDialog");
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if ( dialog == newCreatureDialog ) {
            if ( !newCreatureDialog.isEmptyFields() ) {
                Creature critter = newCreatureDialog.getCritter();
                critter.setCreatureName( NameModifier.makeNameUnique2(groupList, critter.getCreatureName()) );
                groupList.add( critter );
                dbTransaction.insertNewCreature( groupName, critter, groupType );
                listAdapter.sort( Creature.nameComparator() );
                listAdapter.notifyDataSetChanged();
            } else
                invalidFieldMessage();
        }
        if ( dialog == editCreatureDialog ) {
            if ( !editCreatureDialog.isEmptyFields() ) {
                String oldName = groupList.get( indexOfItemToEdit ).getCreatureName();
                Creature thing = editCreatureDialog.getCritter();
                thing.setCreatureName( NameModifier.makeNameUnique2( groupList, thing.getCreatureName() ) );
                groupList.set( indexOfItemToEdit, thing );
                dbTransaction.updateExistingCreature( thing, groupName, oldName, groupType );
                listAdapter.sort( Creature.nameComparator() );
                listAdapter.notifyDataSetChanged();
            } else
                invalidFieldMessage();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    private void invalidFieldMessage() {
        Toast.makeText( this, "All fields must be filled.", Toast.LENGTH_LONG ).show();
    }

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
        }
    }

    @Override
    public void onClick( DialogInterface dialogInterface, int which ) {
        final int SCRATCH = 0;

        if ( dialogInterface == newCreatureChoiceDialog ) {
            if ( which == SCRATCH ) {
                newCreatureFromScratch();
            } else {

            }
        }
    }
}
