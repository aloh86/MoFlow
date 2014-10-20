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
	Type			INTEGER NOT NULL
)
===============================================================================
*/
public class Init_Table implements BaseColumns {
	public static final String TABLE_NAME     		= "Initiative";
	public static final String COL_Init		  		= "Init";
	public static final String COL_CreatureName     = "CreatureName";
	public static final String COL_InitBonus  		= "InitBonus";
	public static final String COL_ArmorClass 		= "ArmorClass";
	public static final String COL_CurrentHP	  	= "CurrentHP";
	public static final String COL_MaxHP	  		= "MaxHP";
	public static final String COL_Type				= "Type";
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
			"CREATE TABLE "  + TABLE_NAME + " ( " +
			_ID				 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COL_Init		 + " TEXT NOT NULL, " +
			COL_CreatureName + " TEXT NOT NULL, " +
			COL_InitBonus 	 + " TEXT NOT NULL, " +
			COL_ArmorClass 	 + " TEXT NOT NULL, " +
			COL_CurrentHP	 + " TEXT NOT NULL, " +
			COL_MaxHP		 + " TEXT NOT NULL, " +
			COL_Type		 + " INTEGER NOT NULL, " +
            COL_STR         + " TEXT NOT NULL DEFAULT '0', " +
            COL_DEX         + " TEXT NOT NULL DEFAULT '0', " +
            COL_CON         + " TEXT NOT NULL DEFAULT '0', " +
            COL_INT         + " TEXT NOT NULL DEFAULT '0', " +
            COL_WIS         + " TEXT NOT NULL DEFAULT '0', " +
            COL_CHA         + " TEXT NOT NULL DEFAULT '0', " +
            COL_FORT        + " TEXT NOT NULL DEFAULT '0', " +
            COL_REF         + " TEXT NOT NULL DEFAULT '0', " +
            COL_WILL        + " TEXT NOT NULL DEFAULT '0' " +
			");";
}
