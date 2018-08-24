package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultFormatter;

import functionality.AdvancedFile;
import functionality.ConfigurationHandler;
import functionality.MusicFolderFile;
import functionality.TaggedFileListHandler;

public class SelectionWindow implements ListSelectionListener, ActionListener, ChangeListener, MouseListener {

	// constants
	private static final String TAGS = "Tags";
	private static final String FAVORITE = "Favorit";
	private static final String REQUEST = "Angefordert";
	private static final String EXCLUDE = "Ausgeschlossen";
	private static final String PROBABILITY = "Wahrscheinlichkeit";
	
	// variables
	private static JList<AdvancedFile> fileList; // the displayed list
	private static JCheckBox favoriteBox; // checkbox for favorites
	private static JCheckBox requestBox; // checkbox for requests
	private static JCheckBox excludeBox; // checkbox for exclusions
	private static JSpinner probabilitySpinner; // spinner for probability
	private static AdvancedFile selectedFile = null; // the currently selected file
	private static MusicFolderFile selectedFolder = null; // the currently selected folder
	private static boolean fileSelectionMode = false; // true: file selection mode // false: folder selection mode
	
	private static JScrollPane filePane;
	
	private static ListCellRenderer<AdvancedFile> listRenderer = new FileListRenderer(); // works, but for good style new renderer should be used music folder exclusive (file renderer has advanced files as value)
	
	private static FastListModel listModel = new FastListModel(); // the list model for file & folder display
	
