package moflow.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


import android.widget.Toast;
import moflow.adapters.*;
import moflow.dialogs.DonateDialogFragment;
import moflow.dialogs.HelpDialogFragment;
import moflow.utility.CommonKey;
/*
===============================================================================
MainMenuActivity.java

Activity for the main menu. This Activity starts when the program is first run.
The main menu is the access point for all of the application's main features:
1) PC Manager 2) Encounter Manager 3) Creature Catalog 4) Initiative Tracker
as well as options and a user manual.
===============================================================================
*/

public class MainMenuActivity extends Activity implements OnItemClickListener {

    private enum Position { PC_MANAGER, ENC_MANAGER, CREATURE, INIT, OPTIONS };
	private HelpDialogFragment helpDlg;
    private DonateDialogFragment donateDlg;
	/**
	 * Initializes the main menu.
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );
		
		MainMenuListAdapter gridAdapter = new MainMenuListAdapter( getApplicationContext() );

        ListView list;
		list = ( ListView ) findViewById( R.id.menuListView );
		list.setAdapter( gridAdapter );
		list.setOnItemClickListener( this );

        helpDlg = new HelpDialogFragment();
        donateDlg = new DonateDialogFragment();
	}

    /**
     * Creates action bar menu.
     */
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.abm_main, menu );
        return super.onCreateOptionsMenu( menu );
    }

    /**
     * Action bar item handling.
     */
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle presses on the action bar items
        switch ( item.getItemId() ) {
            case R.id.actSet_main_help:
                helpDlg.show( getFragmentManager(), "helpDlg" );
                break;
            case R.id.actSet_main_feature:
                startEmailIntent();
                break;
            case R.id.actSet_main_donate:
                donateDlg.show( getFragmentManager(), "donateDlg" );
                break;
            default:
                return super.onOptionsItemSelected( item );
        }
        return true;
    }

	/**
	 * Handler for the list item clicks.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
		if ( position == Position.PC_MANAGER.ordinal() ) {
            Intent intent = new Intent( "moflow.activities.GroupListActivity" );
            intent.putExtra( CommonKey.KEY_GROUP_TYPE, CommonKey.VAL_PARTY );
			startActivityForResult(intent, 1); // not using request code, can be any value
        }
		else if ( position == Position.ENC_MANAGER.ordinal() ) {
            Intent intent = new Intent( "moflow.activities.GroupListActivity" );
            intent.putExtra( CommonKey.KEY_GROUP_TYPE, CommonKey.VAL_ENC );
            startActivityForResult(intent, 1); // not using request code, can be any value
        }
		else if ( position == Position.CREATURE.ordinal() ) {
            Intent intent = new Intent( "moflow.activities.CatalogActivity" );
            intent.putExtra( CommonKey.KEY_PARENT_ACTIVITY, CommonKey.VAL_FROM_MAIN );
            startActivityForResult(intent, 1); // not using request code, can be any value
        }
		else if ( position == Position.INIT.ordinal() )
			;
        else if ( position == Position.OPTIONS.ordinal() )
            startActivity( new Intent( "moflow.activities.SettingsActivity" ) );
	}

    private void startEmailIntent() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"help@wolfpupsoftware.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Feature Request");
        i.putExtra(Intent.EXTRA_TEXT   , "");
        try {
            startActivity( Intent.createChooser( i, "Send mail...") );
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText( this, "There are no email clients installed.", Toast.LENGTH_SHORT ).show();
        }
    }
}