package moflow.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import moflow.activities.R;

import java.util.regex.Pattern;

/**
 * Created by Alex on 4/27/14.
 */
public class HelpDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private View layout;

     @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
         LayoutInflater inflater = getActivity().getLayoutInflater();
         layout = inflater.inflate( R.layout.dialog_help, null );

         AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
         builder.setView( layout );
         builder.setPositiveButton( "Ok", this );

         return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        // DO NOTHING
    }
}
