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

    // column order of players table
    private final int PARTY_NAME = 0;
    private final int PC_NAME = 1;
    private final int INIT_BONUS = 2;
    private final int ARMOR_CLASS = 3;
    private final int MAX_HP = 4;
    private final int STR = 5;
    private final int DEX = 6;
    private final int CON = 7;
    private final int INT = 8;
    private final int WIS = 9;
    private final int CHA = 10;
    private final int FORT = 11;
    private final int REF = 12;
    private final int WILL = 13;

    public DBTransaction(Context ctx) {
        db = new MoFlowDB( ctx );

        try {
            db.open();
        } catch ( SQLiteException e ) {
            Toast.makeText( ctx, "Database could not be opened!", Toast.LENGTH_LONG );
        }
    }

    public void closeDB() {
        db.close();
    }

    // Retrieval

    public ArrayList< String > getGroupList( String groupType ) {
        if ( groupType.equals( CommonKey.VAL_PARTY ) )
            cur = db.getAllParties();
        else
            cur = db.getAllEncounters();

        ArrayList< String > groupList = new ArrayList<String>();

        while ( cur.moveToNext() ) {
            // there is only 1 column in the parties table, hence 0 for getString
            groupList.add( cur.getString( PARTY_NAME ) );
        }
        cur.close();

        return groupList;
    }

    public ArrayList< Creature > getGroupItemList( String groupType, String groupName ) {
        if ( groupType.equals( CommonKey.VAL_PARTY ) )
            cur = db.getPCForGroup( groupName );
        else
            cur = db.getCreaturesForEncounter( groupName );

        ArrayList< Creature > creatureList = new ArrayList();

        final int ADJUST = -1;

        while ( cur.moveToNext() ) {
            Creature critter = new Creature();
            critter.setCreatureName( cur.getString( PC_NAME + ADJUST ) );
            critter.setInitMod( cur.getString( INIT_BONUS + ADJUST ) );
            critter.setArmorClass( cur.getString( ARMOR_CLASS + ADJUST ) );
            critter.setMaxHitPoints( cur.getString( MAX_HP + ADJUST ) );
            critter.setStrength( cur.getString( STR + ADJUST ) );
            critter.setDexterity( cur.getString( DEX + ADJUST ) );
            critter.setConstitution( cur.getString( CON + ADJUST ) );
            critter.setIntelligence( cur.getString( INT + ADJUST ) );
            critter.setWisdom( cur.getString( WIS + ADJUST ) );
            critter.setCharisma( cur.getString( CHA + ADJUST ) );
            critter.setFortitude( cur.getString( FORT + ADJUST ) );
            critter.setReflex( cur.getString( REF + ADJUST ) );
            critter.setWill( cur.getString( WILL + ADJUST ) );

            creatureList.add( critter );
        }

        return creatureList;
    }

    public ArrayList<String> getCatalogItemList()
    {
        cur = db.getCatalog();
        ArrayList<String> list = new ArrayList<String>();

        while ( cur.moveToNext() )
        {
            list.add( cur.getString( 0 ) );
        }

        return list;
    }

    // Insertion

    public void insertNewGroup( String groupName, String groupType ) {
        if ( groupType.equals( CommonKey.VAL_PARTY ) )
            db.insertParty( groupName );
        else
            db.insertEncounter( groupName );
    }

    public void insertNewCreature( String groupName, Creature critter, String groupType ) {
        if ( groupType.equals( CommonKey.VAL_PARTY ) )
            db.insertPlayer( groupName, critter );
        else
            db.insertCreature( groupName, critter );
    }

    public void insertCreaturesFromFile( ArrayList<String []> creatures, int version ) {
        if ( version == 5 ) {
            for ( String [] s : creatures )
                db.insertFileCreatureInCatalog5e( s );
        }
    }

    // Deletion

    public void deleteGroup( final ArrayList< String > toDelete, String groupType ) {
        for ( int i = 0; i < toDelete.size(); i++ ) {
            if ( groupType.equals( CommonKey.VAL_PARTY ) )
                db.deletePartyRecord( toDelete.get( i ) );
            else
                db.deleteEncounterRecord( toDelete.get( i ) );
        }
    }

    public void deleteCreatureFromGroup( final ArrayList< Creature > toDelete, String groupName, String groupType ) {
        for ( int i = 0; i < toDelete.size(); i++ ) {
            if ( groupType.equals( CommonKey.VAL_PARTY ) )
                db.deletePC( groupName, toDelete.get( i ).getCreatureName() );
            else
                db.deleteCreature( groupName, toDelete.get( i ).getCreatureName() );
        }
    }

    public void deleteNonCustomCreatures() {
        db.deleteNonCustomCreatures();
    }

    // Modification

    public void renameGroup( String newName, String oldName, String groupType ) {
        if ( groupType.equals( CommonKey.VAL_PARTY ) )
            db.updatePartyRecord( newName, oldName );
        else
            db.updateEncounterRecord( newName, oldName );
    }

    public void updateExistingCreature( Creature critter, String groupName, String creatureNamePreChange, String groupType ) {
        if ( groupType.equals( CommonKey.VAL_PARTY ) )
            db.updatePlayerRecord( critter, groupName, creatureNamePreChange );
        else
            db.updateCreatureRecord( critter, groupName, creatureNamePreChange );
    }
}
