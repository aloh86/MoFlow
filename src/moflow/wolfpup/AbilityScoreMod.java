package moflow.wolfpup;

import java.util.Map;

/**
 * Created by Alex on 8/24/14.
 */
public class AbilityScoreMod {
    // get ability score mod for 3rd, 4th, and 5th edition.
    public static int get345AbilityScoreMod( int score ) {
        return (int)Math.floor( ( score - 10d ) / 2d );
    }
}
