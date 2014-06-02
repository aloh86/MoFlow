package moflow.utility;

import java.util.ArrayList;

/**
 * Created by Alex on 6/1/14.
 */
public class NameModfier {
    public static String makeNameUnique( ArrayList< String > nameList, String name ) {
        int count = 0;

        for ( int i = 0; i < nameList.size(); i++ ) {
            if ( nameList.get( i ).equalsIgnoreCase( name ) )
                ++count;
        }

        if ( count == 0 )
            return name;

        return name + " " + String.valueOf( count + 1 );
    }
}
