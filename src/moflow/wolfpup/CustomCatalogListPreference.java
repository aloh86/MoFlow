package moflow.wolfpup;

import android.content.Context;
import android.preference.ListPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import moflow.activities.R;
import org.w3c.dom.Text;

/**
 * Created by Alex on 9/27/14.
 */
public class CustomCatalogListPreference extends ListPreference {

    Context mContext;

    public CustomCatalogListPreference( Context context ) {
        super(context);
        setLayoutResource( R.layout.custpref_catalog_list );
    }

    @Override
    protected View onCreateView( ViewGroup parent ) {
        super.onCreateView( parent );
        LayoutInflater inflater = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate( R.layout.custpref_catalog_list, parent );

        TextView title = new TextView( mContext );
        title.setText( this.getTitle() );

        TextView summary = new TextView( mContext );
        summary.setText( this.getSummary() );

        //ProgressBar progBar = ( ProgressBar ) view.findViewById( R.id.catalogLoadProgressBar );

        return view;
    }
}
