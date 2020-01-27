package de.hdmstuttgart.fancygallery.infrastructure.tasks;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import androidx.room.Room;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.hdmstuttgart.fancygallery.abstractions.CallbackNotSet;
import de.hdmstuttgart.fancygallery.abstractions.ICallback;
import de.hdmstuttgart.fancygallery.abstractions.Unused;
import de.hdmstuttgart.fancygallery.core.Constants;
import de.hdmstuttgart.fancygallery.infrastructure.database.AppDatabase;
import de.hdmstuttgart.fancygallery.infrastructure.tasks.requests.RetrieveImageFolderListRequest;
import de.hdmstuttgart.fancygallery.model.Image;
import de.hdmstuttgart.fancygallery.model.ImageFolder;
import de.hdmstuttgart.fancygallery.infrastructure.tasks.results.ResultType;
import de.hdmstuttgart.fancygallery.model.OrderBy;
import de.hdmstuttgart.fancygallery.model.lists.ImageFolderList;

/**
 * Task class which creates an {@link ImageFolderList} which is sorted and does not contain
 * blacklisted folders.
 * <br>
 *
 * Contains {@link ICallback} which are run in UI Thread and
 * have to be set before the task is executed:
 *
 * <ul>
 *  <li> successCallback
 *  <li> errorCallback
 *  <li> noPermissionsCallback
 * </ul>
 * <p>
 *  successCallback:
 *  Called when the task was successful.
 * </p>
 * <p>
 *  errorCallback:
 *  Called when an unexpected error happened.
 * </p>
 * <p>
 *  noPermissionsCallback:
 *  Called when the task can not be performed because of lacking permissions
 * </p>
 */
