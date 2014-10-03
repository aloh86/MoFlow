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
    public static final String COL_STR              = "str";
    public static final String COL_DEX              = "dex";
    public static final String COL_CON              = "con";
    public static final String COL_INT              = "int";
    public static final String COL_WIS              = "wis";
    public static final String COL_CHA              = "cha";
    public static final String COL_FORT             = "fort";
    public static final String COL_REF              = "ref";
    public static final String COL_WILL             = "will";

	
	public static final String DB_CREATE = 
			"CREATE TABLE " 	+ TABLE_NAME + " ( " +
			COL_Encounter 		+ " TEXT NOT NULL, " +
			COL_CreatureName	+ " TEXT NOT NULL, " +
			COL_InitBonus		+ " TEXT NOT NULL, " +
			COL_ArmorClass		+ " TEXT NOT NULL, " +
			COL_MaxHP			+ " TEXT NOT NULL, " +
            COL_STR         + " TEXT NOT NULL DEFAULT '0', " +
            COL_DEX         + " TEXT NOT NULL DEFAULT '0', " +
            COL_CON         + " TEXT NOT NULL DEFAULT '0', " +
            COL_INT         + " TEXT NOT NULL DEFAULT '0', " +
            COL_WIS         + " TEXT NOT NULL DEFAULT '0', " +
            COL_CHA         + " TEXT NOT NULL DEFAULT '0', " +
            COL_FORT        + " TEXT NOT NULL DEFAULT '0', " +
            COL_REF         + " TEXT NOT NULL DEFAULT '0', " +
            COL_WILL        + " TEXT NOT NULL DEFAULT '0', " +
            "PRIMARY KEY (" + COL_Encounter + "," + COL_CreatureName + "), " +
            "FOREIGN KEY (" + COL_Encounter + ") REFERENCES " + Encounters_Table.TABLE_NAME + "(" + Encounters_Table.COL_Encounter + ") " +
            "ON UPDATE CASCADE ON DELETE CASCADE" +
            ");";
}
