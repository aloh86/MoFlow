package moflow.utility;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by alex on 10/16/14.
 */
public class CommonToast {

    public static void invalidFieldToast(Context ctx)
    {
        Toast.makeText(ctx, "All fields must be filled.", Toast.LENGTH_LONG).show();
    }

    public static void invalidDieToast(Context ctx)
    {
        Toast.makeText(ctx, "Please enter valid hit point value or hit die expression.", Toast.LENGTH_LONG).show();
    }
}
