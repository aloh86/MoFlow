package moflow.tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
/*
===============================================================================
ManagerScreen.java
Alex Oh

Handles Manager screen activity.
===============================================================================
*/
public class ManagerScreen extends Activity 
{
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.management );
		
		Button pcManager = ( Button ) findViewById( R.id.pcManagerBtn );
		pcManager.setOnClickListener( 
				new View.OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						startActivity( 
								new Intent( "moflow.tracker.PCM_PartyList" ) );
					}
				});
	}
}
