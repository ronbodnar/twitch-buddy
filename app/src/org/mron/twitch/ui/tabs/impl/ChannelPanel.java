package org.mron.twitch.ui.tabs.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.mron.twitch.TwitchBuddy;
import org.mron.twitch.controller.Channel;
import org.mron.twitch.ui.tabs.HomeTab;
import org.mron.twitch.ui.tabs.LiveChannelsTab;
import org.mron.twitch.util.ImageConstants;
import org.mron.twitch.util.Util;
import org.mron.twitch.util.impl.DrawableImageLabel;

public class ChannelPanel {

	private Channel channel;

	private JPanel box, panel, informationPanel;

	private JLabel title, username, information;

	private JPopupMenu popupMenu;

	private JMenuItem[] menuItems;

	private DrawableImageLabel preview;

	public ChannelPanel(Channel channel) {
		this.channel = channel;

		panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		informationPanel = new JPanel(new BorderLayout());

		popupMenu = new JPopupMenu();

		menuItems = new JMenuItem[] {
		new JMenuItem("Copy link address"), new JMenuItem("Open with browser"), new JMenuItem("-"), new JMenuItem("Add " + channel.getDisplayName() + " to home page"), new JMenuItem("-"), new JMenuItem("Cancel")
		};

		for (JMenuItem item : menuItems) {
			if (item.getText().equals("-")) {
				popupMenu.add(new JSeparator());
			} else {
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						String command = arg0.getActionCommand();
						Channel channel = LiveChannelsTab.getInstance().getChannels().get(LiveChannelsTab.getInstance().getIndex());
						if (command.endsWith("to home page")) {
							HomeTab.getInstance().addFavorite(channel);
						} else if (command.equals("Copy link address")) {
							StringSelection selection = new StringSelection(channel.getUrl());
							Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
							clipboard.setContents(selection, selection);
						} else if (command.equals("Open with browser")) {
							Util.getInstance().openBrowser(channel.getUrl());
						}
					}

				});
				popupMenu.add(item);
			}
		}

		preview = new DrawableImageLabel(1);

		title = new JLabel("", JLabel.CENTER);
		title.setFont(new Font(TwitchBuddy.getImageConstants().getFont().getFamily(), Font.BOLD, 12));

		username = new JLabel("", JLabel.RIGHT);
		username.setFont(new Font(TwitchBuddy.getImageConstants().getFont().getFamily(), Font.PLAIN, 12));

		information = new JLabel("", JLabel.LEFT);
		information.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		information.setFont(new Font(TwitchBuddy.getImageConstants().getFont().getFamily(), Font.PLAIN, 12));
	}

	public JPanel getPanel() {
		box = new JPanel(new BorderLayout()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D graphics = (Graphics2D) g.create();

				if (!HomeTab.getInstance().getHomeChannels().contains(channel)) {
					graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
					graphics.drawImage(TwitchBuddy.getImageConstants().getImage(ImageConstants.VIEWER_ICON), 3, 0, null);
				}
			}

		};
		box.add(information, BorderLayout.WEST);
		box.add(username, BorderLayout.EAST);

		informationPanel.add(title, BorderLayout.NORTH);
		informationPanel.add(box, BorderLayout.SOUTH);
		informationPanel.setPreferredSize(new Dimension(260, 40));

		panel.add(preview);
		panel.add(informationPanel);
		panel.setPreferredSize(new Dimension(260, 200));

		return panel;
	}

	public JPanel getInformationPanel() {
		return informationPanel;
	}

	public JLabel getTitle() {
		return title;
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

	public Channel getChannel() {
		return channel;
	}

	public JLabel getUsername() {
		return username;
	}

	public void setUsername(JLabel username) {
		this.username = username;
	}

}