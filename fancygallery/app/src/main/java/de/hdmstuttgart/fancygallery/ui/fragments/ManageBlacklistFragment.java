package de.hdmstuttgart.fancygallery.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.hdmstuttgart.fancygallery.R;
import de.hdmstuttgart.fancygallery.core.Constants;
import de.hdmstuttgart.fancygallery.infrastructure.database.AppDatabase;
import de.hdmstuttgart.fancygallery.infrastructure.database.BlacklistedFolderDto;
import de.hdmstuttgart.fancygallery.model.database.BlacklistedFolder;
import de.hdmstuttgart.fancygallery.ui.adapters.BlacklistAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ManageBlacklistFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p>
 *     This fragment shows all {@link BlacklistedFolder} and allows the user
 *     to reveal them.
 * </p>
 */
public class ManageBlacklistFragment
        extends Fragment
        implements BlacklistAdapter.BlacklistAdapterListener {
    private final String TAG = "ManageBlacklistFragment";

    private RecyclerView recyclerView;
    private BlacklistAdapter blackBlacklistAdapter;
    private View emptyView;
    private List<BlacklistedFolder> blacklistedFolders = new ArrayList<>();

    public ManageBlacklistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ManageBlacklistFragment.
     */
    public static ManageBlacklistFragment newInstance() {
        ManageBlacklistFragment fragment = new ManageBlacklistFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        View view =  inflater.inflate(
                R.layout.fragment_manage_blacklist,
                container,
                false);
        setupRecyclerView(view);
        emptyView = view.findViewById(R.id.empty_view);
        initialiseBlacklist();
        return view;
    }

    private void setupRecyclerView(View view){
        recyclerView = view.findViewById(R.id.recyclerView);

        blackBlacklistAdapter = new BlacklistAdapter(this);
        recyclerView.setAdapter(blackBlacklistAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration decoration = new DividerItemDecoration(
                recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);

        recyclerView.addItemDecoration(decoration);
    }

    private void initialiseBlacklist(){
        Context context = Objects.requireNonNull(getContext());
        Activity activity = Objects.requireNonNull(getActivity());

        new Thread(() -> {
            AppDatabase db = Room.
                    databaseBuilder(
                        context,
                        AppDatabase.class,
                        Constants.DATABASE_BLACKLIST_NAME)
                    .build();

            blacklistedFolders = db
                    .blacklistedFolderDao()
                    .getAll()
                    .stream()
                    .map(BlacklistedFolderDto::toEntity)
                    .collect(Collectors.toList());

            activity.runOnUiThread(() -> updateBlacklist(blacklistedFolders));
        }).start();
    }

    private void updateBlacklist(List<BlacklistedFolder> blacklistedFolders) {
        blackBlacklistAdapter.updateBlackList(blacklistedFolders);
        if(blacklistedFolders.size() == 0){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onReveal(BlacklistedFolder blacklistedFolder) {
        Context context = Objects.requireNonNull(getContext());
        Activity activity = Objects.requireNonNull(getActivity());

        new Thread(() -> {
            BlacklistedFolderDto dto = new BlacklistedFolderDto(blacklistedFolder);

            AppDatabase db = Room.
                    databaseBuilder(
                            context,
                            AppDatabase.class,
                            Constants.DATABASE_BLACKLIST_NAME)
                    .build();

            db.blacklistedFolderDao().delete(dto);

            activity.runOnUiThread(() -> {
                blacklistedFolders.remove(blacklistedFolder);
                updateBlacklist(blacklistedFolders);
            });
        }).start();
    }
}
