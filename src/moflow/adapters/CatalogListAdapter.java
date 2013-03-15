package moflow.adapters;

import java.util.List;

import moflow.tracker.CatalogItem;
import moflow.tracker.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class CatalogListAdapter extends ArrayAdapter< CatalogItem > {
	private List< CatalogItem > catalog;
	Context mContext;
	LayoutInflater inflater;
	
	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;
	private static final int MAX_TYPES = 2;
	
	public CatalogListAdapter( Context context, int textViewResourceId, List< CatalogItem > objects ) {
		super( context, textViewResourceId, objects );
		mContext = context;
		catalog = objects;
		inflater = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	}
	
	@Override
	public int getCount() {
		return catalog.size();
	}
	
	@Override
	public int getItemViewType( int position ) {
		
		if ( catalog.get( position ).header == true )
			return TYPE_SEPARATOR;
		
		return TYPE_ITEM;
	}
	
	@Override
	public int getViewTypeCount() {
		return MAX_TYPES;
	}
	
	@Override
	public boolean isEnabled( int position ) {
		if ( catalog.get( position ).header )
			return false;
		
		return true;
	}
	
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		ViewHolder holder = null;
		int type = getItemViewType( position );
		
		if ( convertView == null ) {
			holder = new ViewHolder();
			switch( type ) {
				case TYPE_ITEM:
					convertView = inflater.inflate( R.layout.sec_item, null );
					holder.tv = ( TextView ) convertView.findViewById( R.id.listCreatureNameTV );
					break;
				case TYPE_SEPARATOR:
					convertView = inflater.inflate( R.layout.sec_header, null );
					holder.tv = ( TextView ) convertView.findViewById( R.id.listHeaderTV );
					break;
			}
			convertView.setTag( holder );
		} else {
			holder = ( ViewHolder ) convertView.getTag();
		}
		
		CatalogItem item = getItem( position );
		holder.tv.setText( item.name );
		
		return convertView;
	}
	
	public static class ViewHolder {
		public TextView tv;
	}
}
