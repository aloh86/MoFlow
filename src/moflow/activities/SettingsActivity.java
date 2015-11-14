package moflow.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import moflow.dialogs.SimpleMsgDialog;
import moflow.fragment.SettingsFragment;
import moflow.threads.LoadCatalogTask;

/**
 * Created by Alex on 8/4/14.
 */
public class SettingsActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    SimpleMsgDialog openSrcDialog;
    ProgressBar progressBar;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        openSrcDialog = SimpleMsgDialog.newInstance(getString( R.string.openSourceHelpCatalog ));

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        progressBar = (ProgressBar) findViewById(R.id.catalogLoadProgressBar);

        // onSharedPreferenceChanged will be called the very first time SettingsActivity
        // is accessed and before view is created. progressBar will be null, resulting in a crash
        // if passed to the LoadCatalogTask, thus the check. This method is called only if a
        // default value for the preference is set.
//        if (progressBar != null) {
//            if (key.equals("pref_catalogEdition")) {
//                int catalogVersion = Integer.parseInt(sharedPreferences.getString("pref_catalogEdition", "-1"));
//                LoadCatalogTask catalogTask = new LoadCatalogTask(this, progressBar, catalogVersion);
//                catalogTask.execute();
//
//                if (catalogVersion == 3 || catalogVersion == 4)
//                    openSrcDialog.show(getFragmentManager(), "openSourceHelpDialog");
//            }
//        }
    }
}
