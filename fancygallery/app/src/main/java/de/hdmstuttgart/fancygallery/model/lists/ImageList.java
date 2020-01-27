package de.hdmstuttgart.fancygallery.model.lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdmstuttgart.fancygallery.model.Image;

/**
 * Contains a list of {@link Image}
 */
public class ImageList {
    private final List<Image> images;

    public ImageList() {
        this.images = new ArrayList<>();
    }

    public void addImage(Image image){
        images.add(image);
    }

    /**
     * @return Returns an unmodifiableList of Images
     */
    public List<Image> getImages() {
        return Collections.unmodifiableList(images);
    }
}
