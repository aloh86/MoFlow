package moflow.database;

import android.provider.BaseColumns;

/*
===============================================================================
Condition_Table.java

Database table schema for items in initiative.
===============================================================================
*/
public class Condition_Table implements BaseColumns {
	public static final String TABLE_NAME     	= "Conditions";
	public static final String COL_BLINDED	 	= "Blinded";
	public static final String COL_COWERING     = "Cowering";
	public static final String COL_DAZED  		= "Dazed";
	public static final String COL_DEAFENED 	= "Deafened";
	public static final String COL_ENTANGLED	= "Entangled";
	public static final String COL_EXHAUSTED	= "Exhausted";
	public static final String COL_FATIGUED		= "Fatigued";
	public static final String COL_HELPLESS		= "Helpless";
	public static final String COL_MARKED		= "Marked";
	public static final String COL_NAUSEATED	= "Nauseated";
	public static final String COL_PANICKED		= "Panicked";
	public static final String COL_PARALYZED	= "Paralyzed";
	public static final String COL_SHAKEN		= "Shaken";
	public static final String COL_STUNNED		= "Stunned";


	public static final String DB_CREATE =
			"CREATE TABLE "  + TABLE_NAME + " ( " +
			_ID				 + " INTEGER PRIMARY KEY, " +
			COL_BLINDED		 + " VARCHAR(10), " +
			COL_COWERING	 + " VARCHAR(10), " +
			COL_DAZED		 + " VARCHAR(10), " +
			COL_DEAFENED	 + " VARCHAR(10), " +
			COL_ENTANGLED	 + " VARCHAR(10), " +
			COL_EXHAUSTED	 + " VARCHAR(10), " +
			COL_FATIGUED	 + " VARCHAR(10), " +
			COL_HELPLESS	 + " VARCHAR(10), " +
			COL_MARKED		 + " VARCHAR(10), " +
			COL_NAUSEATED	 + " VARCHAR(10), " +
			COL_PANICKED	 + " VARCHAR(10), " +
			COL_PARALYZED	 + " VARCHAR(10), " +
			COL_SHAKEN		 + " VARCHAR(10), " +
			COL_STUNNED		 + " VARCHAR(10) " +
			");";
}
