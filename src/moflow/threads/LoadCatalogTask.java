package moflow.threads;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import moflow.activities.R;
import moflow.utility.DBTransaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Alex on 9/30/14.
 */
public class LoadCatalogTask extends AsyncTask<Void, Void, Void> {

    ProgressBar bar;
    Context mHelperContext;
    ArrayList<String []> critters;
    DBTransaction db;
    int catalogVersion;
    InputStream inputStream;

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        bar.setVisibility(View.VISIBLE);
    }

    public LoadCatalogTask(Context ctx, ProgressBar pBar, int version)
    {
        bar  = pBar;
        mHelperContext = ctx;
        critters = new ArrayList<String[]>();
        db = new DBTransaction(mHelperContext);
        catalogVersion = version;
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        final Resources resources = mHelperContext.getResources();
        inputStream = getInputStreamBasedOnVersion(resources, catalogVersion);

        if (inputStream == null) {
            db.deleteNonCustomCreatures();
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line;
            while (null != (line = reader.readLine())) {
                String [] strings = TextUtils.split(line, ",");
                critters.add(strings);
            }
        } catch (IOException e) {
            Log.e("moflow.runnable.LoadCatalogTask", "readLine() failed to read input.");
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                Log.e("moflow.runnable.LoadCatalogTask", "BufferedReader failed to closed.");
            }
        }

        db.deleteNonCustomCreatures();
        db.insertCreaturesFromFile(critters, catalogVersion);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        bar.setVisibility(View.INVISIBLE);
        if (inputStream != null)
            Toast.makeText(mHelperContext, "Catalog has been loaded", Toast.LENGTH_SHORT).show();
    }

    private InputStream getInputStreamBasedOnVersion(Resources res, int version)
    {
        if (version == 5)
            return res.openRawResource(R.raw.fivecc);

        return null;
    }
}
