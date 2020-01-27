package de.hdmstuttgart.fancygallery.infrastructure.tasks.results;

import android.graphics.Bitmap;

import de.hdmstuttgart.fancygallery.model.Image;

/**
 * Result class of {@link de.hdmstuttgart.fancygallery.infrastructure.tasks.RetrieveThumbnailTask}
 */
public class ThumbnailResult {
    public Bitmap bitmap;
    public Image image;
}
