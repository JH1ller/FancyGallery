package de.hdmstuttgart.fancygallery.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdmstuttgart.fancygallery.R;
import de.hdmstuttgart.fancygallery.model.database.BlacklistedFolder;

/**
 * Adapter class for {@link BlacklistedFolder}
 * <br>
 * Requires an instance of {@link BlacklistAdapterListener}.
 */
public class BlacklistAdapter extends RecyclerView.Adapter<BlacklistAdapter.BlacklistViewHolder> {

    private List<BlacklistedFolder> blackListedFolders;
    private final BlacklistAdapterListener listener;

    public BlacklistAdapter(BlacklistAdapterListener listener) {
        blackListedFolders = new ArrayList<>();
        this.listener = listener;
    }

    public void updateBlackList(List<BlacklistedFolder> blackListedFolders){
        this.blackListedFolders = blackListedFolders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BlacklistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View imageHolder = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_blacklist,parent,false);

        return new BlacklistViewHolder(imageHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull BlacklistViewHolder holder, int position) {
        final BlacklistedFolder currentFolder = blackListedFolders.get(position);
        holder.updateBlacklistedFolder(currentFolder);
    }

    @Override
    public int getItemCount() {
        return blackListedFolders.size();
    }

    public class BlacklistViewHolder extends RecyclerView.ViewHolder{
        final TextView textBlacklist;
        final ImageButton buttonReveal;
        private BlacklistedFolder blacklistedFolder;

        BlacklistViewHolder(View view){
            super(view);
            textBlacklist = view.findViewById(R.id.text_blacklist);
            buttonReveal = view.findViewById(R.id.button_reveal);

            buttonReveal.setOnClickListener(
                    view1 -> listener.onReveal(blacklistedFolder));
        }

        void updateBlacklistedFolder(BlacklistedFolder blacklistedFolder){
            this.blacklistedFolder = blacklistedFolder;
            textBlacklist.setText(blacklistedFolder.Path.toString().substring(7));
        }
    }

    /**
     * Allows communication between Adapter and Fragments
     */
    public interface BlacklistAdapterListener{
        /**
         * Executed when a folder should no longer be blacklisted.
         * @param blacklistedFolder The folder which should be revealed
         */
        void onReveal(BlacklistedFolder blacklistedFolder);
    }
}
