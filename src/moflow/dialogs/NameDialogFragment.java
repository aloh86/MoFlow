package moflow.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import moflow.activities.R;

/**
 * Created by Alex on 5/28/14.
 */
public class NameDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private SimpleDialogListener simpleDialogListener;
    private String title;

    public NameDialogFragment( String dialogTitle ) {
        title = dialogTitle;
    }

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
        builder.setView( getActivity().getLayoutInflater().inflate( R.layout.single_edittext, null ) );
        builder.setTitle( title );
        builder.setPositiveButton( "Ok", this );
        builder.setNegativeButton( "Cancel", this );

        return builder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            simpleDialogListener = (SimpleDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }


    @Override
    public void onClick( DialogInterface dialogInterface, int choice ) {
        if ( choice == DialogInterface.BUTTON_POSITIVE )
            simpleDialogListener.onDialogPositiveClick( this );
        else
            simpleDialogListener.onDialogNegativeClick( this );
    }
}
