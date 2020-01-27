package de.hdmstuttgart.fancygallery.infrastructure.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * AppDatabase providing access to Dao using Room
 */
@Database(
        entities = {
            BlacklistedFolderDto.class
        },
        version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract BlacklistedFolderDao blacklistedFolderDao();
}
