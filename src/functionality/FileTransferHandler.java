package functionality;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;




public class FileTransferHandler {
// handles transfer of files from dropbox(or other) to the music directory
	
	// variables

	//methods
	
	public static void transferFiles() {
		// transfer music files
		ArrayList<AdvancedFile> dropboxFiles = TaggedFileListHandler.getMusicFiles(); // all files in the tlf directory and first grade subfolders
		for (int i = 0; i < dropboxFiles.size(); i++) { // copy files from dropbox to music directory if not currently existent
			if (!dropboxFiles.get(i).isExcluded()) { // check if dropbox file is valid
				if (dropboxFiles.get(i).isRequested()) {
					dropboxFiles.get(i).handleRequest();
				}
				if(!dropboxFiles.get(i).isRepresentative()) {	
					System.out.println("Neue Datei: " + dropboxFiles.get(i));
					dropboxFiles.get(i).copyAdvancedFile(ConfigurationHandler.getMusicPath(), 2);
					if (!dropboxFiles.get(i).isRequested()) {
						dropboxFiles.get(i).replaceWithRepresentative();
					}
				}	
			}
		}
		// transfer playlists
		ArrayList<PlaylistFile> playlistFiles = TaggedFileListHandler.getPlaylistFiles(); // all playlist files saved in the tlf
		for (int i = 0; i < playlistFiles.size(); i++) { // copy playlists to music directory if not currently existent
			if (!playlistFiles.get(i).isExcluded()) { // check if playlist file is valid
				if(!playlistFiles.get(i).isRepresentative()) {	
					System.out.println("Neue Playlistdatei: " + playlistFiles.get(i));
					playlistFiles.get(i).copyAdvancedFile(ConfigurationHandler.getMusicPath(), 1);
				}	
			}
		}
		// handle deletion of files
		if (ConfigurationHandler.isFileValidation()) { // only delete files and folders if there is file validation
			AdvancedFile[] musicFiles =  getLocalFiles(); // all files in the music directory and first grade subfolders
			for (int i = 0; i < musicFiles.length; i++) { // delete all old files
				if (!musicFiles[i].hasCorrespondingFile()) {
					try {
						Files.deleteIfExists(musicFiles[i].toPath());
						System.out.println("Lösche " + musicFiles[i]);
					} catch (IOException e) { // error
						System.out.println("Datei " + musicFiles[i] + " konnte nicht gelöscht werden.");
						e.printStackTrace();
					}
				}
			}
			MusicFolderFile[] musicFolders =  getLocalFolders(); // all folders in the music directory and first grade subfolders
			for (int i = 0; i < musicFolders.length; i++) { // delete all old files
				if (!musicFolders[i].hasCorrespondingFile() && musicFolders[i].listFiles().length == 0) {
					try {
						Files.deleteIfExists(musicFolders[i].toPath());
						System.out.println("Lösche " + musicFolders[i]);
					} catch (IOException e) { // error
						System.out.println("Ordner " + musicFolders[i] + " konnte nicht gelöscht werden.");
						e.printStackTrace();
					}
				}
			}
		}
	}	
	
	// get all local folders // also for update // must be called prior to any file transfer
	public static MusicFolderFile[] getLocalFolders() {
		MusicFolderFile folder = new MusicFolderFile(ConfigurationHandler.getMusicPath());
		return folder.listMusicFolderFiles(); // integrated directory filter
	}
	
	// sets all dropbox files // also for update // must be called prior to any file transfer // redundant; instead use tlf path
	public static AdvancedFile[] getLocalFiles() {
		FileFilter fileFilter = new FileFilter() { // file filter for files // implemented by inner class instead of completely independent class		
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile();
			}
		};
		MusicFolderFile[] folders = getLocalFolders();
		ArrayList<AdvancedFile> files = new ArrayList<AdvancedFile>();
		for (int i = 0; i < folders.length; i++) {
			files.addAll(Arrays.asList(folders[i].listAdvancedFiles(fileFilter)));
		}
		return files.toArray(new AdvancedFile[files.size()]);
	}
	
}
