package de.hdmstuttgart.fancygallery.infrastructure.tasks.requests;

import de.hdmstuttgart.fancygallery.infrastructure.tasks.RetrieveImageFolderListTask;
import de.hdmstuttgart.fancygallery.model.lists.ImageFolderList;

/**
 * Request class required to execute {@link RetrieveImageFolderListTask}
 */
public class RetrieveImageFolderListRequest {
    public final int orderBy;

    /**
     * Creates a new instance of {@link RetrieveImageFolderListRequest}
     *
     * @param orderBy How the task should order the {@link ImageFolderList}
     */
    public RetrieveImageFolderListRequest(int orderBy) {
        this.orderBy = orderBy;
    }
}
