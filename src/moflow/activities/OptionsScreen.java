package moflow.activities;

import moflow.activities.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class OptionsScreen extends Activity 
{
	static private boolean toast, confirm, off;	// related radio buttons for effects message options
	static private boolean autoRollInit;
	
	public enum Effects { TOAST, CONFIRM, OFF };

	public boolean isFxChecked( Effects choice )
	{
		if ( choice == Effects.TOAST )
			return toast;
		if ( choice == Effects.CONFIRM )
			return confirm;
		if ( choice == Effects.OFF )
			return off;
		
		return false;
	}
	
	public boolean isAutoRollChecked()
	{
		return autoRollInit;
	}
	
	
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.options );
		
		RadioGroup fxRadioGroup = ( RadioGroup ) findViewById( R.id.fxOptionGroup );
		fxRadioGroup.setOnCheckedChangeListener( 
				new OnCheckedChangeListener()
				{
					public void onCheckedChanged( RadioGroup group, int checkedId )
					{
						setFxChoice( checkedId );
					}
				});
		
		CheckBox autoRollBox = ( CheckBox ) findViewById( R.id.autoRollCheckBox );
		autoRollBox.setOnClickListener( 
				new View.OnClickListener() 
				{	
					@Override
					public void onClick(View v) 
					{
						if ( !((CheckBox)v).isChecked() )
							autoRollInit = false;
						else
							autoRollInit = true;
					}
				});
	}
	
	private void setFxChoice( int checkedId )
	{
		if ( checkedId == R.id.fxBtnToast )
		{
			toast = true;
			confirm = false;
			off = false;
		}
		else if ( checkedId == R.id.fxBtnConfirm )
		{
			toast = false;
			confirm = true;
			off = false;
		}
		else if ( checkedId == R.id.fxBtnOff )
		{
			toast = false;
			confirm = false;
			off = true;
		}
	}
	
}
