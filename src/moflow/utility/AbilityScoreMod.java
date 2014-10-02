package moflow.utility;

import java.util.Map;

/**
 * Created by Alex on 8/24/14.
 */
public class AbilityScoreMod {
    // get ability score mod for 3rd, 4th, and 5th edition.
    public static String get345AbilityScoreMod(String scoreVal) {
        int score = Integer.parseInt(scoreVal);
        int mod = (int)Math.floor((score - 10d) / 2d);
        String strMod = String.valueOf(mod);

        return strMod;
    }
}
