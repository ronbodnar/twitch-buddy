package org.mron.twitch.util.impl;

import java.awt.CardLayout;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.mron.twitch.ui.settings.SettingPanels;
import org.mron.twitch.ui.settings.SettingsUI;

public class JTreeSelectionListener implements TreeSelectionListener {

	@Override
	public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
		Object[] pathArray = treeSelectionEvent.getNewLeadSelectionPath().getPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) pathArray[pathArray.length - 1];
		CardLayout layout = (CardLayout) SettingsUI.getInstance().getCardPanel().getLayout();

		JFrame frame = SettingsUI.getInstance().getFrame();
		WindowListener[] listeners = frame.getWindowListeners();

		SettingsWindowListener listener = null;

		if (node == null) {
			return;
		}
		if (listeners != null) {
			for (int a = 0; a < listeners.length; a++) {
				Object obj = listeners[a];
				if (obj instanceof SettingsWindowListener) {
					listener = (SettingsWindowListener) obj;
				}
			}
		}

		listener.setSettingPanel(SettingPanels.forString(node.toString()));
		layout.show(SettingsUI.getInstance().getCardPanel(), node.toString());
	}

}