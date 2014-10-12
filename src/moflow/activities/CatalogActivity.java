package moflow.activities;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import moflow.dialogs.CreatureEditDialog;
import moflow.dialogs.NumPickDialog;
import moflow.dialogs.SimpleDialogListener;
import moflow.utility.Key;
import moflow.utility.DBTransaction;
import moflow.utility.NameModifier;
import moflow.wolfpup.Creature;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Alex on 9/22/14.
 */
public class CatalogActivity extends ListActivity implements AdapterView.OnItemClickListener, AbsListView.MultiChoiceModeListener, SimpleDialogListener
{
    private DBTransaction dbTransaction;
    private ArrayList< String > groupList;
    private ArrayAdapter< String > listAdapter;
    private ArrayList< String > deleteList;
    private int indexOfItemToEdit;
    private CreatureEditDialog newCreatureDialog;
    private CreatureEditDialog editCreatureDialog;
    private NumPickDialog numPickDialog;
    private String parentActivity;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setTitle( "Catalog" );

        try {
            parentActivity = getIntent().getExtras().getString( Key.PARENT_ACTIVITY);
        } catch ( NullPointerException npe ) {
            Toast.makeText(this, "onCreate: intent extras could not be extracted.", Toast.LENGTH_LONG).show();
            finish();
        }

        dbTransaction = new DBTransaction( this );

        groupList = dbTransaction.getCatalogItemList();

        listAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_checked,
                groupList);
        setListAdapter( listAdapter );

        getListView().setOnItemClickListener( this );
        getListView().setChoiceMode( ListView.CHOICE_MODE_MULTIPLE_MODAL );
        getListView().setMultiChoiceModeListener( this );

        deleteList = new ArrayList<String>();

        indexOfItemToEdit = -1;

        newCreatureDialog = new CreatureEditDialog( "New Creature" );
        numPickDialog = new NumPickDialog();
        //handleIntent( getIntent() );
    }


    @Override
    protected void onNewIntent( Intent intent )
    {
        handleIntent( intent );
    }

    private void handleIntent(Intent intent)
    {

        if ( Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // use query
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_catalog, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    /**
     * Action bar item handling.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_new:
                newCreatureDialog.show(getFragmentManager(), "newCatalogCreature");
                break;
            case R.id.action_help:
                Toast.makeText(this, "Long-click to delete an item. Tap an item to edit or select.", Toast.LENGTH_LONG).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog)
    {
        if ( dialog == newCreatureDialog ) {
            if ( !newCreatureDialog.isEmptyFields() ) {
                Creature critter = newCreatureDialog.getCritter();
                critter.setCreatureName( NameModifier.makeNameUnique( groupList, critter.getCreatureName() ) );
                groupList.add(critter.getCreatureName());
                dbTransaction.insertNewCreatureIntoCatalog(critter);
                listAdapter.sort( nameComparator() );
                listAdapter.notifyDataSetChanged();
            } else {
                invalidFieldMessage();
            }
        }

        if ( dialog == editCreatureDialog ) {
            if ( !editCreatureDialog.isEmptyFields() ) {
                Creature thing = editCreatureDialog.getCritter();
                String oldName = groupList.get( indexOfItemToEdit );

                if ( !oldName.equals( thing.getCreatureName() ) )
                    thing.setCreatureName( NameModifier.makeNameUnique( groupList, thing.getCreatureName() ) );

                groupList.set( indexOfItemToEdit, thing.getCreatureName() );
                dbTransaction.updateCatalogCreature( thing, oldName );
                listAdapter.sort( nameComparator() );
                listAdapter.notifyDataSetChanged();
            } else
                invalidFieldMessage();
        }

        if ( dialog == numPickDialog ) {
            String name = listAdapter.getItem(indexOfItemToEdit);
            Creature creature = dbTransaction.getCreatureFromCatalog(name);

            Bundle bundle = new Bundle();
            bundle.putParcelable(Key.CREATURE_OBJECT, creature);
            bundle.putInt(Key.NUM__CREATURE_PICKED, numPickDialog.getPickValue());

            Intent intent = getIntent();
            intent.putExtra(Key.NUMPICK_CATALOG_CREATURE_BUNDLE, bundle);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (parentActivity.equals(Key.Val.FROM_MAIN)) {
            String name = listAdapter.getItem(position);
            Creature creature = dbTransaction.getCreatureFromCatalog(name);
            editCreatureDialog = new CreatureEditDialog("Edit", creature, true);
            editCreatureDialog.show( getFragmentManager(), "editCreatureDialog" );
            indexOfItemToEdit = position;
        }

        if (parentActivity.equals(Key.Val.FROM_GROUP_ITEM)) {
            indexOfItemToEdit = position;
            numPickDialog.show(getFragmentManager(), "numPickDialog");
        }
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked)
    {
        if ( checked )
            deleteList.add( listAdapter.getItem( position ) );
        else
            deleteList.remove( listAdapter.getItem( position ) );
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
    {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate( R.menu.actionbar_del, menu );
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
    {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
    {
        switch ( menuItem.getItemId() ) {
            case R.id.action_discard:
                deleteSelectedItems();
                listAdapter.notifyDataSetChanged();
                actionMode.finish();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode)
    {
        deleteList.clear();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog)
    {

    }

    private void deleteSelectedItems() {
        dbTransaction.deleteCreatureFromCatalog(deleteList);
        for ( String name : deleteList ) {
            listAdapter.remove(name);
        }
    }

    public static Comparator<String> nameComparator()
    {
        return new Comparator<String>() {
            @Override
            public int compare( String lhs, String rhs ) {
                return lhs.compareToIgnoreCase( rhs );
            }
        };
    }

    private void invalidFieldMessage()
    {
        Toast.makeText( this, "All fields must be filled.", Toast.LENGTH_LONG ).show();
    }
}
