package moflow.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import moflow.dialogs.NameDialogFragment;
import moflow.utility.CommonKey;
import moflow.utility.DBTransaction;

import java.util.ArrayList;

/**
 * Created by Alex on 8/2/14.
 */
public class EditGroupActivity extends ListActivity {

    private DBTransaction dbTransaction;
    private boolean editMode;
    private boolean deleteMode;
    private ArrayList< String > groupList;
    private ArrayAdapter< String > listAdapter;
    private ArrayList< String > deleteList;
    private int indexOfItemToEdit;
    private NameDialogFragment renameDialog;
    private String groupType;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        try {
            groupType = getIntent().getExtras().getString( CommonKey.KEY_GROUP_TYPE );
        } catch ( NullPointerException npe ) {
            Toast.makeText(this, "onCreate: groupType Extra could not be found.", Toast.LENGTH_LONG);
        }

        dbTransaction = new DBTransaction( this );

        groupList = dbTransaction.getGroupList( groupType );

        // fill the list with parties from database
        listAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                groupList);
        setListAdapter( listAdapter );

        //getListView().setOnItemClickListener(this);

        editMode = false;
        deleteMode = false;

        deleteList = new ArrayList<String>();

        indexOfItemToEdit = -1;

        renameDialog = new NameDialogFragment( "Rename" );
    }
}
