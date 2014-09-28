package moflow.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;
import moflow.fragment.SettingsFragment;

/**
 * Created by Alex on 8/4/14.
 */
public class SettingsActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        //progressBar = ( ProgressBar ) findViewById(R.id.catalogLoadProgressBar);
    }

    @Override
    public void onSharedPreferenceChanged( SharedPreferences sharedPreferences, String key ) {
        if ( key.equals( "pref_catalogEd" ) ) {

        }
    }
}
