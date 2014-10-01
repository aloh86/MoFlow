package moflow.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import moflow.activities.R;

/**
 * Created by Alex on 9/30/14.
 */
public class SimpleMsgDialog extends DialogFragment {
    String message;

    public SimpleMsgDialog( String msg ) {
        message = msg;
    }

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
        builder.setTitle( "Help Out" );
        builder.setMessage( message );
        builder.setPositiveButton( "Ok", null );
        Dialog dialog = builder.create();

        return dialog;
    }
}
