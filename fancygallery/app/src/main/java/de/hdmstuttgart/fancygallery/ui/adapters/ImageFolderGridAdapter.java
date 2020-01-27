package de.hdmstuttgart.fancygallery.ui.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdmstuttgart.fancygallery.R;
import de.hdmstuttgart.fancygallery.core.ApplicationFactory;
import de.hdmstuttgart.fancygallery.model.ImageFolder;

/**
 * Adapter class for {@link ImageFolder} in RecyclerViews.
 * <br>
 * Requires an instance of {@link OnImageFolderClickedListener}.
 */
public class ImageFolderGridAdapter
        extends RecyclerView.Adapter<ImageFolderGridAdapter.ImageViewHolder> {

    private final OnImageFolderClickedListener listener;
    private List<ImageFolder> imageFolderList;

    public ImageFolderGridAdapter(OnImageFolderClickedListener listener){
        this.listener = listener;
        this.imageFolderList = new ArrayList<>();
    }

    public void updateImageList(List<ImageFolder> imageFolderList){
        this.imageFolderList = imageFolderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View imageHolder = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.item_imagefolder, parent, false);

        return new ImageViewHolder(imageHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        final ImageFolder currentImageFolder = imageFolderList.get(position);
        holder.updateImage(currentImageFolder);
    }

    @Override
    public int getItemCount() {
        return imageFolderList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView nameText;
        final TextView countText;

        ImageFolder imageFolder;
        AsyncTask loadThumbnailTask;

        ImageViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.ih_imageView);
            nameText = view.findViewById(R.id.name);
            countText = view.findViewById(R.id.count);

            view.setOnClickListener( unused ->
                    listener.onImageFolderClicked(this));

            view.setOnLongClickListener(unused -> {
                listener.onImageFolderLongClicked(this);
                return true;
            });
        }

        void updateImage( ImageFolder imageFolder){
            this.imageFolder = imageFolder;
            nameText.setText(imageFolder.getName());
            countText.setText(String.format(Locale.getDefault(),"%d",imageFolder.getCount()));
            imageView.setImageBitmap(null);

            if(imageFolder.isSelected()){
                imageView.setAlpha(0.4F);
            }
            else{
                imageView.setAlpha(1F);
            }

            Context context = imageView.getContext();
            if(loadThumbnailTask != null){
                loadThumbnailTask.cancel(true);
            }
            loadThumbnailTask = ApplicationFactory
                    .createRetrieveThumbnailTask(context)
                    .setCallback(thumbnailResult -> {
                        if(thumbnailResult.bitmap != null && this.imageFolder.getFirstImage().getId() == thumbnailResult.image.getId()){
                            Animation animation = AnimationUtils.loadAnimation(imageView.getContext(),R.anim.fade_in);
                            imageView.setImageBitmap(thumbnailResult.bitmap);
                            imageView.startAnimation(animation);
                        }
                    })
                    .execute(imageFolder.getFirstImage());
        }

        public ImageView getImageView() {
            return imageView;
        }

        public ImageFolder getImageFolder() {
            return imageFolder;
        }

    }

    /**
     * Allows communication between Adapter and Fragment
     */
    public interface OnImageFolderClickedListener {
        /**
         * Called when an {@link ImageFolder} has been clicked on
         * @param imageViewHolder The {@link ImageViewHolder} that has been clicked on
         */
        void onImageFolderClicked(ImageViewHolder imageViewHolder);

        /**
         * Called when an {@link ImageFolder} has been long clicked on
         * @param imageViewHolder The {@link ImageViewHolder} that has been long clicked on
         */
        void onImageFolderLongClicked(ImageViewHolder imageViewHolder);
    }
}
