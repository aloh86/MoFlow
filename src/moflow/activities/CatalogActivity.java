package moflow.activities;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import moflow.dialogs.CreatureEditDialog;
import moflow.utility.CommonKey;
import moflow.utility.DBTransaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Alex on 9/22/14.
 */
public class CatalogActivity extends ListActivity implements AdapterView.OnItemClickListener, AbsListView.MultiChoiceModeListener
{
    private DBTransaction dbTransaction;
    private ArrayList< String > groupList;
    private ArrayAdapter< String > listAdapter;
    private ArrayList< String > deleteList;
    private int indexOfItemToEdit;
    private CreatureEditDialog newCreatureDialog;
    private CreatureEditDialog editCreatureDialog;
    private String parentActivity;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setTitle( "Catalog" );

        try {
            parentActivity = getIntent().getExtras().getString( CommonKey.KEY_PARENT_ACTIVITY );
        } catch ( NullPointerException npe ) {
            Toast.makeText(this, "onCreate: intent extras could not be extracted.", Toast.LENGTH_LONG).show();
            finish();
        }

        dbTransaction = new DBTransaction( this );

        groupList = dbTransaction.getCatalogItemList();

        listAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                groupList);
        setListAdapter( listAdapter );

        getListView().setOnItemClickListener( this );
        getListView().setChoiceMode( ListView.CHOICE_MODE_MULTIPLE_MODAL );
        getListView().setMultiChoiceModeListener( this );

        deleteList = new ArrayList<String>();

        indexOfItemToEdit = -1;

        //handleIntent( getIntent() );
    }


    @Override
    protected void onNewIntent( Intent intent ) {
        handleIntent( intent );
    }

    private void handleIntent( Intent intent ) {

        if ( Intent.ACTION_SEARCH.equals( intent.getAction() ) ) {
            String query = intent.getStringExtra( SearchManager.QUERY );
            // use query
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_catalog, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = ( SearchManager ) getSystemService( Context.SEARCH_SERVICE );
        SearchView searchView = ( SearchView ) menu.findItem( R.id.search ).getActionView();
        searchView.setSearchableInfo( searchManager.getSearchableInfo(getComponentName() ) );

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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

    private void loadCatalog() {
        final Resources resources = getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int catalogVersion = Integer.parseInt(sharedPreferences.getString( CommonKey.CATALOG_VERSION_KEY, "-1" ) );
        InputStream inputStream = getInputStreamBasedOnVersion(resources, catalogVersion);
        ArrayList<String[]> critters = new ArrayList<String[]>();

        if ( inputStream == null ) {
            dbTransaction.deleteNonCustomCreatures();
            Toast.makeText( this, "Catalog version " + catalogVersion + " not available.", Toast.LENGTH_SHORT ).show();
            return;
        }

        BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream ) );

        try {
            String line;
            while ( ( line = reader.readLine() ) != null ) {
                String [] strings = TextUtils.split(line, ",");
                critters.add( strings );
            }
        } catch ( IOException e ) {
            Log.e("moflow.runnable.LoadCatalogTask", "readLine() failed to read input.");
        } finally {
            try {
                reader.close();
            } catch ( IOException e ) {
                Log.e( "moflow.runnable.LoadCatalogTask", "BufferedReader failed to closed." );
            }
        }
        dbTransaction.deleteNonCustomCreatures();
        dbTransaction.insertCreaturesFromFile( critters, catalogVersion );
    }

    private InputStream getInputStreamBasedOnVersion( Resources res, int version ) {
        if ( version == 5 )
            return res.openRawResource( R.raw.fivecc );

        return null;
    }
}
