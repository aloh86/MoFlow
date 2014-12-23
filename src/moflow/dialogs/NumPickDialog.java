package moflow.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import moflow.activities.R;

/**
 * Created by Alex on 10/4/2014.
 */
public class NumPickDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private SimpleDialogListener simpleDialogListener;
    private View view;
    private NumberPicker numPicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        view = getActivity().getLayoutInflater().inflate(R.layout.creature_pick, null);
        numPicker = (NumberPicker)view.findViewById(R.id.creatureCatalogNumPicker);
        String [] pickerRange = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
        numPicker.setMaxValue(20);
        numPicker.setMinValue(1);
        numPicker.setDisplayedValues(pickerRange);

        builder.setView(view);
        builder.setPositiveButton("Ok", this);
        builder.setNegativeButton("Cancel", this);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int choice)
    {
        if (choice == DialogInterface.BUTTON_POSITIVE)
            simpleDialogListener.onDialogPositiveClick(this);
        else
            simpleDialogListener.onDialogNegativeClick(this);
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            simpleDialogListener = (SimpleDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SimpleDialogListener");
        }
    }

    public int getPickValue()
    {
        return numPicker.getValue();
    }
}
