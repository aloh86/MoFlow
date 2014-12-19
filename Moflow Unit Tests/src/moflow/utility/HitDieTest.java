package moflow.utility;

import junit.framework.TestCase;
import moflow.utility.HitDie;
import org.junit.Test;

import java.util.ArrayList;
/**
 * Created by Alex on 9/22/14.
 */
public class HitDieTest extends TestCase {

    @Test
    public void test_hitDieSpecifiedRepetition() throws AssertionError
    {
        String expression = "10d6";
        HitDie die = new HitDie( expression );
        ArrayList<Integer> results = collectResults( 100, die );
        assertTrue( resultsWithinRange( 10, 60, results ) );
    }

    @Test
    public void test_hitDieUnspecifiedRepetition() throws AssertionError
    {
        String expression = "d8";
        HitDie die = new HitDie( expression );
        ArrayList<Integer> results = collectResults(100, die);
        assertTrue( resultsWithinRange( 1, 8, results ) );
    }

    @Test
    public void test_hitDieSpecifiedRepetitionWithModifier() throws AssertionError
    {
        String expression = "10d6+20";
        HitDie die = new HitDie( expression );
        ArrayList<Integer> results = collectResults( 100, die );
        assertTrue( resultsWithinRange( 30, 80, results ) );
    }

    @Test
    public void test_hitDieUnspecifiedRepetitionWithModifier() throws AssertionError
    {
        String expression = "d4+10";
        HitDie die = new HitDie( expression );
        ArrayList<Integer> results = collectResults( 100, die );
        assertTrue( resultsWithinRange( 11, 14, results ) );
    }

    @Test
    public void test_hitDieWithNegativeModifier() throws AssertionError
    {
        String expression = "d100-50";
        HitDie die = new HitDie( expression );
        ArrayList<Integer> results = collectResults( 100, die );
        assertTrue( resultsWithinRange( 1, 50, results ) );
    }

    private ArrayList<Integer> collectResults( int maxRolls, HitDie die )
    {
        ArrayList<Integer> results = new ArrayList<Integer>();
        for ( int i = 0; i < maxRolls; i++ )
        {
            results.add( die.rollHitDie() );
        }
        return results;
    }

    private boolean resultsWithinRange( int min, int max, ArrayList<Integer> results )
    {
        for ( int i = 0; i < results.size(); i++ )
        {
            if ( results.get( i ) < min && results.get( i ) > max )
                return false;
        }
        return true;
    }

    @Test
    public void test_isValidExpression() throws AssertionError
    {
        assertTrue(HitDie.isHitDieExpression("10d6"));
        assertTrue( HitDie.isHitDieExpression( "d8" ) );
        assertTrue( HitDie.isHitDieExpression( "2d4+1" ) );
    }

    @Test
    public void test_isDigit() throws AssertionError
    {
        assertTrue( HitDie.isDigit( "45" ) );
    }

    @Test
    public void test_hitDieLessThanZero() throws AssertionError
    {
        String expression = "0d0";
        HitDie die = new HitDie(expression);
        int result = die.rollHitDie();
        assertEquals(1, result);
    }
}