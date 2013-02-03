package moflow.runnables;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.widget.ArrayAdapter;

import moflow.database.MoFlowDB;
import moflow.tracker.Moflow_Party;

public class PartyListRunnable implements Runnable {
	Context context;
	MoFlowDB db;
	ArrayList<Moflow_Party> partyList;
	ArrayAdapter<Moflow_Party> adapter;
	
	public PartyListRunnable( Context ctx, MoFlowDB database, 
			ArrayList<Moflow_Party> list, 
			ArrayAdapter<Moflow_Party> adap ) {
		context = ctx;
		db = database;
		partyList = list;
		adapter = adap;
	}
	@Override
	public void run() {
		Cursor cur;
		
		try {
			db = new MoFlowDB( context );
		} catch ( SQLException e ) {
		}
		
		cur = db.getAllParties();
		
		if ( cur.moveToFirst() ) {
			for ( int i = 0; i < cur.getColumnCount(); i++ ) {
				String colValue = cur.getString( i );
				
			}
		}
	}

}
