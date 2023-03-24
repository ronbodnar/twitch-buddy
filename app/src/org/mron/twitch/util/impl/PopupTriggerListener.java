package org.mron.twitch.util.impl;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTabbedPane;

import org.mron.twitch.ui.TwitchBuddyUI;
import org.mron.twitch.ui.tabs.HighlightsTab;
import org.mron.twitch.ui.tabs.HomeTab;
import org.mron.twitch.ui.tabs.LiveChannelsTab;
import org.mron.twitch.ui.tabs.ProfileTab;
import org.mron.twitch.ui.tabs.impl.ProfileTabs;

public class PopupTriggerListener extends MouseAdapter {

	private int pre = -1, index;

	private JTabbedPane pane = TwitchBuddyUI.getInstance().getTabbedPaneI();

	private JTabbedPane pane2 = ProfileTab.getInstance().getProfileTabs();

	@Override
	public void mousePressed(MouseEvent ev) {
	}

	@Override
	public void mouseReleased(MouseEvent ev) {
		if (pre == -1) {
			pre = index;
		}
		if (ev.isPopupTrigger()) {
			if (pane.getSelectedIndex() == 3) { // user
				if (pane2.getSelectedIndex() == 0) { // highlights
					ProfileTab.getInstance().setIndex(index);
					ProfileTabs.getInstance().getHighlightPanels().get(index).getPopupMenu().show(ev.getComponent(), ev.getX(), ev.getY());
				} else if (pane2.getSelectedIndex() == 1) { // past broadcasts
					ProfileTab.getInstance().setIndex(index);
					ProfileTabs.getInstance().getPastBroadcastPanels().get(index).getPopupMenu().show(ev.getComponent(), ev.getX(), ev.getY());
				}
			} else {
				if (pane.getSelectedIndex() == 0) { // home
					HomeTab.getInstance().setIndex(index);
					HomeTab.getInstance().getFavoritePanels().get(index).getPopupMenu().show(ev.getComponent(), ev.getX(), ev.getY());
				} else if (pane.getSelectedIndex() == 1) { // live channels
					LiveChannelsTab.getInstance().setIndex(index);
					LiveChannelsTab.getInstance().getChannelPanels().get(index).getPopupMenu().show(ev.getComponent(), ev.getX(), ev.getY());
				} else if (pane.getSelectedIndex() == 2) { // highlights
					HighlightsTab.getInstance().setIndex(index);
					HighlightsTab.getInstance().getVideoPanels().get(index).getPopupMenu().show(ev.getComponent(), ev.getX(), ev.getY());
				}
			}
		}
		pre = index;
	}

	@Override
	public void mouseClicked(MouseEvent ev) {
	}

	public void setIndex(int index) {
		this.index = index;
	}

}