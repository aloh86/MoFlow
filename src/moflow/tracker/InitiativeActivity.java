package moflow.tracker;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.TextView;
/*
===============================================================================
InitiativeActivity.java

Keeps track of initiative.
===============================================================================
*/

public class InitiativeActivity extends ListActivity 
implements OnClickListener, OnItemClickListener, OnItemLongClickListener, android.content.DialogInterface.OnClickListener {
	
	TextView roundsText;
	
	Button prevButton;
	Button nextButton;
	
	AlertDialog surpriseDialog;
	AlertDialog itemDialog;
	AlertDialog itemEditDialog;
	AlertDialog graveDialog;
	
	ArrayList< Moflow_Creature > initList;
	
	//////////////////////////////////////////////////////////////////////////
	// ACTIVITY OVERRIDES
	//////////////////////////////////////////////////////////////////////////
	
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.init_layout );
		
		initializeLayout();
		initializeDialogs();
	}
	
	//////////////////////////////////////////////////////////////////////////
	// PRIVATE HELPERS
	//////////////////////////////////////////////////////////////////////////
	
	private void initializeLayout() {
		roundsText = ( TextView ) findViewById( R.id.roundsText );
		
		prevButton = ( Button ) findViewById( R.id.prevButton );
		prevButton.setOnClickListener( this );
		
		nextButton = ( Button ) findViewById( R.id.nextButton );
		nextButton.setOnClickListener( this );
		
		this.getListView().setOnItemClickListener( this );
		this.getListView().setOnItemLongClickListener( this );
	}
	
	private void initializeDialogs() {
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		LayoutInflater inflater = this.getLayoutInflater();
		
		// setup the dialog for creating a new group
		builder.setMessage( "Start with surprise round?" );
		builder.setPositiveButton( "OK", this );
		builder.setNegativeButton( "Cancel", this );
		surpriseDialog = builder.create();
		
		builder = new AlertDialog.Builder( this );
		builder.setMessage( "Remove from list?" );
		builder.setPositiveButton( "OK", this );
		builder.setNegativeButton( "Cancel", this );
		graveDialog = builder.create();
	}

	//////////////////////////////////////////////////////////////////////////
	// LISTENERS
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
