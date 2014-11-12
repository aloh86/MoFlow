package moflow.utility;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;
import moflow.database.MoFlowDB;
import moflow.wolfpup.Creature;

import java.util.ArrayList;

/**
 * Created by Alex on 5/24/14.
 */
public class DBTransaction {
    private MoFlowDB db;
    private Cursor cur;

    public DBTransaction(Context ctx) {
        db = new MoFlowDB( ctx );

        try {
            db.open();
        } catch ( SQLiteException e ) {
            Toast.makeText( ctx, "Database could not be opened!", Toast.LENGTH_LONG );
        }
    }

    public void closeDB()
    {
        db.close();
    }

    // Retrieval

    public ArrayList< String > getGroupList( String groupType ) {
        if ( groupType.equals( Key.Val.PARTY) )
            cur = db.getAllParties();
        else
            cur = db.getAllEncounters();

        ArrayList< String > groupList = new ArrayList<String>();

        while ( cur.moveToNext() ) {
            // there is only 1 column in the parties table, hence 0 for getString
            groupList.add(cur.getString(0));
        }
        cur.close();

        return groupList;
    }

    public ArrayList< Creature > getGroupItemList( String groupType, String groupName ) {
        if ( groupType.equals( Key.Val.PARTY) )
            cur = db.getPCForGroup( groupName );
        else
            cur = db.getCreaturesForEncounter( groupName );

        ArrayList< Creature > creatureList = new ArrayList();

        final int ADJUST = -1;

        // COLUMN ORDER, pc
        // PCName = 0, init = 1, etc...

        while ( cur.moveToNext() ) {
            Creature critter = new Creature();
            critter.setCreatureName(cur.getString(0));
            critter.setInitMod(cur.getString(1));
            critter.setArmorClass(cur.getString(2));
            critter.setMaxHitPoints(cur.getString(3));
            critter.setHitDie(cur.getString((4)));
            critter.setStrength(cur.getString(5));
            critter.setDexterity(cur.getString(6));
            critter.setConstitution(cur.getString(7));
            critter.setIntelligence(cur.getString(8));
            critter.setWisdom(cur.getString(9));
            critter.setCharisma(cur.getString(10));
            critter.setFortitude(cur.getString(11));
            critter.setReflex(cur.getString(12));
            critter.setWill(cur.getString(13));

            creatureList.add( critter );
        }

        return creatureList;
    }

    public ArrayList<String> getCatalogItemList() {
        cur = db.getCatalog();
        ArrayList<String> list = new ArrayList<String>();

        while ( cur.moveToNext() ) {
            list.add( cur.getString( 0 ) );
        }

        return list;
    }

    public Creature getCreatureFromCatalog( String creatureName ) {
        cur = db.getCreatureFromCatalog( creatureName );
        Creature critter = new Creature();

        final int ADJUST = -1;

        while ( cur.moveToNext() ) {
            critter.setCreatureName(cur.getString(0));
            critter.setInitMod(cur.getString(1));
            critter.setArmorClass(cur.getString(2));
            critter.setMaxHitPoints(cur.getString(3));
            critter.setHitDie(cur.getString((4)));
            critter.setStrength(cur.getString(5));
            critter.setDexterity(cur.getString(6));
            critter.setConstitution(cur.getString(7));
            critter.setIntelligence(cur.getString(8));
            critter.setWisdom(cur.getString(9));
            critter.setCharisma(cur.getString(10));
            critter.setFortitude(cur.getString(11));
            critter.setReflex(cur.getString(12));
            critter.setWill(cur.getString(13));
        }
        return critter;
    }

