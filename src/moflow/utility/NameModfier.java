package moflow.utility;

import android.os.PatternMatcher;

import java.util.ArrayList;

/**
 * Created by Alex on 6/1/14.
 */
public class NameModfier {
    public static String makeNameUnique( ArrayList< String > nameList, String name ) {
        int count = 0;

        for ( int i = 0; i < nameList.size(); i++ ) {
            if ( nameList.get( i ).toLowerCase().matches( "^" + name.trim().toLowerCase() + "( \\d)?" ) )
                ++count;
        }

        if ( count == 0 )
            return name;

        return name + " " + String.valueOf( count + 1 );
    }
}
