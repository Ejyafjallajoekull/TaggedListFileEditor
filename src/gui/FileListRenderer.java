package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import functionality.AdvancedFile;

public class FileListRenderer extends JLabel implements ListCellRenderer<AdvancedFile> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Border border;
	private boolean fileSelectionMode = false;
	
	public FileListRenderer() {
		this.setOpaque(true);
		border = BorderFactory.createLineBorder(Color.BLUE, 1);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Component getListCellRendererComponent(JList<? extends AdvancedFile> list, AdvancedFile value, int index, boolean isSelected, boolean cellHasFocus) {
		String fileSize = ""; // the file size as string
		String probability = ""; // the probability as string
	    if (isSelected) {
	    	this.setBackground(list.getSelectionBackground());
	    	this.setForeground(list.getSelectionForeground());
	    } else {
	    	this.setBackground(list.getBackground());
	    	this.setForeground(list.getForeground());
	    }
	    setFont(list.getFont());
		if (value.getProbability() < 10) { // add space depending on the index count
			probability = "[  " + value.getProbability() + "  ]   ";
		} else if (value.getProbability() < 100) {
			probability = "[ " + value.getProbability() + " ]   ";
		} else {
			probability = "[" + value.getProbability() + "]   ";
		}
		if (value.isFavorite()) { // paint a star icon before the text if favorite
			ImageIcon favoriteStar = new ImageIcon(getClass().getResource("/data/yellow-star-icon-5437.png"));
			favoriteStar = new ImageIcon(favoriteStar.getImage().getScaledInstance(list.getFont().getSize(), list.getFont().getSize(), Image.SCALE_SMOOTH));
			this.setIcon(favoriteStar);
		} else { // paint a gap if not a favorite
			ImageIcon emptyIcon = new ImageIcon(getClass().getResource("/data/transparent_256.png"));
			emptyIcon = new ImageIcon(emptyIcon.getImage().getScaledInstance(list.getFont().getSize(), list.getFont().getSize(), Image.SCALE_SMOOTH));
			this.setIcon(emptyIcon);
		}
		if (value.isRequested()) { // add file size to name if requested
			try {
				fileSize = "   [" + Files.size(value.toPath())/1024 + "kB]";
			} catch (IOException e) {
				System.out.println("Die Datei kann nicht gelesen werden.");
				e.printStackTrace();
			}
		}
		if (this.fileSelectionMode) { // make name displaying dependent on file selection mode
			this.setText(probability + value.getTopFolder() + "\\" + value.getName() + fileSize);
		} else {
			this.setText(probability + value.getName() + fileSize);
		}
		if (value.isEdited()) {
			this.setForeground(Color.MAGENTA);
		}
		if (value.isExcluded()) {
			Map attributes = list.getFont().getAttributes();
			attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
			this.setFont(list.getFont().deriveFont(attributes));
		}
	    this.setEnabled(list.isEnabled());
	    if (isSelected && cellHasFocus) {
	    	this.setBorder(border);
	    } else {
	    	this.setBorder(null);
	    }
	    return this;
	}

	public void setFileSelectionMode(boolean fileSelectionMode) {
		this.fileSelectionMode = fileSelectionMode;
	}

}
