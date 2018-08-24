package functionality;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;

public class TaggedFileListHandler {
	
	// private variables
	// private static ArrayList<AdvancedFile> musicFiles = new ArrayList<AdvancedFile>(); // list of all music files
	private static ArrayList<MusicFolderFile> musicFolders = new ArrayList<MusicFolderFile>(); // list of all music folders
	private static ArrayList<PlaylistFile> playlistFiles = new ArrayList<PlaylistFile>(); // list of all playlists
	private static String tlfPath = null;
	
	public static enum sortingCategories {SORT_CAT_NAME, SORT_CAT_FAV, SORT_CAT_EXC, SORT_CAT_REQ, SORT_CAT_PROB, SORT_CAT_EDIT, SORT_CAT_SEARCH}; // all sorting categories
	private static sortingCategories currentSortingCategory = sortingCategories.SORT_CAT_NAME; // the current sorting category
	
	private static final byte[] tlfHeader = ("TaggedListFile").getBytes(); // tlf header
	private static final byte[] tlfVersion = {-125}; // v1.2.0
	
	/* OLD: File list are stored in tagged list files (.tlf) for future use and external manipulation, eg. for easy tagging
	 * Information is stored this way: [start codon | first property [| separator codon | second property ] * n | stop codon |] * p
	 * Each fragment represents one file
	 * First property: File path as String
	 * Second property: Probability as Int
	 * Third property: Tags as booleans (1st bit: fav, 2nd: req, 3rd: exc)
	 * 
	 * NEW: | header[String] | version number [byte] | folder count[int] (| folder path length [int] | 
	 * relative folder path [String] | probability [int] | boolean tags [byte] | file count [int] (| file path length [int] | 
	 * relative file path [String] | probability [int] | boolean tags [byte]) * n)*j | playlist count [int] | 
	 * (playlist path length [int] | playlist path [String] | relative path tag [byte] | tracks per person [int] | 
	 * folder count [int] | (folder path length [int] | folder path [String] | )*m)*k
	 */ 
	