    public ArrayList<Creature> getInitiativeItems() {
        cur = db.getInitListFromDB();

        ArrayList< Creature > creatureList = new ArrayList();

        final int ADJUST = 1;

        while ( cur.moveToNext() ) {
            Creature critter = new Creature();
            critter.setInitiative(cur.getString(1));
            critter.setCreatureName(cur.getString(2));
            critter.setInitMod(cur.getString(3));
            critter.setArmorClass(cur.getString(4));
            critter.setCurrentHitPoints(cur.getString(5));
            critter.setMaxHitPoints(cur.getString(6));
            critter.setHitDie(cur.getString(7));
            critter.setAsMonster((cur.getInt(8) == 0 ? false : true));
            critter.setStrength(cur.getString(9));
            critter.setDexterity(cur.getString(10));
            critter.setConstitution(cur.getString(11));
            critter.setIntelligence(cur.getString(12));
            critter.setWisdom(cur.getString(13));
            critter.setCharisma(cur.getString(14));
            critter.setFortitude(cur.getString(15));
            critter.setReflex(cur.getString(16));
            critter.setWill(cur.getString(17));

            creatureList.add( critter );
        }

        return creatureList;
    }

    // Insertion

    // for new parties or encounters
    public void insertNewGroup( String groupName, String groupType ) {
        if ( groupType.equals( Key.Val.PARTY) )
            db.insertParty( groupName );
        else
            db.insertEncounter( groupName );
    }

    // for new player characters or monsters
    public void insertNewCreature( String groupName, Creature critter, String groupType ) {
        if ( groupType.equals( Key.Val.PARTY) )
            db.insertPlayer( groupName, critter );
        else
            db.insertCreature( groupName, critter );
    }

    public void insertNewCreatureIntoCatalog(Creature critter)
    {
        db.insertCreatureInCatalog(critter);
    }

    // catalog creatures from raw file
    public void insertCreaturesFromFile( ArrayList<String []> creatures, int version ) {
        if ( version == 5 ) {
            for ( String [] s : creatures )
                db.insertFileCreatureInCatalog5e( s );
        }
    }

    public void insertNewCreatureIntoInitiative(Creature critter) {
        db.insertCreatureIntoInitiative(critter);
    }

    // Deletion

    public void deleteGroup( final ArrayList< String > toDelete, String groupType ) {
        for ( int i = 0; i < toDelete.size(); i++ ) {
            if ( groupType.equals( Key.Val.PARTY) )
                db.deletePartyRecord( toDelete.get( i ) );
            else
                db.deleteEncounterRecord( toDelete.get( i ) );
        }
    }

    public void deleteCreatureFromGroup( final ArrayList< Creature > toDelete, String groupName, String groupType ) {
        for ( int i = 0; i < toDelete.size(); i++ ) {
            if ( groupType.equals( Key.Val.PARTY) )
                db.deletePC( groupName, toDelete.get( i ).getCreatureName() );
            else
                db.deleteCreature( groupName, toDelete.get( i ).getCreatureName() );
        }
    }

    public void deleteCreatureFromCatalog(ArrayList<String> toDelete) {
        for (int i = 0; i < toDelete.size(); i++) {
            db.deleteCreatureFromCatalog(toDelete.get(i));
        }
    }

    public void deleteNonCustomCreatures()
    {
        db.deleteNonCustomCreatures();
    }

    public void deleteAllFromInitiative() { db.deleteInitListAll(); }

    public void deletePCsFromInitiative() { db.deleteInitListPCs(); }

    public void deleteMonstersFromInitiative() { db.deleteInitListMonsters(); }

    // Modification

    public void renameGroup( String newName, String oldName, String groupType ) {
        if ( groupType.equals( Key.Val.PARTY) )
            db.updatePartyRecord( newName, oldName );
        else
            db.updateEncounterRecord( newName, oldName );
    }

    public void updateExistingCreature( Creature critter, String groupName, String creatureNamePreChange, String groupType ) {
        if ( groupType.equals( Key.Val.PARTY) )
            db.updatePlayerRecord( critter, groupName, creatureNamePreChange );
        else
            db.updateCreatureRecord( critter, groupName, creatureNamePreChange );
    }
    
    public void updateCatalogCreature( Creature updated, String oldName ) {
        db.updateCreatureInCatalog(updated, oldName);
    }

    public void updateCreatureInInit(Creature updated, String oldCreatureName) {
        db.updateCreatureInInitiative(updated, oldCreatureName);
    }
}
