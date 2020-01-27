package de.hdmstuttgart.fancygallery.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdmstuttgart.fancygallery.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RequestPermissionsFragmentListener} interface
 * to handle interaction events.
 * Use the {@link RequestPermissionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p>
 *     This fragment asks the user to grant permissions.
 *     If the user does not grant permissions, the app closes.
 * </p>
 */
public class RequestPermissionsFragment extends Fragment {

    private RequestPermissionsFragmentListener mListener;
    private final String[] appPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private static final int PERMISSIONS_REQUEST_CODE = 1240;

    public RequestPermissionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment RequestPermissionsFragment.
     */
    public static RequestPermissionsFragment newInstance() {
        return new RequestPermissionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(
                R.layout.fragment_request_permissions,
                container,
                false);

        if (checkAndRequestPermissions()) {
            mListener.onPermissionsGranted();
        }

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof RequestPermissionsFragmentListener) {
            mListener = (RequestPermissionsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ImageFolderListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private boolean checkAndRequestPermissions() {
        // Check which permissions are granted
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String perm : appPermissions) {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this.getContext()), perm) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(perm);
            }
        }

        // Ask for non-granted permissions
        if (!listPermissionsNeeded.isEmpty()) {
            this.requestPermissions(
                    listPermissionsNeeded.toArray(new String[0]),
                    PERMISSIONS_REQUEST_CODE);

            return false;
        }

        // App has all permissions. Proceed ahead
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            HashMap<String, Integer> permissionResults = new HashMap<>();
            int deniedCount = 0;

            // Gather permission grant results
            for (int i = 0; i < grantResults.length; i++) {
                // Add only permissions which are denied
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResults.put(permissions[i], grantResults[i]);
                    deniedCount++;
                }
            }

            // Check if all permissions are granted
            if (deniedCount == 0) {
                // Proceed ahead with the app
                mListener.onPermissionsGranted();
            }
            // At least one or all permissions are denied
            else {
                for (Map.Entry<String, Integer> entry : permissionResults.entrySet()) {
                    String permName = entry.getKey();

                    // permission is denied (this is the first time, when "never ask again" is not checked)
                    // so ask again explaining the usage of permission
                    // shouldShowRequestPermissionRationale will return true
                    if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(this.getActivity()), permName)) {
                        // Show dialog of explanation
                        showDialog("Permissions required",
                                "This app needs storage reading permissions to work.",
                                "Yes, Grant permissions",
                                (DialogInterface dialogInterface, int i) -> {
                                    dialogInterface.dismiss();
                                    checkAndRequestPermissions();
                                },
                                "No, Exit app", (DialogInterface dialogInterface, int i) -> {
                                    dialogInterface.dismiss();
                                    this.getActivity().finish();
                                }
                        );
                    }
                    //permission is denied (and never ask again is  checked)
                    //shouldShowRequestPermissionRationale will return false
                    else {
                        // Ask user to go to settings and manually allow permissions
                        showDialog(
                                "Permissions have been denied",
                                "You have denied some permissions to the app. Please allow all permissions at [Setting] > [Permissions] screen",
                                "Go to Settings",
                                (DialogInterface dialogInterface, int i) -> {
                                    dialogInterface.dismiss();
                                    // Go to app settings
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", this.getActivity().getPackageName(), null));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    this.getActivity().finish();
                                },
                                "No, close app", (DialogInterface dialogInterface, int i) -> {
                                    dialogInterface.dismiss();
                                    this.getActivity().finish();
                                }
                        );
                        break;
                    }
                }
            }
        }
    }

    private void showDialog(
            String title,
            String msg,
            String positiveLabel,
            DialogInterface.OnClickListener positiveOnClick,
            String negativeLabel,
            DialogInterface.OnClickListener negativeOnClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(this.getContext()));
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLabel, positiveOnClick);
        builder.setNegativeButton(negativeLabel, negativeOnClick);

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface RequestPermissionsFragmentListener {
        /**
         * Called when Permissions have been granted by the user
         */
        void onPermissionsGranted();
    }
}
