package moflow.activities;

import android.app.Activity;
import android.os.Bundle;
import moflow.fragment.SettingsFragment;

/**
 * Created by Alex on 8/4/14.
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
