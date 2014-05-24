package moflow.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Alex on 5/20/14.
 */
public class UpgradeDB {
    private ArrayList< String > v1;

    public UpgradeDB() {
        V1_Upgrade_Init();
    }

    public void execV1Upgrades( SQLiteDatabase db ) {
        for ( int i = 0; i < v1.size(); i++ ) {
            db.execSQL( v1.get( i ) );
        }
    }

    private void V1_Upgrade_Init() {
        v1 = new ArrayList<String>();

        // Players table upgrades
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD COLUMN " + Players_Table.COL_STR + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD COLUMN " + Players_Table.COL_DEX + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD COLUMN " + Players_Table.COL_CON + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD COLUMN " + Players_Table.COL_INT + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD COLUMN " + Players_Table.COL_WIS + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD COLUMN " + Players_Table.COL_CHA + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD COLUMN " + Players_Table.COL_FORT + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD COLUMN " + Players_Table.COL_REF + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD COLUMN " + Players_Table.COL_WILL + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD FOREIGN KEY (" + Players_Table.COL_PartyName +
                ") REFERENCES " + Parties_Table.TABLE_NAME + " (" + Parties_Table.COL_PartyName + ") " +
                "ON UPGRADE CASCADE ON DELETE CASCADE;" );

        // Creatures table upgrades
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD COLUMN " + Creatures_Table.COL_STR + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD COLUMN " + Creatures_Table.COL_DEX + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD COLUMN " + Creatures_Table.COL_CON + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD COLUMN " + Creatures_Table.COL_INT + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD COLUMN " + Creatures_Table.COL_WIS + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD COLUMN " + Creatures_Table.COL_CHA + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD COLUMN " + Creatures_Table.COL_FORT + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD COLUMN " + Creatures_Table.COL_REF + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD COLUMN " + Creatures_Table.COL_WILL + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD FOREIGN KEY (" + Creatures_Table.COL_Encounter +
                ") REFERENCES " + Encounters_Table.TABLE_NAME + " (" + Encounters_Table.COL_Encounter + ") " +
                "ON UPGRADE CASCADE ON DELETE CASCADE;" );

        // catalog table upgrades
        v1.add( "ALTER TABLE " + Catalog_Table.TABLE_NAME + " ADD COLUMN " + Catalog_Table.COL_STR + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Catalog_Table.TABLE_NAME + " ADD COLUMN " + Catalog_Table.COL_DEX + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Catalog_Table.TABLE_NAME + " ADD COLUMN " + Catalog_Table.COL_CON + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Catalog_Table.TABLE_NAME + " ADD COLUMN " + Catalog_Table.COL_INT + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Catalog_Table.TABLE_NAME + " ADD COLUMN " + Catalog_Table.COL_WIS + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Catalog_Table.TABLE_NAME + " ADD COLUMN " + Catalog_Table.COL_CHA + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Catalog_Table.TABLE_NAME + " ADD COLUMN " + Catalog_Table.COL_FORT + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Catalog_Table.TABLE_NAME + " ADD COLUMN " + Catalog_Table.COL_REF + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Catalog_Table.TABLE_NAME + " ADD COLUMN " + Catalog_Table.COL_WILL + " INTEGER NOT NULL DEFAULT 0;" );

        // initiative table upgrades
        v1.add( "ALTER TABLE " + Init_Table.TABLE_NAME + " ADD COLUMN " + Init_Table.COL_STR + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Init_Table.TABLE_NAME + " ADD COLUMN " + Init_Table.COL_DEX + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Init_Table.TABLE_NAME + " ADD COLUMN " + Init_Table.COL_CON + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Init_Table.TABLE_NAME + " ADD COLUMN " + Init_Table.COL_INT + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Init_Table.TABLE_NAME + " ADD COLUMN " + Init_Table.COL_WIS + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Init_Table.TABLE_NAME + " ADD COLUMN " + Init_Table.COL_CHA + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Init_Table.TABLE_NAME + " ADD COLUMN " + Init_Table.COL_FORT + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Init_Table.TABLE_NAME + " ADD COLUMN " + Init_Table.COL_REF + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Init_Table.TABLE_NAME + " ADD COLUMN " + Init_Table.COL_WILL + " INTEGER NOT NULL DEFAULT 0;" );
        v1.add( "ALTER TABLE " + Init_Table.TABLE_NAME + " DROP FOREIGN KEY " + Init_Table.COL_CreatureName + ";" );

        // drop condition table and rebuild it.
        v1.add( "DROP TABLE " + Condition_Table.TABLE_NAME + ";" );
    }




}
