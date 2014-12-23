package moflow.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.Gravity;
import android.widget.TextView;
import moflow.activities.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alex on 4/28/14.
 */
public class DonateDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        TextView msg = new TextView(getActivity());
        msg.setText(R.string.actbar_donateMsg);
        msg.setGravity(Gravity.CENTER);

        Linkify.TransformFilter siteURL = new Linkify.TransformFilter()
        {
            public final String transformUrl(final Matcher match, String url) {
                return new String("http://onedayswages.org");
            }
        };

        Pattern pattern = Pattern.compile("One Day's Wages");
        Linkify.addLinks(msg, pattern, "", null, siteURL);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(msg);
        builder.setPositiveButton("Ok", this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i)
    {
        // DO NOTHING
    }
}
