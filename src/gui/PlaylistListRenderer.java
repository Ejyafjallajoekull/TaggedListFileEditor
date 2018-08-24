package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import functionality.PlaylistFile;

public class PlaylistListRenderer extends JLabel implements ListCellRenderer<PlaylistFile> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Border border;
	
	public PlaylistListRenderer() {
		setOpaque(true);
		this.border = BorderFactory.createLineBorder(Color.BLUE, 1);
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Component getListCellRendererComponent(JList<? extends PlaylistFile> list, PlaylistFile value, int index, boolean isSelected, boolean cellHasFocus) {
		String fileSize = ""; // the file size as string
	    if (isSelected) {
	    	this.setBackground(list.getSelectionBackground());
	    	this.setForeground(list.getSelectionForeground());
	    } else {
	    	this.setBackground(list.getBackground());
	    	this.setForeground(list.getForeground());
	    }
	    setFont(list.getFont());

		if (value.isRequested()) { // add file size to name if requested
			try {
				fileSize = "   [" + Files.size(value.toPath())/1024 + "kB]";
			} catch (IOException e) {
				System.out.println("Die Datei kann nicht gelesen werden.");
				e.printStackTrace();
			}
		}
		if (value.isRelativePlaylist()) {
			this.setText("   " + value.getName() + fileSize);
		} else {
			this.setText("   " + value.getAbsolutePath() + fileSize);
		}

		if (value.isEdited()) {
			this.setForeground(Color.MAGENTA);
		}
		if (value.isExcluded()) {
			Map attributes = list.getFont().getAttributes();
			attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
			this.setFont(list.getFont().deriveFont(attributes));
		}
	    setEnabled(list.isEnabled());
	    if (isSelected && cellHasFocus) {
	    	this.setBorder(border);
	    } else {
	    	this.setBorder(null);
	    }
	    return this;
	}



}
