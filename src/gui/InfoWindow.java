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
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import functionality.TaggedFileListHandler;

public class InfoWindow extends JDialog implements ActionListener{
// show version information
	
	// serialization
	private static final long serialVersionUID = 1L;
	
	// constants
	private final String TITLE = "Information";
	private final String EDITORVERSION = "TLFEditor Version";
	private final String TLFVERSION = "Tagged List File (.tlf) Version";
	private final String OK = "Genug Information";
	
	// variables
	JButton okButton = new JButton(this.OK);
	
	// constructor
	public InfoWindow() {
		this.setTitle(TITLE);
		this.setLayout(new BorderLayout());
		
		// editor panel
		JPanel editorPanel = new JPanel();
		editorPanel.setLayout(new GridBagLayout());
		editorPanel.add(new JLabel(this.EDITORVERSION + " " + MainWindow.getTlfEditorVersion()));
		
		// tlf panel
		JPanel tlfPanel = new JPanel();
		tlfPanel.setLayout(new GridBagLayout());
		tlfPanel.add(new JLabel(this.TLFVERSION + " " + Arrays.toString(TaggedFileListHandler.getTlfVersion())));
		
		// ok panel
		JPanel okPanel = new JPanel();
		okPanel.setLayout(new GridBagLayout());
		GridBagConstraints okConstraints = new GridBagConstraints();
		okConstraints.insets = new Insets(20, 20, 10, 20);
		okPanel.add(okButton, okConstraints);
		okButton.addActionListener(this);
		
		// version panel
		JPanel versionPanel = new JPanel();
		versionPanel.setLayout(new GridLayout(0, 1, 0, 5));
		versionPanel.add(editorPanel);
		versionPanel.add(tlfPanel);
		this.add(versionPanel, BorderLayout.CENTER);
		this.add(okPanel, BorderLayout.SOUTH);
		this.add(Box.createRigidArea(new Dimension(0, 20)), BorderLayout.NORTH); // space
		this.add(Box.createRigidArea(new Dimension(20, 0)), BorderLayout.WEST); // space
		this.add(Box.createRigidArea(new Dimension(20, 0)), BorderLayout.EAST); // space
		
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == this.okButton) {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)); // close the settings window
		}
	}
}
