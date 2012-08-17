package moflow.tracker;

import android.os.Parcel;
import android.os.Parcelable;

/*
===============================================================================
Moflow_PC.java
Alex Oh

Definition of class Moflow_PC. Object represents an individual
player character (which is also a type of creature).

This class currently has no operations but is a separate class for reasons
of flexibility should relevant operations need to be added in the future.
===============================================================================
*/
public class Moflow_PC extends Moflow_Creature implements Parcelable
{		
	//-----------------------------------------------------------------------
	/**
	 * Creates "Blank" PC with all scores set to zero and no name.
	 */
	Moflow_PC()
	{
		
	}
	
	/*
	=========================================================================
	Parcelable implementation
	=========================================================================
	*/
	/**
	 * Required static field CREATOR for Parcelable implementations 
	 */
	public static final Parcelable.Creator< Moflow_PC > CREATOR = 
		new Parcelable.Creator< Moflow_PC >()
		{
			public Moflow_PC createFromParcel( Parcel in )
			{
				return new Moflow_PC( in );
			}
			
			public Moflow_PC [] newArray( int size )
			{
				return new Moflow_PC[ size ];
			}
		};
		
	/**
	 * Constructor for reconstructing a Moflow_PC object from a Parcel
	 * @param in the parcel to read from
	 */
	private Moflow_PC( Parcel in )
	{	
		super( in );
	}
	
	/**
	 * Describe the kinds of special objects contained in this Parcelable's 
	 * marshalled representation.
	 * @return a bit mask indicating the set of special object types marshalled 
	 * by the Parcelable.
	 */
	public int describeContents()
	{
		return 0;
	}
	
	/**
	 * Flatten this object in to a Parcel.
	 * @param dest The Parcel in which the object should be written.
	 * @param Additional flags about how the object should be written.
	 */
	public void writeToParcel( Parcel dest, int flags )
	{
		super.writeToParcel( dest, flags );
	}
}
