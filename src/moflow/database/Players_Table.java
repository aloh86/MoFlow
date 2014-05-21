package moflow.database;

import android.provider.BaseColumns;

import java.util.ArrayList;
/*
===============================================================================
Players_Table.java

Database table schema for all players belonging to a party group.
===============================================================================
*/

public abstract class Players_Table implements BaseColumns {
	public static final String TABLE_NAME     = "Players";
	public static final String COL_PartyName  = "PartyName";
	public static final String COL_PCName     = "PCName";
	public static final String COL_InitBonus  = "InitBonus";
	public static final String COL_ArmorClass = "ArmorClass";
	public static final String COL_MaxHP	  = "MaxHP";
    public static final String COL_STR        = "str";
    public static final String COL_DEX        = "dex";
    public static final String COL_CON        = "con";
    public static final String COL_INT        = "int";
    public static final String COL_WIS        = "wis";
    public static final String COL_CHA        = "cha";
    public static final String COL_FORT       = "fort";
    public static final String COL_REF        = "ref";
    public static final String COL_WILL       = "will";

    public static ArrayList< String > V1_UPGRADE;
	
	public static final String DB_CREATE = 
			"CREATE TABLE " + TABLE_NAME + " ( " +
			COL_PartyName   + " VARCHAR(50) NOT NULL, " +
			COL_PCName      + " VARCHAR(50) NOT NULL, " +
			COL_InitBonus   + " INTEGER NOT NULL, " +
			COL_ArmorClass  + " INTEGER NOT NULL, " +
			COL_MaxHP		+ " INTEGER NOT NULL, " +
            COL_STR         + " INTEGER, " +
            COL_DEX         + " INTEGER, " +
            COL_CON         + " INTEGER, " +
            COL_INT         + " INTEGER, " +
            COL_WIS         + " INTEGER, " +
            COL_CHA         + " INTEGER, " +
            COL_FORT        + " INTEGER, " +
            COL_REF         + " INTEGER, " +
            COL_WILL        + " INTEGER, " +
			"PRIMARY KEY (" + COL_PartyName + "," + COL_PCName + "), " +
            "FOREIGN KEY (" + COL_PartyName + ") REFERENCES " + Parties_Table.TABLE_NAME + "(" + Parties_Table.COL_PartyName + ") " +
                    "ON UPGRADE CASCADE ON DELETE CASCADE" +
			");";
}
