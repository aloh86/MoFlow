package moflow.tracker;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
/*
===============================================================================
Moflow_Creature.java
Alex Oh

Base class for Moflow_PC objects (the PCs) and Moflow_Monster objects (all other
creatures/monsters.
player character.
===============================================================================
*/
public class Moflow_Creature implements Parcelable, Cloneable
{	
	protected int armorClass;
	protected int reflex;
	protected int fortitude;
	protected int will;
	
	protected int initMod;
	protected int hitPoints;
	protected int currentHP;
	protected int tempHP;
	
	protected String creatureName;
	
	public enum Effect { NORMAL, DAZED, CONFUSED, PARALYZED, STUNNED };
	public ArrayList<Effect> pcState;
	

	/**
	 * Creates "Blank" PC with all scores set to zero and no name.
	 */
	Moflow_Creature()
	{
		armorClass = 0;
		fortitude = 0;		
		reflex = 0;
		will = 0;
		
		initMod = 0;
		hitPoints = 0;
		currentHP = 0;
		tempHP = 0;
		
		creatureName = "";
	}
	
	/**
	 * Deep copy a Moflow_PC object.
	 */
	public Moflow_Creature clone() {
		Moflow_Creature twin;
		try {
			twin = ( Moflow_Creature ) super.clone();
			twin.creatureName = creatureName;
			twin.initMod = initMod;
			twin.armorClass = armorClass;
			twin.hitPoints = hitPoints;
		} catch ( CloneNotSupportedException e ) {
			throw new Error();
		}
		
		return twin;
	}
	
	/**
	 * Get character's armor class
	 * @return armor class value
	 */
	public int getAC() 		  { return armorClass; }
	/**
	 * Get character's fortitude saving throw
	 * @return fortitude saving throw
	 */
	public int getFortitude() { return fortitude;  }
	/**
	 * Get character's reflex value
	 * @return reflex saving throw
	 */
	public int getReflex() 	  { return reflex; 	   }
	/**
	 * Get character's will value
	 * @return will saving throw
	 */
	public int getWill() 	  { return will;	   }
	
	//-----------------------------------------------------------------------
	
	/**
	 * Get character's initiative modifier
	 * @return initiative modifier
	 */
	public int getInitMod()   { return initMod;	  }
	/**
	 * Get character's hit points
	 * @return hit point total
	 */
	public int getMaxHitPoints() { return hitPoints; }
	/**
	 * Get current amount of character's hit points
	 * @return current hit point value
	 */
	public int getCurrentHP() { return currentHP; }
	/**
	 * Get character's current temporary hit point value
	 * @return temporary hit point value
	 */
	public int getTempHP() 	  { return tempHP;	  }
	
	/**
	 * Get character's name
	 * @return character name
	 */
	public String getCharName() { return creatureName; }
	
	//-----------------------------------------------------------------------
	/**
	 * Set the temp hit point value
	 * @param points the new temp hit point value
	 */
	public void setTempHitPoints( int points ) { tempHP = points; }
	
	/**
	 * Add a new condition effect to the character
	 * @param state the new condition to be added
	 */
	public void setState( Effect state ) { pcState.add( state ); }
	
	/**
	 * Mutator for character name
	 * @param name new name
	 */
	public void setName( String name ) { creatureName = name; }
	
	//-----------------------------------------------------------------------
	/**
	 * Mutator for armor class
	 * @param AC armor class value
	 */
	public void setArmorClass( int AC ) { armorClass = AC; }
	
	/**
	 * Mutator for fortitude defense/saving throw
	 * @param fort fortitude value
	 */
	public void setFortitude( int fort ) { fortitude = fort; }
	
	/**
	 * Mutator for reflex defense/saving throw
	 * @param ref reflex value
	 */
	public void setReflex( int ref ) { reflex = ref; }
	
	/**
	 * Mutator for will defense/saving throw
	 * @param willpower will value
	 */
	public void setWill( int willpower ) { will = willpower; }
	
	//-----------------------------------------------------------------------
	/**
	 * Mutator for initiative modifier
	 * @param mod initiative modifier
	 */
	public void setInitMod( int mod ) { initMod = mod; }
	
	/**
	 * Mutator for max hit points. 
	 * @param hp hit point value
	 */
	public void setHitPoints( int hp ) { hitPoints = hp; }
	
	/**
	 * Mutator for current hit points.
	 */
	public void setCurrentHP( int hp )
	{
		currentHP = hp;
	}
	
	
	/*
	=========================================================================
	Parcelable implementation
	=========================================================================
	*/
	/**
	 * Required static field CREATOR for Parcelable implementations 
	 */
	public static final Parcelable.Creator< Moflow_Creature > CREATOR = 
		new Parcelable.Creator< Moflow_Creature >()
		{
			public Moflow_Creature createFromParcel( Parcel in )
			{
				return new Moflow_Creature( in );
			}
			
			public Moflow_Creature [] newArray( int size )
			{
				return new Moflow_Creature[ size ];
			}
		};
		
	/**
	 * Constructor for reconstructing a Moflow_PC object from a Parcel
	 * @param in the parcel to read from
	 */
	protected Moflow_Creature( Parcel in )
	{		
		armorClass = in.readInt();
		fortitude = in.readInt();
		reflex = in.readInt();
		will = in.readInt();
		
		initMod = in.readInt();
		hitPoints = in.readInt();
		currentHP = in.readInt();
		tempHP = in.readInt();
		
		creatureName = in.readString();
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
		dest.writeInt( armorClass );
		dest.writeInt( fortitude );
		dest.writeInt( reflex );
		dest.writeInt( will );
		
		dest.writeInt( initMod );
		dest.writeInt( hitPoints );
		dest.writeInt( currentHP );
		dest.writeInt( tempHP );
		
		dest.writeString( creatureName );
	}
	
	/*
	=========================================================================
	Overrides
	=========================================================================
	*/
	@Override
	public String toString()
	{
		return creatureName + "\n" +
		"Hit Points: " + hitPoints + "\n" +
		"AC: " + armorClass +
		"  Init Bonus: " + initMod;
	}
}
