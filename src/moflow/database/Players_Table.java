package moflow.database;

import android.provider.BaseColumns;
/*
===============================================================================
Players_Table.java

Database table schema for all players belonging to a party group.

CREATE TABLE PlayerCharacters (
	PartyName  VARCHAR NOT NULL
	PCName	   VARCHAR NOT NULL
	InitBonus  INTEGER NOT NULL
	ArmorClass INTEGER NOT NULL
	MaxHP	   INTEGER NOT NULL
)
===============================================================================
*/

public abstract class Players_Table implements BaseColumns {
	public static final String TABLE_NAME     = "Players";
	public static final String COL_PartyName  = "PartyName";
	public static final String COL_PCName     = "PCName";
	public static final String COL_InitBonus  = "InitBonus";
	public static final String COL_ArmorClass = "ArmorClass";
	public static final String COL_MaxHP	  = "MaxHP";
	
	public static final String DB_CREATE = 
			"CREATE TABLE " + TABLE_NAME + " ( " +
			COL_PartyName   + " VARCHAR(20) NOT NULL " +
			COL_PCName      + " VARCHAR(20) NOT NULL " +
			COL_InitBonus   + " INTEGER NOT NULL " +
			COL_ArmorClass  + " INTEGER NOT NULL " +
			COL_MaxHP		+ " INTEGER NOT NULL " +
			"PRIMARY KEY (" + COL_PCName + ") " +
			");";
}
