package moflow.utility;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;
import moflow.database.MoFlowDB;

import java.util.ArrayList;

/**
 * Created by Alex on 5/24/14.
 */
public class DBTransaction {
    private MoFlowDB db;
    private Cursor cur;

    public DBTransaction(Context ctx) {
        db = new MoFlowDB( ctx );

        try {
            db.open();
        } catch ( SQLiteException e ) {
            Toast.makeText( ctx, "Database could not be opened!", Toast.LENGTH_LONG );
        }
    }

    public void closeDB() {
        db.close();
    }

    // Retrieval

    public ArrayList< String > getGroupList( String groupType ) {
        if ( groupType.equals( CommonKey.VAL_PARTY ) )
            cur = db.getAllParties();
        else
            cur = db.getAllEncounters();

        ArrayList< String > groupList = new ArrayList<String>();

        while ( cur.moveToNext() ) {
            // there is only 1 column in the parties table, hence 0 for getString
            groupList.add( cur.getString( 0 ) );
        }
        cur.close();

        return groupList;
    }

    // Insertion
    public void insertNewGroup( String groupName, String groupType ) {
        if ( groupType.equals( CommonKey.VAL_PARTY ) )
            db.insertParty( groupName );
        else
            db.insertEncounter( groupName );
    }

    // Deletion

    public void deleteGroupListItems( final ArrayList< String > toDelete, String groupType ) {
        for ( int i = 0; i < toDelete.size(); i++ ) {
            if ( groupType.equals( CommonKey.VAL_PARTY ) )
                db.deletePartyRecord( toDelete.get( i ) );
            else
                db.deleteEncounterRecord( toDelete.get( i ) );
        }
    }

    // Modification

    public void renameGroup( String newName, String oldName, String groupType ) {
        if ( groupType.equals( CommonKey.VAL_PARTY ) )
            db.updatePartyRecord( newName, oldName );
        else
            db.updateEncounterRecord( newName, oldName );
    }
}