	// creates a file which contains all valid file information for the corresponding directory
	public static void createTaggedListFile() {
		if (musicFolders != null) { // safety check
			byte[] bytesToWrite = tlfHeader; // byte array of the tlf file with header
			bytesToWrite = joinBytes(bytesToWrite, tlfVersion); // add the tlf version
			ByteBuffer bBuffer = ByteBuffer.allocate(4); // buffer for writing ints
			// create music tfl byte array
			bBuffer.putInt(musicFolders.size()); // number of music folders
			bytesToWrite = joinBytes(bytesToWrite, bBuffer.array());
			bBuffer.rewind(); // rewind buffer for further operations 
			for (int i = 0; i < musicFolders.size(); i++) {
				byte[] relativePath = ("\\" + musicFolders.get(i).getName()).getBytes();
				bBuffer.putInt(relativePath.length); // byte number of relative path
				bytesToWrite = joinBytes(bytesToWrite, bBuffer.array());
				bBuffer.rewind(); // rewind buffer for further operations
				bytesToWrite = joinBytes(bytesToWrite, relativePath); // write relative music folder path as byte array
				bBuffer.putInt(musicFolders.get(i).getProbability()); // probability of music folder as int
				bytesToWrite = joinBytes(bytesToWrite, bBuffer.array());
				bBuffer.rewind(); // rewind buffer for further operations
				BitSet boolBitset = BitSet.valueOf(new byte[1]);
				boolBitset.set(0, musicFolders.get(i).isFavorite()); // fav
				boolBitset.set(1, musicFolders.get(i).isRequested()); // req
				boolBitset.set(2, musicFolders.get(i).isExcluded()); // exc
				boolBitset.set(3, false); // unused
				boolBitset.set(4, false); // unused
				boolBitset.set(5, false); // unused
				boolBitset.set(6, false); // unused
				boolBitset.set(7, true); // necessary to be recognized, bitset only contains info on set bits, so if every bit is 0, the bitset.toByteArray() returns nothing
				bytesToWrite = joinBytes(bytesToWrite, boolBitset.toByteArray()); // write bool bitset
				// files of specific folder
				bBuffer.putInt(musicFolders.get(i).getNumberTracks()); // number of music files
				bytesToWrite = joinBytes(bytesToWrite, bBuffer.array());
				bBuffer.rewind(); // rewind buffer for further operations
				for (int n = 0; n < musicFolders.get(i).getNumberTracks(); n++) {
					AdvancedFile track = musicFolders.get(i).getTracks().get(n);
					relativePath = ("\\" + track.getTopFolder() + "\\" + track.getName()).getBytes();
					bBuffer.putInt(relativePath.length); // byte number of relative path
					bytesToWrite = joinBytes(bytesToWrite, bBuffer.array());
					bBuffer.rewind(); // rewind buffer for further operations
					bytesToWrite = joinBytes(bytesToWrite, relativePath); // write relative music file path as byte array
					bBuffer.putInt(track.getProbability()); // probability of music folder as int
					bytesToWrite = joinBytes(bytesToWrite, bBuffer.array());
					bBuffer.rewind(); // rewind buffer for further operations
					boolBitset = BitSet.valueOf(new byte[1]);
					boolBitset.set(0, track.isFavorite()); // fav
					boolBitset.set(1, track.isRequested()); // req
					boolBitset.set(2, track.isExcluded()); // exc
					boolBitset.set(3, false); // unused
					boolBitset.set(4, false); // unused
					boolBitset.set(5, false); // unused
					boolBitset.set(6, false); // unused
					boolBitset.set(7, true); // necessary to be recognized, bitset only contains info on set bits, so if every bit is 0, the bitset.toByteArray() returns nothing
					bytesToWrite = joinBytes(bytesToWrite, boolBitset.toByteArray()); // write bool bitset
				}
			}
			bBuffer.putInt(playlistFiles.size()); // number of playlists
			bytesToWrite = joinBytes(bytesToWrite, bBuffer.array());
			bBuffer.rewind(); // rewind buffer for further operations 
			for (int i = 0; i < playlistFiles.size(); i++) {
				byte[] playlistPath;
				if (playlistFiles.get(i).isRelativePlaylist()) {
					playlistPath = ("\\" + playlistFiles.get(i).getName()).getBytes();
				} else {
					playlistPath = (playlistFiles.get(i).getAbsolutePath().getBytes());
				}
				bBuffer.putInt(playlistPath.length); // byte number of playlist path
				bytesToWrite = joinBytes(bytesToWrite, bBuffer.array());
				bBuffer.rewind(); // rewind buffer for further operations
				bytesToWrite = joinBytes(bytesToWrite, playlistPath); // write playlist file path as byte array
				BitSet boolBitset = BitSet.valueOf(new byte[1]);
				boolBitset.set(0, playlistFiles.get(i).isRelativePlaylist()); // rel list
				boolBitset.set(1, false); // unused
				boolBitset.set(2, false); // unused
				boolBitset.set(3, false); // unused
				boolBitset.set(4, false); // unused
				boolBitset.set(5, false); // unused
				boolBitset.set(6, false); // unused
				boolBitset.set(7, true); // necessary to be recognized, bitset only contains info on set bits, so if every bit is 0, the bitset.toByteArray() returns nothing
				bytesToWrite = joinBytes(bytesToWrite, boolBitset.toByteArray()); // write bool bitset
				bBuffer.putInt(playlistFiles.get(i).getTracksPD()); // tracks per directory as int
				bytesToWrite = joinBytes(bytesToWrite, bBuffer.array());
				bBuffer.rewind(); // rewind buffer for further operations
				bBuffer.putInt(playlistFiles.get(i).getMusicDirectories().size()); // number of music directories in playlist
				bytesToWrite = joinBytes(bytesToWrite, bBuffer.array());
				bBuffer.rewind(); // rewind buffer for further operations
				for (int n = 0; n < playlistFiles.get(i).getMusicDirectories().size(); n++) {
					byte[] playlistDirPath = ("\\" + playlistFiles.get(i).getMusicDirectories().get(n).getName()).getBytes();
					bBuffer.putInt(playlistDirPath.length); // byte number of playlist path
					bytesToWrite = joinBytes(bytesToWrite, bBuffer.array());
					bBuffer.rewind(); // rewind buffer for further operations
					bytesToWrite = joinBytes(bytesToWrite, playlistDirPath); // write playlist file path as byte array
				}
			}
			try { // write music tfl
				Files.write(Paths.get(tlfPath), bytesToWrite);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Das Tagged List File konnte nicht erstellt werden.");
			}
		} else { // error
			System.out.println("Die Verzeichnislisten sind nicht initialisiert.");
		}
	}
	
