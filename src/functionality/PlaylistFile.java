package functionality;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class PlaylistFile extends AdvancedFile {
// file with all playlist information and functionality
	
	// serialization
	private static final long serialVersionUID = 1L;

	// variables
	private ArrayList<MusicFolderFile> musicDirectories = new ArrayList<MusicFolderFile>(); // list of all music directories featured in this playlist
	private int tracksPD = ConfigurationHandler.getStartTracksPD(); // max count of tracks allowed per music directory
	private boolean relativePlaylist = true; // is playlist path relative or absolute
	private ArrayList<AdvancedFile> selectedFiles = new ArrayList<AdvancedFile>(); // all currently selected files for this playlist
	
	// superclass constructors
	public PlaylistFile(File parent, String child) {
		super(parent, child);
		// TODO Auto-generated constructor stub
	}

	public PlaylistFile(String parent, String child) {
		super(parent, child);
		// TODO Auto-generated constructor stub
	}

	public PlaylistFile(String pathname) {
		super(pathname);
		// TODO Auto-generated constructor stub
	}

	public PlaylistFile(URI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}
	
	// select n files from each directory
	public void selectTracks() {
		selectedFiles.clear(); // clear before drawing
		for (int i = 0; i < musicDirectories.size(); i++) {
			if (TaggedFileListHandler.getMusicFolders().contains(musicDirectories.get(i)) && !musicDirectories.get(i).isExcluded()) {
				selectedFiles.addAll(musicDirectories.get(i).drawTracks(tracksPD));
				System.out.println("Select from " + musicDirectories.get(i));
			}
		}
	}
	
	// adds the directory from corresponding tlf string if existent
	public boolean addDirectoryFromTLF(String directory) {
		ArrayList<MusicFolderFile> musicFolders = TaggedFileListHandler.getMusicFolders();
		for (int i = 0; i < musicFolders.size(); i++) { // scan music folders
			if (("\\" + musicFolders.get(i).getName()).equals(directory)) {
				musicDirectories.add(musicFolders.get(i));
				System.out.println("Added music folder " + musicFolders.get(i) + " to playlist " + this);
				return true;
			}
		}
		return false;
	}
	
	// create playlist physically as .m3u
	public void generatePlaylist() {
		this.selectTracks(); // randomly draw tracks
		// try-with resources for autoclose when try-block ends -> removes need for a finally-block with close() for every stream
		try (BufferedWriter fw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this), Charset.forName("UTF-8").newEncoder()))) {
			fw1.write("#EXTM3U" + System.getProperty("line.separator")); // file header // \n not used for new line because no adaption to OS
			for (int i = 0; i < selectedFiles.size(); i++) {
				fw1.write(selectedFiles.get(i).toRelativePath() + System.getProperty("line.separator")); // write all relative paths into the playlist
			}
		} catch (IOException e) {
			System.err.println( "Die Wiedergabeliste konnte nicht erstellt werden." );
			e.printStackTrace();
		}
	}
	
	
	public MusicFolderFile[] getMusicDirectoriesArray() {
		return musicDirectories.toArray(new MusicFolderFile[musicDirectories.size()]);
	}
	
	// getters
	public ArrayList<MusicFolderFile> getMusicDirectories() {
		return musicDirectories;
	}

	public int getTracksPD() {
		return tracksPD;
	}

	public boolean isRelativePlaylist() {
		return relativePlaylist;
	}

	//setters
	public void setMusicDirectories(ArrayList<MusicFolderFile> musicDirectories) {
		this.musicDirectories = musicDirectories;
	}

	public void setTracksPD(int tracksPD) {
		this.tracksPD = tracksPD;
	}

	public void setRelativePlaylist(boolean relativePlaylist) {
		this.relativePlaylist = relativePlaylist;
	}

}
