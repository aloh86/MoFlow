package moflow.activities;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;
import moflow.dialogs.CreatureEditDialog;
import moflow.dialogs.NumPickDialog;
import moflow.dialogs.SimpleDialogListener;
import moflow.utility.CommonToast;
import moflow.utility.Key;
import moflow.utility.DBTransaction;
import moflow.utility.NameModifier;
import moflow.wolfpup.Creature;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Alex on 9/22/14.
 */
public class CatalogActivity extends ListActivity
        implements AdapterView.OnItemClickListener, AbsListView.MultiChoiceModeListener, SimpleDialogListener,
        MenuItem.OnActionExpandListener, DialogInterface.OnClickListener
{
    private DBTransaction dbTransaction;
    private ArrayList< String > catalogList;
    private ArrayAdapter< String > listAdapter;
    private ArrayList< String > deleteList;
    private int indexOfItemToEdit;
    private CreatureEditDialog newCreatureDialog;
    private CreatureEditDialog editCreatureDialog;
    private NumPickDialog numPickDialog;
    private String parentActivity;
    private Dialog deleteConfirmDialog;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle("Catalog");

        try {
            parentActivity = getIntent().getExtras().getString(Key.PARENT_ACTIVITY);
        } catch (NullPointerException npe) {
            Toast.makeText(this, "onCreate: intent extras could not be extracted.", Toast.LENGTH_LONG).show();
            finish();
        }

        dbTransaction = new DBTransaction(this);

        catalogList = dbTransaction.getCatalogItemList();

        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, catalogList);
        setListAdapter(listAdapter);

        getListView().setOnItemClickListener(this);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener(this);

        deleteList = new ArrayList<String>();

        indexOfItemToEdit = -1;

        newCreatureDialog = CreatureEditDialog.newInstance("New Creature", null, Key.Val.CATALOG_ACTIVITY);

        numPickDialog = new NumPickDialog();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove all")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Are you sure you want to remove all creatures from the catalog?")
                .setNegativeButton("No", this)
                .setPositiveButton("Yes", this);
        deleteConfirmDialog = builder.create();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if (!catalogList.isEmpty())
            outState.putStringArrayList("catalogList", catalogList);

        if (!deleteList.isEmpty())
            outState.putStringArrayList("deleteList", deleteList);

        outState.putInt("editIndex", indexOfItemToEdit);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState)
    {
        super.onRestoreInstanceState(inState);

        FragmentManager fm = getFragmentManager();

        if (null != fm.findFragmentByTag("editCreatureDialog"))
            editCreatureDialog = (CreatureEditDialog) fm.findFragmentByTag("editCreatureDialog");

        if (null != fm.findFragmentByTag("newCreatureDialog"))
            newCreatureDialog = (CreatureEditDialog) fm.findFragmentByTag("newCreatureDialog");

        if (null != fm.findFragmentByTag("numPickDialog"))
            numPickDialog = (NumPickDialog) fm.findFragmentByTag("numPickDialog");

        if (inState.containsKey("catalogList")) {
            catalogList.clear();
            catalogList  = inState.getStringArrayList("catalogList");
            listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, catalogList);
            setListAdapter(listAdapter);
        }

        if (inState.containsKey("deleteList")) {
            deleteList.clear();
            ArrayList<String> dList = inState.getStringArrayList("deleteList");

            for (int i = 0; i < dList.size(); i++) {
                int indexToCheck = getItemPosition(dList.get(i));
                if (indexToCheck != -1) {
                    getListView().setItemChecked(indexToCheck, true);
                }
            }
        }

        indexOfItemToEdit = inState.getInt("editIndex");
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent)
    {

        if ( Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            filterResults(query.toLowerCase());
        }
    }

    private void filterResults(String query)
    {
        ArrayList<String> filterList = new ArrayList<String>();

        for (int i = 0; i < catalogList.size(); i++) {
            if (query.length() > 3) {
                if (catalogList.get(i).toLowerCase().startsWith(query) || catalogList.get(i).toLowerCase().contains(query)) {
                    filterList.add(catalogList.get(i));
                }
            }
            else if (query.length() > 0 && query.length() <= 3) {
                if (catalogList.get(i).toLowerCase().startsWith(query)) {
                    filterList.add(catalogList.get(i));
                }
            }
        }

        if (filterList.isEmpty()) {
            filterList.add(getString(R.string.no_results));
        }

        listAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_checked,
                filterList);
        setListAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_catalog, menu);
        MenuItem item = menu.findItem(R.id.search);
        item.setOnActionExpandListener(this);

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
                newCreatureDialog.show(getFragmentManager(), "newCreatureDialog");
                break;
            case R.id.action_remove_all:
                deleteConfirmDialog.show();
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
        if (dialog == newCreatureDialog) {
            if (!newCreatureDialog.hasEmptyFields()) {
                Creature critter = newCreatureDialog.getCritter();

                if (critter == null) {
                    CommonToast.invalidDieToast(this);
                    return;
                }

                critter.setCreatureName(NameModifier.makeNameUnique(catalogList, critter.getCreatureName()));
                catalogList.add(critter.getCreatureName());
                dbTransaction.insertNewCreatureIntoCatalog(critter);
                listAdapter.sort(nameComparator());
                listAdapter.notifyDataSetChanged();
            } else {
                CommonToast.invalidFieldToast(this);
            }
        }

        if (dialog == editCreatureDialog) {
            if (!editCreatureDialog.hasEmptyFields()) {
                Creature thing = editCreatureDialog.getCritter();

                if (thing == null) {
                    CommonToast.invalidDieToast(this);
                    return;
                }

                String oldName = catalogList.get(indexOfItemToEdit);

                if (!oldName.equals(thing.getCreatureName()))
                    thing.setCreatureName(NameModifier.makeNameUnique(catalogList, thing.getCreatureName()));

                catalogList.set(indexOfItemToEdit, thing.getCreatureName());
                dbTransaction.updateCatalogCreature( thing, oldName );
                listAdapter.sort(nameComparator());
                listAdapter.notifyDataSetChanged();
            } else
                CommonToast.invalidFieldToast(this);
        }

        if (dialog == numPickDialog) {
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
        if (listAdapter.getCount() == 1) {
            if (listAdapter.getItem(0).equals(getString(R.string.no_results))) {
                return;
            }
        }

        if (parentActivity.equals(Key.Val.MAIN_ACTIVITY)) {
            String name = listAdapter.getItem(position);
            Creature creature = dbTransaction.getCreatureFromCatalog(name);
            editCreatureDialog = CreatureEditDialog.newInstance("Edit", creature, Key.Val.CATALOG_ACTIVITY);
            editCreatureDialog.show( getFragmentManager(), "editCreatureDialog" );
            indexOfItemToEdit = position;
        }

        if (parentActivity.equals(Key.Val.EDITGROUP_ACTIVITY) || parentActivity.equals(Key.Val.INITIATIVE_ACTIVITY)) {
            indexOfItemToEdit = position;
            numPickDialog.show(getFragmentManager(), "numPickDialog");
        }
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked)
    {
        if (checked)
            deleteList.add(listAdapter.getItem(position));
        else
            deleteList.remove(listAdapter.getItem(position));
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
    {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate( R.menu.context_actionbar_delete, menu );

        if (listAdapter.getCount() == 1) {
            if (listAdapter.getItem(0).equals(getString(R.string.no_results))) {
                menu.findItem(R.id.action_discard).setEnabled(false).getIcon().setAlpha(128);
            }
        }
        else {
            menu.findItem(R.id.action_discard).setEnabled(true).getIcon().setAlpha(255);
        }

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
        switch (menuItem.getItemId()) {
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
            public int compare(String lhs, String rhs) {
                return lhs.compareToIgnoreCase(rhs);
            }
        };
    }

    private void invalidFieldMessage()
    {
        Toast.makeText( this, "All fields must be filled.", Toast.LENGTH_LONG ).show();
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem)
    {
        catalogList.clear();
        catalogList = dbTransaction.getCatalogItemList();
        listAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_checked,
                catalogList);
        setListAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Filters cleared", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int choice)
    {
        if (dialogInterface == deleteConfirmDialog) {
            if (choice == Dialog.BUTTON_POSITIVE) {
                dbTransaction.deleteAllCatalogCreatures();
            }
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
            settings.edit().putString("pref_catalogEdition", "-1").commit();
            catalogList.clear();
            listAdapter.notifyDataSetChanged();
        }
    }

    private int getItemPosition(String s)
    {
        for (int pos = 0; pos < catalogList.size(); pos++) {
            if (s == catalogList.get(pos)) {
                return pos;
            }
        }
        return -1;
    }
}
