package moflow.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.*;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import moflow.adapters.DisplayItemAdapter;
import moflow.dialogs.CreatureEditDialog;
import moflow.utility.DBTransaction;
import moflow.wolfpup.Creature;

import java.util.ArrayList;

/**
 * Created by alex on 10/19/14.
 */
public class InitiativeActivity extends ListActivity
        implements AdapterView.OnItemClickListener, AbsListView.MultiChoiceModeListener,
                    DialogInterface.OnClickListener {

    private DBTransaction dbTransaction;
    private ArrayList< Creature > groupList;
    private ArrayAdapter< Creature > listAdapter;
    private ArrayList< Creature > deleteList;
    private int indexOfItemToEdit;
    private CreatureEditDialog newCreatureDialog;
    private CreatureEditDialog editCreatureDialog;
    private Dialog newCreatureChoiceDialog;
    private Dialog deleteCreatureChoiceDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTitle("Initiative");

        dbTransaction = new DBTransaction( this );
        groupList = dbTransaction.getInitiativeItems();

        // fill the list with parties from database
        listAdapter = new DisplayItemAdapter(
                this,
                R.layout.groupitemdisplay,
                groupList,
                false );
        setListAdapter(listAdapter);

        getListView().setOnItemClickListener(this);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener(this);

        deleteList = new ArrayList<Creature>();

        indexOfItemToEdit = -1;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Creature")
                .setItems(R.array.newCatalogCreatureDialogChoices, this);
        newCreatureChoiceDialog = builder.create();
    }

    /**
     * Creates action bar menu.
     */
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.actionbar_initiative, menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public void onItemClick(AdapterView<?> listView, View view, int position, long id) {

    }

    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }
}
