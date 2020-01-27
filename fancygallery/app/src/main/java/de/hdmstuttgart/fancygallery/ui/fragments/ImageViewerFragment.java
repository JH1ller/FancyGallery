package de.hdmstuttgart.fancygallery.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import de.hdmstuttgart.fancygallery.R;
import de.hdmstuttgart.fancygallery.core.ApplicationFactory;
import de.hdmstuttgart.fancygallery.infrastructure.providers.CurrentImageListProvider;
import de.hdmstuttgart.fancygallery.ui.adapters.ViewPagerAdapter;


public class ImageViewerFragment
        extends Fragment {

    private int currentPosition;

    public ImageViewerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ImageListFragment.
     */
    public static ImageViewerFragment newInstance() {
        return new ImageViewerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CurrentImageListProvider currentImageListProvider = ApplicationFactory.getCurrentImageListProvider();
        currentPosition = currentImageListProvider.getCurrentPosition();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(
                R.layout.fragment_image_viewer,
                container,
                false);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getContext(), view);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(currentPosition);
        ImageButton closeBtn = view.findViewById(R.id.close);

        closeBtn.setOnClickListener(__ -> {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if(activity != null) {
                activity.onBackPressed();
            }
        });

        return view;
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
