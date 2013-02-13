package moflow.runnables;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.widget.ArrayAdapter;

import moflow.database.MoFlowDB;
import moflow.tracker.Moflow_Party;

public class PartyListRunnable implements Runnable {
	private Context context;
	private MoFlowDB db;
	private ArrayAdapter<Moflow_Party> adapter;
	
	public PartyListRunnable( Context ctx, MoFlowDB database, 
			ArrayAdapter<Moflow_Party> adap ) {
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
		}
		
		cur = db.getAllParties();
		
		// if the database is empty, just return
		if ( cur.getCount() == 0 ) {
			cur.close();
			db.close();
			return;
		}
		
		// else, load the party names
		if ( cur.moveToFirst() ) {
			for ( int i = 0; i < cur.getColumnCount(); i++ ) {
				String colValue = cur.getString( i );
				Moflow_Party party = new Moflow_Party();
				party.setPartyName( colValue );
				adapter.add( party );
			}
			cur.moveToNext();
		}
		adapter.notifyDataSetChanged();
		cur.close();
		db.close();
	}
}
