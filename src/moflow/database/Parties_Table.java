package moflow.database;

import android.provider.BaseColumns;
/*
===============================================================================
Parties_Table.java

Database table schema for parties.

CREATE TABLE PCGroup (
	PartyName VARCHAR NOT NULL
)
===============================================================================
*/

public abstract class Parties_Table implements BaseColumns {
	public static final String TABLE_NAME = "Parties";
	public static final String COL_PartyName = "PartyName";
	
	public static final String DB_CREATE =
			"CREATE TABLE " + TABLE_NAME + " ( " +
			COL_PartyName   + " VARCHAR(50) NOT NULL, " +
			"PRIMARY KEY (" + COL_PartyName + ") " +
			");";
}
