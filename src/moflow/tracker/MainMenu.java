package moflow.tracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainMenu extends Activity 
{
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.main );
		
		Button manageBtn = ( Button ) findViewById( R.id.manageBtn );
		manageBtn.setOnClickListener(
				new View.OnClickListener() 
				{	
					@Override
					public void onClick(View v) 
					{
						startActivity( 
								new Intent( "moflow.tracker.ManagerScreen" ) );
					}
				});
		
		Button optionsBtn = ( Button ) findViewById( R.id.optionsBtn );
		optionsBtn.setOnClickListener( 
				new View.OnClickListener() 
				{
					@Override
					public void onClick( View v ) 
					{
						startActivity( 
								new Intent( "moflow.tracker.OptionsScreen" ) );
					}
				});
		
		Button aboutBtn = ( Button ) findViewById( R.id.aboutBtn );
		aboutBtn.setOnClickListener( 
				new View.OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						final int DIALOG_CASE = 0;
						showDialog( DIALOG_CASE );
					}
				});
		
		Button quitBtn = ( Button ) findViewById( R.id.quitBtn );
		quitBtn.setOnClickListener(
				new View.OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						finish();
					}
				});
	}
	
	
	@Override
	protected Dialog onCreateDialog( int id )
	{
		switch( id )
		{
			case 0:
				return new AlertDialog.Builder( this )
				.setTitle( "About MoFlow")
				.setMessage( R.string.about_info )
				.setPositiveButton( "Close", 
						new DialogInterface.OnClickListener() 
						{	
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								// do nothing
							}
						})
				.create();
		}
		return null;
	}
}