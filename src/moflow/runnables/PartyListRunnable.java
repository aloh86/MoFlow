package moflow.runnables;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import moflow.wolfpup.Party;
import moflow.database.MoFlowDB;

public class PartyListRunnable implements Runnable {
	private Context context;
	private MoFlowDB db;
	private ArrayAdapter<Party> adapter;
	
	public PartyListRunnable( Context ctx, MoFlowDB database, 
			ArrayAdapter<Party> adap ) {
		context = ctx;
		db = database;
		adapter = adap;
	}
	@Override
	public void run() {
		Cursor cur;
		
		try {
			db = new MoFlowDB( context );
			db.open();
		} catch ( SQLException e ) {
			Toast.makeText( context, "Database could not be opened!", Toast.LENGTH_LONG ).show();
		}
		
		cur = db.getAllParties();
		
		// if the database is empty, just return
		if ( cur.getCount() == 0 ) {
			cur.close();
			db.close();
			return;
		}
		
		// else, load the party names
		Party party = null;
		while( cur.moveToNext() ) {
			for ( int i = 0; i < cur.getColumnCount(); i++ ) {
				String colValue = cur.getString( i );
				party = new Party();
				party.setPartyName( colValue );
			}
			adapter.add( party );
		}
		
		//adapter.notifyDataSetChanged();
		cur.close();
		db.close();
	}
}
