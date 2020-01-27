package de.hdmstuttgart.fancygallery.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;

import java.io.File;

import de.hdmstuttgart.fancygallery.R;
import de.hdmstuttgart.fancygallery.core.ApplicationFactory;
import de.hdmstuttgart.fancygallery.infrastructure.providers.CurrentImageListProvider;
import de.hdmstuttgart.fancygallery.model.lists.ImageList;
import de.hdmstuttgart.fancygallery.ui.imageviews.TouchViewPagerImageView;

public class ViewPagerAdapter extends PagerAdapter {
    private final Context context;
    private final ImageList imageList;
    private final CurrentImageListProvider currentImageListProvider;
    private final View parentView;

    public ViewPagerAdapter(Context context, View view) {
        this.context = context;
        currentImageListProvider = ApplicationFactory.getCurrentImageListProvider();
        this.imageList = currentImageListProvider.getImageListToShow();
        this.parentView = view;
    }

    /*
     * This callback is responsible for creating a page. We inflate the layout and set the drawable
     * to the ImageView based on the position. In the end we add the inflated layout to the parent
     * parent. This method returns an object key to identify the page view, but in this example page
     * view itself acts as the object key
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.item_pager, parent,false);


        TouchViewPagerImageView imageView =  view.findViewById(R.id.iv_image_viewer);

        imageView.setImageURI(imageList.getImages().get(position).getUri());

        currentImageListProvider.setCurrentPosition(position);

        parent.addView(view);
        return view;
    }

    /*
     called when visible item is changed
     */
    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        TextView imageName = parentView.findViewById(R.id.name);
        String fileName = imageList.getImages().get(position).getFilename();
        String formattedFileName = fileName.length() > 50 ? "..." + fileName.substring(fileName.length() - 50) : fileName;
        imageName.setText(formattedFileName);
        ImageButton shareBtn = parentView.findViewById(R.id.share);

        // setup share button via FileProvider
        shareBtn.setOnClickListener(event -> {
            File imageFile = new File(imageList.getImages().get(position).getUri().getPath());
            Uri imageUri = FileProvider.getUriForFile(context, "de.hdmstuttgart.fancygallery.provider", imageFile);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setType("image/*");
            context.startActivity(Intent.createChooser(shareIntent, "Share image"));
        });
    }

    /*
      This callback is responsible for destroying a page. Since we are using view only as the
      object key we just directly remove the view from parent container
    */
    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object view) {
        container.removeView((View) view);
    }

    /*
    Returns the count of the total pages
    */
    @Override
    public int getCount() {
        return imageList.getImages().size();
    }

    /*
    Used to determine whether the page view is associated with object key returned by instantiateItem.
    Since here view only is the key we return view==object
    */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }


}
