package moflow.utility;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by alex on 11/7/14.
 */
public class HitDieInputFilter implements InputFilter {

    public static String hitDieSource = "0123456789d+-";

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        for (int i = start; i < end; i++) {
            if ( source.charAt(i) != 'd'
                    && source.charAt(i) != 'D'
                    && source.charAt(i) != '+'
                    && source.charAt(i) != '-'
                    && !Character.isDigit(source.charAt(i))) {
                return "";
            }
        }
        return null;
    }
}
