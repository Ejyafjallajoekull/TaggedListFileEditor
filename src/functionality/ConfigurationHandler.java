package functionality;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;

public class ConfigurationHandler {
	// handles all configurations and provides .ini support
	
	// constants
	private static final String ININAME = "TLFEditor";
	
	// ini variables
	private static String musicPath = new File(".").getAbsolutePath().substring(0, new File(".").getAbsolutePath().length() - 2); // path to the music folder // standard local path
	private static String dropboxPath = System.getProperty("user.home") + "\\Dropbox\\Kellermusik"; // dropbox path
	private static boolean fileValidation = true; // enable/disable file validation
	private static String representativeTag = "Stellvertreterdatei"; // the tag for representative files // internal variable
	private static int maxProbability = 999; // maximal probability for a track // internal variable
	private static int maxTracksPD = 9999; // maximum number of tracks allowed to be drawn from a single directory// internal variable
	private static int startTracksPD = 25; // standard tracks per directory number // internal variable
	
	
	// writes all configuration variables to external .ini file
	public static void writeINI() {
		// try-with resources for autoclose when try-block ends
		try (BufferedWriter iniWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ININAME + ".ini"), Charset.forName("UTF-8").newEncoder()))) {
			// config file creation in working directory (relative)
			// file header
			iniWriter.write(ININAME + " Konfigurationsdatei" + System.getProperty("line.separator") + System.getProperty("line.separator")); // title
			// music folder
			iniWriter.write("MusicFolderPath=" + musicPath + System.getProperty("line.separator"));
			iniWriter.write("; Pfad des Musikverzeichnisses" + System.getProperty("line.separator") + System.getProperty("line.separator"));				
			// dropbox folder
			iniWriter.write("DropboxPath=" + dropboxPath + System.getProperty("line.separator"));
			iniWriter.write("; Pfad des lokalen Dropbox-Ordners" + System.getProperty("line.separator") + System.getProperty("line.separator"));			
			// file validation
			iniWriter.write("EnableFileValidation=" + fileValidation + System.getProperty("line.separator"));
			iniWriter.write("; \"false\" keine Überprüfung der Dateien und Ordner auf Existenz" + System.getProperty("line.separator"));								
			iniWriter.write("; \"true\" Überprüfung der Dateien und Ordner auf Existenz" + System.getProperty("line.separator") + System.getProperty("line.separator"));					
			// representative tag
//			iniWriter.write("RepresentativeTag=" + representativeTag + System.getProperty("line.separator"));
//			iniWriter.write("; Datei-Tag für Stellvertreterdateien" + System.getProperty("line.separator") + System.getProperty("line.separator"));												
		} catch (IOException e) {
			System.err.println(ININAME + ".ini konnte nicht erstellt werden.");
			e.printStackTrace();
		}	
	}
	
	// imports all configuration variables from external .ini file
	public static void readINI() {
		ArrayList<String> configLines = new ArrayList<String>(); // list of all lines in config file as strings without line separators
		File configFile = new File(ININAME + ".ini");
		if (configFile.exists()) { // check if there is a config file
			try {
				// UTF-8 standard for addLines
				configLines.addAll(Files.readAllLines(configFile.toPath())); // easier than FileReader(), but substitutes line separators // casted to ArrayList to save a line of code (import list)
				System.out.println(configLines);
				for (int configlength = configLines.size() - 1; configlength >= 0; configlength--) { // search every line for variable definitions
					// music folder path
					if (configLines.get(configlength).contains("MusicFolderPath=")) {
						musicPath = configLines.get(configlength).replaceFirst("MusicFolderPath=", "");
						System.out.println("MusicFolderPath=" + musicPath);
					//  dropbox path
					} else if (configLines.get(configlength).contains("DropboxPath=")) {
						dropboxPath = configLines.get(configlength).replaceFirst("DropboxPath=", "");
						System.out.println("DropboxPath=" + dropboxPath);				
					// file validation
					} else if (configLines.get(configlength).contains("EnableFileValidation=")) {
						fileValidation = Boolean.parseBoolean(configLines.get(configlength).replaceFirst("EnableFileValidation=", ""));
						System.out.println("EnableFileValidation=" + fileValidation);
					// representative tag
//					} else if (configLines.get(configlength).contains("RepresentativeTag=")) {
//						representativeTag = configLines.get(configlength).replaceFirst("RepresentativeTag=", "");
//						System.out.println("RepresentativeTag=" + representativeTag);
					}
				}
			} catch (IOException e) {
				System.out.println(ININAME + ".ini konnte nicht gelesen werden.");
				e.printStackTrace();
			}
		} else {
			System.out.println(ININAME + ".ini ist nicht vorhanden und wird nun erstellt.");
			writeINI();
		}
	}

	
	
	// getters
	public static String getMusicPath() {
		return musicPath;
	}
	public static boolean isFileValidation() {
		return fileValidation;
	}
	public static String getDropboxPath() {
		return dropboxPath;
	}
	public static String getRepresentativeTag() {
		return representativeTag;
	}
	public static int getMaxProbability() {
		return maxProbability;
	}

	// setters
	public static void setMusicPath(String musicPath) {
		ConfigurationHandler.musicPath = musicPath;
	}

	public static void setDropboxPath(String dropboxPath) {
		ConfigurationHandler.dropboxPath = dropboxPath;
	}

	public static void setFileValidation(boolean fileValidation) {
		ConfigurationHandler.fileValidation = fileValidation;
	}

	public static int getMaxTracksPD() {
		return maxTracksPD;
	}

	public static int getStartTracksPD() {
		return startTracksPD;
	}
}
