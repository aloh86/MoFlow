package moflow.wolfpup;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/*
===============================================================================
Moflow_Creature.java
Alex Oh

Representation of a creature (PCs and Monsters).
===============================================================================
*/
public class Creature implements Parcelable, Cloneable, Comparable {
    // "core" info
    private String creatureName;
    private int armorClass;
    private int initMod;
    private int maxHitPoints;

    // ability scores
    private int strength;
    private int dexterity;
    private int constitution;
    private int intelligence;
    private int wisdom;
    private int charisma;

    // saving throws
    private int fort;
    private int reflex;
    private int will;

    // initiative state info
    private int currentHitPoints;
    private int initiative;
    private boolean isMonster;
    private boolean hasInit;

	/**
	 * Creates "Blank" PC with all scores set to zero and no name.
	 */
	public Creature() {
        creatureName = "";
		armorClass = 0;
		initMod = 0;
        maxHitPoints = 0;

        strength = 0;
        dexterity = 0;
        constitution = 0;
        intelligence = 0;
        wisdom = 0;
        charisma = 0;

        fort = 0;
        reflex = 0;
        will = 0;

        currentHitPoints = maxHitPoints;
		initiative = 0;
		isMonster = true;
		hasInit = false;
	}
	
	/**
	 * Deep copy a Creature object.
	 */
	public Creature clone() {
		Creature twin;
		try {
			twin = (Creature) super.clone();
			twin.creatureName = creatureName;
            twin.armorClass = armorClass;
			twin.initMod = initMod;
			twin.maxHitPoints = maxHitPoints;

            twin.strength = strength;
            twin.dexterity = dexterity;
            twin.constitution = constitution;
            twin.intelligence = intelligence;
            twin.wisdom = wisdom;
            twin.charisma = charisma;

            twin.fort = fort;
            twin.reflex = reflex;
            twin.will = will;
		} catch ( CloneNotSupportedException e ) {
			throw new Error();
		}
		
		return twin;
	}
	
    /*
	=========================================================================
	Accessor
	=========================================================================
	*/
    public String getCreatureName() { return creatureName; }
    public int    getArmorClass() { return armorClass; }
    public int    getInitMod() { return initMod; }
    public int    getMaxHitPoints() { return maxHitPoints; }

    public int    getStrength() { return strength; }
    public int    getDexterity() { return dexterity; }
    public int    getConstitution() { return constitution; }
    public int    getIntelligence() { return intelligence; }
    public int    getWisdom() { return wisdom; }
    public int    getCharisma() { return charisma; }

    public int    getFortitude() { return fort; }
    public int    getReflex() { return reflex; }
    public int    getWill() { return will; }

    public int    getCurrentHitPoints() { return currentHitPoints; }
    public int    getInitiative() { return initiative; }
    public boolean isMonster() { return isMonster; }
    public boolean hasInit() { return hasInit; }

    /*
	=========================================================================
	Mutator
	=========================================================================
	*/
    public void setCreatureName( String name ) {
        name.trim();

        if ( name.length() > 50 ) {
            creatureName = name.substring( 0, 50 );
        } else
            creatureName = name;
    }

    public void setArmorClass( int AC ) { armorClass = AC; }
    public void setInitMod( int mod ) { initMod = mod; }
    public void setMaxHitPoints( int points ) { maxHitPoints = points; }

    public void setStrength( int score ) { strength = validateScore( score ); }
    public void setDexterity( int score ) { dexterity = validateScore( score ); }
    public void setConstitution( int score ) { constitution = validateScore( score ); }
    public void setIntelligence( int score ) { intelligence = validateScore( score ); }
    public void setWisdom( int score ) { wisdom = validateScore( score ); }
    public void setCharisma( int score ) { charisma = validateScore( score ); }

    public void setFortitude( int mod ) { fort = mod; }
    public void setReflex( int mod ) { reflex = mod; }
    public void setWill( int mod ) { will = mod; }

    public void setCurrentHitPoints( int hp ) { currentHitPoints = hp; }
    public void setInitiative( int init ) { initiative = init; }
    public void setAsMonster( boolean val ) { isMonster = val; }
    public void setHasInit( boolean hasInitiative ) { hasInit = hasInitiative; }
	
	private int validateScore( int score ) {
        if ( score >= 0 )
            return score;

        return 0;
    }

	/*
	=========================================================================
	Parcelable implementation
	=========================================================================
	*/
	/**
	 * Required static field CREATOR for Parcelable implementations 
	 */
	public static final Parcelable.Creator<Creature> CREATOR =
		new Parcelable.Creator<Creature>()
		{
			public Creature createFromParcel( Parcel in )
			{
				return new Creature( in );
			}
			
			public Creature[] newArray( int size )
			{
				return new Creature[ size ];
			}
		};
		
	/**
	 * Constructor for reconstructing a Creature object from a Parcel
	 * @param in the parcel to read from
	 */
	protected Creature( Parcel in )
	{
        creatureName = in.readString();
		armorClass = in.readInt();
		initMod = in.readInt();
		maxHitPoints = in.readInt();

        strength = in.readInt();
        dexterity = in.readInt();
        constitution = in.readInt();
        intelligence = in.readInt();
        wisdom = in.readInt();
        charisma = in.readInt();

        fort = in.readInt();
        reflex = in.readInt();
        will = in.readInt();

        currentHitPoints = in.readInt();
		initiative = in.readInt();
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
	 * @param flags Additional flags about how the object should be written.
	 */
	public void writeToParcel( Parcel dest, int flags )
	{
        dest.writeString( creatureName );
		dest.writeInt( armorClass );
		dest.writeInt( initMod );
		dest.writeInt( maxHitPoints );

        dest.writeInt( strength );
        dest.writeInt( dexterity );
        dest.writeInt( constitution );
        dest.writeInt( intelligence );
        dest.writeInt( wisdom );
        dest.writeInt( charisma );

        dest.writeInt( fort );
        dest.writeInt( reflex );
        dest.writeInt( will );

		dest.writeInt( currentHitPoints );
		dest.writeFloat( initiative );
	}
	
	/*
	=========================================================================
	Overrides
	=========================================================================
	*/
	@Override
	public String toString()
	{
		return creatureName;
	}

	/**
	 * If this creature is equal to, return 0. If less than, return -1.
	 * If greater than, return 1.
	 */
	@Override
	public int compareTo( Object obj ) {
		Creature creature = (Creature) obj;
		
		if ( this.initiative == creature.initiative )
			return 0;
		else if ( this.initiative < creature.initiative )
			return -1;
		else
			return 1;
	}

    public static Comparator< Creature > nameComparator() {
        return new Comparator<Creature>() {
            @Override
            public int compare(Creature lhs, Creature rhs) {
                return lhs.getCreatureName().compareToIgnoreCase( rhs.getCreatureName() );
            }
        };
    }
}
