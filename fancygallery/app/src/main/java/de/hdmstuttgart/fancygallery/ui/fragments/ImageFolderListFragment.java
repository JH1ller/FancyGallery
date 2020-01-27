package de.hdmstuttgart.fancygallery.ui.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.hdmstuttgart.fancygallery.R;
import de.hdmstuttgart.fancygallery.abstractions.IOnBackPressed;
import de.hdmstuttgart.fancygallery.core.ApplicationFactory;
import de.hdmstuttgart.fancygallery.core.Constants;
import de.hdmstuttgart.fancygallery.infrastructure.database.AppDatabase;
import de.hdmstuttgart.fancygallery.infrastructure.database.BlacklistedFolderDto;
import de.hdmstuttgart.fancygallery.infrastructure.providers.CurrentImageListProvider;
import de.hdmstuttgart.fancygallery.infrastructure.tasks.requests.RetrieveImageFolderListRequest;
import de.hdmstuttgart.fancygallery.model.ImageFolder;
import de.hdmstuttgart.fancygallery.model.OrderBy;
import de.hdmstuttgart.fancygallery.model.lists.ImageFolderList;
import de.hdmstuttgart.fancygallery.ui.adapters.ImageFolderGridAdapter;
import de.hdmstuttgart.fancygallery.ui.interfaces.InteractableLayout;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageFolderListFragmentListener} interface
 * to handle interaction events.
 * Use the {@link ImageFolderListFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p>
 *     This Fragment shows all Folders containing Images.<br>
 *     This fragment does not show blacklisted folders.<br>
 * </p>
 * <p>
 *     Long Pressing an ImageFolder causes the Fragment to
 *     {@link ImageFolderListFragment#enterSelectionMode()}
 * </p>
 * <p>
 *     When no {@link ImageFolder} is selected, back is pressed or cancel has been clicked
 *     the fragment {@link ImageFolderListFragment#leaveSelectionMode()}.
 * </p>
 */
public class ImageFolderListFragment
        extends Fragment
        implements
            ImageFolderGridAdapter.OnImageFolderClickedListener,
            InteractableLayout,
            PopupMenu.OnMenuItemClickListener,
            IOnBackPressed {

    private ImageFolderListFragmentListener listener;
    private ImageFolderGridAdapter imageFolderGridAdapter;
    private ImageFolderList imageFolderList;
    private GridLayoutManager layoutManager;
    private TextView textToolbarCount;
    private RelativeLayout toolbarSelection;
    private boolean isInSelectionMode;
    private CurrentImageListProvider currentImageListProvider;
    private Toolbar toolbar;
    private View emptyView;
    private RecyclerView recyclerView;
    private ImageButton filterButton;

    public ImageFolderListFragment() {
        // Required empty constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ImageFolderListFragment.
     */
    public static ImageFolderListFragment newInstance() {
        return new ImageFolderListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentImageListProvider = ApplicationFactory.getCurrentImageListProvider();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(!(context instanceof ImageFolderListFragmentListener)){
            throw new RuntimeException(context.toString()
                    + " must implement ImageFolderListFragmentListener");
        }
        listener = (ImageFolderListFragmentListener) context;
    }

    @Override
    public void onPause() {
        super.onPause();
        currentImageListProvider.setFolderListLayoutState(layoutManager.onSaveInstanceState());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(
                R.layout.fragment_image_folder_list,
                container,
                false);

        emptyView = view.findViewById(R.id.empty_view);
        currentImageListProvider.setImageListLayoutState(null);
        setupToolbar(view);
        setupToolbarSelection(view);
        setupRecyclerView(view);
        loadFolderList();
        updateSpanCountPreferences(0);

        return view;
    }

    private int getSpanCount() {
        Context context = Objects.requireNonNull(getContext());
        int orientation = getResources().getConfiguration().orientation;
        String setting;
        int defaultValue;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setting = Constants.FOLDER_LIST_SPAN_COUNT_LANDSCAPE;
            defaultValue = Constants.FOLDER_LIST_SPAN_COUNT_LANDSCAPE_DEFAULT;
        } else {
            setting = Constants.FOLDER_LIST_SPAN_COUNT_PORTRAIT;
            defaultValue = Constants.FOLDER_LIST_SPAN_COUNT_PORTRAIT_DEFAULT;
        }
        return context.getSharedPreferences(Constants.PREFS, 0)
                .getInt(setting, defaultValue);
    }

    private void setupToolbar(View view){
        toolbar = view.findViewById(R.id.image_folder_list_toolbar);
        toolbar.inflateMenu(R.menu.home_menu);
        filterButton = toolbar.findViewById(R.id.filter_options);

        setupSettingsMenuItemListener();
        setupFilterMenuItemListener();
    }
    private void setupToolbarSelection(View view){
        toolbarSelection = view.findViewById(R.id.toolbar_selection);
        toolbarSelection.setVisibility(View.GONE);

        Button buttonToolbarHide = view.findViewById(R.id.hide);
        buttonToolbarHide.setOnClickListener(__ -> hideSelectedFolders());

        Button buttonToolbarCancel = view.findViewById(R.id.cancel);
        buttonToolbarCancel.setOnClickListener(__ -> clearSelection());

        textToolbarCount = view.findViewById(R.id.text_selected_count);
    }

    private void setupFilterMenuItemListener(){
        filterButton.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(getContext(),view);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.image_folder_filter_menu,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        });
    }

    private void setupSettingsMenuItemListener() {
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            switch (id){
                case R.id.manage_hidden:
                    listener.onShowManageBlacklist();
                    return true;
                case R.id.zoom_out:
                    zoomOut();
                    return true;
                case R.id.zoom_in:
                    zoomIn();
                    return true;
                default:
                    return false;
            }
        });
    }

    private void setupRecyclerView(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
        imageFolderGridAdapter = new ImageFolderGridAdapter(this);

        layoutManager = new GridLayoutManager(
                getContext(),
                Constants.FOLDER_LIST_SPAN_COUNT_PORTRAIT_DEFAULT);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imageFolderGridAdapter);
    }

    private void loadFolderList() {
        int orderBy = getOrderBy();
        RetrieveImageFolderListRequest request = new RetrieveImageFolderListRequest(orderBy);
        ApplicationFactory
                .createRetrieveImageFolderTask(getContext())
                .setSuccessCallback(this::showFolderImages)
                .setNoPermissionsCallback(unused -> notifyPermissionsRequiredListener())
                .setErrorCallback(unused1 -> notifyErrorListener())
                .execute(request);
    }

    private int getOrderBy() {
        Context context = Objects.requireNonNull(getContext());
        return context.getSharedPreferences(Constants.PREFS, 0)
                .getInt(Constants.FOLDER_LIST_ORDER_BY, Constants.FOLDER_LIST_ORDER_BY_DEFAULT);
    }

    private void saveOrderBy(int orderBy){
        Context context = Objects.requireNonNull(getContext());
        context.getSharedPreferences(Constants.PREFS, 0)
                .edit()
                .putInt(Constants.FOLDER_LIST_ORDER_BY, orderBy)
                .apply();
    }

    private void showFolderImages(ImageFolderList result) {
        imageFolderList = result;
        imageFolderGridAdapter.updateImageList(imageFolderList.getImageFolders());
        layoutManager.onRestoreInstanceState(currentImageListProvider.getFolderListLayoutState());
        if(result.getImageFolders().size() == 0){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void notifyPermissionsRequiredListener(){
        listener.onPermissionsRequired();
    }

    private void notifyErrorListener(){
        listener.onError();
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
        if(newSpanCount > Constants.FOLDER_LIST_MAX_SPAN_COUNT
                || newSpanCount < Constants.FOLDER_LIST_MIN_SPAN_COUNT){
            newSpanCount = spanCount;
        }

        updateUiForNewSpanCount(newSpanCount);

        if(delta != 0){
            saveSpanCountPreferences(newSpanCount);
        }
    }

    private void updateUiForNewSpanCount(int newSpanCount) {
        MenuItem item = toolbar.getMenu().findItem(R.id.zoom_out);
        item.setEnabled(newSpanCount < Constants.FOLDER_LIST_MAX_SPAN_COUNT);

        item = toolbar.getMenu().findItem(R.id.zoom_in);
        item.setEnabled(newSpanCount >  Constants.FOLDER_LIST_MIN_SPAN_COUNT);

        layoutManager.setSpanCount(newSpanCount);
    }

    private void saveSpanCountPreferences(int currentSpanCount) {
        Context context = Objects.requireNonNull(getContext());
        String setting;
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setting = Constants.FOLDER_LIST_SPAN_COUNT_LANDSCAPE;
        } else {
            setting = Constants.FOLDER_LIST_SPAN_COUNT_PORTRAIT;
        }

        context.getSharedPreferences(Constants.PREFS, 0)
                .edit()
                .putInt(setting, currentSpanCount)
                .apply();
    }

    @Override
    public void onImageFolderClicked(ImageFolderGridAdapter.ImageViewHolder imageViewHolder) {
        if(isInSelectionMode) {
            handleImageViewHolderSelection(imageViewHolder);
        }
        else {
            Uri folderPath = imageViewHolder.getImageFolder().getFolderPath();
            listener.onImageFolderClicked(folderPath);
        }
    }

    @Override
    public void onImageFolderLongClicked(ImageFolderGridAdapter.ImageViewHolder imageViewHolder) {
        enterSelectionMode();
        handleImageViewHolderSelection(imageViewHolder);
    }

    private void enterSelectionMode(){
        if(!isInSelectionMode){
            toolbarSelection.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.GONE);
            isInSelectionMode = true;
        }
    }

    private void leaveSelectionMode(){
        if(isInSelectionMode){
            toolbarSelection.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            isInSelectionMode = false;
        }
    }

    private void handleImageViewHolderSelection(
            ImageFolderGridAdapter.ImageViewHolder imageViewHolder){
        toggleImageViewHolder(imageViewHolder);
        updateSelectionToolbar();
    }

    private void toggleImageViewHolder(ImageFolderGridAdapter.ImageViewHolder imageViewHolder){
        ImageFolder imageFolder = imageViewHolder.getImageFolder();
        ImageView imageView = imageViewHolder.getImageView();
        if(imageFolder.isSelected()){
            imageFolder.setSelected(false);
            imageView.setAlpha(1F);
        }
        else{
            imageFolder.setSelected(true);
            imageView.setAlpha(0.3F);
        }
    }

    private void updateSelectionToolbar(){
        long selectedCount = imageFolderList.getImageFolders()
                .stream()
                .filter(ImageFolder::isSelected)
                .count();
        if(selectedCount == 0){
            leaveSelectionMode();
        }
        else {
            int total = imageFolderList.getImageFolders().size();
            String countText = selectedCount + " / " + total;
            textToolbarCount.setText(countText);
        }
    }

    private void clearSelection(){
        List<ImageFolder> selectedFolders = imageFolderList
                .getImageFolders()
                .stream()
                .filter(ImageFolder::isSelected)
                .collect(Collectors.toList());
        selectedFolders.forEach(imageFolder -> imageFolder.setSelected(false));
        leaveSelectionMode();
        loadFolderList();
    }

    private void hideSelectedFolders(){
        List<ImageFolder> imageFoldersToHide = imageFolderList
                .getImageFolders()
                .stream()
                .filter(ImageFolder::isSelected)
                .collect(Collectors.toList());

        new Thread(() -> {
            Context context = Objects.requireNonNull(getContext());
            AppDatabase db = Room
                    .databaseBuilder(
                        context,
                        AppDatabase.class,
                        Constants.DATABASE_BLACKLIST_NAME)
                    .build();

            for(ImageFolder imageFolder:imageFoldersToHide){
                BlacklistedFolderDto dto = new BlacklistedFolderDto(imageFolder);
                db.blacklistedFolderDao().insert(dto);
            }
        }).start();

        leaveSelectionMode();
        loadFolderList();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onBackPressed() {
        if(isInSelectionMode){
            List<ImageFolder> imageFolders = imageFolderList.getImageFolders();
            for(ImageFolder imageFolder:imageFolders){
                imageFolder.setSelected(false);
            }
            imageFolderGridAdapter.updateImageList(imageFolders);
            leaveSelectionMode();
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.name_ascending:
                saveOrderBy(OrderBy.NAME_ASCENDING);
                break;
            case R.id.name_descending:
                saveOrderBy(OrderBy.NAME_DESCENDING);
                break;
            case R.id.date_modified_ascending:
                saveOrderBy(OrderBy.DATE_MODIFIED_ASCENDING);
                break;
            case R.id.date_modified_descending:
                saveOrderBy(OrderBy.DATE_MODIFIED_DESCENDING);
                break;
            case R.id.date_taken_ascending:
                saveOrderBy(OrderBy.DATE_TAKEN_ASCENDING);
                break;
            case R.id.date_taken_descending:
                saveOrderBy(OrderBy.DATE_TAKEN_DESCENDING);
                break;
            case R.id.count_ascending:
                saveOrderBy(OrderBy.COUNT_ASCENDING);
                break;
            case R.id.count_descending:
                saveOrderBy(OrderBy.COUNT_DESCENDING);
                break;
            default:
                return false;
        }
        loadFolderList();
        return true;

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface ImageFolderListFragmentListener {
        /**
         * Called when a ImageFolder has been selected
         *
         * @param uri The Uri of the ImageFolder
         */
        void onImageFolderClicked(Uri uri);

        /**
         * Called when fragment requires permissions
         */
        void onPermissionsRequired();

        /**
         * Called when ManageBlacklist should be shown
         */
        void onShowManageBlacklist();

        /**
         * Called when fragment requires permissions
         */
        void onError();
    }
}
