package moflow.utility;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by fallo on 11/14/2015.
 */
public class CatalogFilenameFilter implements FilenameFilter {

    public boolean accept(File dir, String fileName)
    {
        if (fileName.matches("^moflow_catalog_[a-z]+?$.csv"))
            return true;

        return false;
    }
}
