package functionality;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class AdvancedFile extends File{
	// file with different additional informations and functions
	
		 // serialization
		private static final long serialVersionUID = 1L; // no idea how this works, but it suppresses the warning

		// private variables
		private Path myPath = this.toPath(); // advanced file path // internal variable
		private int probability = 1; // probability for playlist selection
		private boolean requested = false; // is requested
		private boolean favorite = false; // is favorite
		private boolean excluded = false; // is excluded
		private boolean edited = false; // has been edited in the current editor session

		
		public AdvancedFile(File parent, String child) {
			super(parent, child);
		}

		public AdvancedFile(String parent, String child) {
			super(parent, child);
		}

		public AdvancedFile(URI uri) {
			super(uri);
		}

		public AdvancedFile(String pathname) {
			super(pathname);		
		}
		
		// get the top folder name
		public String getTopFolder() {
				return this.toPath().getName(this.toPath().getNameCount() - 2).toString();
		}		
		
		
		// listFiles() for AdvancedFiles // redundant
//		public AdvancedFile[] listAdvancedFiles() {
//			File[] pathArray = this.listFiles(); // file array of all subfiles
//			AdvancedFile[] advancedFileArray = new AdvancedFile[pathArray.length]; // output advanced file array
//			for (int i = 0; i < pathArray.length; i++) { // converting every file entry to an advanced file entry
//				advancedFileArray[i] = new AdvancedFile(pathArray[i].getAbsolutePath());
//			}
//			System.out.println(Arrays.asList(this.listFiles()));
//			return advancedFileArray;
//		}
		
		@Override
		public AdvancedFile[] listFiles() {
	        String[] ss = this.list();
	        if (ss == null) return null;
	        int n = ss.length;
	        AdvancedFile[] fs = new AdvancedFile[n];
	        for (int i = 0; i < n; i++) {
	            fs[i] = new AdvancedFile(this, ss[i]);
	        }
	        return fs;
	    }
		
		// listFiles(FileFilter) for AdvancedFiles
		public AdvancedFile[] listAdvancedFiles(FileFilter filter) {
			File[] pathArray = this.listFiles(filter); // file array of all subfiles
			AdvancedFile[] advancedFileArray = new AdvancedFile[pathArray.length]; // output advanced file array
			for (int i = 0; i < pathArray.length; i++) { // converting every file entry to an advanced file entry
				advancedFileArray[i] = new AdvancedFile(pathArray[i].getAbsolutePath());
			}
//			System.out.println(Arrays.asList(this.listFiles()));
			return advancedFileArray;
		}
		
		// returns music folder/file as relative path
		public String toRelativePath() {
			Path absolutePath = this.toPath();
			return absolutePath.subpath(absolutePath.getNameCount() - 2, absolutePath.getNameCount()).toString();
		}
		
		// copy advanced file with depth top folders
		public void copyAdvancedFile(String targetDir, int depth) {
			if(targetDir != null && this.exists()) {
				if (new File(targetDir).exists() && new File(targetDir).isDirectory()) {
					if (this.myPath.getNameCount() < depth) { // if directory depth greater than possible set to greatest possible value 
						depth = this.myPath.getNameCount();
					}
					if (depth < 1) { // check depth is not invalid
						depth = 1;
					}
					String targetPath = targetDir + "\\" + this.myPath.subpath(this.myPath.getNameCount() - depth, this.myPath.getNameCount()); // path of the copied file
					Path target = Paths.get(targetPath); // target directory as path for code optimization
					if (!new File(target.getParent().toString()).exists()) { // check if top directories are existent
						System.out.println("Erstelle Verzeichnis " + target.getParent().toString());
						new File(target.getParent().toString()).mkdirs(); // create missing top directories
					}
					try { // copy file and replace existing one
						Files.copy(this.myPath, target, REPLACE_EXISTING);
						System.out.println("Kopiere " + this + " nach " + targetPath);
					} catch (IOException e) { // error
						System.out.println("Die Datei konnte nicht erstellt werden.");
						e.printStackTrace();
					}
				} else { // error
					System.out.println("Der angegebene Zielpfad existiert nicht.");
				}
			} else { // error
				System.out.println("Zielverzeichnis ist nicht initialisiert oder die zu kopierende Datei existiert nicht.");
			}
		}

	// check if file is a representative file
	public boolean isRepresentative() {
		if (this.exists()) {
			try {
				byte[] byteArray = Files.readAllBytes(this.toPath()); 
				int length = byteArray.length;
				if (byteArray.length > ConfigurationHandler.getRepresentativeTag().getBytes().length + 200) { // check if significant byte amount and suppress the display of whole files as byte code
					length = ConfigurationHandler.getRepresentativeTag().getBytes().length + 200; // + 200 to cover possible empty spaces in representative files
				}
				if (byteArray.length > 0) {
					if (new String(byteArray, 0, length).trim().equals(ConfigurationHandler.getRepresentativeTag().trim())) {
						return true;
					} else {
						return false;
					}
				} else { // error
					return false; // empty file, but still no representative file  
				}
			} catch (IOException e) {
				System.out.println("Datei konnte nicht gelesen werden.");
				e.printStackTrace();
				return true; // return true so it is ignored if the reading went wrong
			}
		} else { // file does not exist // return true so the file gets ignored
			return true;
		}
	}
		
	// replace current file with representative file
	public void replaceWithRepresentative() {
		if (this.exists()) {
			try {
				Files.write(this.toPath(), ConfigurationHandler.getRepresentativeTag().trim().getBytes());
			} catch (IOException e) {
				System.out.println("Stellvertreterdatie konnte nicht erstellt werden.");
				e.printStackTrace();
			}
		} else {
			System.out.println("Die Datei " + this + " existiert nicht und kann deshalb nicht mit einer Stellvertreterdatei ersetzt werden.");
		}
	}
	
	// copy requested files to dropbox folder
	public void handleRequest() {
		if (this.hasCorrespondingFile()) {
			AdvancedFile requestedFile = new AdvancedFile(this.getCorrespondingPath().toString());
			if (requestedFile.exists() && requestedFile.isFile()) {
				requestedFile.copyAdvancedFile(this.toPath().getParent().getParent().toString(), 2);
				System.out.println("Die Datei " + requestedFile + " wurde angefordert.");
			} else {
				System.out.println("Die angeforderte Datei existiert nicht.");
			}
		}
	}
	
	// returns the corresponding file path
	public Path getCorrespondingPath() {
		Path subPath;
		Path correspondingPath;
		if (this.isFile()) {
			subPath = myPath.getParent().getParent();
			if (subPath != null) {
				if (subPath.toString().equals(TaggedFileListHandler.getTlfFolderPath())) { // tlf folder
					correspondingPath = Paths.get(ConfigurationHandler.getMusicPath(), this.getTopFolder(), this.getName());
				} else { // music folder
					correspondingPath = Paths.get(TaggedFileListHandler.getTlfFolderPath(), this.getTopFolder(), this.getName());
				}
			} else { // error
				return null;
			}
		} else if (this.isDirectory()) { // is folder
			subPath = myPath.getParent();
			if (subPath != null) {
				if (subPath.toString().equals(TaggedFileListHandler.getTlfFolderPath())) { // dropbox folder
					correspondingPath = Paths.get(ConfigurationHandler.getMusicPath(), this.getName());
				} else { // music folder
					correspondingPath = Paths.get(TaggedFileListHandler.getTlfFolderPath(), this.getName());
				}
			} else { // error
				return null;
			}
		} else { // error
			return null;
		}
		return correspondingPath;
	}
	
	// checks if there is a corresponding file in the target directory
	public boolean hasCorrespondingFile() {
		return new File(this.getCorrespondingPath().toString()).exists();
	}
		
	// getters & setters
	public int getProbability() {
		return probability;
	}
	public boolean isRequested() {
		return requested;
	}
	public void setProbability(int probability) {
		this.probability = probability;
	}
	public void setRequested(boolean requested) {
		this.requested = requested;
	}
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
	public void setExcluded(boolean excluded) {
		this.excluded = excluded;
	}
	public boolean isFavorite() {
		return favorite;
	}
	public boolean isExcluded() {
		return excluded;
	}

	public boolean isEdited() {
		return edited;
	}

	public void setEdited(boolean edited) {
		this.edited = edited;
	}

}
