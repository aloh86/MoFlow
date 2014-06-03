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

    public ArrayList< String > getAllParties() {
        cur = db.getAllParties();

        ArrayList< String > partyList = new ArrayList<String>();

        while ( cur.moveToNext() ) {
            // there is only 1 column in the parties table, hence 0 for getString
            partyList.add( cur.getString( 0 ) );
        }
        cur.close();

        return partyList;
    }

    // Insertion
    public long insertNewParty( String partyName ) {
        return db.insertParty( partyName );
    }

    // Deletion

    public void deleteParties( final ArrayList< String > parties ) {
        for ( int i = 0; i < parties.size(); i++ ) {
            db.deletePartyRecord( parties.get( i ) );
        }
    }

    // Modification

    public void renameParty( String newName, String oldName ) {
        db.updatePartyRecord( newName, oldName );
    }
}
