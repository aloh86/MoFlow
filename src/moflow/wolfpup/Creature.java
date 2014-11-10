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
    private String armorClass;
    private String initMod;
    private String maxHitPoints;
    private String hitDie;

    // ability scores
    private String strength;
    private String dexterity;
    private String constitution;
    private String intelligence;
    private String wisdom;
    private String charisma;

    // saving throws
    private String fort;
    private String reflex;
    private String will;

    // initiative state info
    private String currentHitPoints;
    private String initiative;
    private boolean isMonster;
    private boolean hasInit;

	/**
	 * Creates "Blank" PC with all scores set to zero and no name.
	 */
	public Creature() {
        creatureName = "";
		armorClass = "0";
		initMod = "0";
        maxHitPoints = "0";

        strength = "0";
        dexterity = "0";
        constitution = "0";
        intelligence = "0";
        wisdom = "0";
        charisma = "0";

        fort = "0";
        reflex = "0";
        will = "0";

        maxHitPoints = "X";
        currentHitPoints = maxHitPoints;
        hitDie = "XdX";
		initiative = "0";
		isMonster = false;
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
            twin.hitDie = hitDie;

            twin.strength = strength;
            twin.dexterity = dexterity;
            twin.constitution = constitution;
            twin.intelligence = intelligence;
            twin.wisdom = wisdom;
            twin.charisma = charisma;

            twin.fort = fort;
            twin.reflex = reflex;
            twin.will = will;

            twin.currentHitPoints = twin.maxHitPoints;
            twin.initiative = initiative;
            twin.isMonster = isMonster;
            twin.hasInit = hasInit;
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
    public String    getArmorClass() { return armorClass; }
    public String    getInitMod() { return initMod; }
    public String    getMaxHitPoints() { return maxHitPoints; }
    public String    getHitDie() { return hitDie; }

    public String    getStrength() { return strength; }
    public String    getDexterity() { return dexterity; }
    public String    getConstitution() { return constitution; }
    public String    getIntelligence() { return intelligence; }
    public String    getWisdom() { return wisdom; }
    public String    getCharisma() { return charisma; }

    public String    getFortitude() { return fort; }
    public String    getReflex() { return reflex; }
    public String    getWill() { return will; }

    public String    getCurrentHitPoints() { return currentHitPoints; }
    public String    getInitiative() { return initiative; }
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

    public void setArmorClass( String AC ) { armorClass = AC; }
    public void setInitMod( String mod ) { initMod = mod; }
    public void setMaxHitPoints( String points ) { maxHitPoints = points; }
    public void setHitDie(String dieExpression) { hitDie = dieExpression; }

    public void setStrength( String score ) { strength = validateScore( score ); }
    public void setDexterity( String score ) { dexterity = validateScore( score ); }
    public void setConstitution( String score ) { constitution = validateScore( score ); }
    public void setIntelligence( String score ) { intelligence = validateScore( score ); }
    public void setWisdom( String score ) { wisdom = validateScore( score ); }
    public void setCharisma( String score ) { charisma = validateScore( score ); }

    public void setFortitude( String mod ) { fort = mod; }
    public void setReflex( String mod ) { reflex = mod; }
    public void setWill( String mod ) { will = mod; }

    public void setCurrentHitPoints( String hp ) { currentHitPoints = hp; }
    public void setInitiative( String init ) { initiative = init; }
    public void setAsMonster( boolean val ) { isMonster = val; }
    public void setHasInit( boolean hasInitiative ) { hasInit = hasInitiative; }
	
	private String validateScore( String score ) {
        int scoreNum = Integer.valueOf( score );
        if (scoreNum >= 0)
            return String.valueOf( scoreNum );

        return "0";
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
		armorClass = in.readString();
		initMod = in.readString();
		maxHitPoints = in.readString();
        hitDie = in.readString();

        strength = in.readString();
        dexterity = in.readString();
        constitution = in.readString();
        intelligence = in.readString();
        wisdom = in.readString();
        charisma = in.readString();

        fort = in.readString();
        reflex = in.readString();
        will = in.readString();

        currentHitPoints = in.readString();
		initiative = in.readString();
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
        dest.writeString(creatureName);
		dest.writeString(armorClass);
		dest.writeString(initMod);
		dest.writeString(maxHitPoints);
        dest.writeString(hitDie);

        dest.writeString(strength);
        dest.writeString(dexterity);
        dest.writeString(constitution);
        dest.writeString(intelligence);
        dest.writeString(wisdom);
        dest.writeString(charisma);

        dest.writeString(fort);
        dest.writeString(reflex);
        dest.writeString(will);

		dest.writeString(currentHitPoints);
		dest.writeString(initiative);
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

    @Override
    public boolean equals( Object o ) {
        Creature c = ( Creature )o;

        if ( this.creatureName == c.creatureName &&
                this.armorClass == c.armorClass &&
                this.maxHitPoints == c.maxHitPoints &&
                this.hitDie == c.hitDie &&
                this.initMod == c.initMod &&
                this.strength == c.strength &&
                this.dexterity == c.dexterity &&
                this.constitution == c.constitution &&
                this.intelligence == c.intelligence &&
                this.wisdom == c.wisdom &&
                this.charisma == c.charisma &&
                this.fort == c.fort &&
                this.reflex == c.reflex &&
                this.will == c.will &&
                this.currentHitPoints == c.currentHitPoints &&
                this.initiative == c.initiative &&
                this.isMonster == c.isMonster &&
                this.hasInit == c.hasInit )
            return true;

        return false;
    }

	/**
	 * If this creature is equal to, return 0. If less than, return -1.
	 * If greater than, return 1.
	 */
	@Override
	public int compareTo( Object obj ) {
		Creature creature = (Creature) obj;

        int thisInit = Integer.valueOf( this.initiative );
        int creatureInit = Integer.valueOf( creature.initiative );
		
		if ( this.initiative == creature.initiative )
			return 0;
		else if ( thisInit < creatureInit )
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
