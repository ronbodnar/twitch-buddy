package org.mron.twitch.util.impl;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.mron.twitch.Constants;
import org.mron.twitch.controller.Channel;
import org.mron.twitch.ui.TwitchBuddyUI;
import org.mron.twitch.ui.tabs.HighlightsTab;
import org.mron.twitch.ui.tabs.HomeTab;
import org.mron.twitch.ui.tabs.LiveChannelsTab;
import org.mron.twitch.ui.tabs.ProfileTab;
import org.mron.twitch.ui.tabs.impl.ProfileTabs;
import org.mron.twitch.util.JSONParser;
import org.mron.twitch.util.Util;

public class MouseLinkListener implements MouseListener {

	private int index;

	private int tab;

	public MouseLinkListener(int tab) {
		this.tab = tab;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		TwitchBuddyUI.getInstance().getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		TwitchBuddyUI.getInstance().getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		Point point = arg0.getPoint();
		if (arg0.getButton() == MouseEvent.BUTTON1) {
			if (tab == 1) {
				if ((point.getX() >= 223.0 && point.getX() <= 242.0) && (point.getY() >= 115.0 && point.getY() <= 133.0)) {
					Channel channel = LiveChannelsTab.getInstance().getChannels().get(index);

					HomeTab.getInstance().addFavorite(channel);
					return;
				}
			}
			switch (tab) {
				case 0: // home
					if (((JLabel) arg0.getComponent()).getIcon() == null) { // JLabel
						if (index >= HomeTab.getInstance().getHomeChannels().size()) {
							boolean found = false;
							String result = JOptionPane.showInputDialog(null, "Enter a channel name", "Add Channel", JOptionPane.INFORMATION_MESSAGE);
							for (Channel chan : HomeTab.getInstance().getHomeChannels()) {
								if (chan.getName().equals(result)) {
									found = true;
								}
							}
							if (result == null) {
								// cancelled
							} else if (result.length() <= 0 || result.equals("") || !JSONParser.getInstance().channelExists(result)) {
								TwitchBuddyUI.getInstance().displayError("Error", "You have entered an invalid channel name");
							} else if (found) {
								TwitchBuddyUI.getInstance().displayError("Duplicate", "The channel " + result + " already exists on your home page.");
							} else {
								HomeTab.getInstance().addFavorite(result);
							}
						} else {
							ProfileTab.getInstance().parseResults(HomeTab.getInstance().getHomeChannels().get(index).getName());
						}
					} else { // DrawableImageLabel
						if (index >= HomeTab.getInstance().getHomeChannels().size()) {
							boolean found = false;
							String result = JOptionPane.showInputDialog(null, "Enter a channel name", "Add Channel", JOptionPane.INFORMATION_MESSAGE);
							for (Channel chan : HomeTab.getInstance().getHomeChannels()) {
								if (chan.getName().equals(result)) {
									found = true;
								}
							}
							if (result == null) {
								// cancelled
							} else if (result.length() <= 0 || result.equals("") || !JSONParser.getInstance().channelExists(result)) {
								TwitchBuddyUI.getInstance().displayError("Error", "You have entered an invalid channel name");
							} else if (found) {
								TwitchBuddyUI.getInstance().displayError("Duplicate", "The channel " + result + " already exists on your home page.");
							} else {
								HomeTab.getInstance().addFavorite(result);
							}
						} else {
							ProfileTab.getInstance().parseResults(HomeTab.getInstance().getHomeChannels().get(index).getName());
						}
					}
					break;

				case 1: // live
					if (((JLabel) arg0.getComponent()).getIcon() == null) { // JLabel
						ProfileTab.getInstance().parseResults(LiveChannelsTab.getInstance().getChannels().get(index).getName());
					} else { // DrawableImageLabel
						Util.getInstance().openBrowser(LiveChannelsTab.getInstance().getChannels().get(index).getUrl());
					}
					break;

				case 2: // video
					if (((JLabel) arg0.getComponent()).getIcon() == null) { // JLabel
						ProfileTab.getInstance().parseResults(HighlightsTab.getInstance().getVideos().get(index).getName());
					} else { // DrawableImageLabel
						Util.getInstance().openBrowser(HighlightsTab.getInstance().getVideos().get(index).getUrl());
					}
					break;

				case 3: // clear history
					File file = new File(Constants.RECENT_SEARCHES_PATH);
					try {
						Desktop.getDesktop().edit(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;

				case 4: // user highlights
					Util.getInstance().openBrowser("http://twitch.tv/" + ProfileTabs.getInstance().getHighlightPanels().get(index).getVideo().getUrl() + "/");
					break;

				case 5: // user broadcasts
					Util.getInstance().openBrowser("http://twitch.tv/" + ProfileTabs.getInstance().getPastBroadcastPanels().get(index).getVideo().getUrl() + "/");
					break;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void setIndex(int index) {
		this.index = index;
	}

}