package moflow.database;
/*
===============================================================================
Encounter_Table.java

Database table schema for encounters.

CREATE TABLE Encounters (
	Encounter VARCHAR NOT NULL
);
===============================================================================
*/
public class Encounters_Table {
	public static final String TABLE_NAME = "Encounters";
	public static final String COL_Encounter = "Encounter";
	
	public static final String DB_CREATE = 
			"CREATE TABLE " + TABLE_NAME + " ( " +
			COL_Encounter + " VARCHAR(20) NOT NULL, " +
			"PRIMARY KEY (" + COL_Encounter + ") " +
			");";
}
