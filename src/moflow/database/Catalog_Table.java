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
	public static final String TABLE_NAME     		= "Catalog";
	public static final String COL_CreatureName     = "CreatureName";
	public static final String COL_InitBonus  		= "InitBonus";
	public static final String COL_ArmorClass 		= "ArmorClass";
	public static final String COL_MaxHP	  		= "MaxHP";
    public static final String COL_STR        = "str";
    public static final String COL_DEX        = "dex";
    public static final String COL_CON        = "con";
    public static final String COL_INT        = "int";
    public static final String COL_WIS        = "wis";
    public static final String COL_CHA        = "cha";
    public static final String COL_FORT       = "fort";
    public static final String COL_REF        = "ref";
    public static final String COL_WILL       = "will";
	
	public static final String DB_CREATE =
			"CREATE TABLE "  + TABLE_NAME + " ( " +
			COL_CreatureName + " VARCHAR(50) NOT NULL, " + 
			COL_InitBonus 	 + " INTEGER NOT NULL, " +
			COL_ArmorClass 	 + " INTEGER NOT NULL, " +
			COL_MaxHP		 + " INTEGER NOT NULL, " +
            COL_STR         + " INTEGER NOT NULL DEFAULT 0, " +
            COL_DEX         + " INTEGER NOT NULL DEFAULT 0, " +
            COL_CON         + " INTEGER NOT NULL DEFAULT 0, " +
            COL_INT         + " INTEGER NOT NULL DEFAULT 0, " +
            COL_WIS         + " INTEGER NOT NULL DEFAULT 0, " +
            COL_CHA         + " INTEGER NOT NULL DEFAULT 0, " +
            COL_FORT        + " INTEGER NOT NULL DEFAULT 0, " +
            COL_REF         + " INTEGER NOT NULL DEFAULT 0, " +
            COL_WILL        + " INTEGER NOT NULL DEFAULT 0, " +
			"PRIMARY KEY (" + COL_CreatureName +  ") " + 
			");";
}