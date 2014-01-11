package moflow.activities;

import android.content.Context;
/****************************************************************************
 * DeviceInfo.java
 *
 * Gives info on device type such as size.
 */
import android.content.res.Configuration;


public class DeviceInfo {

        public DeviceInfo() {
                
        }
        
        /**
         * Get general size info of the device. The return value should be compared
         * with the values:
         * SCREENLAYOUT_SIZE_LARGE
         * SCREENLAYOUT_SIZE_NORMAL
         * SCREENLAYOUT_SIZE_SMALL
         * @param context application context
         * @return int integer size code 
         */
        static public int getSizeInfo( Context context ) {
                if ( ( context.getResources().getConfiguration().screenLayout
                                & Configuration.SCREENLAYOUT_SIZE_MASK )
                                >= Configuration.SCREENLAYOUT_SIZE_LARGE )
                        return Configuration.SCREENLAYOUT_SIZE_LARGE;
                
                else if ( ( context.getResources().getConfiguration().screenLayout
                                & Configuration.SCREENLAYOUT_SIZE_MASK )
                                >= Configuration.SCREENLAYOUT_SIZE_NORMAL )
                        return Configuration.SCREENLAYOUT_SIZE_NORMAL;
                
                else if ( ( context.getResources().getConfiguration().screenLayout
                                & Configuration.SCREENLAYOUT_SIZE_MASK )
                                >= Configuration.SCREENLAYOUT_SIZE_SMALL )
                        return Configuration.SCREENLAYOUT_SIZE_SMALL;
                
                return -1;
        }
}
