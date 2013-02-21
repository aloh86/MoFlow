package moflow.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import moflow.tracker.Moflow_PC;

/*
===============================================================================
MoFlowDB.java

Database for MoFlow. Has operations for inserting and removing entries from
a database.
===============================================================================
*/
public class MoFlowDB {
	private Context context;
	private SQLiteDatabase db;
	private DatabaseHelper DBHelper;
	
	private final String DB_NAME = "MoFlowDB";
	private final int DB_VERSION = 1;
	
	public MoFlowDB( Context ctx ) {
		context = ctx;
		DBHelper = new DatabaseHelper( context, DB_NAME, null, DB_VERSION );
	}
	
	/**-----------------------------------------------------------------------
	 * Opens the database.
	 * @return this
	 * @throws SQLException
	 */
	public MoFlowDB open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	/**-----------------------------------------------------------------------
	 * Closes the database.
	 */
	public void close () {
		DBHelper.close();
	}
	
	/*************************************************************************
	 *  INSERTS
	 */
	
	/**-----------------------------------------------------------------------
	 * Inserts a group name into the PCGroup table.
	 * @param partyName The name of the PC group.
	 * @return tuple position of this entry
	 */
	public long insertGroup( String partyName ) {
		ContentValues initialValues = new ContentValues();
		initialValues.put( Parties_Table.COL_PartyName , partyName );
		//return db.insert( Parties_Table.TABLE_NAME, null, initialValues );
		return db.insertWithOnConflict( Parties_Table.TABLE_NAME, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE );
	}
	
	/**-----------------------------------------------------------------------
	 * Inserts a new player entry into the PlayerCharacters table.
	 * @param party party name that the PC is in.
	 * @param pcName the pc's name.
	 * @param init the pc's init bonus.
	 * @param AC the pc's armor class.
	 * @param hp the pc's max hit points
	 * @return tuple position of this entry
	 */
	public long insertPlayer( String party, String pcName, int init, int AC, 
			int hp ) {
		ContentValues initVal = new ContentValues();
		initVal.put( Players_Table.COL_PartyName, party );
		initVal.put( Players_Table.COL_PCName, pcName );
		initVal.put( Players_Table.COL_InitBonus, init );
		initVal.put( Players_Table.COL_ArmorClass, AC );
		initVal.put( Players_Table.COL_MaxHP, hp );
		//return db.insert( Players_Table.TABLE_NAME, null, initVal );
		return db.insertWithOnConflict( Players_Table.TABLE_NAME, null, initVal, SQLiteDatabase.CONFLICT_IGNORE );
	}
	
	/*************************************************************************
	 *  QUERIES
	 */
	
	/**-----------------------------------------------------------------------
	 * Get all the party names.
	 * @return Cursor to all party tuples.
	 */
	public Cursor getAllParties() {
		String [] columns = new String [] { Parties_Table.COL_PartyName };
		return db.query(
				Parties_Table.TABLE_NAME, 
				columns, 
				null, 
				null, 
				null, 
				null, 
				null );
	}
	
	/**-----------------------------------------------------------------------
	 * Get all the PCs belonging to a group.
	 * @param groupName The group that the PCs belong to.
	 * @return Cursor to the queried tuples.
	 */
	public Cursor getPCForGroup( String groupName ) {
		String [] columns = { 
				Players_Table.COL_PartyName, 
				Players_Table.COL_PCName, 
				Players_Table.COL_InitBonus,
				Players_Table.COL_ArmorClass,
				Players_Table.COL_MaxHP 
				};
		String [] selectionArgs = { groupName };
		return db.query(
				Players_Table.TABLE_NAME, 
				columns, 
				"PartyName = ?", 
				selectionArgs, 
				null, 
				null, 
				null );
	}
	
	/*************************************************************************
	 *  UPDATES
	 */
	
	/**
	 * Updates a player record
	 * @param updated the updated PC
	 * @param partyName the party name the PC belongs to
	 * @param pcName PC's name
	 * @return the number of rows affected
	 */
	public int updatePlayerRecord( Moflow_PC updated, String partyName, String pcName ) {
		String whereClause = 
				Players_Table.COL_PartyName + " = ? AND " +
				Players_Table.COL_PCName + " = ?";
		String [] whereArgs = { partyName, pcName };
		
		ContentValues initVal = new ContentValues();
		initVal.put( Players_Table.COL_PartyName, partyName );
		initVal.put( Players_Table.COL_PCName, updated.getCharName() );
		initVal.put( Players_Table.COL_InitBonus, updated.getInitMod() );
		initVal.put( Players_Table.COL_ArmorClass, updated.getAC() );
		initVal.put( Players_Table.COL_MaxHP, updated.getMaxHitPoints() );
		
		return db.update( Players_Table.TABLE_NAME, initVal, whereClause, whereArgs );
	}
	
	/**
	 * Updates a party record
	 * @param uniqueName the new party name
	 * @param oldName the old party name
	 * @return the number of rows affected
	 */
	public int updatePartyRecord( String uniqueName, String oldName )
	{
		String whereClause =
				Parties_Table.COL_PartyName + " = ?";
		String [] whereArgs = { oldName };
		ContentValues initVal = new ContentValues();
		initVal.put( Parties_Table.COL_PartyName, uniqueName );
		
		return db.update( Parties_Table.TABLE_NAME, initVal, whereClause, whereArgs );
	}
	
	/*************************************************************************
	 *  DELETIONS
	 */
	
	/**
	 * Delete party from party table.
	 * @param partyName Name of the party to be deleted
	 * @return the number of rows affected
	 */
	public int deletePartyRecord( String partyName )
	{
		String whereClause = Parties_Table.COL_PartyName + " = ?";
		String [] whereArgs = { partyName };
		return db.delete( Parties_Table.TABLE_NAME, whereClause, whereArgs );
	}
	
	public int deletePartyMembers( String partyName )
	{
		String whereClause = Players_Table.COL_PartyName + " = ?";
		String [] whereArgs = { partyName };
		return db.delete( Players_Table.TABLE_NAME, whereClause, whereArgs );
	}
	
}