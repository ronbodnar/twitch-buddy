package org.mron.twitch.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.mron.twitch.controller.Settings;
import org.mron.twitch.ui.settings.SettingsUI;
import org.mron.twitch.util.IOHandler;

public class MenuBar implements ActionListener {

	private JMenuBar menuBar;

	private static MenuBar instance;

	public static MenuBar getInstance() {
		if (instance == null) {
			instance = new MenuBar();
		}
		return instance;
	}

	private Object[][] menuData = new Object[][] {
	{
	"File", new String[] {
		"Exit"
	}
	}, {
	"Settings", new String[] {
	"Settings", "-", "[c]Always On Top"
	}
	}, {
	"Help", new String[] {
		"About TwitchBuddy"
	}
	}
	};

	public JMenuBar getJMenuBar() {
		menuBar = new JMenuBar();
		for (Object[] data : menuData) {
			JMenu menu = new JMenu((String) data[0]);
			menuBar.add(menu);

			String[] menuContents = (String[]) data[1];
			for (String e : menuContents) {
				if (e.equals("-")) {
					menu.addSeparator();
					continue;
				}
				if (e.startsWith("[c]")) {
					JCheckBoxMenuItem checkBox = new JCheckBoxMenuItem(e.replaceAll("\\[c\\]", ""));
					checkBox.addActionListener(this);
					checkBox.setSelected(Settings.getInstance().isAlwaysOnTop());
					menu.add(checkBox);
				} else {
					JMenuItem menuItem = new JMenuItem(e);
					menuItem.addActionListener(this);
					menu.add(menuItem);
				}
			}
		}

		return menuBar;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		String command = arg0.getActionCommand();
		if (command.equals("Exit")) {
			System.exit(0);
		} else if (command.equals("Settings")) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					SettingsUI.getInstance().constructFrame();
				}

			});
		} else if (command.equals("Always On Top")) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					Settings.getInstance().setAlwaysOnTop(!Settings.getInstance().isAlwaysOnTop());
					IOHandler.getInstance().saveSettings();
					TwitchBuddyUI.getInstance().getFrame().setAlwaysOnTop(Settings.getInstance().isAlwaysOnTop());
				}

			});
		} else {
			System.out.println("Unrecognized action: " + command);
		}
	}

}