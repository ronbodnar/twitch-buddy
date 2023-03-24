package org.mron.twitch.ui.tabs.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.mron.twitch.controller.Channel;
import org.mron.twitch.ui.tabs.HomeTab;
import org.mron.twitch.util.Util;
import org.mron.twitch.util.impl.DrawableImageLabel;

public class FavoritePanel {

	private Channel channel;

	private JPanel panel, informationPanel;

	private JLabel status, usernameLabel;

	private JPopupMenu popupMenu;

	private JMenuItem[] menuItems;

	private DrawableImageLabel preview;

	public FavoritePanel(Channel channel) {
		if (channel == null) {
			panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

			informationPanel = new JPanel(new BorderLayout());

			preview = new DrawableImageLabel(0);

			status = new JLabel("", JLabel.CENTER);
		} else {
			this.channel = channel;

			panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

			informationPanel = new JPanel(new BorderLayout());

			popupMenu = new JPopupMenu();

			menuItems = new JMenuItem[] {
			new JMenuItem("Open with browser"), new JMenuItem("-"), new JMenuItem("Remove from home page"), new JMenuItem("-"), new JMenuItem("Cancel")
			};

			for (JMenuItem item : menuItems) {
				JMenuItem tempItem = item;
				if (item.getText().equals("-")) {
					popupMenu.add(new JSeparator());
				} else {
					tempItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							String command = arg0.getActionCommand();
							Channel channel = HomeTab.getInstance().getHomeChannels().get(HomeTab.getInstance().getIndex());
							if (command.equals("Remove from home page")) {
								HomeTab.getInstance().removeChannel(channel, true);
							} else if (command.equals("Open with browser")) {
								Util.getInstance().openBrowser(channel.getUrl());
							}
						}

					});
					popupMenu.add(tempItem);
				}
			}

			preview = new DrawableImageLabel(0);

			status = new JLabel("", JLabel.CENTER);

			usernameLabel = new JLabel("", JLabel.CENTER);
		}
	}

	public JPanel getPanel() {
		if (channel == null) {
			informationPanel.add(status, BorderLayout.CENTER);
			informationPanel.setPreferredSize(new Dimension(130, 30));

			panel.add(preview);
			panel.add(informationPanel);
			panel.setPreferredSize(new Dimension(130, 110));
		} else {
			informationPanel.add(usernameLabel, BorderLayout.NORTH);
			informationPanel.add(status, BorderLayout.CENTER);
			informationPanel.setPreferredSize(new Dimension(130, 35));

			panel.add(preview);
			panel.add(informationPanel);
			panel.setPreferredSize(new Dimension(130, 120));
		}
		return panel;
	}

	public JPanel getInformationPanel() {
		return informationPanel;
	}

	public JLabel getUsernameLabel() {
		return usernameLabel;
	}

	public JLabel getStatus() {
		return status;
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

	public Channel getChannel() {
		return channel;
	}

}