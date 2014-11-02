package moflow.utility;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by Alex on 9/21/14.
 */
// ^(\d+)?d\d+((\+|\-)?\d+)?$|^\d+$
public class HitDie
{
    private int repetitions;
    private int range;
    private int modifier;

    public HitDie( String die )
    {
        repetitions = -1;
        range = -1;
        modifier = 0;
        evaluateExpression(die);
    }
    
    public int rollHitDie()
    {
        Random r = new Random();
        int result = 0;
        int min = 1;

        for (int i = 0; i < repetitions; i++)
        {
            result += r.nextInt(range) + min;
        }

        result += modifier;

        return result;
    }

    public int getMaxVal() {
        int result = 0;

        for (int i = 0; i < repetitions; i++) {
            result += range;
        }
        result += modifier;

        return result;
    }

    /**
     * This method assumes that the string is a valid hit die expression.
     * @param expression
     */
    private void evaluateExpression(String expression)
    {
        int dPosition = expression.indexOf("d");
        String tempRepetitions = expression.substring(0, dPosition);

        if (tempRepetitions.length() > 0)
            repetitions = Integer.valueOf(tempRepetitions);
        else
            repetitions = 1;

        int rangePosition = -1;
        int modSignPosition = -1;

        boolean isNegative = false;
        if (expression.contains("+"))
        {
            modSignPosition = expression.indexOf("+");
        }
        if (expression.contains("-"))
        {
            modSignPosition = expression.indexOf("-");
            isNegative = true;
        }

        if (modSignPosition == -1)
        {
            String tempRange = expression.substring(dPosition + 1, expression.length());
            range = Integer.valueOf( tempRange );
            return;
        }

        String tempRange = expression.substring(dPosition + 1, modSignPosition);
        range = Integer.valueOf(tempRange);

        String tempModifier;
        if (isNegative)
        {
            tempModifier = expression.substring(modSignPosition, expression.length());
        }
        else
        {
            tempModifier = expression.substring(modSignPosition + 1, expression.length());
        }
        modifier = Integer.valueOf(tempModifier);
    }

    public static boolean isHitDieExpression(String expression)
    {
        return expression.matches("^(\\d+)?d\\d+((\\+|\\-)?\\d+)?$");
    }

    public static boolean isDigit(String expression)
    {
        return expression.matches("^\\d+$");
    }
}
