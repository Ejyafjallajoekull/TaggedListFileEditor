package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultFormatter;

import functionality.SearchComparator;
import functionality.TaggedFileListHandler;
import functionality.TaggedFileListHandler.sortingCategories;

public class MainMenu extends JMenuBar implements ActionListener{
// menu bar and all its items
	
	// serialization
	private static final long serialVersionUID = 1L;

	// constants
	private final String DATA = "Datei";
	private final String NEW_FILE = "Neu";
	private final String OPENFILE = "Öffnen";
	private final String SAVEFILE = "Speichern";
	private final String CLOSE = "Schließen";
	private final String UPDATE = "Update Dateiliste";
	private final String SORT = "Sortieren";
	private final String SORT_NAME = "Name";
	private final String SORT_FAV = "Favorit";
	private final String SORT_EXC = "Ausgeschlossen";
	private final String SORT_REQ = "Angefordert";
	private final String SORT_PROB = "Wahrscheinlichkeit";
	private final String SORT_EDIT = "Bearbeitet";
	private final String VARIOUS = "Sonstiges";
	private final String MUSIC = "Musik";
	private final String SWITCH = "Wiedergabelisten anzeigen";
	private final String SETTINGS = "Einstellungen";
	private final String INFO = "Information";
	private final String ALREADY_EXISTS = "Diese Datei existiert bereits.";
	
	// variables
	private JMenuItem newFileDataItem = new JMenuItem(this.NEW_FILE);
	private JMenuItem openDataItem = new JMenuItem(this.OPENFILE);
	private JMenuItem updateDataItem = new JMenuItem(this.UPDATE);
	private JMenuItem saveDataItem = new JMenuItem(this.SAVEFILE);
	private JMenuItem closeDataItem = new JMenuItem(this.CLOSE);
	private JMenuItem settingsDataItem = new JMenuItem(this.SETTINGS);
	private JRadioButton nameSortItem = new JRadioButton(this.SORT_NAME);
	private JRadioButton favoriteSortItem = new JRadioButton(this.SORT_FAV);
	private JRadioButton excludedSortItem = new JRadioButton(this.SORT_EXC);
	private JRadioButton requestedSortItem = new JRadioButton(this.SORT_REQ);
	private JRadioButton probabilitySortItem = new JRadioButton(this.SORT_PROB);
	private JRadioButton editedSortItem = new JRadioButton(this.SORT_EDIT);
	private ButtonGroup sortingButtons = new ButtonGroup();
	private JMenuItem infoVariousItem = new JMenuItem(this.INFO);
	private JCheckBoxMenuItem musicVariousItem = new JCheckBoxMenuItem(this.MUSIC);
	private JButton switchButton = new JButton(this.SWITCH);
	private JFormattedTextField searchField = new JFormattedTextField(new DefaultFormatter());
	private Clip clip; // audio clip
	private PlaylistSelectionWindow playlistWindow = null;
	private InfoWindow infoWindow = null;
	private SearchComparator searchComp = new SearchComparator();
	private int timerSpeed = 300; // time difference for update // 0.3 sec
	private Timer timer = new Timer(timerSpeed, this); // update timer for search field // for performance reasons
	private JFileChooser fileChooser = new JFileChooser(new File(".").toString());

	
	// constructor
	public MainMenu() {
		// file chooser
		FileFilter tlfFilter = new FileNameExtensionFilter("Tagged File List", "tlf");
		this.fileChooser.addChoosableFileFilter(tlfFilter); // add .tlf extension filter
		this.fileChooser.setFileFilter(tlfFilter); // set tlf filter to standard
		this.fileChooser.removeChoosableFileFilter(fileChooser.getChoosableFileFilters()[0]); // remove standard filter "all files"

		
		// menus
		JMenu dataMenu = new JMenu(this.DATA);
		JMenu sortingMenu = new JMenu(this.SORT);
		JMenu variousMenu = new JMenu(this.VARIOUS);
		JToolBar switchMenu = new JToolBar(this.SWITCH);
		
	    DefaultFormatter formatter = (DefaultFormatter) searchField.getFormatter(); // get the formatter
	    System.out.println(searchField.getFormatter());
	    formatter.setCommitsOnValidEdit(true); // sets the formatter to validate values directly on input
	    formatter.setOverwriteMode(false); // prevent overwriting
	    
		this.sortingButtons.add(nameSortItem);
		this.sortingButtons.add(favoriteSortItem);
		this.sortingButtons.add(excludedSortItem);
		this.sortingButtons.add(requestedSortItem);
		this.sortingButtons.add(probabilitySortItem);
		this.sortingButtons.add(editedSortItem);
		this.sortingButtons.setSelected(nameSortItem.getModel(), true); // initialise sorted by name // just cosmetical
		
		switchMenu.setFloatable(false); // no dragging -> fixed menu item
		dataMenu.add(newFileDataItem);
		dataMenu.addSeparator();
		dataMenu.add(openDataItem);
		dataMenu.addSeparator();
		dataMenu.add(updateDataItem);
		dataMenu.addSeparator();
		dataMenu.add(saveDataItem);
		dataMenu.addSeparator();
		dataMenu.add(settingsDataItem);
		dataMenu.addSeparator();
		dataMenu.add(closeDataItem);
		sortingMenu.add(nameSortItem);
		sortingMenu.addSeparator();
		sortingMenu.add(favoriteSortItem);
		sortingMenu.addSeparator();
		sortingMenu.add(requestedSortItem);
		sortingMenu.addSeparator();
		sortingMenu.add(excludedSortItem);
		sortingMenu.addSeparator();
		sortingMenu.add(probabilitySortItem);
		sortingMenu.addSeparator();
		sortingMenu.add(editedSortItem);
		variousMenu.add(musicVariousItem);
		variousMenu.addSeparator();
		variousMenu.add(infoVariousItem);
		switchMenu.add(switchButton);
		this.add(dataMenu);
		this.add(sortingMenu);
		this.add(variousMenu);
		this.add(switchMenu);
		this.add(searchField);
		// action listener
		switchButton.addActionListener(this);
		newFileDataItem.addActionListener(this);
		openDataItem.addActionListener(this);
		updateDataItem.addActionListener(this);
		saveDataItem.addActionListener(this);
		settingsDataItem.addActionListener(this);
		closeDataItem.addActionListener(this);
		nameSortItem.addActionListener(this);
		favoriteSortItem.addActionListener(this);
		excludedSortItem.addActionListener(this);
		requestedSortItem.addActionListener(this);
		probabilitySortItem.addActionListener(this);
		editedSortItem.addActionListener(this);
		musicVariousItem.addActionListener(this);
		infoVariousItem.addActionListener(this);
		searchField.addActionListener(this);
		searchField.addPropertyChangeListener("value", evt -> timer.restart());
	}
	
