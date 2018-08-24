package functionality;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class MusicFolderFile extends AdvancedFile {
// advanced file which represents a music folder containing different tracks
	
	// serialization
	private static final long serialVersionUID = 1L;
	
	// variables
	private ArrayList<AdvancedFile> tracks = new ArrayList<AdvancedFile>();
	FileFilter directoryFilter = new FileFilter() { // file filter for directories // implemented by inner class instead of completely independent class		
		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	};

	// constructors
	public MusicFolderFile(File parent, String child) {
		super(parent, child);
		// TODO Auto-generated constructor stub
	}

	public MusicFolderFile(String parent, String child) {
		super(parent, child);
		// TODO Auto-generated constructor stub
	}

	public MusicFolderFile(URI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}

	public MusicFolderFile(String pathname) {
		super(pathname);
		// TODO Auto-generated constructor stub
	}
	
	// randomly draw specific amount of tracks
	public ArrayList<AdvancedFile> drawTracks(int number) {
		if (number > 0 && number < this.tracks.size()) { // draw tracks
			ArrayList<AdvancedFile> selectedTracksList = new ArrayList<AdvancedFile>(); // list containing all randomly selected tracks
			ArrayList<AdvancedFile> favoriteList = this.getFavoriteTracks(); // list of all favored tracks in this folder
			ArrayList<AdvancedFile> normalList = this.getNormalTracks(); // list of all normal tracks in this folder
			Random rand = new Random(); // create a new Random object
			System.out.println("Favorisierte Tracks in \"" + this + "\" : " + favoriteList);
			// add favorites to selected track list
			if (favoriteList.size() <= number) { // add all favorites if enough space in list
				selectedTracksList.addAll(favoriteList);
				System.out.println("Favorisierte Tracks in \"" + this + "\" ausgewählt: " + selectedTracksList);
			} else { // if more favorites than tracks per folder allowed, select them randomly
				while (selectedTracksList.size() < number) {
					int p = rand.nextInt(favoriteList.size());
					if (selectedTracksList.contains(favoriteList.get(p)) == false) { // check if the random file has already been selected
						selectedTracksList.add(favoriteList.get(p));
						favoriteList.remove(p); // ensure this track cannot be drawn twice
						System.out.println("Favorisierter Track \"" + favoriteList.get(p) + "\" der Liste hinzugefügt.");
					}
				}
			}
			// add normal tracks to track list
			if (normalList.size() <= (number - selectedTracksList.size())) { // add all remaining tracks if enough space
				selectedTracksList.addAll(normalList);
				System.out.println("Gewöhnliche Tracks in \"" + this + "\" ausgewählt: " + selectedTracksList);
			} else { // if more remaining tracks than tracks per person allowed, select them randomly
				ArrayList<AdvancedFile> probabilityList = new ArrayList<AdvancedFile>();
				for (int i = 0; i < normalList.size(); i++) {
					int probability = normalList.get(i).getProbability(); // the probability of the track
					if (probability > ConfigurationHandler.getMaxProbability()) { // restrict probability for performance reasons
						probability = ConfigurationHandler.getMaxProbability();
					}
					for (int n = 0; n < probability; n++) {
						probabilityList.add(normalList.get(i));
					}
				}
				while (selectedTracksList.size() < number) {
					int p = rand.nextInt(probabilityList.size());
					if (!selectedTracksList.contains(probabilityList.get(p))) { // check if the random file has already been selected
						selectedTracksList.add(probabilityList.get(p));
						System.out.println("Gewöhnlicher Track \"" + probabilityList.get(p) + "\" der Liste hinzugefügt.");
						probabilityList.remove(p); // minor statistical performance boost
					}
				}
			}
			System.out.println("Folgende Tracks in \"" + this + "\" ausgewählt: " + selectedTracksList + " " + selectedTracksList.size());
			return selectedTracksList;		
		} else { // return all tracks
			return new ArrayList<AdvancedFile>(this.tracks);
		}
	}
	
	// return favorite tracks without excluded files
	public ArrayList<AdvancedFile> getFavoriteTracks() {
		ArrayList<AdvancedFile> favoriteList = new ArrayList<AdvancedFile>();
		for (int i = 0; i < this.tracks.size(); i++) {
			if (this.tracks.get(i).isFavorite() && !this.tracks.get(i).isExcluded()) {
				favoriteList.add(this.tracks.get(i));
			}
		}
		return favoriteList;
	}
	
	// return normal tracks without excluded files
	public ArrayList<AdvancedFile> getNormalTracks() {
		ArrayList<AdvancedFile> normalList = new ArrayList<AdvancedFile>();
		for (int i = 0; i < this.tracks.size(); i++) {
			if (!this.tracks.get(i).isFavorite() && !this.tracks.get(i).isExcluded()) {
				normalList.add(this.tracks.get(i));
			}
		}
		return normalList;
	}
	
	// listFiles() for MusicFolderFiles
	public MusicFolderFile[] listMusicFolderFiles() {
		File[] pathArray = this.listFiles(directoryFilter); // file array of all subfolders
		MusicFolderFile[] musicFolderFileArray = new MusicFolderFile[pathArray.length]; // output music folder file array
		for (int i = 0; i < pathArray.length; i++) { // converting every file entry to a music folder file entry
			musicFolderFileArray[i] = new MusicFolderFile(pathArray[i].getAbsolutePath());
		}
//		System.out.println(Arrays.asList(this.listFiles()));
		return musicFolderFileArray;
	}
	
	// add track to folder
	public boolean addTrack(AdvancedFile track) {
		if (track != null) { //&& (track.exists() || !ConfigurationHandler.isFileValidation())) {
			if (!this.tracks.contains(track)) {
				this.tracks.add(track);
				return true;
			} else {
				return false;
			}
		} else { // error
			return false;
		}
	}
	
	// remove tracks which are non existent or folders // important for on/off-switch of file validation
	public void removeNonExistentTracks() {
		if (ConfigurationHandler.isFileValidation()) {
			for (AdvancedFile track : tracks) {
				if (!track.exists() || !track.isFile()) {
					tracks.remove(track);
				}
			}
		} else { // error
			System.out.println("Cannot remove non existent tracks while file validation is not active");
		}
	}
	
	// sort tracks
	public void sortTracks(Comparator<AdvancedFile> comp) {
		if (comp != null) {
			Collections.sort(this.tracks, comp);
		} else {
			Collections.sort(this.tracks);
			
		}
	}
	
	// number of all tracks
	public int getNumberTracks() {
		return this.tracks.size();
	}

	// getters
	public ArrayList<AdvancedFile> getTracks() {
		return this.tracks;
	}
	public AdvancedFile[] getTrackArray() {
		return this.tracks.toArray(new AdvancedFile[tracks.size()]);
	}

}
