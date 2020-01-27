package de.hdmstuttgart.fancygallery.infrastructure.tasks;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;

import de.hdmstuttgart.fancygallery.abstractions.ICallback;
import de.hdmstuttgart.fancygallery.abstractions.Unused;
import de.hdmstuttgart.fancygallery.model.Image;
import de.hdmstuttgart.fancygallery.infrastructure.tasks.results.ImageListResult;
import de.hdmstuttgart.fancygallery.infrastructure.tasks.results.ResultType;

/**
 * Task class which creates an {@link ImageListResult} which is sorted.
 * <br>
 *
 * Contains {@link ICallback} which are run in UI Thread and
 * have to be set before the task is executed:
 * <ul>
 *  <li> successCallback
 * </ul>
 */
// TODO: Rewrite to be similar to RetrieveImageFolderListTask
public class RetrieveImageListTask extends AsyncTask<Uri, ImageListResult, Unused> {
    private static final String TAG = "RetrieveImageListTask";

    private ICallback<ImageListResult> successCallback;
    private final WeakReference<Context> contextWeakReference;

    public RetrieveImageListTask(WeakReference<Context> contextWeakReference) {
        this.contextWeakReference = contextWeakReference;
    }

    public RetrieveImageListTask setSuccessCallback(ICallback<ImageListResult> callback){
        this.successCallback = callback;
        return this;
    }

    @Override
    protected Unused doInBackground(Uri... uris) {
        for(Uri uri : uris){
            ImageListResult result = load(uri);
            publishProgress(result);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(ImageListResult... imageListResults){
        successCallback.onCallback(imageListResults[0]);
    }

    private ImageListResult load(Uri folder) {
        ImageListResult result = new ImageListResult();

        if(folder == null || folder.getPath() == null){
            Log.wtf(TAG,"folder should never be null here");
            throw new IllegalArgumentException("folder");
        }

        // A reference to context is needed to access the MediaStore.
        Context context = contextWeakReference.get();
        if(context == null){
            Log.e(TAG,"Context is unexpectedly null. Can't access MediaStore without context.");
            result.resultType = ResultType.error;
            return result;
        }

        // Check Permissions
        if (context.checkSelfPermission( Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.w(TAG,"No permissions to read external storage. Canceling Task.");
            result.resultType = ResultType.noPermission;
            return result;
        }

        // Prepare Query arguments
        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
        };
        String selection = "";
        String[] selectionArgs = new String[]{};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED+" DESC";

        // Perform Query
        try( Cursor cursor = context.getContentResolver().query(
                contentUri,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )) {
            if (cursor == null )
            {
                Log.w(TAG,"Cursor is unexpectedly null.");
                Log.w(TAG,"ContentUri: " + contentUri);
                result.resultType = ResultType.error;
                return result;
            }
            int imagePathColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int imageIdColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

            while (cursor.moveToNext()){
                String imagePath = cursor.getString(imagePathColumnIndex);
                File imageFile = new File(imagePath);
                if(imageFile.exists() && imageFile.getParentFile().getPath().equals(folder.getPath())){
                    long imageId = cursor.getLong(imageIdColumnIndex);
                    Uri imageUri = Uri.fromFile(imageFile);
                    Image image = new Image(imageId, imageUri);
                    result.imageList.addImage(image);
                }
            }
        }
        return result;
    }
}
