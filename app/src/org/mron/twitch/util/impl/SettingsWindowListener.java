package org.mron.twitch.util.impl;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.mron.twitch.controller.Settings;
import org.mron.twitch.ui.settings.SettingPanel;
import org.mron.twitch.ui.settings.SettingPanels;

public class SettingsWindowListener implements WindowListener {

	private JFrame frame;

	private SettingPanel settingPanel;

	private String[] options = {
	"Save and close", "Close without saving", "Cancel"
	};

	public SettingsWindowListener(JFrame frame) {
		this.frame = frame;
	}

	public void setSettingPanel(SettingPanel settingPanel) {
		this.settingPanel = settingPanel;
	}

	public SettingPanel getSettingPanel() {
		return settingPanel;
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		if (Settings.getInstance().isUnsavedSettings()) {
			int confirmation = JOptionPane.showOptionDialog(null, "You have unsaved settings. What would you like to do?", "", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, options, "default");
			if (confirmation == 0) {
				for (SettingPanels panel : SettingPanels.values()) {
					panel.getSettingPanel().save();
				}
			}
		}
		frame.dispose();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
	}

}