	// read tlf
	public static void readTaggedListFile() {
		if (new AdvancedFile(tlfPath).exists()) {
			try { 
				// read music tlf
				byte[] allBytes = Files.readAllBytes(Paths.get(tlfPath)); // read the tlf bytes
				if (Arrays.toString(tlfHeader).equals(Arrays.toString(subSequence(allBytes, 0, (tlfHeader.length - 1))))) {
					int offset = tlfHeader.length + tlfVersion.length;
					ByteBuffer bBuffer = ByteBuffer.wrap(allBytes);
					bBuffer.position(offset); // set position to version byte
					// folder
					int fCount = bBuffer.getInt();
					for (int i = 0; i < fCount; i++) {	
						int stringLength = bBuffer.getInt(); // get byte[] length of following string
						MusicFolderFile codedFolder = new MusicFolderFile(Paths.get(tlfPath).getParent().toString() + new String(bBuffer.array(), bBuffer.position(), stringLength));
						bBuffer.position(bBuffer.position() + stringLength);
						codedFolder.setProbability(bBuffer.getInt());
						byte[] boolBytes = new byte[1];
						boolBytes[0] = bBuffer.get();
						BitSet boolBitset = BitSet.valueOf(boolBytes);
						codedFolder.setFavorite(boolBitset.get(0)); // fav
						codedFolder.setRequested(boolBitset.get(1)); // req
						codedFolder.setExcluded(boolBitset.get(2)); // exc
						// files
						int fileCount = bBuffer.getInt();
						for (int n = 0; n < fileCount; n++) {
							stringLength = bBuffer.getInt(); // get byte[] length of following string
							AdvancedFile codedFile = new AdvancedFile(Paths.get(tlfPath).getParent().toString() + new String(bBuffer.array(), bBuffer.position(), stringLength));
							bBuffer.position(bBuffer.position() + stringLength);
							codedFile.setProbability(bBuffer.getInt());
							boolBytes[0] = bBuffer.get();
							boolBitset = BitSet.valueOf(boolBytes);
							codedFile.setFavorite(boolBitset.get(0)); // fav
							codedFile.setRequested(boolBitset.get(1)); // req
							codedFile.setExcluded(boolBitset.get(2)); // exc
							if (codedFile.isFile() || !ConfigurationHandler.isFileValidation()) {
								if (!codedFile.isDirectory()) { // returns false for nonexistents
							//		if (!musicFiles.contains(codedFile)) { // redundant
								//		musicFiles.add(codedFile);
									//	System.out.println("File Path: " + codedFile);
								//	}
									codedFolder.addTrack(codedFile); // check for duplicate integrated in method
								}
							}
						}
						if ((codedFolder.isDirectory() || !ConfigurationHandler.isFileValidation()) && !musicFolders.contains(codedFolder)) { // is directory returns false for nonexistents 
							if (!codedFolder.isFile()) { // returns false for nonexistents
								musicFolders.add(codedFolder);
								System.out.println("Folder Path: " + codedFolder);
							}
						}	
					}
					// playlists
					int pCount = bBuffer.getInt(); // playlist count
					for (int i = 0; i < pCount; i++) {
						int stringLength = bBuffer.getInt(); // get byte[] length of following string
						String playlistPath = new String(bBuffer.array(), bBuffer.position(), stringLength);
						bBuffer.position(bBuffer.position() + stringLength); // move buffer
						PlaylistFile codedFile;
						byte[] boolBytes = new byte[1];
						boolBytes[0] = bBuffer.get();
						BitSet boolBitset = BitSet.valueOf(boolBytes);
						boolean relList = boolBitset.get(0); // rel list
						if(relList) {
							codedFile = new PlaylistFile(Paths.get(tlfPath).getParent().toString() + playlistPath);
						} else {
							codedFile = new PlaylistFile(playlistPath);
						}
						codedFile.setRelativePlaylist(relList);
						codedFile.setTracksPD(bBuffer.getInt());
						fCount = bBuffer.getInt(); // folder count
						for (int n = 0; n < fCount; n++) {
							stringLength = bBuffer.getInt(); // get byte[] length of following string
							codedFile.addDirectoryFromTLF(new String(bBuffer.array(), bBuffer.position(), stringLength)); // search for folder and add it to file
							bBuffer.position(bBuffer.position() + stringLength); // move buffer				
						}
						if (!codedFile.isDirectory() && !playlistFiles.contains(codedFile)) {
							playlistFiles.add(codedFile);
							System.out.println(" Path: " + codedFile);
							codedFile.selectTracks();
						}
					}
				} else { // error
					System.out.println("Wrong file formate");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			sortFiles();
		} else {
			System.out.println("Die Datei existiert nicht.");
		}
	}
	
	public static void updateTaggedListFile() {
		if (tlfPath != null) {
			// list all music files and folders
			MusicFolderFile[] folderArray = new MusicFolderFile(Paths.get(tlfPath).getParent().toString()).listMusicFolderFiles();
			for (MusicFolderFile folder  : folderArray) {
				if (folder.exists() || !ConfigurationHandler.isFileValidation()) { // only proceed if existent, directory and not already in list
					if (!musicFolders.contains(folder)) {
						musicFolders.add(folder); // add as valid folder
					}
					AdvancedFile[] fileArray = folder.listFiles();
					for (AdvancedFile file : fileArray) {
						if ((file.exists() || !ConfigurationHandler.isFileValidation()) && file.isFile()) { // only proceed if existent, file and not already in list
			//				musicFiles.add(fileArray[n]); // add as valid file // redundant
							musicFolders.get(musicFolders.indexOf(folder)).addTrack(file);
						}
					}
				}
			}
			if (ConfigurationHandler.isFileValidation()) { // only delete non existents if file validation is enabled
				for (MusicFolderFile folder : musicFolders) {
					if (!folder.exists() || !folder.isDirectory()) {
						musicFolders.remove(folder);
					} else { // delete non existent tracks if folder exists
						folder.removeNonExistentTracks();
					}
				}
			}
			sortFiles();
		} else {
			System.out.println("Der Zielpfad ist ungültig.");
		}
	}
	
	public static void sortFiles() {
		switch (currentSortingCategory) {
			case SORT_CAT_NAME:
				sortFileLists(null); // standard sorting
				break;
				
			case SORT_CAT_EXC:
				sortFileLists((AdvancedFile fileA, AdvancedFile fileB) -> {
					if (fileA.isExcluded() == fileB.isExcluded()) {
						return fileA.compareTo(fileB);
					} else {
						return Boolean.compare(fileB.isExcluded(), fileA.isExcluded());
					}
				});
				break;
				
			case SORT_CAT_FAV:
				sortFileLists((AdvancedFile fileA, AdvancedFile fileB) -> {
					if (fileA.isFavorite() == fileB.isFavorite()) {
						return fileA.compareTo(fileB);
					} else {
						return Boolean.compare(fileB.isFavorite(), fileA.isFavorite());
					}
				});
				break;
				
			case SORT_CAT_REQ:
				sortFileLists((AdvancedFile fileA, AdvancedFile fileB) -> {
					if (fileA.isRequested() == fileB.isRequested()) {
						return fileA.compareTo(fileB);
					} else {
						return Boolean.compare(fileB.isRequested(), fileA.isRequested());
					}
				});
				break;
				
			case SORT_CAT_PROB:
				sortFileLists((AdvancedFile fileA, AdvancedFile fileB) -> {
					if (fileA.getProbability() == fileB.getProbability()) {
						return fileA.compareTo(fileB);
					} else {
						return Integer.compare(fileB.getProbability(), fileA.getProbability());
					}
				});
				break;
				
			case SORT_CAT_EDIT:
				sortFileLists((AdvancedFile fileA, AdvancedFile fileB) -> {
					if (fileA.isEdited() == fileB.isEdited()) {
						return fileA.compareTo(fileB);
					} else {
						return Boolean.compare(fileB.isEdited(), fileA.isEdited());
					}
				});
				break;
				
			case SORT_CAT_SEARCH:
				break;
				
			default:
				sortFileLists(null); // standard sorting
				break;
		}
	}
	
	// helper function for sorting
	public static void sortFileLists(Comparator<AdvancedFile> comp) {
		if (comp != null) {
	//		Collections.sort(musicFiles, comp); // redundant
			Collections.sort(musicFolders, comp);
			Collections.sort(playlistFiles, comp);
		} else {
	//		Collections.sort(musicFiles); // redundant
			Collections.sort(musicFolders);
			Collections.sort(playlistFiles);
		}
		for (MusicFolderFile folder : musicFolders) {
			folder.sortTracks(comp); // same implementation, so null can be passed
		}
	}
	
	public static void close() {
		tlfPath = null;
	//	musicFiles.clear(); // redundant
		musicFolders.clear();
		playlistFiles.clear();
	}
	
	public static void generateAllPlaylists() {
		for (int i = 0; i < playlistFiles.size(); i++) {
			playlistFiles.get(i).generatePlaylist();
		}
	}
	
	public static MusicFolderFile[] getFolderArray() {
		return musicFolders.toArray(new MusicFolderFile[musicFolders.size()]);
	}
	
	public static AdvancedFile[] getFileArray() {
		ArrayList<AdvancedFile> musicFiles = getMusicFiles();
		return musicFiles.toArray(new AdvancedFile[musicFiles.size()]);
	}
	public static PlaylistFile[] getPlaylistArray() {
		return playlistFiles.toArray(new PlaylistFile[playlistFiles.size()]);
	}

	// combine two byte arrays into one
	public static byte[] joinBytes(byte[] first, byte[] second) {
		if (first != null && second != null) {
			int newLength = first.length + second.length;
			byte[] joinedBytes = new byte[newLength];
			for (int i = first.length - 1; i >= 0; i--) {
				joinedBytes[i] = first[i];
			}
			for (int i = second.length - 1; i >= 0; i--) {
				joinedBytes[i+first.length] = second[i];
			}
			return joinedBytes;
		} else { // error
			return new byte[0]; // return empty array
		}
	}
	
	

	
	// returns a sub sequence of the byte array from index start to end (inclusive)
	public static byte[] subSequence(byte[] bytes, int start, int end) {
		if (start >= 0 && end >= 0 && bytes != null) {
			if (end < bytes.length) { // check the end is not out of range
				byte[] subSequence = new byte[(end + 1) - start];
				for (int i = 0; i < subSequence.length; i++) {
					subSequence[i] = bytes[start + i];
				}
				return subSequence;
			} else {
				System.out.println("End liegt außerhalb des Arrays.");
				return new byte[0];
			}
		} else {
			return new byte[0];
		}
	}

	// get tlf root folder
	public static String getTlfFolderPath() {
		if (tlfPath != null) {
			return Paths.get(tlfPath).getParent().toString();
		} else {
			return null;
		}
	}
	
	// get all music files
	public static ArrayList<AdvancedFile> getMusicFiles() {
		ArrayList<AdvancedFile> musicFiles = new ArrayList<AdvancedFile>(); // list of all music files
		for (MusicFolderFile folder : musicFolders) {
			musicFiles.addAll(folder.getTracks());
		}
		return musicFiles;
	}
	
	// getters
	public static ArrayList<MusicFolderFile> getMusicFolders() {
		return musicFolders;
	}

	public static String getTlfPath() {
		return tlfPath;
	}

	public static void setTlfPath(String tlfPath) {
		TaggedFileListHandler.tlfPath = tlfPath;
	}

	public static byte[] getTlfVersion() {
		return tlfVersion;
	}

	public static ArrayList<PlaylistFile> getPlaylistFiles() {
		return playlistFiles;
	}

	public static sortingCategories getCurrentSortingCategory() {
		return currentSortingCategory;
	}

	// setters
	public static void setCurrentSortingCategory(sortingCategories currentSortingCategory) {
		TaggedFileListHandler.currentSortingCategory = currentSortingCategory;
		sortFiles();
	}
	
}

