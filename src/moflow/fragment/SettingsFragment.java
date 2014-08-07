package moflow.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import moflow.activities.R;

/**
 * Created by Alex on 8/4/14.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource( R.xml.preferences );
    }

}
