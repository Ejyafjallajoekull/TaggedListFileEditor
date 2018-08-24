package gui;

import java.io.File;

import javax.swing.JFrame;

import functionality.ConfigurationHandler;
import functionality.FileTransferHandler;
import functionality.TaggedFileListHandler;


public class MainWindow{

	// TODO: general code optimisation
	
	private static final String TLFEDITORVERSION = "1.2.3.7";
	private static JFrame mainWindow = null;

	
	public static void main(String[] args) {
		ConfigurationHandler.readINI(); // check for config file // if none is found, create a standard config file // probably add warning if standard file is used
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
		}
		if (args.length == 2) {
			File tlfToOpen = new File(args[1]);
			if (args[0].equals("-h") && tlfToOpen.exists() && tlfToOpen.isFile() && isTLF(tlfToOpen)) {
				TaggedFileListHandler.setTlfPath(args[1]);
				TaggedFileListHandler.readTaggedListFile(); // open tlf
				TaggedFileListHandler.updateTaggedListFile(); // update tlf
				TaggedFileListHandler.generateAllPlaylists(); // generate all playlists
				TaggedFileListHandler.createTaggedListFile(); // save possible update changes
				FileTransferHandler.transferFiles(); // transfer files
			} else {
				lunchEditor();
			}
		} else {
			lunchEditor();
		}
	}
	
	private static void lunchEditor() {
		if (mainWindow == null) {
			mainWindow = new JFrame("Tagged List File Editor");
			mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mainWindow.setSize(660, 500);
//			mainWindow.setSize(ScreenConverter.getAbsoluteWidth(0.39285713f), ScreenConverter.getAbsoluteHeight(0.47619048f));
			mainWindow.setLocationRelativeTo(null);
			MainMenu mainMenu = new MainMenu();
			mainWindow.add(mainMenu);
			mainWindow.setJMenuBar(mainMenu);
			new SelectionWindow();
			mainWindow.setVisible(true);
		}
	}
	
	private static boolean isTLF(File file) {
		if (file != null) {
			String path = file.getAbsolutePath();
			return path.substring(path.length()-4).equalsIgnoreCase(".tlf");
		} else {
			return false;	
		}
	}

	public static String getTlfEditorVersion() {
		return TLFEDITORVERSION;
	}

	public static JFrame getMainWindow() {
		return mainWindow;
	}

	
}
