package moflow.activities;

import java.util.HashMap;

import android.util.SparseIntArray;

public class Conditions {
	public static final int BLINDED = 0;
	public static final int COWERING = 1;
	public static final int DAZED = 2;
	public static final int DEAFENED = 3;
	public static final int ENTANGLED = 4;
	public static final int EXHAUSTED = 5;
	public static final int FATIGUED = 6;
	public static final int HELPLESS = 7;
	public static final int MARKED = 8;
	public static final int NAUSEATED = 9;
	public static final int PANICKED = 10;
	public static final int PARALYZED = 11;
	public static final int SHAKEN = 12;
	public static final int STUNNED = 13;
	
	private SparseIntArray conditions;
	
	public Conditions() {
		conditions = new SparseIntArray();
		conditions.put( BLINDED, -1 );
		conditions.put( COWERING, -1 );
		conditions.put( DAZED, -1 );
		conditions.put( DEAFENED, -1 );
		conditions.put( ENTANGLED, -1 );
		conditions.put( EXHAUSTED, -1 );
		conditions.put( FATIGUED, -1 );
		conditions.put( HELPLESS, -1 );
		conditions.put( MARKED, -1 );
		conditions.put( NAUSEATED, -1 );
		conditions.put( PANICKED, -1 );
		conditions.put( PARALYZED, -1 );
		conditions.put( SHAKEN, -1 );
		conditions.put( STUNNED, -1 );
	}
	
	public void setState( int state, int length ) {
		conditions.put( state, length );
	}
	
	public int getState( int state ) {
		return conditions.get( state );
	}
	
	public int getSize() {
		return conditions.size();
	}
}
