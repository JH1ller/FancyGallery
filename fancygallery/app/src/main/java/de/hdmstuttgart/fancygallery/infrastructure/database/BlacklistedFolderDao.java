package de.hdmstuttgart.fancygallery.infrastructure.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Dao used to access {@link BlacklistedFolderDto} stored in {@link AppDatabase}
 */
@Dao
public interface BlacklistedFolderDao {

    @Query("SELECT * FROM BlacklistedFolderDto")
    List<BlacklistedFolderDto> getAll();

    @Insert
    void insert(BlacklistedFolderDto blacklistedFolderDto);

    @Delete
    void delete(BlacklistedFolderDto blacklistedFolderDto);
}
