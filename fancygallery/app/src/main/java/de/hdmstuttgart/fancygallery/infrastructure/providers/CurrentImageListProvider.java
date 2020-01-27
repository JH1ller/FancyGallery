package de.hdmstuttgart.fancygallery.infrastructure.providers;

import android.os.Parcelable;

import de.hdmstuttgart.fancygallery.model.lists.ImageList;

/**
 * Provider class used to inject ImageLists in different fragments which the user is looking at
 * that moment.
 *
 * This reduces redundant AsyncTask calls to retrieve ImageLists.
 */
public class CurrentImageListProvider {

    private ImageList imageListToShow;
    private int currentPosition;
    private Parcelable folderListLayoutState;
    private Parcelable ImageListLayoutState;

    public CurrentImageListProvider() {
        imageListToShow = new ImageList();
    }

    public ImageList getImageListToShow() {
        return imageListToShow;
    }

    public void setImageListToShow(ImageList imageListToShow) {
        this.imageListToShow = imageListToShow;
        this.currentPosition = 0;
    }

    public Parcelable getFolderListLayoutState() {
        return folderListLayoutState;
    }

    public void setFolderListLayoutState(Parcelable folderListLayoutState) {
        this.folderListLayoutState = folderListLayoutState;
    }

    public Parcelable getImageListLayoutState() {
        return ImageListLayoutState;
    }

    public void setImageListLayoutState(Parcelable imageListLayoutState) {
        ImageListLayoutState = imageListLayoutState;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
}
