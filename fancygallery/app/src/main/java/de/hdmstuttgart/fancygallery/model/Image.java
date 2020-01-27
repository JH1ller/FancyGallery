package de.hdmstuttgart.fancygallery.model;

import android.net.Uri;

import java.io.Serializable;

/**
 * Represents an image on a device
 */
public class Image implements Serializable {

    private final Uri uri;
    private final long id;

    public Image(long id, Uri uri) {
        this.id = id;
        this.uri = uri;
    }

    public long getId() {
        return id;
    }

    public Uri getUri() {
        return uri;
    }

    public String getFilename() {
        return uri.getLastPathSegment();
    }
}
