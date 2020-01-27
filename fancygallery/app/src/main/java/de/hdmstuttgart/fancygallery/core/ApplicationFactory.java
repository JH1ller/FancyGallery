package de.hdmstuttgart.fancygallery.core;

import android.content.Context;

import java.lang.ref.WeakReference;

import de.hdmstuttgart.fancygallery.infrastructure.providers.CurrentImageListProvider;
import de.hdmstuttgart.fancygallery.infrastructure.tasks.RetrieveImageFolderListTask;
import de.hdmstuttgart.fancygallery.infrastructure.tasks.RetrieveImageListTask;
import de.hdmstuttgart.fancygallery.infrastructure.tasks.RetrieveThumbnailTask;

/**
 * Factory class mainly used for Task creation which ensures that contexts are wrapped
 * by WeakReferences
 *
 * Also provides a singleton of {@link CurrentImageListProvider}.
 */
public class ApplicationFactory {

    public static RetrieveImageFolderListTask createRetrieveImageFolderTask(Context context){
        WeakReference<Context> weakReference = new WeakReference<>(context);
        return new RetrieveImageFolderListTask(weakReference);
    }

    public static RetrieveImageListTask createRetrieveImageListTask(Context context){
        WeakReference<Context> weakReference = new WeakReference<>(context);
        return new RetrieveImageListTask(weakReference);
    }

    public static RetrieveThumbnailTask createRetrieveThumbnailTask(Context context){
        WeakReference<Context> weakReference = new WeakReference<>(context);
        return new RetrieveThumbnailTask(weakReference);
    }

    /**
     * Singleton
     */
    private static CurrentImageListProvider currentImageListProvider;

    public static CurrentImageListProvider getCurrentImageListProvider(){
        if(currentImageListProvider == null){
            currentImageListProvider = new CurrentImageListProvider();
        }
        return currentImageListProvider;
    }

}
