package moflow.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import moflow.utility.AbilityScoreMod;
import moflow.wolfpup.Conditions;
import moflow.wolfpup.Creature;

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
        db.execSQL( "PRAGMA foreign_keys = ON" );
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
	public long insertParty(String partyName) {
		ContentValues initialValues = new ContentValues();
		initialValues.put( Parties_Table.COL_PartyName , partyName );
		return db.insertWithOnConflict( Parties_Table.TABLE_NAME, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE );
	}
	
	/**
	 * Inserts a new player entry into the Players table.
	 * @param party party name that the PC is in.
	 * @param critter the creature to insert
	 * @return tuple position of this entry
	 */
	public long insertPlayer( String party, Creature critter ) {
		ContentValues initVal = new ContentValues();
		initVal.put( Players_Table.COL_PartyName, party );
		initVal.put( Players_Table.COL_PCName, critter.getCreatureName() );
		initVal.put( Players_Table.COL_InitBonus, critter.getInitMod() );
		initVal.put( Players_Table.COL_ArmorClass, critter.getArmorClass() );
		initVal.put( Players_Table.COL_MaxHP, critter.getMaxHitPoints() );
        initVal.put(Players_Table.COL_HitDie, critter.getHitDie());
        initVal.put( Players_Table.COL_STR, critter.getStrength() );
        initVal.put( Players_Table.COL_DEX, critter.getDexterity() );
        initVal.put( Players_Table.COL_CON, critter.getConstitution() );
        initVal.put( Players_Table.COL_INT, critter.getIntelligence() );
        initVal.put( Players_Table.COL_WIS, critter.getWisdom() );
        initVal.put( Players_Table.COL_CHA, critter.getCharisma() );
        initVal.put( Players_Table.COL_FORT, critter.getFortitude() );
        initVal.put( Players_Table.COL_REF, critter.getReflex() );
        initVal.put( Players_Table.COL_WILL, critter.getWill() );

		//return db.insert( Players_Table.TABLE_NAME, null, initVal );
		return db.insertWithOnConflict( Players_Table.TABLE_NAME, null, initVal, SQLiteDatabase.CONFLICT_IGNORE );
	}
	
	/**
	 * Insert a single creature into the catalog
     * @param  creature the creature to insert
	 * @return tuple position of this entry
	 */
	public long insertCreatureInCatalog( Creature creature ) {
		ContentValues initVal = new ContentValues();
		initVal.put( Catalog_Table.COL_CreatureName, creature.getCreatureName() );
		initVal.put( Catalog_Table.COL_InitBonus, creature.getInitMod() );
		initVal.put( Catalog_Table.COL_ArmorClass, creature.getArmorClass() );
		initVal.put( Catalog_Table.COL_MaxHP, creature.getMaxHitPoints() );
        initVal.put(Catalog_Table.COL_HitDie, creature.getHitDie());
        initVal.put( Catalog_Table.COL_STR, creature.getStrength() );
        initVal.put( Catalog_Table.COL_DEX, creature.getDexterity() );
        initVal.put( Catalog_Table.COL_CON, creature.getConstitution() );
        initVal.put( Catalog_Table.COL_INT, creature.getIntelligence() );
        initVal.put( Catalog_Table.COL_WIS, creature.getWisdom() );
        initVal.put( Catalog_Table.COL_CHA, creature.getCharisma() );
        initVal.put( Catalog_Table.COL_FORT, creature.getFortitude() );
        initVal.put( Catalog_Table.COL_REF, creature.getReflex() );
        initVal.put( Catalog_Table.COL_WILL, creature.getWill() );
        initVal.put( Catalog_Table.COL_CUSTOM, 1 );
		return db.insertWithOnConflict( Catalog_Table.TABLE_NAME, null, initVal, SQLiteDatabase.CONFLICT_IGNORE );
	}

    /**
     * Inserting creatures from fifth edition creatures file
     * @param creature creatures to insert
     * @return
     */
    public long insertFileCreatureInCatalog5e( String [] creature ) {
        // 0 = name, 1 = armor class, 2 = hit die, 3 -> 8 = ability scores
        ContentValues initVal = new ContentValues();
        initVal.put( Catalog_Table.COL_CreatureName, creature[ 0 ] );
        initVal.put(Catalog_Table.COL_InitBonus, AbilityScoreMod.get345AbilityScoreMod( creature[4] ) ); // getting the ability score mod since no init bonus in the catalog file
        initVal.put( Catalog_Table.COL_ArmorClass, creature[ 1 ]);
        initVal.put(Catalog_Table.COL_MaxHP, "XdX");
        initVal.put( Catalog_Table.COL_HitDie, creature[ 2 ] );
        initVal.put( Catalog_Table.COL_STR, creature[ 3 ]);
        initVal.put( Catalog_Table.COL_DEX, creature[ 4 ]);
        initVal.put( Catalog_Table.COL_CON, creature[ 5 ]);
        initVal.put( Catalog_Table.COL_INT, creature[ 6 ]);
        initVal.put( Catalog_Table.COL_WIS, creature[ 7 ]);
        initVal.put( Catalog_Table.COL_CHA, creature[ 8 ]);
        initVal.put( Catalog_Table.COL_CUSTOM, 0 ); // 0 means that this creature was loaded from file, not a user's custom creature.
        long error = db.insertWithOnConflict( Catalog_Table.TABLE_NAME, null, initVal, SQLiteDatabase.CONFLICT_IGNORE );
        return error;
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
	
	/**
	 * Insert a creature entry into the Creature table
	 * @param encounterName The name of the encounter creature is in.
	 * @param critter the Creature to insert
	 * @return tuple position of this entry
	 */
	public long insertCreature( String encounterName, Creature critter ) {
		ContentValues initVal = new ContentValues();
		initVal.put( Creatures_Table.COL_Encounter, encounterName );
		initVal.put( Creatures_Table.COL_CreatureName, critter.getCreatureName() );
		initVal.put( Creatures_Table.COL_InitBonus, critter.getInitMod() );
		initVal.put( Creatures_Table.COL_ArmorClass, critter.getArmorClass() );
		initVal.put( Creatures_Table.COL_MaxHP, critter.getMaxHitPoints() );
        initVal.put(Creatures_Table.COL_HitDie, critter.getHitDie());
        initVal.put( Creatures_Table.COL_STR, critter.getStrength() );
        initVal.put( Creatures_Table.COL_DEX, critter.getDexterity() );
        initVal.put( Creatures_Table.COL_CON, critter.getConstitution() );
        initVal.put( Creatures_Table.COL_INT, critter.getIntelligence() );
        initVal.put( Creatures_Table.COL_WIS, critter.getWisdom() );
        initVal.put( Creatures_Table.COL_CHA, critter.getCharisma() );
        initVal.put( Creatures_Table.COL_FORT, critter.getFortitude() );
        initVal.put( Creatures_Table.COL_REF, critter.getReflex() );
        initVal.put( Creatures_Table.COL_WILL, critter.getWill() );
		
		return db.insertWithOnConflict( Creatures_Table.TABLE_NAME, null, initVal, SQLiteDatabase.CONFLICT_IGNORE );
	}
	
	/**
	 * Insert a creature from the initiative list.
	 * @param creature the creature to insert
	 * @return tuple position of this entry
	 */
	public long insertCreatureIntoInitiative( Creature creature ) {
		ContentValues initVal = new ContentValues();
		initVal.put( Init_Table.COL_Init, creature.getInitiative() );
		initVal.put( Init_Table.COL_CreatureName, creature.getCreatureName() );
		initVal.put( Init_Table.COL_InitBonus, creature.getInitMod() );
		initVal.put( Init_Table.COL_ArmorClass, creature.getArmorClass() );
		initVal.put( Init_Table.COL_CurrentHP, creature.getCurrentHitPoints() );
		initVal.put( Init_Table.COL_MaxHP, creature.getMaxHitPoints() );
        initVal.put(Init_Table.COL_HitDie, creature.getHitDie());
		initVal.put( Init_Table.COL_Type, ( creature.isMonster() == false ? 0 : 1 ) ); // 0 = PC, 1 = Creature
        initVal.put( Init_Table.COL_STR, creature.getStrength() );
        initVal.put( Init_Table.COL_DEX, creature.getDexterity() );
        initVal.put( Init_Table.COL_CON, creature.getConstitution() );
        initVal.put( Init_Table.COL_INT, creature.getIntelligence() );
        initVal.put( Init_Table.COL_WIS, creature.getWisdom() );
        initVal.put( Init_Table.COL_CHA, creature.getCharisma() );
        initVal.put( Init_Table.COL_FORT, creature.getFortitude() );
        initVal.put( Init_Table.COL_REF, creature.getReflex() );
        initVal.put( Init_Table.COL_WILL, creature.getWill() );
		
		return db.insert( Init_Table.TABLE_NAME, null, initVal );
	}
	
	/**
	 * Save all conditions for a certain id.
	 * @param id id for creature
	 * @param condition set of conditions
	 * @return tuple position of this entry
	 */
	public long insertConditions( long id, Conditions condition ) {
		ContentValues initVal = new ContentValues();
		initVal.put( Condition_Table._ID, id );
		initVal.put( Condition_Table.COL_BLINDED, condition.getState( Conditions.BLINDED ) );
		initVal.put( Condition_Table.COL_COWERING, condition.getState( Conditions.COWERING ) );
		initVal.put( Condition_Table.COL_DAZED, condition.getState( Conditions.DAZED ) );
		initVal.put( Condition_Table.COL_DEAFENED, condition.getState( Conditions.DEAFENED ) );
		initVal.put( Condition_Table.COL_ENTANGLED, condition.getState( Conditions.ENTANGLED ) );
		initVal.put( Condition_Table.COL_EXHAUSTED, condition.getState( Conditions.EXHAUSTED ) );
		initVal.put( Condition_Table.COL_FATIGUED, condition.getState( Conditions.FATIGUED ) );
		initVal.put( Condition_Table.COL_HELPLESS, condition.getState( Conditions.HELPLESS ) );
		initVal.put( Condition_Table.COL_MARKED, condition.getState( Conditions.MARKED ) );
		initVal.put( Condition_Table.COL_NAUSEATED, condition.getState( Conditions.NAUSEATED ) );
		initVal.put( Condition_Table.COL_PANICKED, condition.getState( Conditions.PANICKED ) );
		initVal.put( Condition_Table.COL_PARALYZED, condition.getState( Conditions.PARALYZED ) );
		initVal.put( Condition_Table.COL_SHAKEN, condition.getState( Conditions.SHAKEN ) );
		initVal.put( Condition_Table.COL_STUNNED, condition.getState( Conditions.STUNNED ) );

		return db.insertWithOnConflict( Condition_Table.TABLE_NAME, null, initVal, SQLiteDatabase.CONFLICT_IGNORE );
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
				//Players_Table.COL_PartyName, 
				Players_Table.COL_PCName, 
				Players_Table.COL_InitBonus,
				Players_Table.COL_ArmorClass,
				Players_Table.COL_MaxHP,
                Players_Table.COL_HitDie,
                Players_Table.COL_STR,
                Players_Table.COL_DEX,
                Players_Table.COL_CON,
                Players_Table.COL_INT,
                Players_Table.COL_WIS,
                Players_Table.COL_CHA,
                Players_Table.COL_FORT,
                Players_Table.COL_REF,
                Players_Table.COL_WILL
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
				Catalog_Table.COL_CreatureName
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
				Catalog_Table.COL_MaxHP,
                Catalog_Table.COL_HitDie,
                Catalog_Table.COL_STR,
                Catalog_Table.COL_DEX,
                Catalog_Table.COL_CON,
                Catalog_Table.COL_INT,
                Catalog_Table.COL_WIS,
                Catalog_Table.COL_CHA,
                Catalog_Table.COL_FORT,
                Catalog_Table.COL_REF,
                Catalog_Table.COL_WILL
				};
		String whereClause = Catalog_Table.COL_CreatureName + " = ?";
		String [] whereArgs = { name };
		
		return db.query( Catalog_Table.TABLE_NAME, columns, whereClause, whereArgs, null, null, null );
	}
	
	/**
	 * Get all the creatures belonging to an encounter.
	 * @param encounterName name of the encounter creature belongs to
	 * @return Cursor to the queried creature
	 */
	public Cursor getCreaturesForEncounter( String encounterName ) {
		String [] columns = {
				Creatures_Table.COL_CreatureName,
				Creatures_Table.COL_InitBonus,
				Creatures_Table.COL_ArmorClass,
				Creatures_Table.COL_MaxHP,
                Creatures_Table.COL_HitDie,
                Creatures_Table.COL_STR,
                Creatures_Table.COL_DEX,
                Creatures_Table.COL_CON,
                Creatures_Table.COL_INT,
                Creatures_Table.COL_WIS,
                Creatures_Table.COL_CHA,
                Creatures_Table.COL_FORT,
                Creatures_Table.COL_REF,
                Creatures_Table.COL_WILL
				};
		String whereClause = Creatures_Table.COL_Encounter + " = ?";
		String [] whereArgs = { encounterName };
		
		return db.query( 
				Creatures_Table.TABLE_NAME, 
				columns, whereClause, 
				whereArgs, 
				null, 
				null, 
				Creatures_Table.COL_CreatureName + " COLLATE NOCASE" );
	}
	
	/**
	 * Retrieve all saved initiative items.
	 * @return Cursor to the rows
	 */
	public Cursor getInitListFromDB() {
		return db.query( 
				Init_Table.TABLE_NAME, 
				null, 
				null, 
				null, 
				null, 
				null, 
				Init_Table.COL_Init + " DESC" );
	}
	
	/**
	 * Get condition for a certain item in the initiative list
	 * @param id identifier for item in initiative list
	 * @return Cursor to condition for item in list.
	 */
	public Cursor getCondition( long id ) {
		String selection = Init_Table._ID + " = ?";
		String [] whereClause = { String.valueOf( id ) };
		return db.query( 
				Init_Table.TABLE_NAME, 
				null, 
				selection, 
				whereClause, 
				null, 
				null, 
				null );
	}
	
	/*************************************************************************
	 *  UPDATES
	 */
	
	/**
	 * Updates a player record in the Players table
	 * @param updated the updated PC
	 * @param partyName the party name the PC belongs to
	 * @param pcName PC's name before change
	 * @return the number of rows affected
	 */
	public int updatePlayerRecord( Creature updated, String partyName, String pcName ) {
		String whereClause = 
				Players_Table.COL_PartyName + " = ? AND " +
				Players_Table.COL_PCName + " = ?";
		String [] whereArgs = { partyName, pcName };
		
		ContentValues initVal = new ContentValues();
		initVal.put( Players_Table.COL_PartyName, partyName );
		initVal.put( Players_Table.COL_PCName, updated.getCreatureName() );
		initVal.put( Players_Table.COL_InitBonus, updated.getInitMod() );
		initVal.put( Players_Table.COL_ArmorClass, updated.getArmorClass() );
		initVal.put( Players_Table.COL_MaxHP, updated.getMaxHitPoints() );
        initVal.put(Players_Table.COL_HitDie, updated.getHitDie());
        initVal.put( Players_Table.COL_STR, updated.getStrength() );
        initVal.put( Players_Table.COL_DEX, updated.getDexterity() );
        initVal.put( Players_Table.COL_CON, updated.getConstitution() );
        initVal.put( Players_Table.COL_INT, updated.getIntelligence() );
        initVal.put( Players_Table.COL_WIS, updated.getWisdom() );
        initVal.put( Players_Table.COL_CHA, updated.getCharisma() );
        initVal.put( Players_Table.COL_FORT, updated.getFortitude() );
        initVal.put( Players_Table.COL_REF, updated.getReflex() );
        initVal.put( Players_Table.COL_WILL, updated.getWill() );

		
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
	public int updateCreatureInCatalog( Creature updated, String oldCreatureName ) {
		String whereClause = Catalog_Table.COL_CreatureName + " = ?";
		String [] whereArgs = { oldCreatureName };
		
		ContentValues initVal = new ContentValues();
		initVal.put( Catalog_Table.COL_CreatureName, updated.getCreatureName() );
		initVal.put( Catalog_Table.COL_InitBonus, updated.getInitMod() );
		initVal.put( Catalog_Table.COL_ArmorClass, updated.getArmorClass() );
		initVal.put( Catalog_Table.COL_MaxHP, updated.getMaxHitPoints() );
        initVal.put(Catalog_Table.COL_HitDie, updated.getHitDie());
        initVal.put( Catalog_Table.COL_STR, updated.getStrength() );
        initVal.put( Catalog_Table.COL_DEX, updated.getDexterity() );
        initVal.put( Catalog_Table.COL_CON, updated.getConstitution() );
        initVal.put( Catalog_Table.COL_INT, updated.getIntelligence() );
        initVal.put( Catalog_Table.COL_WIS, updated.getWisdom() );
        initVal.put( Catalog_Table.COL_CHA, updated.getCharisma() );
        initVal.put( Catalog_Table.COL_FORT, updated.getFortitude() );
        initVal.put( Catalog_Table.COL_REF, updated.getReflex() );
        initVal.put( Catalog_Table.COL_WILL, updated.getWill() );
		
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
	 * @return the number of rows affected
	 */
	public int updateEncountersForCreatures( String newEncName, String oldEncName ) {
		String whereClause = Creatures_Table.COL_Encounter + " =?";
		String [] whereArgs = { oldEncName };
		
		ContentValues initVal = new ContentValues();
		initVal.put( Creatures_Table.COL_Encounter, newEncName );
		
		return db.update( Creatures_Table.TABLE_NAME, initVal, whereClause, whereArgs );
	}
	
	/**
	 * Update a creature in the Creatures table.
	 * @param oldName old name of creature
	 * @param encounterName the encounter creature belongs to
     * @param updated the updated Creature object
	 * @return the number of rows affected
	 */
	public int updateCreatureRecord( Creature updated, String encounterName, String oldName ) {
		String whereClause = 
				Creatures_Table.COL_Encounter + " = ? AND " +
				Creatures_Table.COL_CreatureName + " = ?";
		String [] whereArgs = { encounterName, oldName };
		
		ContentValues initVal = new ContentValues();
		initVal.put( Creatures_Table.COL_CreatureName,updated.getCreatureName() );
		initVal.put( Creatures_Table.COL_InitBonus, updated.getInitMod() );
		initVal.put( Creatures_Table.COL_ArmorClass, updated.getArmorClass() );
		initVal.put( Creatures_Table.COL_MaxHP, updated.getMaxHitPoints() );
        initVal.put(Creatures_Table.COL_HitDie, updated.getHitDie());
        initVal.put( Creatures_Table.COL_STR, updated.getStrength() );
        initVal.put( Creatures_Table.COL_DEX, updated.getDexterity() );
        initVal.put( Creatures_Table.COL_CON, updated.getConstitution() );
        initVal.put( Creatures_Table.COL_INT, updated.getIntelligence() );
        initVal.put( Creatures_Table.COL_WIS, updated.getWisdom() );
        initVal.put( Creatures_Table.COL_CHA, updated.getCharisma() );
        initVal.put( Creatures_Table.COL_FORT, updated.getFortitude() );
        initVal.put( Creatures_Table.COL_REF, updated.getReflex() );
        initVal.put( Creatures_Table.COL_WILL, updated.getWill() );
		
		return db.updateWithOnConflict( Creatures_Table.TABLE_NAME, initVal, whereClause, whereArgs, SQLiteDatabase.CONFLICT_IGNORE );
	}

    public int updateCreatureInInitiative(Creature updated, String nameOfOldCreature) {
        String whereClause = Init_Table.COL_CreatureName + " = ?";
        String [] whereArgs = {nameOfOldCreature};

        ContentValues initVal = new ContentValues();
        initVal.put(Init_Table.COL_Init, updated.getInitiative());
        initVal.put( Init_Table.COL_CreatureName, updated.getCreatureName() );
        initVal.put( Init_Table.COL_InitBonus, updated.getInitMod() );
        initVal.put( Init_Table.COL_ArmorClass, updated.getArmorClass() );
        initVal.put(Init_Table.COL_CurrentHP, updated.getCurrentHitPoints());
        initVal.put( Init_Table.COL_MaxHP, updated.getMaxHitPoints() );
        initVal.put(Init_Table.COL_HitDie, updated.getHitDie());
        initVal.put( Init_Table.COL_STR, updated.getStrength() );
        initVal.put( Init_Table.COL_DEX, updated.getDexterity() );
        initVal.put( Init_Table.COL_CON, updated.getConstitution() );
        initVal.put( Init_Table.COL_INT, updated.getIntelligence() );
        initVal.put( Init_Table.COL_WIS, updated.getWisdom() );
        initVal.put( Init_Table.COL_CHA, updated.getCharisma() );
        initVal.put( Init_Table.COL_FORT, updated.getFortitude() );
        initVal.put( Init_Table.COL_REF, updated.getReflex() );
        initVal.put( Init_Table.COL_WILL, updated.getWill() );
        initVal.put(Init_Table.COL_Type, updated.isMonster() == false ? 0 : 1);

        return db.updateWithOnConflict(Init_Table.TABLE_NAME, initVal, whereClause, whereArgs, SQLiteDatabase.CONFLICT_REPLACE);
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
	public int deleteEncounterRecord(String encounterName) {
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
	
	/**
	 * Delete a creature from the Creatures table
	 * @param encounterName the encounter creature belongs to
	 * @param creatureName the name of the creature to delete
	 * @return the number of rows affected
	 */
	public int deleteCreature( String encounterName, String creatureName ) {
		String whereClause = 
				Creatures_Table.COL_Encounter + " = ? AND " +
				Creatures_Table.COL_CreatureName + " = ?";
		String [] whereArgs = { encounterName, creatureName };
		return db.delete( Creatures_Table.TABLE_NAME, whereClause, whereArgs );
	}
	
	/**
	 * Delete everything.
	 * @return number of rows affected.
	 */
	public int deleteInitListAll() {
		return db.delete( Init_Table.TABLE_NAME, null, null );
	}
	
	/**
	 * Delete only PCs from the init list.
	 * @return number of rows affected
	 */
	public int deleteInitListPCs() {
		String whereClause = Init_Table.COL_Type + " = ?";
		String [] whereArgs = { String.valueOf( 0 ) };
		return db.delete( Init_Table.TABLE_NAME, whereClause, whereArgs );
	}
	
	/**
	 * Delete only monsters from the init list.
	 * @return number of rows affected
	 */
	public int deleteInitListMonsters() {
		String whereClause = Init_Table.COL_Type + " = ?";
		String [] whereArgs = { String.valueOf( 1 ) };
		return db.delete( Init_Table.TABLE_NAME, whereClause, whereArgs );
	}

    public void deleteNonCustomCreatures() {
        db.execSQL( "DELETE FROM " + Catalog_Table.TABLE_NAME + " WHERE CUSTOM = 0;" );
    }

    public int deleteCreatureFromInit(String creatureName) {
        String whereClause = Init_Table.COL_CreatureName + " = ?";
        String [] whereArgs = {creatureName};
        return db.delete(Init_Table.TABLE_NAME, whereClause, whereArgs);
    }
}