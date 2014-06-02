package moflow.dialogs;

import android.app.DialogFragment;

/**
 * Created by Alex on 6/1/14.
 */
public interface SimpleDialogListener {
    public void onDialogPositiveClick(DialogFragment dialog);
    public void onDialogNegativeClick(DialogFragment dialog);
}
