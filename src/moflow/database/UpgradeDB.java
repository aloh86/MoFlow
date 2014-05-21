package moflow.database;

import java.util.ArrayList;

/**
 * Created by Alex on 5/20/14.
 */
public class UpgradeDB {

    public UpgradeDB() {
        V1_Upgrade_Init();
    }

    private void V1_Upgrade_Init() {
        Players_Table.V1_UPGRADE = new ArrayList<String>();
        ArrayList< String > v1 = Players_Table.V1_UPGRADE;

        // Players table upgrades.
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD COLUMN " + Players_Table.COL_STR + " INTEGER;" );
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD COLUMN " + Players_Table.COL_DEX + " INTEGER;" );
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD COLUMN " + Players_Table.COL_CON + " INTEGER;" );
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD COLUMN " + Players_Table.COL_INT + " INTEGER;" );
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD COLUMN " + Players_Table.COL_WIS + " INTEGER;" );
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD COLUMN " + Players_Table.COL_CHA + " INTEGER;" );
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD COLUMN " + Players_Table.COL_FORT + " INTEGER;" );
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD COLUMN " + Players_Table.COL_REF + " INTEGER;" );
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD COLUMN " + Players_Table.COL_WILL + " INTEGER;" );
        v1.add( "ALTER TABLE " + Players_Table.TABLE_NAME + " ADD FOREIGN KEY (" + Players_Table.COL_PartyName +
                ") REFERENCES " + Parties_Table.TABLE_NAME + " (" + Parties_Table.COL_PartyName + ") " +
                "ON UPGRADE CASCADE ON DELETE CASCADE;" );

        // Creatures table upgrades.
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD COLUMN " + Creatures_Table.COL_STR + " INTEGER;" );
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD COLUMN " + Creatures_Table.COL_DEX + " INTEGER;" );
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD COLUMN " + Creatures_Table.COL_CON + " INTEGER;" );
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD COLUMN " + Creatures_Table.COL_INT + " INTEGER;" );
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD COLUMN " + Creatures_Table.COL_WIS + " INTEGER;" );
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD COLUMN " + Creatures_Table.COL_CHA + " INTEGER;" );
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD COLUMN " + Creatures_Table.COL_FORT + " INTEGER;" );
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD COLUMN " + Creatures_Table.COL_REF + " INTEGER;" );
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD COLUMN " + Creatures_Table.COL_WILL + " INTEGER;" );
        v1.add( "ALTER TABLE " + Creatures_Table.TABLE_NAME + " ADD FOREIGN KEY (" + Creatures_Table.COL_Encounter +
                ") REFERENCES " + Encounters_Table.TABLE_NAME + " (" + Encounters_Table.COL_Encounter + ") " +
                "ON UPGRADE CASCADE ON DELETE CASCADE;" );
    }


}
