package org.mron.twitch.ui.settings.general;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.mron.twitch.controller.Game;
import org.mron.twitch.controller.Settings;
import org.mron.twitch.ui.settings.SettingPanel;
import org.mron.twitch.util.IOHandler;
import org.mron.twitch.util.Util;
import org.mron.twitch.util.impl.MyComboBoxRenderer;

public class GeneralSettings implements SettingPanel {

	private JPanel panel;

	private JButton applyButton, cancelButton, restoreButton;

	private JComboBox<String> gameComboBox;

	public static JPanel get() {
		return new GeneralSettings().getPanel();
	}

	@Override
	public JPanel getPanel() {
		panel = new JPanel(new BorderLayout());
		panel.add(getHeaderPanel(), BorderLayout.NORTH);
		panel.add(getContentPanel(), BorderLayout.CENTER);
		panel.add(getButtonPanel(), BorderLayout.SOUTH);

		return panel;
	}

	@Override
	public JPanel getHeaderPanel() {
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.add(new JLabel("<html><font size=\"20\">General Settings</font></html>", JLabel.CENTER), BorderLayout.NORTH);
		headerPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);

		return headerPanel;
	}

	@Override
	public JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		applyButton = new JButton("Apply");
		applyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				save();
			}

		});

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// save();
			}

		});

		restoreButton = new JButton("Restore Defaults");
		restoreButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				reset();
			}

		});

		buttonPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		buttonPanel.add(applyButton);
		buttonPanel.add(cancelButton);
		buttonPanel.add(restoreButton);

		return buttonPanel;
	}

	@Override
	public JPanel getContentPanel() {
		JPanel contentPanel = new JPanel(new BorderLayout());

		JPanel t = new JPanel(new BorderLayout());
		JLabel label = new JLabel("<html>welcome to ur settings =)</html>", JLabel.CENTER);

		t.add(label, BorderLayout.NORTH);
		t.add(new JSeparator(), BorderLayout.SOUTH);

		String[] list = new String[Util.getInstance().getGames().size()];
		for (int i = 0; i < list.length; i++) {
			list[i] = Util.getInstance().getGames().get(i).getName();
		}
		gameComboBox = new JComboBox<String>(list);
		gameComboBox.setRenderer(new MyComboBoxRenderer(1));
		gameComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean test = !((String) gameComboBox.getSelectedItem()).equals(Settings.getInstance().getPreferredGame());
				Settings.getInstance().setUnsavedSettings(test);
			}

		});

		gameComboBox.addItem("N/A");

		for (Game game : Util.getInstance().getGames()) {
			gameComboBox.addItem(game.getName());
		}
		gameComboBox.setSelectedItem(Settings.getInstance().getPreferredGame());

		JPanel tt = new JPanel(new FlowLayout(FlowLayout.CENTER));
		tt.add(new JLabel("Preferred game:"));
		tt.add(gameComboBox);
		tt.add(new JLabel("Preferred quality:"));
		tt.add(new JComboBox<String>(new String[] {
		"Best", "Low", "Medium", "High"
		}));

		contentPanel.add(t, BorderLayout.NORTH);
		contentPanel.add(tt, BorderLayout.CENTER);

		return contentPanel;
	}

	public JComboBox<String> getGameComboBox() {
		return gameComboBox;
	}

	@Override
	public void save() {
		Settings.getInstance().setPreferredGame((String) gameComboBox.getSelectedItem());
		IOHandler.getInstance().saveSettings();
		Settings.getInstance().setUnsavedSettings(false);
	}

	@Override
	public void reset() {
		int confirmation = JOptionPane.showConfirmDialog(panel, "This will reset all general settings to default.\n\nWould you like to continue?", "Restore Defaults", JOptionPane.YES_NO_OPTION);
		if (confirmation == 0) {
			Settings.getInstance().setPreferredGame("N/A");
			gameComboBox.setSelectedItem(Settings.getInstance().getPreferredGame());
			IOHandler.getInstance().saveSettings();
		}
	}

}