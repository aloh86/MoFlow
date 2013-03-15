package moflow.tracker;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Window;

public class InitiativeActivity extends ListActivity {
	
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.init_layout );
	}
}
