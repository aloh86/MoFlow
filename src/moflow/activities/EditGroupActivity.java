package moflow.activities;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import moflow.adapters.DisplayItemAdapter;
import moflow.dialogs.CreatureEditDialog;
import moflow.dialogs.SimpleDialogListener;
import moflow.utility.*;
import moflow.wolfpup.Creature;

import java.util.ArrayList;

/**
 * Created by Alex on 8/2/14.
 */
public class EditGroupActivity extends ListActivity
        implements AdapterView.OnItemClickListener, SimpleDialogListener, DialogInterface.OnClickListener, AbsListView.MultiChoiceModeListener {

    private String groupType;
    private String groupName;
    private DBTransaction dbTransaction;
    private ArrayList< Creature > groupList;
    private ArrayAdapter< Creature > listAdapter;
    private ArrayList< Creature > deleteList;
    private int indexOfItemToEdit;
    private CreatureEditDialog newCreatureDialog;
    private CreatureEditDialog editCreatureDialog;
    private Dialog newCreatureChoiceDialog;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        try {
            groupType = getIntent().getExtras().getString(Key.GROUP_TYPE);
            groupName = getIntent().getExtras().getString(Key.GROUP_NAME);
        } catch ( NullPointerException npe ) {
            Toast.makeText(this, "onCreate: intent extras could not be extracted.", Toast.LENGTH_LONG).show();
        }

        this.setTitle( groupName );

        dbTransaction = new DBTransaction( this );

        groupList = dbTransaction.getGroupItemList( groupType, groupName );

        // fill the list with parties from database
        listAdapter = new DisplayItemAdapter(
                this,
                R.layout.groupitemdisplay,
                groupList,
                false );
        setListAdapter(listAdapter);

        getListView().setOnItemClickListener( this );
        getListView().setChoiceMode( ListView.CHOICE_MODE_MULTIPLE_MODAL );
        getListView().setMultiChoiceModeListener( this );

        deleteList = new ArrayList<Creature>();

        indexOfItemToEdit = -1;

        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle( "New Creature" )
                .setItems( R.array.newCreatureDialogChoices, this );
        newCreatureChoiceDialog = builder.create(); // used in encounter manager to choose between new or catalog creature.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Key.PICK_CREATURE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras().getBundle(Key.NUMPICK_CATALOG_CREATURE_BUNDLE);
                int numPicked = bundle.getInt(Key.NUM__CREATURE_PICKED);
                Creature pickedCreature = bundle.getParcelable(Key.CREATURE_OBJECT);
                addPickedCreature( numPicked, pickedCreature );
            }
        }
    }

    private void addPickedCreature(int numPicked, Creature pickedCreature)
    {
        for (int i = 0; i < numPicked; i++) {
            Creature copy = pickedCreature.clone();
            copy.setCreatureName(NameModifier.makeNameUnique2(groupList, copy.getCreatureName()));

            listAdapter.add(copy);
            listAdapter.sort(Creature.nameComparator());
            listAdapter.notifyDataSetChanged();
            dbTransaction.insertNewCreature(groupName, copy, groupType);
        }
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
                chooseNewCreatureOptions();
                break;
            case R.id.action_help:
                Toast.makeText( this, "Long-click to delete an item. Tap an item to edit.", Toast.LENGTH_LONG ).show();
                break;
            default:
                return super.onOptionsItemSelected( item );
        }
        return false;
    }

    private void chooseNewCreatureOptions() {
        if ( groupType.equals( Key.Val.ENCOUNTER) )
            newCreatureChoiceDialog.show();
        else
            newCreatureFromScratch();
    }


    private void newCreatureFromScratch() {
        String type = ( groupType == Key.Val.PARTY ? "PC" : "Creature" );
        newCreatureDialog = CreatureEditDialog.newInstance("New " + type, null, Key.Val.EDITGROUP_ACTIVITY);
        newCreatureDialog.show(getFragmentManager(), "newCreatureDialog");
    }

    // Positive click handler for new or edit creature dialog
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if ( dialog == newCreatureDialog) {
            if ( !newCreatureDialog.hasEmptyFields() ) {
                Creature critter = newCreatureDialog.getCritter();

                if (critter == null) {
                    CommonToast.invalidDieToast(this);
                    return;
                }

                critter.setCreatureName( NameModifier.makeNameUnique2( groupList, critter.getCreatureName() ) );
                groupList.add( critter );
                dbTransaction.insertNewCreature( groupName, critter, groupType );
                listAdapter.sort( Creature.nameComparator() );
                listAdapter.notifyDataSetChanged();
            } else
                CommonToast.invalidFieldToast(this);
        }

        if ( dialog == editCreatureDialog ) {
            if ( !editCreatureDialog.hasEmptyFields() ) {
                Creature thing = editCreatureDialog.getCritter();

                if (thing == null) {
                    CommonToast.invalidDieToast(this);
                    return;
                }

                if ( thing.equals( groupList.get( indexOfItemToEdit ) ) ) {
                    return;
                } else {
                    String oldName = groupList.get( indexOfItemToEdit ).getCreatureName();

                    if ( !oldName.equals( thing.getCreatureName() ) )
                        thing.setCreatureName( NameModifier.makeNameUnique2( groupList, thing.getCreatureName() ) );

                    groupList.set( indexOfItemToEdit, thing );
                    dbTransaction.updateExistingCreature( thing, groupName, oldName, groupType );
                    listAdapter.sort( Creature.nameComparator() );
                    listAdapter.notifyDataSetChanged();
                }
            } else
                CommonToast.invalidFieldToast(this);
        }
    }

    // Negative click handler for new or edit creature dialog
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    // Activity ListView item click handler
    @Override
    public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
        Creature c = listAdapter.getItem( position );
        editCreatureDialog = CreatureEditDialog.newInstance("Edit", c, Key.Val.EDITGROUP_ACTIVITY);
        editCreatureDialog.show( getFragmentManager(), "editCreatureDialog" );
        indexOfItemToEdit = position;
    }

    // New or Catalog Creature dialog onClick handler for encounter manager
    @Override
    public void onClick( DialogInterface dialogInterface, int which ) {
        final int SCRATCH = 0;

        if ( dialogInterface == newCreatureChoiceDialog ) {
            if ( which == SCRATCH ) {
                newCreatureFromScratch();
            } else {
                Intent intent = new Intent("moflow.activities.CatalogActivity");
                intent.putExtra(Key.PARENT_ACTIVITY, Key.Val.EDITGROUP_ACTIVITY);
                startActivityForResult(intent, Key.PICK_CREATURE);
            }
        }
    }

    // Contextual Action Mode
    @Override
    public void onItemCheckedStateChanged( ActionMode actionMode, int position, long id, boolean checked ) {
        if ( checked )
            deleteList.add( listAdapter.getItem( position ) );
        else
            deleteList.remove( listAdapter.getItem( position ) );
    }

    // Contextual Action Mode
    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        listAdapter = null;
        listAdapter = new ArrayAdapter<Creature>(
                this,
                android.R.layout.simple_list_item_checked,
                groupList);
        setListAdapter( listAdapter );
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate( R.menu.actionbar_del, menu );

        return true;
    }


    // Contextual Action Mode
    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    // Contextual Action Mode
    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch ( menuItem.getItemId() ) {
            case R.id.action_discard:
                deleteSelectedItems();
                listAdapter.notifyDataSetChanged();
                actionMode.finish();
                return true;
        }
        return false;
    }

    // Contextual Action Mode
    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        deleteList.clear();

        listAdapter = null;
        listAdapter = new DisplayItemAdapter(
                this,
                R.layout.groupitemdisplay,
                groupList,
                false );
        setListAdapter(listAdapter);
    }

    private void deleteSelectedItems() {
        dbTransaction.deleteCreatureFromGroup( deleteList, groupName, groupType );

        for ( Creature c : deleteList ) {
            listAdapter.remove( c );
        }
    }
}
