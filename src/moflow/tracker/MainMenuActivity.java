package moflow.tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import moflow.adapters.*;
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
	
	private enum Position { PC_MANAGER, ENC_MANAGER, CREATURE, INIT, MANUAL };
	private GridView grid;
	private LinearLayout mainRLayout;
	
	/**
	 * Initializes the main menu.
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.main );
		
		MenuGridAdapter gridAdapter = new MenuGridAdapter( getApplicationContext() );
		
		mainRLayout = ( LinearLayout ) findViewById( R.id.mainRlayout );
		//mainRLayout.setBackgroundResource( R.drawable.moflow_main_screen );
		
		grid = ( GridView ) findViewById( R.id.menuGridView );
		grid.setAdapter( gridAdapter );
		grid.setOnItemClickListener( this );
	}

	/**
	 * Handler for the grid item clicks.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
		if ( position == Position.PC_MANAGER.ordinal() )
			startActivity( new Intent( "moflow.tracker.PCM_PartyListActivity" ) );
		else if ( position == Position.ENC_MANAGER.ordinal() )
			startActivity( new Intent( "moflow.tracker.EncounterListActivity" ) );
		else if ( position == Position.CREATURE.ordinal() )
			startActivity( new Intent( "moflow.tracker.CreatureCatalogActivity" ) );
		else if ( position == Position.INIT.ordinal() )
			startActivity( new Intent( "moflow.tracker.InitiativeActivity" ) );
		else if ( position == Position.MANUAL.ordinal() )
			startActivity( new Intent( "moflow.tracker.ManualActivity" ) );
	}
}