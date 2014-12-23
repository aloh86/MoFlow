package moflow.wolfpup;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import moflow.activities.R;
import moflow.threads.LoadCatalogTask;

/**
 * Created by Alex on 9/27/14.
 */
public class CustomCatalogListPreference extends ListPreference {

    Context mContext;

    public CustomCatalogListPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
    }

    public CustomCatalogListPreference(Context context)
    {
        super(context);
        mContext = context;
    }

    @Override
    public View onCreateView(ViewGroup parent)
    {
        super.onCreateView(parent);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.custpref_catalog_list, null);
    }

    @Override
    protected void onBindView(View view)
    {
        super.onBindView(view);
    }
}
