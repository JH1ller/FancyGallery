package de.hdmstuttgart.fancygallery.model.lists;

import java.util.ArrayList;
import java.util.List;

import de.hdmstuttgart.fancygallery.model.ImageFolder;

/**
 * Contains a list of {@link ImageFolder}
 */
public class ImageFolderList {
    private final List<ImageFolder> imageFolders;

    public ImageFolderList(){
        imageFolders = new ArrayList<>();
    }

    public ImageFolderList(List<ImageFolder> imageFolders){
        this.imageFolders = imageFolders;
    }

    public List<ImageFolder> getImageFolders(){
        return imageFolders;
    }
}
