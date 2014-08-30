package moflow.activities;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import moflow.adapters.DisplayItemAdapter;
import moflow.dialogs.CreatureEditDialog;
import moflow.dialogs.NameDialogFragment;
import moflow.dialogs.SimpleDialogListener;
import moflow.utility.CommonKey;
import moflow.utility.DBTransaction;
import moflow.wolfpup.Creature;

import java.util.ArrayList;

/**
 * Created by Alex on 8/2/14.
 */
public class EditGroupActivity extends ListActivity implements SimpleDialogListener {

    private DBTransaction dbTransaction;
    private boolean editMode;
    private boolean deleteMode;
    private ArrayList< Creature > groupList;
    private ArrayAdapter< Creature > listAdapter;
    private ArrayList< String > deleteList;
    private int indexOfItemToEdit;
    private CreatureEditDialog newCreatureDialog;
    private CreatureEditDialog editCreatureDialog;
    private String groupType;
    private String groupName;

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

        //getListView().setOnItemClickListener(this);

        editMode = false;
        deleteMode = false;

        deleteList = new ArrayList<String>();

        indexOfItemToEdit = -1;

        newCreatureDialog = new CreatureEditDialog( "Rename" );
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
                newCreatureDialog.show(getFragmentManager(), "newGroupDialog");
                break;
            case R.id.action_edit:
                //editPrep();
                break;
            case R.id.action_discard:
                //discardPrep();
                break;
            case R.id.action_confirm:
                //editOrDeleteItems();
                break;
            case R.id.action_cancel:
                //restoreCommonMenu();
                indexOfItemToEdit = -1;
                break;
            default:
                return super.onOptionsItemSelected( item );
        }
        return true;
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
