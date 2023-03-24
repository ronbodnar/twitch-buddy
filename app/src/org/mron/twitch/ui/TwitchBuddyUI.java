package org.mron.twitch.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.mron.twitch.TwitchBuddy;
import org.mron.twitch.controller.Game;
import org.mron.twitch.controller.Settings;
import org.mron.twitch.ui.tabs.HighlightsTab;
import org.mron.twitch.ui.tabs.HomeTab;
import org.mron.twitch.ui.tabs.LiveChannelsTab;
import org.mron.twitch.ui.tabs.ProfileTab;
import org.mron.twitch.util.ImageConstants;
import org.mron.twitch.util.Util;
import org.mron.twitch.util.impl.MyComboBoxRenderer;

public class TwitchBuddyUI {

	private JFrame frame;

	private ArrayList<String> games;

	private JTabbedPane tabbedPane;

	private JComboBox<String> streams;

	private JComboBox<String> featuredGames;

	private static TwitchBuddyUI instance;

	private HomeTab homeTab = HomeTab.getInstance();

	private ProfileTab profileTab = ProfileTab.getInstance();

	private HighlightsTab highlightsTab = HighlightsTab.getInstance();

	private LiveChannelsTab liveChannelsTab = LiveChannelsTab.getInstance();

	/**
	 * rename the following variables:
	 */
	private JPanel gameInputPanel, gameInputComponentPanel, resultPanel;

	/**
	 * end of filter
	 */

	public void constructFrame() {
		frame = new JFrame("TwitchBuddy");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());
		frame.setJMenuBar(MenuBar.getInstance().getJMenuBar());
		frame.add(getPanel(), BorderLayout.NORTH);
		frame.setIconImage(TwitchBuddy.getImageConstants().getImage(ImageConstants.FAVICON));
		frame.pack();

		Util.getInstance().centerWindow(frame);

		frame.setVisible(true);
	}

	public JPanel getPanel() {
		compileGameList();

		gameInputPanel = new JPanel(new BorderLayout());

		String[] list = new String[games.size()];
		for (int i = 0; i < list.length; i++) {
			list[i] = games.get(i);
		}
		featuredGames = new JComboBox<String>(list);

		boolean found = false;
		String preferredGame = Settings.getInstance().getPreferredGame();
		for (String game : list) {
			if (game.contains(preferredGame)) {
				found = true;
				featuredGames.setSelectedItem(game);
			}
		}
		if (!found && !preferredGame.equals("N/A")) {
			featuredGames.addItem(preferredGame);
		}

		featuredGames.setRenderer(new MyComboBoxRenderer(0));
		featuredGames.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						tabbedPane.setSelectedIndex(1); // sets to Live Channels tab
						getHighlightsTab().buildVideoList(true);
						getLiveChannelsTab().buildChannelList(true);
					}

				});
			}

		});

		gameInputComponentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		gameInputComponentPanel.add(new JLabel("Select a game:"));
		gameInputComponentPanel.add(featuredGames);

		resultPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		resultPanel.add(getTabbedPane());

		gameInputPanel.add(gameInputComponentPanel, BorderLayout.NORTH);
		gameInputPanel.add(resultPanel, BorderLayout.SOUTH);

		return gameInputPanel;
	}

	public JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.addTab("Home", getHomeTab().getPanel());
			tabbedPane.addTab("Live Channels", getLiveChannelsTab().getPanel());
			tabbedPane.addTab("Highlights", getHighlightsTab().getPanel());
			tabbedPane.addTab("Profile", getSearchUserTab().getPanel());
			tabbedPane.setPreferredSize(new Dimension(1200, 550));
		}
		return tabbedPane;
	}

	public void compileGameList() {
		games = new ArrayList<String>();
		for (Game game : Util.getInstance().getGames()) {
			games.add(game.getName() + " (" + NumberFormat.getInstance().format(game.getViewerCount()) + " viewers)");
		}
	}

	public void displayError(String title, String error) {
		JOptionPane.showOptionDialog(frame, error, title, JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[] {
			"Continue"
		}, "Continue");
	}

	public JFrame getFrame() {
		return frame;
	}

	public JTabbedPane getTabbedPaneI() {
		return tabbedPane;
	}

	public HomeTab getHomeTab() {
		return homeTab;
	}

	public ProfileTab getSearchUserTab() {
		return profileTab;
	}

	public HighlightsTab getHighlightsTab() {
		return highlightsTab;
	}

	public LiveChannelsTab getLiveChannelsTab() {
		return liveChannelsTab;
	}

	public JComboBox<String> getFeaturedGames() {
		return featuredGames;
	}

	public JComboBox<String> getStreams() {
		return streams;
	}

	public static TwitchBuddyUI getInstance() {
		if (instance == null) {
			instance = new TwitchBuddyUI();
		}
		return instance;
	}

}