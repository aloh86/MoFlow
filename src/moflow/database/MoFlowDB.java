package moflow.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import moflow.tracker.Moflow_Creature;
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
	
	/**
	 * Opens the database.
	 * @return this
	 * @throws SQLException
	 */
	public MoFlowDB open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * Closes the database.
	 */
	public void close () {
		DBHelper.close();
	}
	
	/*************************************************************************
	 *  INSERTS
	 */
	
	/**
	 * Inserts a group name into the Parties table.
	 * @param partyName The name of the PC group.
	 * @return tuple position of this entry
	 */
	public long insertGroup( String partyName ) {
		ContentValues initialValues = new ContentValues();
		initialValues.put( Parties_Table.COL_PartyName , partyName );
		//return db.insert( Parties_Table.TABLE_NAME, null, initialValues );
		return db.insertWithOnConflict( Parties_Table.TABLE_NAME, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE );
	}
	
	/**
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
	
	/**
	 * Insert a single creature into the catalog
	 * @param name name of creature
	 * @param init init bonus of creature
	 * @param AC ac of creature
	 * @param hp hit points of creature
	 * @return tuple position of this entry
	 */
	public long insertCreatureInCatalog( String name, int init, int AC, int hp ) {
		ContentValues initVal = new ContentValues();
		initVal.put( Catalog_Table.COL_CreatureName, name );
		initVal.put( Catalog_Table.COL_InitBonus, init );
		initVal.put( Catalog_Table.COL_ArmorClass, AC );
		initVal.put( Catalog_Table.COL_MaxHP, hp );
		return db.insertWithOnConflict( Catalog_Table.TABLE_NAME, null, initVal, SQLiteDatabase.CONFLICT_IGNORE );
	}
	
	/**
	 * Inserts an encounter into the Encounter table.
	 * @param name name of the encounter
	 * @return tuple position of this entry
	 */
	public long insertEncounter( String name ) {
		ContentValues initVal = new ContentValues();
		initVal.put( Encounters_Table.COL_Encounter, name );
		return db.insertWithOnConflict( Encounters_Table.TABLE_NAME, null, initVal, SQLiteDatabase.CONFLICT_IGNORE );
	}
	
	/*************************************************************************
	 *  QUERIES
	 */
	
	/**
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
				Parties_Table.COL_PartyName + " COLLATE NOCASE"  );
	}
	
	/**
	 * Get all the encounter names
	 * @return Cursor to all encounter tuples.
	 */
	public Cursor getAllEncounters() {
		String [] columns = new String [] { Encounters_Table.COL_Encounter };
		return db.query( 
				Encounters_Table.TABLE_NAME, 
				columns, 
				null, 
				null, 
				null, 
				null, 
				Encounters_Table.COL_Encounter + " COLLATE NOCASE" );
	}
	
	/**
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
				Players_Table.COL_PartyName + " = ?", 
				selectionArgs, 
				null, 
				null, 
				Players_Table.COL_PCName + " COLLATE NOCASE" );
	}
	
	/**
	 * Get all the entries in the catalog sorted by name (ascending).
	 * @return
	 */
	public Cursor getCatalog() {
		String [] columns = {
				Catalog_Table.COL_CreatureName,
				Catalog_Table.COL_InitBonus,
				Catalog_Table.COL_ArmorClass,
				Catalog_Table.COL_MaxHP
				};
		return db.query(
				Catalog_Table.TABLE_NAME,
				columns,
				null,
				null,
				null,
				null,
				Catalog_Table.COL_CreatureName + " COLLATE NOCASE" );
	}
	
	/**
	 * Retrieve a single creature from the catalog.
	 * @param name name of the creature to retrieve
	 * @return Cursor to the queried creature
	 */
	public Cursor getCreatureFromCatalog( String name ) {
		String [] columns = {
				Catalog_Table.COL_CreatureName,
				Catalog_Table.COL_InitBonus,
				Catalog_Table.COL_ArmorClass,
				Catalog_Table.COL_MaxHP
				};
		String whereClause = Catalog_Table.COL_CreatureName + " = ?";
		String [] whereArgs = { name };
		
		return db.query( Catalog_Table.TABLE_NAME, columns, whereClause, whereArgs, null, null, null );
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
	public int updatePartyRecord( String uniqueName, String oldName ) {
		String whereClause =
				Parties_Table.COL_PartyName + " = ?";
		String [] whereArgs = { oldName };
		ContentValues initVal = new ContentValues();
		initVal.put( Parties_Table.COL_PartyName, uniqueName );
		
		return db.update( Parties_Table.TABLE_NAME, initVal, whereClause, whereArgs );
	}
	
	/**
	 * When a party is renamed, its member's membership to that party is renamed.
	 * @param oldPartyName old party name
	 * @param newPartyName new party name
	 * @return the number of rows affected
	 */
	public int updateMembersForParty( String newPartyName, String oldPartyName ) {
		String whereClause = Players_Table.COL_PartyName + " = ?";
		String [] whereArgs = { oldPartyName };
		ContentValues initVal = new ContentValues();
		initVal.put( Players_Table.COL_PartyName, newPartyName );
		
		return db.update( Players_Table.TABLE_NAME, initVal, whereClause, whereArgs );
	}
	
	/**
	 * Updates values for a record in the catalog.
	 * @param updated the updated creature
	 * @param oldCreatureName the name of the record to update
	 * @return the number of rows affected
	 */
	public int updateCreatureInCatalog( Moflow_Creature updated, String oldCreatureName ) {
		String whereClause = Catalog_Table.COL_CreatureName + " = ?";
		String [] whereArgs = { oldCreatureName };
		
		ContentValues initVal = new ContentValues();
		initVal.put( Catalog_Table.COL_CreatureName, updated.getCharName() );
		initVal.put( Catalog_Table.COL_InitBonus, updated.getInitMod() );
		initVal.put( Catalog_Table.COL_ArmorClass, updated.getAC() );
		initVal.put( Catalog_Table.COL_MaxHP, updated.getMaxHitPoints() );
		
		return db.update( Catalog_Table.TABLE_NAME, initVal, whereClause, whereArgs );
	}
	
	/**
	 * Updates an encounter record in the Encounters table.
	 * @param newName new encounter name
	 * @param oldName old encounter name 
	 * @return the number of rows affected
	 */
	public int updateEncounterRecord( String newName, String oldName ) {
		String whereClause = Encounters_Table.COL_Encounter + " = ?";
		String [] whereArgs = { oldName };
		
		ContentValues initVal = new ContentValues();
		initVal.put( Encounters_Table.COL_Encounter, newName );
		
		return db.update( Encounters_Table.TABLE_NAME, initVal, whereClause, whereArgs );
	}
	
	/**
	 * When an encounter is renamed, its member's membership to that encounter is renamed.
	 * @param newEncName new encounter name
	 * @param oldEncName old encounter name
	 * @return
	 */
	public int updateEncountersForCreatures( String newEncName, String oldEncName ) {
		String whereClause = Creatures_Table.COL_Encounter + " =?";
		String [] whereArgs = { oldEncName };
		
		ContentValues initVal = new ContentValues();
		initVal.put( Creatures_Table.COL_Encounter, newEncName );
		
		return db.update( Creatures_Table.TABLE_NAME, initVal, whereClause, whereArgs );
	}
	
	/*************************************************************************
	 *  DELETIONS
	 */
	
	/**
	 * Delete party from party table.
	 * @param partyName Name of the party to be deleted
	 * @return the number of rows affected
	 */
	public int deletePartyRecord( String partyName ) {
		String whereClause = Parties_Table.COL_PartyName + " = ?";
		String [] whereArgs = { partyName };
		return db.delete( Parties_Table.TABLE_NAME, whereClause, whereArgs );
	}
	
	/**
	 * Deletes all party members belong to party specified by partyName. 
	 * Called when a party is deleted.
	 * @param partyName The party name that the members belong to
	 * @return the number of rows affected
	 */
	public int deletePartyMembers( String partyName ) {
		String whereClause = Players_Table.COL_PartyName + " = ?";
		String [] whereArgs = { partyName };
		return db.delete( Players_Table.TABLE_NAME, whereClause, whereArgs );
	}
	
	/**
	 * Deletes an individual PC from the party.
	 * @param partyName The party the PC belongs to
	 * @param pcName The name of the PC.
	 * @return the number of rows affected
	 */
	public int deletePC( String partyName, String pcName ) {
		String whereClause = Players_Table.COL_PartyName + " = ? AND " +  Players_Table.COL_PCName + " = ?";
		String [] whereArgs = { partyName, pcName };
		return db.delete( Players_Table.TABLE_NAME, whereClause, whereArgs );
	}
	
	/**
	 * Deletes a single creature from the catalog.
	 * @param name the creature to delete
	 * @return the number of rows affected
	 */
	public int deleteCreatureFromCatalog( String name ) {
		String whereClause = Catalog_Table.COL_CreatureName + " = ?";
		String [] whereArgs = { name };
		return db.delete( Catalog_Table.TABLE_NAME, whereClause, whereArgs );
	}
	
	/**
	 * Deletes an encounter from Encounter table.
	 * @param encounterName the encounter to delete
	 * @return the number of rows affected
	 */
	public int deleteEncounter( String encounterName ) {
		String whereClause = Encounters_Table.COL_Encounter + " = ?";
		String [] whereArgs = { encounterName };
		return db.delete( Encounters_Table.TABLE_NAME, whereClause, whereArgs );
	}
	
	/**
	 * Deletes all creatures belonging to an encounter specified by encounterName.
	 * Called when deleting an encounter from the encounters list.
	 * @param encounterName the encounter that the creatures belong to.
	 * @return the number of rows affected
	 */
	public int deleteEncounterCreatures( String encounterName ) {
		String whereClause = Creatures_Table.COL_Encounter + " = ?";
		String [] whereArgs = { encounterName };
		return db.delete( Creatures_Table.TABLE_NAME, whereClause, whereArgs );
	}
}