	public SelectionWindow () {
		fileList = new JList<AdvancedFile>(listModel); // start with folder selection
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // only allow single selection	
		fileList.setCellRenderer(listRenderer);
		listModel.rebuild(TaggedFileListHandler.getFolderArray());
		filePane = new JScrollPane(fileList);
//		fileList.setFixedCellHeight(ScreenConverter.getAbsoluteHeight(0.015238095f)); // fixed values or else iteration over every element takes place to find the preferred size => huge performance impact
//		fileList.setFixedCellWidth(ScreenConverter.getAbsoluteWidth(0.89285713f)); // fixed values or else iteration over every element takes place to find the preferred size => huge performance impact
		fileList.setFixedCellHeight(16); // fixed values or else iteration over every element takes place to find the preferred size => huge performance impact
		fileList.setFixedCellWidth(1500); // fixed values or else iteration over every element takes place to find the preferred size => huge performance impact

		favoriteBox = new JCheckBox(FAVORITE);
		requestBox = new JCheckBox(REQUEST);
		excludeBox = new JCheckBox(EXCLUDE);
		SpinnerModel probModel = new SpinnerNumberModel(1, 0, ConfigurationHandler.getMaxProbability(), 1);
		probabilitySpinner = new JSpinner(probModel);
		probabilitySpinner.setValue(1);
		probabilitySpinner.setMaximumSize(new Dimension(100, 30));
		// workaround for spinner not working right when entering numbers by keyboard
		JComponent comp = probabilitySpinner.getEditor(); // obtains the text field of the spinner as component
	    JFormattedTextField field = (JFormattedTextField) comp.getComponent(0); // casts the text field component to a formatted text field
	    DefaultFormatter formatter = (DefaultFormatter) field.getFormatter(); // get the formatter
	    formatter.setCommitsOnValidEdit(true); // sets the formatter to validate values directly on input
		
	    // tag panel
	    JPanel tagPanel = new JPanel();
	    tagPanel.setBorder(BorderFactory.createTitledBorder(TAGS));
		tagPanel.setLayout(new BoxLayout(tagPanel, BoxLayout.Y_AXIS));
		tagPanel.add(favoriteBox);
		tagPanel.add(requestBox);
		tagPanel.add(excludeBox);
		
		// probability panel
	    JPanel probPanel = new JPanel();
	    probPanel.setBorder(BorderFactory.createTitledBorder(PROBABILITY));
		probPanel.setLayout(new GridBagLayout());
		probPanel.add(probabilitySpinner);
	    
		// selection panel
		JPanel selectionPanel = new JPanel();
		selectionPanel.setLayout(new GridBagLayout());
		GridBagConstraints selectionConstraints = new GridBagConstraints();
		selectionConstraints.insets = new Insets(15, 15, 0, 15);
		selectionConstraints.fill = GridBagConstraints.HORIZONTAL;
		selectionConstraints.anchor = GridBagConstraints.PAGE_START;
		selectionConstraints.gridy = 0;
		selectionConstraints.weighty = 0.02;
		selectionPanel.add(tagPanel, selectionConstraints);
		selectionConstraints.weighty = 1.0;
		selectionConstraints.gridy = 1;	
		selectionPanel.add(probPanel, selectionConstraints);
		
		JSplitPane viewPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, filePane, selectionPanel);
		viewPane.setResizeWeight(1);
		MainWindow.getMainWindow().add(viewPane);
		fileList.addListSelectionListener(this);
		favoriteBox.addActionListener(this);
		requestBox.addActionListener(this);
		excludeBox.addActionListener(this);
		probabilitySpinner.addChangeListener(this);
		fileList.addMouseListener(this);
	}

	// update the JList to reflect changes in the underlying list
	public static void updateLists() {
		((FileListRenderer) listRenderer).setFileSelectionMode(false); // set display mode to file names without folder names
		if (fileSelectionMode) { // display files
			if (selectedFolder != null) { // show files from Folder
				// filePane.setViewportView(fileList);
				// fileList.setListData(selectedFolder.getTrackArray()); // update JList to display changes
				listModel.rebuild(selectedFolder.getTrackArray());
			} else { // show all files
				((FileListRenderer) listRenderer).setFileSelectionMode(true); // enable folder/file-display
		//		filePane.setViewportView(fileList);
		//		fileList.setListData(TaggedFileListHandler.getFileArray()); // update JList to display changes
				listModel.rebuild(TaggedFileListHandler.getFileArray());
			}
		} else { // display folders
	//		filePane.setViewportView(folderList);
	//		folderList.setListData(TaggedFileListHandler.getFolderArray()); // update JList to display changes
			listModel.rebuild(TaggedFileListHandler.getFolderArray());
			selectedFolder = null;
			selectedFile = null; // no file selected
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (selectedFile != null && TaggedFileListHandler.getTlfPath() != null && fileList.getSelectedValue() != null && fileSelectionMode) {
			if (e.getSource() == favoriteBox) {
				selectedFile.setFavorite(favoriteBox.isSelected());
				selectedFile.setEdited(true); // mark file as edited
				resortList(TaggedFileListHandler.sortingCategories.SORT_CAT_FAV); // resort list after edit
			} else if (e.getSource() == requestBox) {
				selectedFile.setRequested(requestBox.isSelected());
				selectedFile.setEdited(true); // mark file as edited
				resortList(TaggedFileListHandler.sortingCategories.SORT_CAT_REQ);
			} else if (e.getSource() == excludeBox) {
				selectedFile.setExcluded(excludeBox.isSelected());
				selectedFile.setEdited(true); // mark file as edited
				resortList(TaggedFileListHandler.sortingCategories.SORT_CAT_EXC);
			}
		} if (selectedFolder != null && TaggedFileListHandler.getTlfPath() != null && fileList.getSelectedValue() != null && !fileSelectionMode) {
			if (e.getSource() == favoriteBox) {
				selectedFolder.setFavorite(favoriteBox.isSelected());
				selectedFolder.setEdited(true); // mark file as edited
				resortList(TaggedFileListHandler.sortingCategories.SORT_CAT_FAV);
			} else if (e.getSource() == requestBox) {
				selectedFolder.setRequested(requestBox.isSelected());
				selectedFolder.setEdited(true); // mark file as edited
				resortList(TaggedFileListHandler.sortingCategories.SORT_CAT_REQ);
			} else if (e.getSource() == excludeBox) {
				selectedFolder.setExcluded(excludeBox.isSelected());
				selectedFolder.setEdited(true); // mark file as edited
				resortList(TaggedFileListHandler.sortingCategories.SORT_CAT_EXC);
			}
		}		
	}
	
	// helper function sorting list after edit
	private void resortList(TaggedFileListHandler.sortingCategories cat) {
		if (TaggedFileListHandler.getCurrentSortingCategory() == cat) { // resort files after editing
			TaggedFileListHandler.sortFiles();
			if (fileSelectionMode) {
				AdvancedFile temp = selectedFile; // deselection takes place after list update, so store selected item in temporary variable
				updateLists();
				fileList.setSelectedValue(temp, true); // reset selected file
			} else {
				MusicFolderFile temp = selectedFolder; // deselection takes place after list update, so store selected item in temporary variable
				updateLists();
				fileList.setSelectedValue(temp, true); // reset selected file
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (TaggedFileListHandler.getTlfPath() != null) {
			if (e.getSource() == fileList && fileList.getSelectedValue() != null) {
				if (fileSelectionMode) {
					selectedFile = fileList.getSelectedValue();
					favoriteBox.setSelected(selectedFile.isFavorite());
					requestBox.setSelected(selectedFile.isRequested());
					excludeBox.setSelected(selectedFile.isExcluded());
					probabilitySpinner.setValue(selectedFile.getProbability());
				} else {
					selectedFolder = (MusicFolderFile) fileList.getSelectedValue();
					favoriteBox.setSelected(selectedFolder.isFavorite());
					requestBox.setSelected(selectedFolder.isRequested());
					excludeBox.setSelected(selectedFolder.isExcluded());
					probabilitySpinner.setValue(selectedFolder.getProbability());
					selectedFile = null;
				}
			} else { // set no file selected
				selectedFile = null;
				favoriteBox.setSelected(false);
				requestBox.setSelected(false);
				excludeBox.setSelected(false);
				probabilitySpinner.setValue(1);
			}
		} else { // clear selection on closing
			selectedFile = null;
			selectedFolder = null;
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (selectedFile != null && TaggedFileListHandler.getTlfPath() != null && fileList.getSelectedValue() != null) {
			if (fileSelectionMode) {
				if (e.getSource() == probabilitySpinner && selectedFile.getProbability() != (int) probabilitySpinner.getValue()) { // check because event sometimes fired despite no actual change
					selectedFile.setProbability((int) probabilitySpinner.getValue());
					selectedFile.setEdited(true); // mark file as edited
					resortList(TaggedFileListHandler.sortingCategories.SORT_CAT_PROB); // resort files after editing
				}
			} else {
				if (e.getSource() == probabilitySpinner && selectedFolder.getProbability() != (int) probabilitySpinner.getValue()) { // check because event sometimes fired despite no actual change
					selectedFolder.setProbability((int) probabilitySpinner.getValue());
					selectedFolder.setEdited(true); // mark file as edited
					resortList(TaggedFileListHandler.sortingCategories.SORT_CAT_PROB); // resort files after editing
				}
			}
		}
	}

	// getters & setters
	public static boolean isFileSelectionMode() {
		return fileSelectionMode;
	}

	public static void setFileSelectionMode(boolean fileSelectionMode) {
		SelectionWindow.fileSelectionMode = fileSelectionMode;
	}

	// mouse events
	@Override
	public void mouseClicked(MouseEvent me) {
		if (me.getSource() == fileList && me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() > 1) { // to file list with double click on folder
			if (TaggedFileListHandler.getTlfPath() != null) { // only allow switching if a file is opened
				if (!isFileSelectionMode()){
					setFileSelectionMode(true);
					updateLists();
					fileList.clearSelection();
				}
			}
		} else if (me.getSource() == fileList && me.getButton() == MouseEvent.BUTTON3) { // back to folders with right mouse click
			if (TaggedFileListHandler.getTlfPath() != null) { // only allow switching if a file is opened
				if (isFileSelectionMode()){
					setFileSelectionMode(false);
					updateLists();
					fileList.clearSelection();
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