	// close playlist window // used for updates and tlf closing events
	public void closePlaylistWindow() {
		if (playlistWindow != null) {
			playlistWindow.dispatchEvent(new WindowEvent(playlistWindow, WindowEvent.WINDOW_CLOSING)); // close the window
			playlistWindow = null;
		}
	}
	
	// action listener
	public void actionPerformed (ActionEvent ae) {
		if (ae.getSource() == this.openDataItem) { // open files
			if (this.fileChooser.showOpenDialog(MainWindow.getMainWindow()) == JFileChooser.APPROVE_OPTION) {
				TaggedFileListHandler.close(); // before opening a new file clear all file lists
				this.closePlaylistWindow(); // close playlist window
				SelectionWindow.setFileSelectionMode(false); // set to folder selection
				TaggedFileListHandler.setTlfPath(this.fileChooser.getSelectedFile().getAbsolutePath());
				TaggedFileListHandler.readTaggedListFile();
				SelectionWindow.updateLists();
			}
		} else if (ae.getSource() == this.newFileDataItem) { // create new tagged list file
			if (this.fileChooser.showSaveDialog(MainWindow.getMainWindow()) == JFileChooser.APPROVE_OPTION) {
				String path = this.fileChooser.getSelectedFile().getAbsolutePath();
				if (!path.substring(path.length()-4).equalsIgnoreCase(".tlf")) { // add .tlf if not done manually
					path = path + ".tlf";
				}
				if (!new File(path).exists()) {
					TaggedFileListHandler.close(); // before opening a new file clear all file lists
					this.closePlaylistWindow(); // close playlist window
					SelectionWindow.setFileSelectionMode(false); // set to folder selection
					TaggedFileListHandler.setTlfPath(path); // set the file path
					TaggedFileListHandler.updateTaggedListFile(); // update the file
					TaggedFileListHandler.createTaggedListFile(); // create the new file
					SelectionWindow.updateLists(); // display file
				} else {
					JOptionPane.showMessageDialog(this, ALREADY_EXISTS); // if tlf already exists stop code execution
				}
			}
		} else if (ae.getSource() == this.updateDataItem) { // update file list
			if (TaggedFileListHandler.getTlfPath() != null) { // only update if file is opened
				TaggedFileListHandler.updateTaggedListFile();
				SelectionWindow.updateLists(); // update lists to display changes
			}
		} else if (ae.getSource() == this.saveDataItem) { // save list
			if (TaggedFileListHandler.getTlfPath() != null) { // only allow saving while a file is opened
				TaggedFileListHandler.createTaggedListFile();
			}
		} else if (ae.getSource() == this.settingsDataItem) { // open settings
			new SettingsWindow();
		} else if (ae.getSource() == this.closeDataItem) { // close file
			TaggedFileListHandler.close();
			this.closePlaylistWindow(); // close playlist window
			SelectionWindow.setFileSelectionMode(false); // set to folder selection
			SelectionWindow.updateLists(); // show cleared lists
		} else if (ae.getSource() == this.musicVariousItem) { // play music
			if (musicVariousItem.isSelected()) { // checkbox
				try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/data/Keygen Music.wav"))) {
			        clip = AudioSystem.getClip();
			        clip.open(audioInputStream);
			        clip.loop(Clip.LOOP_CONTINUOUSLY); // loop indefinitely
			    } catch(Exception ex) {
			        System.out.println("Error with playing sound.");
			        ex.printStackTrace();
			    }
			} else { // checkbox deselected
				clip.stop(); // stop music
			}
		} else if (ae.getSource() == infoVariousItem) {
			if (infoWindow == null || !infoWindow.isVisible()) {
				infoWindow = new InfoWindow();;
			} else {
				infoWindow.requestFocus();
			}
		} else if (ae.getSource() == switchButton) { // switch between folder and file list
			if (TaggedFileListHandler.getTlfPath() != null) { // only allow switching if a file is opened
				if (playlistWindow == null || !playlistWindow.isVisible()) {
					playlistWindow = new PlaylistSelectionWindow();
				} else {
					playlistWindow.requestFocus();
				}
			}
		} else if (ae.getSource() == nameSortItem){
			TaggedFileListHandler.setCurrentSortingCategory(sortingCategories.SORT_CAT_NAME);
			SelectionWindow.updateLists();
		} else if (ae.getSource() == favoriteSortItem){
			TaggedFileListHandler.setCurrentSortingCategory(sortingCategories.SORT_CAT_FAV);
			SelectionWindow.updateLists();
		} else if (ae.getSource() == excludedSortItem){
			TaggedFileListHandler.setCurrentSortingCategory(sortingCategories.SORT_CAT_EXC);
			SelectionWindow.updateLists();
		} else if (ae.getSource() == requestedSortItem){
			TaggedFileListHandler.setCurrentSortingCategory(sortingCategories.SORT_CAT_REQ);
			SelectionWindow.updateLists();
		} else if (ae.getSource() == probabilitySortItem){
			TaggedFileListHandler.setCurrentSortingCategory(sortingCategories.SORT_CAT_PROB);
			SelectionWindow.updateLists();
		} else if (ae.getSource() == editedSortItem){
			TaggedFileListHandler.setCurrentSortingCategory(sortingCategories.SORT_CAT_EDIT);
			SelectionWindow.updateLists();
		} else if (ae.getSource() == this.timer) {
			timer.stop();
			this.updateSearch();
		}
	}

	public SearchComparator getSearchComp() {
		return searchComp;
	}

	public void setSearchComp(SearchComparator searchComp) {
		this.searchComp = searchComp;
	}
	
	private void updateSearch(){
		if (searchField.getText() != null && !searchField.getText().equals("")) {
			this.searchComp.setSearchTerm(this.searchField.getText());
			this.sortingButtons.clearSelection();
			TaggedFileListHandler.setCurrentSortingCategory(sortingCategories.SORT_CAT_SEARCH);
			TaggedFileListHandler.sortFileLists(searchComp);
		} else {
			this.sortingButtons.setSelected(nameSortItem.getModel(), true); // display sorted by name // just cosmetical
			TaggedFileListHandler.setCurrentSortingCategory(sortingCategories.SORT_CAT_NAME);
		}
		SelectionWindow.updateLists();
	}

}
