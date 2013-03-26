package moflow.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public DatabaseHelper(Context context, String DBName, CursorFactory factory,
			int version) {
		super( context, DBName, factory, version );
	}
	
	@Override
	public void onCreate( SQLiteDatabase db ) {
		try {
			db.execSQL( Players_Table.DB_CREATE );
			db.execSQL( Parties_Table.DB_CREATE );
			db.execSQL( Encounters_Table.DB_CREATE );
			db.execSQL( Creatures_Table.DB_CREATE );
			db.execSQL( Catalog_Table.DB_CREATE );
			db.execSQL( Init_Table.DB_CREATE );
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion ) {
	}

}
