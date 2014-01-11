package moflow.activities;

import moflow.tracker.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class ManualActivity extends Activity {

	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.manual );
	}
}