public class RetrieveImageFolderListTask
        extends AsyncTask<
            RetrieveImageFolderListRequest,
            Unused,
            RetrieveImageFolderListTask.TaskDto> {
    private static final String TAG = "RetrieveImageFolderListTask";

    private ICallback<ImageFolderList> successCallback;
    private ICallback<Unused> errorCallback;
    private ICallback<Unused> noPermissionsCallback;

    private final WeakReference<Context> contextWeakReference;

    public RetrieveImageFolderListTask(WeakReference<Context> contextWeakReference) {
        this.contextWeakReference = contextWeakReference;

        this.successCallback = new CallbackNotSet<>();
        this.errorCallback = new CallbackNotSet<>();
        this.noPermissionsCallback = new CallbackNotSet<>();
    }

    public RetrieveImageFolderListTask setSuccessCallback(ICallback<ImageFolderList> successCallback) {
        this.successCallback = successCallback;
        return this;
    }

    public RetrieveImageFolderListTask setErrorCallback(ICallback<Unused> errorCallback){
        this.errorCallback = errorCallback;
        return this;
    }

    public RetrieveImageFolderListTask setNoPermissionsCallback(ICallback<Unused>noPermissionsCallback){
        this.noPermissionsCallback = noPermissionsCallback;
        return this;
    }

    /**
     * Holds data which is used during a task
     */
    class TaskDto {

        Context context;
        ImageFolderList imageFolderList;
        ResultType resultType;

        Uri queryContentUri;
        String[] queryProjection;
        String querySelection;
        String[] querySelectionArgs;
        String querySortOrder;
        final RetrieveImageFolderListRequest request;

        TaskDto(RetrieveImageFolderListRequest request){
            this.request = request;
            imageFolderList = new ImageFolderList();
            resultType = ResultType.success;
        }
    }


    @Override
    protected TaskDto doInBackground(RetrieveImageFolderListRequest... requests) {
        TaskDto dto = new TaskDto(requests[0]);

        extractContext(dto);
        checkPermissions(dto);
        prepareQueryArguments(dto);
        performQuery(dto);
        applyBlacklist(dto);
        applyOrderBy(dto);

        return dto;
    }

    @Override
    protected void onPostExecute(TaskDto dto) {
        switch (dto.resultType){
            case error:
                errorCallback.onCallback(null);
                break;
            case success:
                successCallback.onCallback(dto.imageFolderList);
                break;
            case noPermission:
                noPermissionsCallback.onCallback(null);
                break;
            default:
                throw new RuntimeException("ResultType not implemented");
        }
    }

    private void extractContext(TaskDto dto){
        if(dto.resultType != ResultType.success){
            return;
        }

        // A reference to context is needed to access the MediaStore.
        Context context = contextWeakReference.get();
        if (context == null) {
            Log.e(TAG, "Context is unexpectedly null. Can't access MediaStore without context.");
            dto.resultType = ResultType.error;
        }
        else{
            dto.context = context;
        }
    }

    private void checkPermissions(TaskDto dto){
        if(dto.resultType != ResultType.success){
            return;
        }

        if (dto.context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.w(TAG, "No permissions to read external storage. Canceling Task.");
            dto.resultType = ResultType.noPermission;
        }

    }

    private void prepareQueryArguments(TaskDto dto){
        if(dto.resultType != ResultType.success){
            return;
        }
        dto.queryContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        dto.queryProjection = new String[]{
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
        };
        dto.querySelection = "";
        dto.querySelectionArgs = new String[]{};
        switch (dto.request.orderBy){
            case OrderBy.DATE_TAKEN_ASCENDING:
                dto.querySortOrder = MediaStore.Images.Media.DATE_TAKEN + " ASC";
                break;
            case OrderBy.DATE_TAKEN_DESCENDING:
                dto.querySortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";
                break;
            case OrderBy.DATE_MODIFIED_ASCENDING:
                dto.querySortOrder = MediaStore.Images.Media.DATE_MODIFIED + " ASC";
                break;
            case OrderBy.DATE_MODIFIED_DESCENDING:
                dto.querySortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC";
                break;
            default:
                dto.querySortOrder = "";
                break;
        }
    }

    private void performQuery(TaskDto dto) {

        if(dto.resultType != ResultType.success){
            return;
        }

        try (Cursor cursor = dto.context.getContentResolver().query(
                dto.queryContentUri,
                dto.queryProjection,
                dto.querySelection,
                dto.querySelectionArgs,
                dto.querySortOrder
        )) {
            // Created cursor can be null - cancel if it is null
            if (cursor == null) {
                Log.w(TAG, "Cursor is unexpectedly null.");
                Log.w(TAG, "ContentUri: " + dto.queryContentUri);
                dto.resultType = ResultType.error;
                return;
            }

            int imagePathId = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int imageIdColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

            // Using HashMap for O(1) contain checks
            // We still need a separate list to maintain correct insertion order
            HashMap<Uri, ImageFolder> foundImageFolders = new HashMap<>();
            List<ImageFolder> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                // Extract information from cursor ...
                String imagePath = cursor.getString(imagePathId);
                // Create File from path and extract FileUri / FolderUri
                File file = new File(imagePath);

                Uri folderUri = Uri.fromFile(file.getParentFile());

                ImageFolder tmp = foundImageFolders.get(folderUri);
                if (tmp != null ){
                    tmp.addCountByOne();
                } else if (file.exists()) {
                    // Create new ImageFolder since it doesn't exist yet
                    int count = 1;
                    String directoryName = file.getParentFile().getName();
                    Uri imageUri = Uri.fromFile(file);
                    long imageId = cursor.getLong(imageIdColumnIndex);
                    Image image = new Image(imageId,imageUri);
                    ImageFolder imageFolder = new ImageFolder(count, directoryName, folderUri, image);
                    foundImageFolders.put(folderUri,imageFolder);
                    list.add(imageFolder);
                }
            }
            dto.imageFolderList = new ImageFolderList(list);
        }
    }

    private void applyBlacklist(TaskDto dto){
        List<ImageFolder> list = dto.imageFolderList.getImageFolders();
        AppDatabase db = Room
                .databaseBuilder(
                        dto.context,
                        AppDatabase.class,
                        Constants.DATABASE_BLACKLIST_NAME)
                .build();

        // Use set for O(1) contain checks
        Set<Uri> blacklistedPaths = db
                .blacklistedFolderDao()
                .getAll()
                .stream()
                .map(folderDto -> Uri.parse(folderDto.UriPath))
                .collect(Collectors.toSet());

        list = list
                .stream()
                .filter(imageFolder -> !blacklistedPaths.contains(imageFolder.getFolderPath()))
                .collect(Collectors.toList());

        dto.imageFolderList = new ImageFolderList(list);
    }

    private void applyOrderBy(TaskDto dto){
        switch (dto.request.orderBy){
            case OrderBy.NAME_ASCENDING:
                orderByNameAscending(dto);
                break;
            case OrderBy.NAME_DESCENDING:
                orderByNameDescending(dto);
                break;
            case OrderBy.COUNT_ASCENDING:
                orderByCountAscending(dto);
                break;
            case OrderBy.COUNT_DESCENDING:
                orderByCountDescending(dto);
                break;
            default:
                break;
        }
    }

    private void orderByNameAscending(TaskDto dto) {
        Collections.sort(dto.imageFolderList.getImageFolders(),(folder1,folder2) ->
                folder2.getFolderPath().getLastPathSegment().toLowerCase().compareTo(
                        folder1.getFolderPath().getLastPathSegment().toLowerCase()));
    }

    private void orderByNameDescending(TaskDto dto) {
        Collections.sort(dto.imageFolderList.getImageFolders(),(folder1,folder2) ->
                folder1.getFolderPath().getLastPathSegment().toLowerCase().compareTo(
                        folder2.getFolderPath().getLastPathSegment().toLowerCase()));
    }

    private void orderByCountDescending(TaskDto dto) {
        Collections.sort(dto.imageFolderList.getImageFolders(),(folder1,folder2) ->
                folder2.getCount() - folder1.getCount());
    }

    private void orderByCountAscending(TaskDto dto) {
        Collections.sort(dto.imageFolderList.getImageFolders(),(folder1,folder2) ->
                folder1.getCount() - folder2.getCount());
    }

}
