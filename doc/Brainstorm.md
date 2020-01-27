# Brainstorm

## ImageFolderCollection

Übersicht aller Ordner, die Bilder enthalten.  
Enthält ImageFolders.  

- ImageFolderCollectionToolbar (Settings?)
- Recyclerview (ImageFolder)
- ImageFolders selektierbar(?)
    - Hide Folder

## ImageFolderCollectionToolbar

Zeigt Titel an (FancyGallery).  

  - Settings (3 Punkte vertikal)
  	- Manage Hidden Folders

# Manage Hidden Folders
Zeigt eine Liste der Blacklisted Folders an.  
Erlaubt es, Blacklisted Folders zu löschen.

## ImageFolder
Zeigt Infos zum Ordner an.  
(Erstes Bild vom Ordner, Name, Anzahl Bilder)

- ImageView
- Textview (Foldername)
- Textview (Count)
- onClick -> ImageCollection
	
## ImageCollection
Übersicht aller Bilder in einem Ordner

- ImageCollectionToolbar
- Recyclerview (ImageThumb)

## ImageThumb
Ähnlich wie ImageFolder, besitzt jedoch nur eine ImageView  

- ImageView
- onClick -> ImageDisplay
	
## ImageDisplay
Zeigt das eigentliche Bild an  

- Toolbar (teilen, delete?)
	
## HiddenFolderManager

Zeigt Pfade an, die versteckt werden.  
Erlaubt es, Einträge zu löschen.