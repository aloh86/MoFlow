package moflow.activities;

import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;
/*
===============================================================================
Moflow_Party.java
Alex Oh

Definition of class Moflow_Party. Groups characters (PCs) by a party name.
===============================================================================
*/
public class Moflow_Party implements Parcelable 
{
	private String partyName;
	private ArrayList<Moflow_Creature> party;
	
	
	//-----------------------------------------------------------------------
	/**
	 * Default constructor
	 */
	public Moflow_Party()
	{
		party = new ArrayList< Moflow_Creature >();
		partyName = "";
	}
	
	/**
	 * Copy constructor
	 * @return a copy of this
	 */
	public Moflow_Party copy( Moflow_Party obj )
	{
		obj.partyName = partyName;
		
		obj.party.clear();
		for ( int i = 0; i < party.size(); i++ ) {
			obj.party.add( party.get( i ) );
		}
		
		return obj;
	}
	
	
	//-----------------------------------------------------------------------
	/**
	 * Returns party name as String object
	 * @return the party name
	 */
	public String getPartyName() 
	{
		return partyName;
	}
	
	
	//-----------------------------------------------------------------------
	/**
	 * Returns party size
	 * @return the party size
	 */
	public int getPartySize() 
	{
		return party.size();
	}
	
	
	//-----------------------------------------------------------------------
	/**
	 * Get a member from the party by specifying the name.
	 * @return get party member
	 * objects.
	 */
	public Moflow_Creature getMember( String name )
	{
		for ( int i = 0; i < party.size(); i++ )
		{
			if ( party.get( i ).getCharName().equals( name ) )
				return party.get( i );
		}
		
		return null;
	}
	
	
	
	//-----------------------------------------------------------------------
	/**
	 * Get a member from the party by position.
	 * @return get party member
	 */
	public Moflow_Creature getMember( int position )
	{
		return party.get( position );
	}
	
	
	
	/**
	 * Add a player to the party.
	 * @param pc the character to be added to the party
	 */
	public void addMember( Moflow_Creature pc )
	{
		party.add( pc );
	}
	
	
	
	//-----------------------------------------------------------------------
	/**
	 * Mutator for party name; Name is set through EditText field.
	 */
	public void setPartyName( String name )
	{
		partyName = name;
	}
	
	
	
	//-----------------------------------------------------------------------
	/**
	 * Removes a player character from the party.
	 * @param name of the player to remove
	 */
	public void RemovePC( String playerName )
	{
		for ( int i = 0; i < party.size(); i++ )
		{
			if ( party.get( i ).getCharName().equals( playerName ) )
				party.remove( i );
		}
	}
	
	
	
	//-----------------------------------------------------------------------
	/**
	 * Removes a player character from the party.
	 * @param name of the player to remove
	 */
	public void RemovePC( Moflow_Creature pc )
	{
		for ( int i = 0; i < party.size(); i++ )
		{
			if ( party.get( i ) == pc )
				party.remove( i );
		}
	}
	
	/**-----------------------------------------------------------------------
	 * Removes all members from the party.
	 */
	public void clearMembers() {
		party.clear();
	}
	
	
	/*
	=========================================================================
	Parcelable implementation
	=========================================================================
	*/
	/**
	 * Required static field CREATOR for Parcelable implementations 
	 */
	public static final Parcelable.Creator< Moflow_Party > CREATOR = 
		new Parcelable.Creator< Moflow_Party >()
		{
			public Moflow_Party createFromParcel( Parcel in )
			{
				return new Moflow_Party( in );
			}
			
			public Moflow_Party [] newArray( int size )
			{
				return new Moflow_Party[ size ];
			}
		};
		
	
		
	/**
	 * Constructor for reconstructing a Moflow_PC object from a Parcel
	 * @param in the parcel to read from
	 */
	@SuppressWarnings("unchecked")
	private Moflow_Party( Parcel in )
	{	
		partyName = in.readString();
		party = in.readArrayList( Moflow_Creature.class.getClassLoader() );
	}
	
	
	
	/**
	 * Describe the kinds of special objects contained in this Parcelable's 
	 * marshalled representation.
	 * @return a bitmask indicating the set of special object types marshalled 
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
		dest.writeString( partyName );
		dest.writeList( party );
	}
	

	/**
	 * Prints object for ListActivity for activity PCM_PartyList
	 */
	@Override
	public String toString()
	{
		return partyName;
	}
}
