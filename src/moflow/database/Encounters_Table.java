package moflow.database;

import android.provider.BaseColumns;

/*
===============================================================================
Encounter_Table.java

Database table schema for encounters.

CREATE TABLE Encounters (
	Encounter VARCHAR NOT NULL
);
===============================================================================
*/
public class Encounters_Table implements BaseColumns {
	public static final String TABLE_NAME = "Encounters";
	public static final String COL_Encounter = "Encounter";
	
	public static final String DB_CREATE = 
			"CREATE TABLE " + TABLE_NAME + " ( " +
			COL_Encounter + " VARCHAR(50) NOT NULL, " +
			"PRIMARY KEY (" + COL_Encounter + ") " +
			");";
}
