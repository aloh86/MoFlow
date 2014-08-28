package moflow.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import moflow.adapters.DisplayItemAdapter;
import moflow.dialogs.NameDialogFragment;
import moflow.utility.CommonKey;
import moflow.utility.DBTransaction;
import moflow.wolfpup.Creature;

import java.util.ArrayList;

/**
 * Created by Alex on 8/2/14.
 */
public class EditGroupActivity extends ListActivity {

    private DBTransaction dbTransaction;
    private boolean editMode;
    private boolean deleteMode;
    private ArrayList< Creature > groupList;
    private ArrayAdapter< Creature > listAdapter;
    private ArrayList< String > deleteList;
    private int indexOfItemToEdit;
    private NameDialogFragment renameDialog;
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

        renameDialog = new NameDialogFragment( "Rename" );
    }
}
