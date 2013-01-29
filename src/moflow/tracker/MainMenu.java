package moflow.tracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import moflow.adapters.*;

public class MainMenu extends Activity implements OnItemClickListener {
	
	private enum Position { PC_MANAGER, ENC_MANAGER, CREATURE, INIT, OPTIONS, MANUAL };
	private GridView grid;
	
	
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.main );
		
		MenuGridAdapter gridAdapter = new MenuGridAdapter( getApplicationContext() );
		
		grid = ( GridView ) findViewById( R.id.menuGridView );
		grid.setAdapter( gridAdapter );
		grid.setOnItemClickListener( this );
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
		if ( position == Position.PC_MANAGER.ordinal() )
			startActivity( new Intent( "moflow.tracker.PCM_PartyList" ) );
	}
}