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
            createTables( db );
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion ) {
        switch ( oldVersion ) {
            case 1:
                dropTables( db );
                createTables( db );
            default:
        }
	}

    private void createTables( SQLiteDatabase db )
    {
        db.execSQL( Parties_Table.DB_CREATE );
        db.execSQL( Players_Table.DB_CREATE );
        db.execSQL( Encounters_Table.DB_CREATE );
        db.execSQL( Creatures_Table.DB_CREATE );
        db.execSQL( Catalog_Table.DB_CREATE );
        db.execSQL( Init_Table.DB_CREATE );
    }

    private void dropTables( SQLiteDatabase db )
    {
        db.execSQL( "DROP TABLE " + Parties_Table.TABLE_NAME );
        db.execSQL( "DROP TABLE " + Players_Table.TABLE_NAME );
        db.execSQL( "DROP TABLE " + Encounters_Table.TABLE_NAME );
        db.execSQL( "DROP TABLE " + Creatures_Table.TABLE_NAME );
        db.execSQL( "DROP TABLE " + Catalog_Table.TABLE_NAME );
        db.execSQL( "DROP TABLE " + Init_Table.TABLE_NAME );
    }

}
