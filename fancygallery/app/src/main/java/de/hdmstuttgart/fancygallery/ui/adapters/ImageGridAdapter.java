package de.hdmstuttgart.fancygallery.ui.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdmstuttgart.fancygallery.R;
import de.hdmstuttgart.fancygallery.core.ApplicationFactory;
import de.hdmstuttgart.fancygallery.model.Image;
import de.hdmstuttgart.fancygallery.infrastructure.tasks.results.ThumbnailResult;

/**
 * Adapter class for {@link Image} in RecyclerViews
 * <br>
 * Requires an instance of {@link OnImageClickedListener}.
 */
public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.MyViewHolder> {
    private final OnImageClickedListener onImageClickedListener;
    private List<Image> imageList;

    public ImageGridAdapter(OnImageClickedListener onImageClickedListener) {
        this.onImageClickedListener = onImageClickedListener;
        this.imageList = new ArrayList<>();
    }

    public void updateImageList(List<Image> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View imageHolder = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(imageHolder);
        viewHolder.imageView = imageHolder.findViewById(R.id.ih_imageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Image currentImage = imageList.get(position);
        holder.updateImage(currentImage);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Image image;
        AsyncTask loadThumbnailTask;

        MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.ih_imageView);

            view.setOnClickListener(listener ->
                    onImageClickedListener.onImageClicked(image));
        }

        void updateImage(Image image) {
            this.image = image;
            imageView.setImageBitmap(null);
            Context context = imageView.getContext();
            if(loadThumbnailTask != null){
                loadThumbnailTask.cancel(true);
            }
            loadThumbnailTask = ApplicationFactory
                    .createRetrieveThumbnailTask(context)
                    .setCallback(this::updateImageView)
                    .execute(image);
        }

        private void updateImageView(ThumbnailResult thumbnailResult){
            if (thumbnailResult.bitmap != null
                    && this.image.getId() == thumbnailResult.image.getId()) {

                Animation animation = AnimationUtils
                        .loadAnimation(imageView.getContext(), R.anim.fade_in);

                imageView.setImageBitmap(thumbnailResult.bitmap);
                imageView.startAnimation(animation);
            }
        }
    }

    /**
     * Allows communication between Adapter and Fragment
     */
    public interface OnImageClickedListener {
        /**
         * Executed when an {@link Image} has been clicked on
         * @param image The {@link Image} that has been clicked on
         */
        void onImageClicked(Image image);
    }
}