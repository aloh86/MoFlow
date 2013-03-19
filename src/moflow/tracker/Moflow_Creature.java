package moflow.tracker;

import java.util.ArrayList;
import java.util.HashMap;

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
public class Moflow_Creature implements Parcelable, Cloneable, Comparable
{	
	protected int armorClass;
	protected int initMod;
	protected int hitPoints;
	protected int currentHP;
	protected int initiative;
	protected boolean isMonster;
	protected boolean hasInit;
	protected String creatureName;
	
	public Conditions conditions;
	

	/**
	 * Creates "Blank" PC with all scores set to zero and no name.
	 */
	Moflow_Creature()
	{
		armorClass = 0;		
		initMod = 0;
		hitPoints = 0;
		currentHP = hitPoints;
		initiative = 0;
		isMonster = true;
		creatureName = "";
		hasInit = false;
		conditions = new Conditions();
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
	 * Get character's name
	 * @return character name
	 */
	public String getCharName() { return creatureName; }
	
	/**
	 * Get character's initiative
	 * @return initiative value
	 */
	public int getInitiative() { return initiative; }
	
	/**
	 * Determine whether item is a "monster" creature or a player character.
	 * @return true if monster creature, false if player character
	 */
	public boolean isCreature() { return isMonster; }
	
	/**
	 * Mutator for character name
	 * @param name new name
	 */
	public void setName( String name ) { creatureName = name; }
	
	/**
	 * Mutator for armor class
	 * @param AC armor class value
	 */
	public void setArmorClass( int AC ) { armorClass = AC; }
	
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
	public void setCurrentHP( int hp ) { currentHP = hp; }
	
	/**
	 * Mutator for initiative.
	 * @param init initiative value
	 */
	public void setInitiative( int init ) { initiative = init; }
	
	public void setAsMonster( int type ) { 
		isMonster = ( type == 0 ? false : true ); // 0 = PC, 1 = monster 
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
		
		initMod = in.readInt();
		hitPoints = in.readInt();
		currentHP = in.readInt();
		initiative = in.readInt();
		
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
		
		dest.writeInt( initMod );
		dest.writeInt( hitPoints );
		dest.writeInt( currentHP );
		dest.writeFloat( initiative );
		
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

	/**
	 * If this creature is equal to, return 0. If less than, return -1.
	 * If greater than, return 1.
	 */
	@Override
	public int compareTo( Object obj ) {
		Moflow_Creature creature = ( Moflow_Creature ) obj;
		
		if ( this.initiative == creature.initiative )
			return 0;
		else if ( this.initiative < creature.initiative )
			return -1;
		else
			return 1;
	}
}
