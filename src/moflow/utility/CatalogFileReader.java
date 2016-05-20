package moflow.utility;

import java.io.File;
import java.util.ArrayList;

import android.os.Environment;

/**
 * Created by fallo on 11/14/2015.
 */
public class CatalogFileReader {


    public String [] getFilesListDownloadDir()
    {
        if (!isExternalStorageReadable())
            return null;

        File downloadDir = getDownloadsDirectory();
        String [] downFiles = downloadDir.list(new CatalogFilenameFilter());

        return downFiles;
    }

    public String [] getFileListDocumentDir()
    {
        if (!isExternalStorageReadable())
            return null;

        File fileDir = getDocumentsDirectory();
        String [] docFiles = fileDir.list(new CatalogFilenameFilter());

        return docFiles;
    }

    public boolean isExternalStorageWritable()
    {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state))
            return true;

        return false;
    }

    private boolean isExternalStorageReadable()
    {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state) && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
            return true;

        return false;
    }

    private File getDocumentsDirectory()
    {
        File file = new File(Environment.DIRECTORY_DOCUMENTS);
        return file;
    }

    private File getDownloadsDirectory()
    {
        File file = new File(Environment.DIRECTORY_DOWNLOADS);
        return file;
    }

    private File getCatalogFile(String catalogName)
    {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), catalogName);

        if (!file.mkdirs());
            // TODO report error

        return file;
    }
}
