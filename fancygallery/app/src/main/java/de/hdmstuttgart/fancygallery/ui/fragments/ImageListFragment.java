package de.hdmstuttgart.fancygallery.ui.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.util.Objects;

import de.hdmstuttgart.fancygallery.R;
import de.hdmstuttgart.fancygallery.core.ApplicationFactory;
import de.hdmstuttgart.fancygallery.core.Constants;
import de.hdmstuttgart.fancygallery.infrastructure.providers.CurrentImageListProvider;
import de.hdmstuttgart.fancygallery.model.Image;
import de.hdmstuttgart.fancygallery.infrastructure.tasks.results.ImageListResult;
import de.hdmstuttgart.fancygallery.model.ImageFolder;
import de.hdmstuttgart.fancygallery.model.lists.ImageList;
import de.hdmstuttgart.fancygallery.ui.adapters.ImageGridAdapter;
import de.hdmstuttgart.fancygallery.ui.interfaces.InteractableLayout;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnImageClickedListener} interface
 * to handle interaction events.
 * Use the {@link ImageListFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p>
 *     This fragment shows all {@link Image} of a {@link ImageFolder}.
 * </p>
 * <p>
 *     If the user clicks on an {@link Image} the activity gets notified.
 * </p>
 */
public class ImageListFragment
        extends Fragment
        implements ImageGridAdapter.OnImageClickedListener, InteractableLayout {

    private static final String ARG_IMAGE_FOLDER_URI = "param1";

    private CurrentImageListProvider currentImageListProvider;
    private OnImageClickedListener listener;
    private Uri imageFolderUri;
    private Toolbar toolbar;
    private ImageList imageList;
    private ImageGridAdapter imageGridAdapter;

    private GridLayoutManager layoutManager;

    public ImageListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param imageFolderUri The Uri of an ImageFolder
     * @return A new instance of fragment ImageListFragment.
     */
    public static ImageListFragment newInstance(Uri imageFolderUri) {
        ImageListFragment fragment = new ImageListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_FOLDER_URI, imageFolderUri.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String imageFolderUriString = getArguments().getString(ARG_IMAGE_FOLDER_URI);
            imageFolderUri = Uri.parse(imageFolderUriString);
        }
        currentImageListProvider = ApplicationFactory.getCurrentImageListProvider();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(
                R.layout.fragment_image_list,
                container,
                false);

        setupRecyclerView(view);
        setupToolbar(view);

        ApplicationFactory
                .createRetrieveImageListTask(getContext())
                .setSuccessCallback(this::showImages)
                .execute(imageFolderUri);
        updateSpanCountPreferences(0);
        return view;
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(getContext(), 2);
        imageGridAdapter = new ImageGridAdapter(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(200);
        recyclerView.setAdapter(imageGridAdapter);
    }

    @Override
    public void onStart() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if(activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().hide();
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void showImages(ImageListResult result) {
        imageList = result.imageList;
        currentImageListProvider.setImageListToShow(imageList);
        imageGridAdapter.updateImageList(imageList.getImages());
        layoutManager.onRestoreInstanceState(currentImageListProvider.getImageListLayoutState());
    }

    @Override
    public void onPause() {
        super.onPause();
        currentImageListProvider.setImageListLayoutState(layoutManager.onSaveInstanceState());
    }

    public void zoomOut() {
        updateSpanCountPreferences(-1);
    }
    public void zoomIn() {
        updateSpanCountPreferences(1);
    }

    /**
     * Determines the new spanCount and updates user preferences.
     * Also updates if menu items are enabled
     * @param delta 0: no changes, positive: zoom in, negative: zoom out
     */
    private void updateSpanCountPreferences(int delta){
        int spanCount = getSpanCount();
        int newSpanCount = spanCount - delta;

        // Reset newSpanCount if it exceeds limits.
        if(newSpanCount > Constants.IMAGE_LIST_MAX_SPAN_COUNT
                || newSpanCount < Constants.IMAGE_LIST_MIN_SPAN_COUNT){
            newSpanCount = spanCount;
        }

        MenuItem item = toolbar.getMenu().findItem(R.id.zoom_out);
        item.setEnabled(newSpanCount < Constants.IMAGE_LIST_MAX_SPAN_COUNT);

        item = toolbar.getMenu().findItem(R.id.zoom_in);
        item.setEnabled(newSpanCount >  Constants.IMAGE_LIST_MIN_SPAN_COUNT);
        layoutManager.setSpanCount(newSpanCount);
        if(delta != 0){
            saveSpanCountPreferences(newSpanCount);
        }
    }

    private void saveSpanCountPreferences(int currentSpanCount) {
        Context context = Objects.requireNonNull(getContext());
        String setting;
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setting = Constants.IMAGE_LIST_SPAN_COUNT_LANDSCAPE;
        } else {
            setting = Constants.IMAGE_LIST_SPAN_COUNT_PORTRAIT;
        }

        context.getSharedPreferences(Constants.PREFS, 0)
                .edit()
                .putInt(setting, currentSpanCount)
                .apply();
    }

    private int getSpanCount() {
        Context context = Objects.requireNonNull(getContext());
        int orientation = getResources().getConfiguration().orientation;
        String setting;
        int defaultValue;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setting = Constants.IMAGE_LIST_SPAN_COUNT_LANDSCAPE;
            defaultValue = Constants.IMAGE_LIST_SPAN_COUNT_LANDSCAPE_DEFAULT;
        } else {
            setting = Constants.IMAGE_LIST_SPAN_COUNT_PORTRAIT;
            defaultValue = Constants.IMAGE_LIST_SPAN_COUNT_PORTRAIT_DEFAULT;
        }
        return context.getSharedPreferences(Constants.PREFS, 0)
                .getInt(setting, defaultValue);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnImageClickedListener) {
            listener = (OnImageClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnImageClickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }

    /**
     * Forwards the clicked image to the listener
     *
     * @param image The image that has been clicked
     */
    @Override
    public void onImageClicked(Image image) {
        int currentPosition = imageList.getImages().indexOf(image);
        currentImageListProvider.setCurrentPosition(currentPosition);
        listener.onImageClicked(image);
    }

    private void setupToolbar(View view) {
        String folderName = new File(imageFolderUri.getPath()).getName();
        toolbar = view.findViewById(R.id.image_list_toolbar);
        toolbar.inflateMenu(R.menu.image_list_menu);
        toolbar.setTitle(folderName);
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.zoom_out) {
                zoomOut();
                return true;
            }
            if (id == R.id.zoom_in) {
                zoomIn();
                return true;
            }
            return false;
        });
        toolbar.setNavigationOnClickListener(__ -> {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity != null) {
                activity.onBackPressed();
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnImageClickedListener {
        void onImageClicked(Image image);
    }
}
