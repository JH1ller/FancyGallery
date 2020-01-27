package de.hdmstuttgart.fancygallery.model.database;

import android.net.Uri;

import de.hdmstuttgart.fancygallery.model.ImageFolder;

/**
 * Represents Folders which have been blacklisted by the user
 */
public class BlacklistedFolder {

    public BlacklistedFolder() {
        this(0,null);
    }

    public BlacklistedFolder(ImageFolder imageFolder){
        this(0,imageFolder.getFolderPath());
    }

    // Different UIDs are provided by Room
    @SuppressWarnings("SameParameterValue")
    private BlacklistedFolder(int uid, Uri path) {
        this.uid = uid;
        Path = path;
    }

    public int uid;

    public Uri Path;
}
