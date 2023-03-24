package org.mron.twitch.ui.settings;

import javax.swing.JPanel;

public interface SettingPanel {

	public JPanel getPanel();

	public JPanel getHeaderPanel();

	public JPanel getContentPanel();

	public JPanel getButtonPanel();

	public void save();

	public void reset();

}