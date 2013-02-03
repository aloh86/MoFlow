package moflow.database;

import android.provider.BaseColumns;
/*
===============================================================================
Catalog_Table.java

Database table schema for all creatures in the Creature Catalog.

CREATE TABLE Catalog (
	CreatureName	VARCHAR NOT NULL
	InitBonus  		INTEGER NOT NULL
	ArmorClass 		INTEGER NOT NULL
	MaxHP	   		INTEGER NOT NULL
)
===============================================================================
*/

public class Catalog_Table implements BaseColumns {
	public static final String TABLE_NAME     		= "Creatures";
	public static final String COL_CreatureName     = "CreatureName";
	public static final String COL_InitBonus  		= "InitBonus";
	public static final String COL_ArmorClass 		= "ArmorClass";
	public static final String COL_MaxHP	  		= "MaxHP";
	
	public static final String DB_CREATE =
			"CREATE TABLE "  + TABLE_NAME + " ( " +
			COL_CreatureName + " VARCHAR(20) NOT NULL " + 
			COL_InitBonus 	 + " INTEGER NOT NULL " +
			COL_ArmorClass 	 + " INTEGER NOT NULL " +
			COL_MaxHP		 + " INTEGER NOT NULL " +
			"PRIMARY KEY (" + COL_CreatureName + ") " + 
			");";
}