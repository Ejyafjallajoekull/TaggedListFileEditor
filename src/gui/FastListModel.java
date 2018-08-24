package gui;

import javax.swing.DefaultListModel;

import functionality.AdvancedFile;

public class FastListModel extends DefaultListModel<AdvancedFile> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private boolean listenersEnabled = true;

    public boolean getListenersEnabled() {
    	return listenersEnabled;
    }

    public void setListenersEnabled(boolean enabled) {
    	listenersEnabled = enabled;
    }

    @Override
    public void fireContentsChanged(Object source, int index0, int index1) {
    	if (getListenersEnabled()) {
    		super.fireContentsChanged(source, index0, index1);
		}
    }
    
    @Override
    public void fireIntervalAdded(Object source, int index0, int index1) {
    	if (getListenersEnabled()) {
			super.fireIntervalAdded(source, index0, index1);
		}
    }

    @Override
    public void fireIntervalRemoved(Object source, int index0, int index1) {
    	if (getListenersEnabled()) {
    		super.fireIntervalAdded(source, index0, index1);
    	}
    }

	public void rebuild(AdvancedFile[] array) {
		if (array != null) {
			listenersEnabled = false;
			this.clear();
			for (AdvancedFile file : array) {
				this.addElement(file);
			}
			listenersEnabled = true;
			this.fireContentsChanged(this, 0, this.size() - 1);
		}
	}
}
