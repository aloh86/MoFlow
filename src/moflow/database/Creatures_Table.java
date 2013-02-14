package moflow.database;

import android.provider.BaseColumns;
/*
===============================================================================
Creatures_Table.java

Database table schema for all creatures belonging to an encounter group.

CREATE TABLE Creatures (
	_id		   		INTEGER PRIMARY KEY AUTOINCREMENT
	Encounter  		VARCHAR NOT NULL
	CreatureName	VARCHAR NOT NULL
	InitBonus  		INTEGER NOT NULL
	ArmorClass 		INTEGER NOT NULL
	MaxHP	   		INTEGER NOT NULL
)
===============================================================================
*/

public abstract class Creatures_Table implements BaseColumns {
	public static final String TABLE_NAME     		= "Creatures";
	public static final String COL_Encounter  		= "Encounter";
	public static final String COL_CreatureName     = "CreatureName";
	public static final String COL_InitBonus  		= "InitBonus";
	public static final String COL_ArmorClass 		= "ArmorClass";
	public static final String COL_MaxHP	  		= "MaxHP";
	
	public static final String DB_CREATE = 
			"CREATE TABLE " 	+ TABLE_NAME+ " ( " +
			COL_Encounter 		+ " VARCHAR(20) NOT NULL, " +
			COL_CreatureName	+ " VARCHAR(20) NOT NULL, " +
			COL_InitBonus		+ " INTEGER NOT NULL, " +
			COL_ArmorClass		+ " INTEGER NOT NULL, " +
			COL_MaxHP			+ " INTEGER NOT NULL, " +
			"PRIMARY KEY (" + COL_Encounter + "," + COL_CreatureName + ")" +
			");";
}