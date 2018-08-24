package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import functionality.PlaylistFile;
import functionality.TaggedFileListHandler;

public class PlaylistSelectionWindow extends JDialog implements ActionListener, MouseListener{

	// serialization
	private static final long serialVersionUID = 1L;
	
	// constants
	private final String TITLE = "Wiedergabelisten";
	private final String FINISH = "Fertig";
	private final String EDIT = "Bearbeiten";
	private final String CREATE = "Neu";
	private final String DELETE = "Löschen";
	private final String CONFIRMDELETION = "Möchten Sie diese Wiedergabeliste wirklich löschen?";
	private final String GENERATE = "Generieren";
	private final String GENERATEALL = "Alle Generieren";
	private final String CONFIRMGENERATEALL = "Möchten Sie wirklich alle Wiedergabelisten generieren?";
	
	
	// variables
	private JButton finishButton = new JButton(FINISH);
	private JList<PlaylistFile> playlistList = new JList<PlaylistFile>(TaggedFileListHandler.getPlaylistArray());
	private JScrollPane scrollPane = new JScrollPane(playlistList); // main pane
	private JPopupMenu popupMenu = new JPopupMenu(); // popup menu
	private JMenuItem createMenuItem = new JMenuItem(CREATE); // create new playlist
	private JMenuItem editMenuItem = new JMenuItem(EDIT); // edit playlist
	private JMenuItem deleteMenuItem = new JMenuItem(DELETE); // edit playlist
	private JMenuItem generateMenuItem = new JMenuItem(GENERATE); // generate playlist physically
	private JMenuItem generateAllMenuItem = new JMenuItem(GENERATEALL); // generate all playlists physically
	
	
	// constructor
	public PlaylistSelectionWindow() {
		this.setTitle(TITLE);
//		this.setSize(ScreenConverter.getAbsoluteWidth(0.17857143f), ScreenConverter.getAbsoluteHeight(0.47619048f));
		this.setSize(300, 500);
		this.setLocationRelativeTo(null);
		this.setLayout(new BorderLayout());
		playlistList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // only allow single selection	
		playlistList.setCellRenderer(new PlaylistListRenderer());
		this.add(scrollPane, BorderLayout.CENTER);

		// finish panel
		JPanel finishPanel = new JPanel();
		finishPanel.add(finishButton);
		this.add(finishPanel, BorderLayout.SOUTH);
		finishButton.addActionListener(this);
		
		// popup menu
		popupMenu.add(createMenuItem);
		popupMenu.addSeparator();
		popupMenu.add(editMenuItem);
		popupMenu.addSeparator();
		popupMenu.add(generateMenuItem);
		popupMenu.addSeparator();
		popupMenu.add(generateAllMenuItem);
		popupMenu.addSeparator();
		popupMenu.add(deleteMenuItem);

		
		// listener
		playlistList.addMouseListener(this);
		createMenuItem.addActionListener(this);
		editMenuItem.addActionListener(this);
		deleteMenuItem.addActionListener(this);
		generateMenuItem.addActionListener(this);
		generateAllMenuItem.addActionListener(this);
		this.setVisible(true);	
	}

	// action events
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == createMenuItem) {
			new PlaylistModificationWindow(null); // open blank playlist window
			playlistList.setListData(TaggedFileListHandler.getPlaylistArray()); // update JList
		} else if (arg0.getSource() == editMenuItem) {
			new PlaylistModificationWindow(playlistList.getSelectedValue()); // open playlist
			playlistList.setListData(TaggedFileListHandler.getPlaylistArray()); // update JList
		} else if (arg0.getSource() == deleteMenuItem) { // deletion with confirmation
			if (playlistList.getSelectedValue() != null) { // do not try to delete a null
				if (JOptionPane.showConfirmDialog(this, CONFIRMDELETION, DELETE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					TaggedFileListHandler.getPlaylistFiles().remove(playlistList.getSelectedValue()); // open playlist
					playlistList.setListData(TaggedFileListHandler.getPlaylistArray()); // update JList
				}
			}
		} else if (arg0.getSource() == generateMenuItem) {
			if (playlistList.getSelectedValue() != null) { // generate playlist if selected
				playlistList.getSelectedValue().generatePlaylist();
			}
		} else if (arg0.getSource() == generateAllMenuItem) {
			if (JOptionPane.showConfirmDialog(this, CONFIRMGENERATEALL, GENERATEALL, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { // generate playlist if selected
				TaggedFileListHandler.generateAllPlaylists();
			}
		} else if (arg0.getSource() == finishButton) {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)); // close the settings window
		}
		
	}

	// mouse events
	@Override
	public void mouseClicked(MouseEvent me) {
		if ( me.getSource() == playlistList && me.getButton() == MouseEvent.BUTTON3)
	        {
				popupMenu.show(me.getComponent(), me.getX(), me.getY());
	            int row = playlistList.locationToIndex(me.getPoint()); // select nearest list entry to mouse positions
	            playlistList.setSelectedIndex(row);
	        } else if (me.getSource() == playlistList && me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() > 1) {
	        	if (playlistList.getSelectedValue() != null) {
	    			new PlaylistModificationWindow(playlistList.getSelectedValue()); // open playlist
	    			playlistList.setListData(TaggedFileListHandler.getPlaylistArray()); // update JList
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
	public void mousePressed(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


}
