package moflow.database;

import android.provider.BaseColumns;

/*
===============================================================================
Init_Table.java

Database table schema for items in initiative.

CREATE TABLE Init_Table (
	_id				INTEGER PRIMARY KEY NOT NULL,
	Initiative		INTEGER NOT NULL,	
	CreatureName	VARCHAR NOT NULL,
	InitBonus  		INTEGER NOT NULL,
	ArmorClass 		INTEGER NOT NULL,
	CurrentHP		INTEGER NOT NULL,
	MaxHP	   		INTEGER NOT NULL,
	IsMonster		INTEGER NOT NULL
)
===============================================================================
*/
public class Init_Table implements BaseColumns {
	public static final String TABLE_NAME     		= "Initiative";
	public static final String COL_Encounter  		= "Encounter";
	public static final String COL_CreatureName     = "CreatureName";
	public static final String COL_InitBonus  		= "InitBonus";
	public static final String COL_ArmorClass 		= "ArmorClass";
	public static final String COL_MaxHP	  		= "MaxHP";
}
