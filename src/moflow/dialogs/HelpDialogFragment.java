package moflow.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import moflow.activities.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alex on 4/27/14.
 */
public class HelpDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private View layout;

     @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
     {
         LayoutInflater inflater = getActivity().getLayoutInflater();
         layout = inflater.inflate(R.layout.dialog_help, null);

         Linkify.TransformFilter tubeURL = new Linkify.TransformFilter() {
             public final String transformUrl(final Matcher match, String url) {
                 return new String("http://youtube.com/wolfpupsoftware");
             }
         };

         TextView tubeLink = (TextView) layout.findViewById(R.id.dlg_main_helpYoutubeTV);
         Pattern pattern = Pattern.compile("Wolfpup YouTube Channel");
         Linkify.addLinks(tubeLink, pattern, "", null, tubeURL);

         Linkify.TransformFilter siteURL = new Linkify.TransformFilter() {
             public final String transformUrl(final Matcher match, String url) {
                 return new String("http://wolfpupsoftware.com");
             }
         };

         TextView webLink = (TextView) layout.findViewById(R.id.dlg_main_helpSiteLinkTV);
         pattern = Pattern.compile("Wolfpup Software Website");
         Linkify.addLinks(webLink, pattern, "", null, siteURL);

         AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
         builder.setView(layout);
         builder.setPositiveButton("Ok", this);

         return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i)
    {
        // DO NOTHING
    }
}
