package de.hdmstuttgart.fancygallery.infrastructure.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.lang.ref.WeakReference;

import de.hdmstuttgart.fancygallery.abstractions.ICallback;
import de.hdmstuttgart.fancygallery.abstractions.Unused;
import de.hdmstuttgart.fancygallery.model.Image;
import de.hdmstuttgart.fancygallery.infrastructure.tasks.results.ThumbnailResult;

/**
 * Task class which create a {@link ThumbnailResult} for the given {@link Image}
 * <br>
 *
 * Contains {@link ICallback} which are run in UI Thread and
 * have to be set before the task is executed:
 * <ul>
 *  <li> successCallback
 * </ul>
 */
public class RetrieveThumbnailTask extends AsyncTask<Image, ThumbnailResult, Unused> {
    private static final String TAG = "RetrieveThumbnailTask";

    private ICallback<ThumbnailResult> callback;
    private final WeakReference<Context> contextWeakReference;

    public RetrieveThumbnailTask(WeakReference<Context> contextWeakReference) {
        this.contextWeakReference = contextWeakReference;
    }

    public RetrieveThumbnailTask setCallback(ICallback<ThumbnailResult> callback){
        this.callback = callback;
        return this;
    }

    @Override
    protected Unused doInBackground(Image... images) {
        for(Image image:images){
            Log.d(TAG,"Loading image: " + image.getUri() );
            Bitmap bitmap = loadThumbnail(image);
            ThumbnailResult thumbnailResult = new ThumbnailResult();
            thumbnailResult.bitmap = bitmap;
            thumbnailResult.image = image;
            publishProgress(thumbnailResult);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(ThumbnailResult... thumbnailResults){
        Log.d(TAG,"Loading thumbnail" );
        callback.onCallback(thumbnailResults[0]);
    }

    private Bitmap loadThumbnail(Image image) {
        Bitmap bitmap;
        Context context = contextWeakReference.get();

        if(context == null){
            Log.w(TAG,"Context is null. Is the Activity no longer available?");
            return null;
        }

        if(isCancelled()){
            return null;
        }

        bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                context.getContentResolver(),
                image.getId(),
                MediaStore.Images.Thumbnails.MINI_KIND,
                null );

        if(isCancelled()){
            return null;
        }

        if(bitmap == null){
            Bitmap fullImage = BitmapFactory.decodeFile(image.getUri().getPath());
            bitmap = ThumbnailUtils.extractThumbnail(fullImage, 512,512, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        Log.d(TAG,"Returning bitmap");
        return bitmap;
    }
}
