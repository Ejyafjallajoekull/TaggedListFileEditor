package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DefaultFormatter;

import functionality.ConfigurationHandler;
import functionality.MusicFolderFile;
import functionality.PlaylistFile;
import functionality.TaggedFileListHandler;

public class PlaylistModificationWindow extends JDialog implements ActionListener{
// window for adjusting playlists
	
	// serialization
	private static final long serialVersionUID = 1L;
	
	// constants
	private final String TITLE = "Wiedergabelistenanpassung";
	private final String NAME = "Wiedergabelistenname";
	private final String PATH = "Wiedergabelistenpfad";
	private final String RELATIVE = "Relative Wiedergabeliste";
	private final String TRACKSPD = "Tracks pro Verzeichnis";
	private final String SAVE = "Speichern";
	private final String ABORT = "Abbrechen";
	private final String NEWPLAYLIST = "Neue Wiedergabeliste";
	private final String ALREADYEXISTS = "Diese Wiedergabeliste existiert bereits.";
	private final String ADD = ">>";
	private final String REMOVE = "<<";
	private final String MUSICFOLDERS = "Musikordner";
	private final String PLAYLISTFOLDERS = "Playlistordner";
	
	
	// variables
	private JButton saveButton = new JButton(SAVE); // save playlist button
	private JButton abortButton = new JButton(ABORT); // cancel playlist changes button
	private PlaylistFile playlist = null; // the playlist this window modifies
	private JTextField nameField = new JTextField(NEWPLAYLIST);
	private JFileChooser openDirectory = new JFileChooser(); // directory chooser
	private JButton pathSelector = new JButton(Paths.get(TaggedFileListHandler.getTlfPath()).getParent().toString()); // the playlist save location
	private JCheckBox relativeSelector = new JCheckBox(); // checkbox for relative playlist
	private JSpinner tracksPDSpinner = new JSpinner(new SpinnerNumberModel(ConfigurationHandler.getStartTracksPD(), 0, ConfigurationHandler.getMaxTracksPD(), 1)); // spinner for probability
	private JList<MusicFolderFile> playlistFolderList = new JList<MusicFolderFile>();
	private ArrayList<MusicFolderFile> playlistFolders = new ArrayList<MusicFolderFile>();
	private JList<MusicFolderFile> musicFolderList = new JList<MusicFolderFile>(TaggedFileListHandler.getFolderArray());
	private JButton addButton = new JButton(ADD); // button to add music directories to playlist
	private JButton removeButton = new JButton(REMOVE); // button to temove music directories to playlist
	private JScrollPane playlistFolderScrollPane = new JScrollPane();
	private JPanel playlistFolderPanel = new JPanel();
	private JScrollPane musicFolderScrollPane = new JScrollPane();
	
	
	// constructor
	public PlaylistModificationWindow(PlaylistFile file) {
		if (file != null) { // if null is passed a new playlist is created and added to the playlist list
			this.playlist = file;
			nameField.setText(this.playlist.getName().replaceAll(".m3u", ""));
			relativeSelector.setSelected(this.playlist.isRelativePlaylist());
			pathSelector.setText(Paths.get(this.playlist.getAbsolutePath()).getParent().toString());
			relativeSelector.setSelected(this.playlist.isRelativePlaylist());
			tracksPDSpinner.setValue(this.playlist.getTracksPD());
			this.playlistFolders = this.playlist.getMusicDirectories();
			playlistFolderList = new JList<MusicFolderFile>(this.getPlaylistFolderArray());
		} else {
			relativeSelector.setSelected(true);
		}
		this.setTitle(TITLE);
		this.setModal(true); // no other modifications until window is closed
		openDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // allow only folders to be selected
		this.setLayout(new BorderLayout());
		
		// workaround for spinner not working right when entering numbers by keyboard
		JComponent comp = tracksPDSpinner.getEditor(); // obtains the text field of the spinner as component
	    JFormattedTextField field = (JFormattedTextField) comp.getComponent(0); // casts the text field component to a formatted text field
	    DefaultFormatter formatter = (DefaultFormatter) field.getFormatter(); // get the formatter
	    formatter.setCommitsOnValidEdit(true); // sets the formatter to validate values directly on input

		
		// name panel
		JPanel namePanel = new JPanel();
		namePanel.setBorder(BorderFactory.createTitledBorder(NAME));
		namePanel.setLayout(new GridBagLayout());
		GridBagConstraints nameConstraints = new GridBagConstraints();
		nameConstraints.insets = new Insets(5, 5, 5, 5); // space between border and text field
		nameConstraints.fill = GridBagConstraints.HORIZONTAL;
		nameConstraints.weightx = 1.0; // take up all horizontal space
		namePanel.add(nameField, nameConstraints);
		
		// path panel
		JPanel pathPanel = new JPanel();
		pathPanel.setBorder(BorderFactory.createTitledBorder(PATH));
		pathPanel.setLayout(new GridBagLayout());
		GridBagConstraints pathConstraints = new GridBagConstraints();
		pathConstraints.insets = new Insets(5, 5, 5, 5); // space between border and text field
		pathPanel.add(pathSelector, pathConstraints);
		pathSelector.addActionListener(this);
		
		// relative panel
		JPanel relPanel = new JPanel();
		relPanel.setBorder(BorderFactory.createTitledBorder(RELATIVE));
		relPanel.setLayout(new GridBagLayout());
		GridBagConstraints relConstraints = new GridBagConstraints();
		relConstraints.insets = new Insets(5, 0, 5, 0);
		relPanel.add(relativeSelector, relConstraints);
		
		// spinner panel
		JPanel spinnerPanel = new JPanel();
		spinnerPanel.setBorder(BorderFactory.createTitledBorder(TRACKSPD));
		spinnerPanel.setLayout(new GridBagLayout());
		GridBagConstraints spinnerConstraints = new GridBagConstraints();
		spinnerConstraints.insets = new Insets(5, 0, 5, 0);
		spinnerPanel.add(tracksPDSpinner, spinnerConstraints);
		
		// spinner + rel panel
		JPanel combPanel = new JPanel();
		combPanel.setLayout(new GridBagLayout());
		GridBagConstraints combConstraints = new GridBagConstraints();
		combConstraints.gridx = 0;
		combConstraints.gridy = 0;
		combConstraints.fill = GridBagConstraints.HORIZONTAL;
		combConstraints.weightx = 1.0;
		combConstraints.anchor = GridBagConstraints.WEST;
		combConstraints.insets = new Insets(0, 0, 0, 10);
		combPanel.add(relPanel, combConstraints);
		combConstraints.weightx = 0.5;
		combConstraints.gridx = 1;
		combConstraints.anchor = GridBagConstraints.EAST;
		combConstraints.insets = new Insets(0, 10, 0, 0);
		combPanel.add(spinnerPanel, combConstraints);
		
		// music folder panel
		JPanel musicFolderPanel = new JPanel();
		musicFolderPanel.setBorder(BorderFactory.createTitledBorder(MUSICFOLDERS));
		musicFolderPanel.setLayout(new GridBagLayout());
		GridBagConstraints musicFolderConstraints = new GridBagConstraints();
		musicFolderConstraints.fill = GridBagConstraints.BOTH;
		musicFolderConstraints.weightx = 1.0;
		musicFolderConstraints.weighty = 1.0;
		musicFolderPanel.add(musicFolderList, musicFolderConstraints);
		musicFolderList.setCellRenderer(new FileListRenderer());
		this.musicFolderScrollPane = new JScrollPane(musicFolderPanel);
		
		// playlist folder panel
		this.playlistFolderPanel.setBorder(BorderFactory.createTitledBorder(PLAYLISTFOLDERS));
		this.playlistFolderPanel.setLayout(new GridBagLayout());
		GridBagConstraints playlistFolderConstraints = new GridBagConstraints();
		playlistFolderConstraints.fill = GridBagConstraints.BOTH;
		playlistFolderConstraints.weightx = 1.0;
		playlistFolderConstraints.weighty = 1.0;
		this.playlistFolderPanel.add(this.playlistFolderList, playlistFolderConstraints);
		this.playlistFolderList.setCellRenderer(new FileListRenderer());
		this.playlistFolderScrollPane = new JScrollPane(this.playlistFolderPanel);
		
		// directory button panel
		JPanel dirButtonPanel = new JPanel();
		dirButtonPanel.setLayout(new GridBagLayout());
		GridBagConstraints dirButtonConstraints = new GridBagConstraints();
		dirButtonConstraints.insets = new Insets(5, 5, 5, 5);
		dirButtonPanel.add(addButton, dirButtonConstraints);
		dirButtonConstraints.gridy = 1;
		dirButtonPanel.add(removeButton, dirButtonConstraints);
		addButton.addActionListener(this);
		removeButton.addActionListener(this);
		
		// directory panel
		JPanel dirPanel = new JPanel();
		dirPanel.setLayout(new GridBagLayout());
		GridBagConstraints dirConstraints = new GridBagConstraints();
		dirConstraints.fill = GridBagConstraints.BOTH;
		dirConstraints.weightx = 1.0;
		dirConstraints.weighty = 1.0;
		dirPanel.add(this.musicFolderScrollPane, dirConstraints);
		dirConstraints.weightx = 0.2;
		dirPanel.add(dirButtonPanel, dirConstraints);
		dirConstraints.weightx = 1.0;
		dirPanel.add(this.playlistFolderScrollPane, dirConstraints);
		
		// settings JPanel
		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(new GridBagLayout());
		GridBagConstraints settingsConstraints = new GridBagConstraints();
		settingsConstraints.fill = GridBagConstraints.BOTH;
		settingsConstraints.insets = new Insets(15, 15, 15, 15);
		settingsConstraints.weightx = 1.0;
		settingsConstraints.weighty = 0.0;
		settingsPanel.add(namePanel, settingsConstraints);
		settingsConstraints.gridy = 1;
		settingsPanel.add(pathPanel, settingsConstraints);
		settingsConstraints.gridy = 2;
		settingsPanel.add(combPanel, settingsConstraints);
		settingsConstraints.gridy = 3;
		settingsConstraints.weighty = 1.0;
		settingsPanel.add(dirPanel, settingsConstraints);
		this.add(settingsPanel, BorderLayout.CENTER);
		
		// save panel
		JPanel savePanel = new JPanel();
		savePanel.setLayout(new GridBagLayout());
		GridBagConstraints saveConstraints = new GridBagConstraints();
		saveConstraints.gridx = 0;
		saveConstraints.gridy = 0;
		saveConstraints.weightx = 1.0;
		saveConstraints.anchor = GridBagConstraints.WEST;
		saveConstraints.insets = new Insets(20, 50, 20, 10);
		savePanel.add(saveButton, saveConstraints);
		saveConstraints.gridx = 1;
		saveConstraints.anchor = GridBagConstraints.EAST;
		saveConstraints.insets = new Insets(20, 10, 20, 50);
		savePanel.add(abortButton, saveConstraints);
		this.add(savePanel, BorderLayout.SOUTH);
		saveButton.addActionListener(this);
		abortButton.addActionListener(this);
		
		this.add(Box.createRigidArea(new Dimension(0, 20)), BorderLayout.NORTH); // spacer
		this.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.EAST); // spacer
		this.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.WEST); // spacer
		this.pack(); // size window based on components
		this.setLocationRelativeTo(null); // centered
		this.setVisible(true); // has to be last or modal will block other code
	}
	
	public void selectDirectory(JButton selector) {
		openDirectory.setCurrentDirectory(new File(selector.getText())); // set current directory as starting directory
		if (openDirectory.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { // on approval rename corresponding button
			selector.setText(openDirectory.getSelectedFile().getAbsolutePath());
		}
		this.pack();
		this.setLocationRelativeTo(null);
	}
	
	public MusicFolderFile[] getPlaylistFolderArray() {
		return playlistFolders.toArray(new MusicFolderFile[playlistFolders.size()]);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.saveButton) {
			String playlistPath = "\\" + nameField.getText().trim() + ".m3u"; // path of the playlist
			if (relativeSelector.isSelected()) {
				playlistPath = Paths.get(TaggedFileListHandler.getTlfPath()).getParent().toString() + playlistPath;
			} else {
				playlistPath = pathSelector.getText() + playlistPath;
			}
			if (playlist == null) { // if no old file exists, create new file
				playlist = new PlaylistFile(playlistPath);
				if (TaggedFileListHandler.getPlaylistFiles().contains(playlist)) {
					JOptionPane.showMessageDialog(this, ALREADYEXISTS);
					playlist = null; // reset playlist so it's still the creation of a new playlist
					return; // if playlist already exists stop code execution
				}
				TaggedFileListHandler.getPlaylistFiles().add(playlist);
				Collections.sort(TaggedFileListHandler.getPlaylistFiles());
			} else if (!this.playlist.getAbsolutePath().equals(playlistPath)) { // if file path or name has changed, alter playlist list
				System.out.println("Playlist " + playlist + " is updated to new path: " + playlistPath);
				if (TaggedFileListHandler.getPlaylistFiles().contains(new PlaylistFile(playlistPath))) {
					JOptionPane.showMessageDialog(this, ALREADYEXISTS); // if playlist already exists stop code execution
					return;
				}
				TaggedFileListHandler.getPlaylistFiles().remove(playlist);
				playlist = new PlaylistFile(playlistPath);
				TaggedFileListHandler.getPlaylistFiles().add(playlist);
				Collections.sort(TaggedFileListHandler.getPlaylistFiles());
			}
			this.playlist.setMusicDirectories(playlistFolders);
			this.playlist.setTracksPD((int) this.tracksPDSpinner.getValue());
			this.playlist.setRelativePlaylist(this.relativeSelector.isSelected());
			this.playlist.setEdited(true); // mark as edited
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)); // close the window
		} else if (e.getSource() == this.abortButton) {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)); // close the window
		} else if (e.getSource() == this.pathSelector) {
			this.selectDirectory(pathSelector);
		} else if (e.getSource() == this.addButton) { // add music folder to playlist
			if (musicFolderList.getSelectedValue() != null && !playlistFolders.contains(musicFolderList.getSelectedValue())) {
				playlistFolders.add(musicFolderList.getSelectedValue());
				Collections.sort(playlistFolders); // sort list
				playlistFolderList.setListData(this.getPlaylistFolderArray()); // update list
				playlistFolderScrollPane.setViewportView(playlistFolderPanel); // update scroll pane
			}
		} else if (e.getSource() == this.removeButton) { // remove music folder from playlist
			if (playlistFolderList.getSelectedValue() != null) {
				playlistFolders.remove(playlistFolderList.getSelectedValue());
				playlistFolderList.setListData(this.getPlaylistFolderArray()); // update list
				playlistFolderScrollPane.setViewportView(playlistFolderPanel); // update scroll pane
			}
		}
	}

}
