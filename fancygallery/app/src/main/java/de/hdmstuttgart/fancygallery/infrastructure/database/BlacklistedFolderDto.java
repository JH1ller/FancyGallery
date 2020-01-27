package de.hdmstuttgart.fancygallery.infrastructure.database;

import android.net.Uri;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import de.hdmstuttgart.fancygallery.model.ImageFolder;
import de.hdmstuttgart.fancygallery.model.database.BlacklistedFolder;


/**
 * Dto class which can be converted to {@link BlacklistedFolder}.
 * Used to store entities of {@link BlacklistedFolder} in e.g. a database.
 */
// Room needs to access fields.
// Thus they can't be private
@SuppressWarnings({"WeakerAccess"})
@Entity
public class BlacklistedFolderDto {

    @PrimaryKey(autoGenerate = true)
    public int Uid;

    public String UriPath;

    public BlacklistedFolderDto() {
    }

    public BlacklistedFolderDto(ImageFolder imageFolder) {
        this(new BlacklistedFolder(imageFolder));
    }

    public BlacklistedFolderDto(BlacklistedFolder blacklistedFolder) {
        Uid = blacklistedFolder.uid;
        UriPath = blacklistedFolder.Path.toString();
    }

    public BlacklistedFolder toEntity() {
        BlacklistedFolder blacklistedFolder = new BlacklistedFolder();
        blacklistedFolder.uid = Uid;
        blacklistedFolder.Path = Uri.parse(UriPath);
        return blacklistedFolder;
    }
}
