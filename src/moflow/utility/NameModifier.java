package moflow.utility;

import android.os.PatternMatcher;
import moflow.wolfpup.Creature;

import java.util.ArrayList;

/**
 * Created by Alex on 6/1/14.
 */
public class NameModifier {
    public static String makeNameUnique( ArrayList< String > nameList, String name ) {
        ArrayList< String > matchList = new ArrayList< String >();

        for ( int i = 0; i < nameList.size(); i++ ) {
            if ( nameList.get( i ).toLowerCase().matches( "^" + name.trim().toLowerCase() + "( \\d)?" ) ) {
                matchList.add( nameList.get( i ) );
            }
        }

        if ( matchList.size() == 0 )
            return name;

        int count = 1;
        boolean unique = false;

        while ( !unique ) {
            for ( String n : matchList ) {
                if ( n.equalsIgnoreCase( name + " " + String.valueOf( count ) ) )
                    ++count;
                else {
                    name = name + " " + String.valueOf( count );
                    unique = true;
                }
            }
        }

        return name;
    }

    public static String makeNameUnique2( ArrayList< Creature > creatures, String name ) {
        ArrayList< String > names = new ArrayList< String >();

        for( Creature c : creatures )
            names.add( c.getCreatureName() );

        return NameModifier.makeNameUnique( names, name );
    }
}
