package de.hdmstuttgart.fancygallery.model;

import android.net.Uri;

/**
 * Used to represents a folder containing images on a device
 * Does only contain the first image of a folder
 * Can be selected.
 */
public class ImageFolder {

    private final String name;
    private final Uri folderPath;
    private final Image firstImage;
    private boolean isSelected;
    private int count;

    public ImageFolder(
            int count,
            String name,
            Uri folderPath,
            Image firstImage ) {
        this.count = count;
        this.name = name;
        this.folderPath = folderPath;
        this.firstImage = firstImage;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public void addCountByOne(){
        count++;
    }

    public Uri getFolderPath() {
        return folderPath;
    }

    public Image getFirstImage() {
        return firstImage;
    }

}
