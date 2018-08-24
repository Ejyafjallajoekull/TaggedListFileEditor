package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import functionality.ConfigurationHandler;

public class SettingsWindow extends JDialog implements ActionListener{
	// settings window
	
	// serialization
	private static final long serialVersionUID = 1L;

	// constants
	private final String SETTINGS = "Einstellungen";
	private final String MUSICPATH = "Musikverzeichnis";
	private final String DROPBOXPATH = "Dropboxverzeichnis";
	private final String FILEVALIDATION = "Dateivalidierung";
	private final String SAVESETTINGS = "Speichern";
	private final String ABORTSETTINGS = "Abbrechen";
	
	// variables
	private JFileChooser openDirectory = new JFileChooser();
	JButton musicPathSelector = new JButton(ConfigurationHandler.getMusicPath());
	JButton dropboxPathSelector = new JButton(ConfigurationHandler.getDropboxPath());
	JCheckBox fileValidationSelector = new JCheckBox();
	JButton saveSettingsButton = new JButton(SAVESETTINGS);
	JButton abortSettingsButton = new JButton(ABORTSETTINGS);
	
	// constructor
	public SettingsWindow() {
		this.setTitle(SETTINGS);
		this.setModal(true); // block other windows
		openDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // allow only folders to be selected
		fileValidationSelector.setSelected(ConfigurationHandler.isFileValidation()); // set file validation checkbox
	
		// save panel
		JPanel savePanel = new JPanel();
		savePanel.setLayout(new GridBagLayout());
		GridBagConstraints saveConstraints = new GridBagConstraints();
		saveConstraints.gridx = 0;
		saveConstraints.gridy = 0;
		saveConstraints.weightx = 1.0;
		saveConstraints.anchor = GridBagConstraints.WEST;
		saveConstraints.insets = new Insets(20, 50, 20, 10);
		savePanel.add(saveSettingsButton, saveConstraints);
		saveConstraints.gridx = 1;
		saveConstraints.anchor = GridBagConstraints.EAST;
		saveConstraints.insets = new Insets(20, 10, 20, 50);
		savePanel.add(abortSettingsButton, saveConstraints);

		// music path panel
		JPanel musicPathPanel = new JPanel();
		musicPathPanel.setBorder(BorderFactory.createTitledBorder(MUSICPATH));
		musicPathPanel.setLayout(new GridBagLayout());
		musicPathPanel.add(musicPathSelector);
		
		// dropbox path panel
		JPanel dropboxPathPanel = new JPanel();
		dropboxPathPanel.setBorder(BorderFactory.createTitledBorder(DROPBOXPATH));
		dropboxPathPanel.setLayout(new GridBagLayout());
		dropboxPathPanel.add(dropboxPathSelector);
		
		// file validation panel
		JPanel fileValidationPanel = new JPanel();
		fileValidationPanel.setBorder(BorderFactory.createTitledBorder(FILEVALIDATION));
		fileValidationPanel.setLayout(new GridBagLayout());
		fileValidationPanel.add(fileValidationSelector);
				
		// settings JPanel
		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(new GridLayout(0, 1, 15, 15));
		settingsPanel.add(musicPathPanel);
		settingsPanel.add(dropboxPathPanel);
		settingsPanel.add(fileValidationPanel);
		
		// main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());		
		mainPanel.add(settingsPanel, BorderLayout.CENTER);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)), BorderLayout.NORTH); // spacer
		mainPanel.add(savePanel, BorderLayout.SOUTH);
		mainPanel.add(Box.createRigidArea(new Dimension(20, 0)), BorderLayout.EAST); // spacer
		mainPanel.add(Box.createRigidArea(new Dimension(20, 0)), BorderLayout.WEST); // spacer
		JScrollPane scrollPanel = new JScrollPane(mainPanel); // create a scrollable version of the main panel
		this.add(scrollPanel); // add scrollable main panel

		// action listener
		musicPathSelector.addActionListener(this);
		dropboxPathSelector.addActionListener(this);
		fileValidationSelector.addActionListener(this);
		saveSettingsButton.addActionListener(this);
		abortSettingsButton.addActionListener(this);
		
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true); // has to be last or blocks code execution due to being modal
		
	}
	
	public void selectDirectory(JButton selector) {
		openDirectory.setCurrentDirectory(new File(selector.getText())); // set current directory as starting directory
		if (openDirectory.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { // on approval rename corresponding button
			selector.setText(openDirectory.getSelectedFile().getAbsolutePath());
		}
		this.pack();
		this.setLocationRelativeTo(null);
	}
	
	public void saveSettings() { // save settings
		ConfigurationHandler.setMusicPath(this.musicPathSelector.getText());
		ConfigurationHandler.setDropboxPath(this.dropboxPathSelector.getText());
		ConfigurationHandler.setFileValidation(this.fileValidationSelector.isSelected());
		ConfigurationHandler.writeINI();
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)); // close the settings window after saving
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.musicPathSelector) {
			this.selectDirectory(musicPathSelector); // adjust music path button
		} else if (e.getSource() == this.dropboxPathSelector) {
			this.selectDirectory(dropboxPathSelector); // adjust dropbox path button
		} else if (e.getSource() == this.saveSettingsButton) {
			this.saveSettings(); // save the displayed settings
		} else if (e.getSource() == this.abortSettingsButton) {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)); // close the settings window
		}
	}
}
