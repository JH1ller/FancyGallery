package de.hdmstuttgart.fancygallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.FragmentManager;

import android.net.Uri;
import android.os.Bundle;

import de.hdmstuttgart.fancygallery.abstractions.IOnBackPressed;
import de.hdmstuttgart.fancygallery.model.Image;
import de.hdmstuttgart.fancygallery.model.ImageFolder;
import de.hdmstuttgart.fancygallery.model.database.BlacklistedFolder;
import de.hdmstuttgart.fancygallery.ui.fragments.ImageFolderListFragment;
import de.hdmstuttgart.fancygallery.ui.fragments.ImageListFragment;
import de.hdmstuttgart.fancygallery.ui.fragments.ImageViewerFragment;
import de.hdmstuttgart.fancygallery.ui.fragments.ManageBlacklistFragment;
import de.hdmstuttgart.fancygallery.ui.fragments.RequestPermissionsFragment;

/**
 * Main Activity which contains a single fragment.<br>
 *
 * This Activity connects all available Fragments by acting as a listener for various events
 * occurring in Fragments.
 *
 * The Home/Main fragment is {@link ImageFolderListFragment}.<br>
 */
public class AppActivity
    extends AppCompatActivity
    implements
        ImageFolderListFragment.ImageFolderListFragmentListener,
        ImageListFragment.OnImageClickedListener,
        RequestPermissionsFragment.RequestPermissionsFragmentListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showImageFolderListFragment();
    }

    private void replaceFragment(Fragment nextFragment){
        replaceFragment(nextFragment,null);
    }

    private void replaceFragment(Fragment nextFragment, String tag){
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, nextFragment,tag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Transitions to the {@link ImageFolderListFragment} which is the entry fragment of this
     * activity.
     * <br>
     * It is tagged to ensure only one instance of {@link ImageFolderListFragment} exists.
     */
    private void showImageFolderListFragment(){
        String TAG_IMAGE_FOLDER_LIST = "ImageFolder";
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(TAG_IMAGE_FOLDER_LIST);

        if(fragment  == null) {
            fragment = ImageFolderListFragment.newInstance();
            replaceFragment(fragment, TAG_IMAGE_FOLDER_LIST);
        }
    }

    /**
     * Transitions to the {@link ImageListFragment} when an {@link ImageFolder} has been clicked on.
     * @param imageFolderUri The Uri of the {@link ImageFolder} that has been clicked on.
     */
    @Override
    public void onImageFolderClicked(Uri imageFolderUri) {
        Fragment fragment = ImageListFragment.newInstance(imageFolderUri);
        replaceFragment(fragment);
    }

    /**
     * Transitions to the {@link RequestPermissionsFragment} to ask the user
     * for required permissions.
     */
    @Override
    public void onPermissionsRequired() {
        Fragment nextFragment = RequestPermissionsFragment.newInstance();
        replaceFragment(nextFragment);
    }

    /**
     * Transitions to the {@link ManageBlacklistFragment} which allows the user to manage
     * {@link BlacklistedFolder}
     */
    @Override
    public void onShowManageBlacklist() {
        Fragment nextFragment = ManageBlacklistFragment.newInstance();
        replaceFragment(nextFragment);
    }

    @Override
    public void onError() {
        // TODO: Show error to user
    }

    /**
     * Handles back presses.
     * Forwards a back press to the current fragment if it implements {@link IOnBackPressed}.
     */
    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if(fragment instanceof IOnBackPressed){
            forwardBackPressToFragment((IOnBackPressed)fragment);
        }
        else{
            super.onBackPressed();
        }
    }

    private void forwardBackPressToFragment(IOnBackPressed fragment){
        FragmentManager manager = getSupportFragmentManager();

        boolean backHandled = fragment.onBackPressed();
        if(!backHandled){
            manager.popBackStack();
            if(manager.getBackStackEntryCount() == 1){
                super.onBackPressed();
            }
        }
    }

    /**
     * Transitions to the {@link ImageViewerFragment} if an {@link Image} has been clicked on.
     * @param image the image which has been clicked on.
     */
    @Override
    public void onImageClicked(Image image) {
        Fragment nextFragment = ImageViewerFragment.newInstance();
        replaceFragment(nextFragment);
    }

    /**
     * Transitions to the {@link ImageListFragment} when the app has the required permissions.
     */
    @Override
    public void onPermissionsGranted() {
        getSupportFragmentManager().popBackStack();
        showImageFolderListFragment();
    }
}
