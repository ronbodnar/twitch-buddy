package org.mron.twitch.ui.tabs.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.mron.twitch.TwitchBuddy;
import org.mron.twitch.controller.Video;
import org.mron.twitch.ui.tabs.ProfileTab;
import org.mron.twitch.util.Util;
import org.mron.twitch.util.impl.DrawableImageLabel;

public class SearchVideoPanel {

	private Video video;

	private JPanel panel, informationPanel;

	private JLabel description, information, title;

	private JPopupMenu popupMenu;

	private JMenuItem[] menuItems;

	private DrawableImageLabel preview;

	public SearchVideoPanel(final int tab, Video video) {
		this.video = video;

		panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		informationPanel = new JPanel(new BorderLayout());

		popupMenu = new JPopupMenu();

		switch (tab) {
			case 4: // highlights
				menuItems = new JMenuItem[] {
				new JMenuItem("Open with browser"), new JMenuItem("-"), new JMenuItem("Cancel")
				};
				break;

			case 5: // past broadcasts
				menuItems = new JMenuItem[] {
				new JMenuItem("Open with browser"), new JMenuItem("-"), new JMenuItem("Cancel")
				};
				break;
		}

		for (JMenuItem item : menuItems) {
			if (item.getText().equals("-")) {
				popupMenu.add(new JSeparator());
			} else {
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						String command = arg0.getActionCommand();
						switch (tab) {
							case 4: // highlights
								Video video = ProfileTabs.getInstance().getHighlights().get(ProfileTab.getInstance().getIndex());
								if (command.equals("Open with browser")) {
									Util.getInstance().openBrowser(video.getUrl());
								}
								break;

							case 5: // past broadcasts
								video = ProfileTabs.getInstance().getPastBroadcasts().get(ProfileTab.getInstance().getIndex());
								if (command.equals("Open with browser")) {
									Util.getInstance().openBrowser(video.getUrl());
								}
								break;
						}
					}

				});
				popupMenu.add(item);
			}
		}

		preview = new DrawableImageLabel(tab);

		title = new JLabel("", JLabel.CENTER);
		title.setFont(new Font(TwitchBuddy.getImageConstants().getFont().getFamily(), Font.BOLD, 12));

		description = new JLabel("", JLabel.CENTER);
		description.setFont(new Font(TwitchBuddy.getImageConstants().getFont().getFamily(), Font.ITALIC, 12));

		information = new JLabel("", JLabel.CENTER);
		information.setFont(new Font(TwitchBuddy.getImageConstants().getFont().getFamily(), Font.BOLD, 12));
	}

	public JPanel getPanel() {
		informationPanel.add(title, BorderLayout.NORTH);
		informationPanel.add(description, BorderLayout.CENTER);
		informationPanel.add(information, BorderLayout.SOUTH);
		informationPanel.setPreferredSize(new Dimension(250, 55));

		panel.add(preview);
		panel.add(informationPanel);

		panel.setPreferredSize(new Dimension(260, 205));

		return panel;
	}

	public JPanel getInformationPanel() {
		return informationPanel;
	}

	public JLabel getTitle() {
		return title;
	}

	public JLabel getDescription() {
		return description;
	}

	public JLabel getInformation() {
		return information;
	}

	public DrawableImageLabel getPreview() {
		return preview;
	}

	public void setPreview(DrawableImageLabel preview) {
		this.preview = preview;
	}

	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}

	public JMenuItem[] getMenuItems() {
		return menuItems;
	}

	public JMenuItem getMenuItem(int index) {
		return index > menuItems.length ? null : menuItems[index];
	}

	public Video getVideo() {
		return video;
	}

}