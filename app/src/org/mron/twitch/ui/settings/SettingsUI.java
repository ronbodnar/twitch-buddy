package org.mron.twitch.ui.settings;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.mron.twitch.ui.settings.general.GeneralSettings;
import org.mron.twitch.util.Util;
import org.mron.twitch.util.impl.SettingsWindowListener;

public class SettingsUI {

	private JFrame frame;

	private JPanel panel, cardPanel;

	public static SettingsUI instance;

	public void constructFrame() {
		frame = new JFrame("Settings");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new SettingsWindowListener(frame));
		frame.setResizable(false);
		frame.add(getPanel());
		frame.setSize(500, 500);
		Util.getInstance().centerWindow(frame);
		frame.setVisible(true);
	}

	public JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel(new BorderLayout());
			panel.add(SettingsTree.getInstance().getSettingsTree(), BorderLayout.WEST);
			panel.add(getCardPanel(), BorderLayout.CENTER);
		}
		return panel;
	}

	public JPanel getCardPanel() {
		if (cardPanel == null) {
			cardPanel = new JPanel(new CardLayout());
			cardPanel.add(GeneralSettings.get(), "General");
			for (SettingPanels setting : SettingPanels.values()) {
				if (setting == null) {
					continue;
				}
				cardPanel.add(setting.getSettingPanel().getPanel(), setting.getName());
			}
		}
		return cardPanel;
	}

	public JFrame getFrame() {
		return frame;
	}

	public static SettingsUI getInstance() {
		if (instance == null) {
			instance = new SettingsUI();
		}
		return instance;
	}

}