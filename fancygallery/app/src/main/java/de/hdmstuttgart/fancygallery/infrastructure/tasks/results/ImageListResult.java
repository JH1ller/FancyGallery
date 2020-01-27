package de.hdmstuttgart.fancygallery.infrastructure.tasks.results;

import de.hdmstuttgart.fancygallery.model.lists.ImageList;

/**
 * Result class of {@link de.hdmstuttgart.fancygallery.infrastructure.tasks.RetrieveImageListTask}
 */
public class ImageListResult {
    public final ImageList imageList;
    public ResultType resultType;

    public ImageListResult(){
        imageList = new ImageList();
        resultType = ResultType.success;
    }
}
