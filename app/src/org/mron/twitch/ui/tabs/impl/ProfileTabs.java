package org.mron.twitch.ui.tabs.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.mron.twitch.TwitchBuddy;
import org.mron.twitch.controller.Video;
import org.mron.twitch.ui.tabs.ProfileTab;
import org.mron.twitch.util.JSONParser;
import org.mron.twitch.util.impl.MouseLinkListener;
import org.mron.twitch.util.impl.PopupTriggerListener;

public class ProfileTabs {

	private JPanel highlightsPanel, pastBroadcastsPanel;

	private JScrollPane highlightsPane, pastBroadcastsPane;

	private ArrayList<Video> highlights = new ArrayList<Video>();

	private ArrayList<Video> pastBroadcasts = new ArrayList<Video>();

	private ArrayList<SearchVideoPanel> highlightPanels = new ArrayList<SearchVideoPanel>();

	private ArrayList<SearchVideoPanel> pastBroadcastPanels = new ArrayList<SearchVideoPanel>();

	private static ProfileTabs instance;

	public JScrollPane getHighlightsPanel() {
		if (highlightsPanel == null || highlightsPane == null) {
			highlightsPanel = new JPanel(new GridLayout(0, 4, 5, 5));

			highlightsPane = new JScrollPane(highlightsPanel);
			highlightsPane.getVerticalScrollBar().setUnitIncrement(10);
			highlightsPane.setPreferredSize(new Dimension((int) highlightsPanel.getPreferredSize().getWidth() + 30, 500));
		}
		return highlightsPane;
	}

	public JScrollPane getPastBroadcastsPane() {
		if (pastBroadcastsPanel == null || pastBroadcastsPane == null) {
			pastBroadcastsPanel = new JPanel(new GridLayout(0, 4, 5, 5));

			pastBroadcastsPane = new JScrollPane(pastBroadcastsPanel);
			pastBroadcastsPane.getVerticalScrollBar().setUnitIncrement(10);
			pastBroadcastsPane.setPreferredSize(new Dimension((int) pastBroadcastsPanel.getPreferredSize().getWidth() + 30, 500));
		}
		return pastBroadcastsPane;
	}

	public void buildVideos(final int tab) {
		switch (tab) {
			case 0: // highlights
				highlightsPanel.removeAll();

				highlightPanels.clear();

				JSONParser.getInstance().parseHighlights(ProfileTab.getInstance().getActiveUsername(), false);

				if (highlights.size() <= 0) {
					highlightsPanel.setLayout(new BorderLayout());
					highlightsPanel.add(new JLabel("<html><i>This user has no available highlights.</i</html>", JLabel.CENTER));
					highlightsPanel.revalidate();
					highlightsPanel.repaint();
					return;
				}

				if (highlightsPanel.getLayout() instanceof BorderLayout) {
					highlightsPanel.setLayout(new GridLayout(0, 4, 5, 5));
				}

				for (int i = 0; i < highlights.size(); i++) {
					highlightPanels.add(new SearchVideoPanel(4, highlights.get(i)));
				}

				for (int i = 0; i < highlights.size(); i++) {
					SearchVideoPanel videoPanel = highlightPanels.get(i);

					videoPanel.getTitle().setText(TwitchBuddy.getImageConstants().truncate(240, videoPanel.getVideo().getTitle(), videoPanel.getTitle().getFontMetrics(videoPanel.getTitle().getFont())));

					videoPanel.getDescription().setText(TwitchBuddy.getImageConstants().truncate(240, videoPanel.getVideo().getDescription(), videoPanel.getDescription().getFontMetrics(videoPanel.getDescription().getFont())));

					videoPanel.getInformation().setText(TwitchBuddy.getImageConstants().truncate(240, "Created " + videoPanel.getVideo().getTimeAgo(), videoPanel.getInformation().getFontMetrics(videoPanel.getInformation().getFont())));

					try {
						BufferedImage image = ImageIO.read(new URL(videoPanel.getVideo().getPreview()));
						image = resize(image, 250, 140);

						videoPanel.getPreview().addMouseListener(new MouseLinkListener(4));
						videoPanel.getPreview().addMouseListener(new PopupTriggerListener());
						videoPanel.getPreview().setOffline(false);
						videoPanel.getPreview().setIcon(new ImageIcon(image));
						videoPanel.getPreview().setViews(NumberFormat.getInstance().format(videoPanel.getVideo().getViews()));
						videoPanel.getPreview().setLength((int) videoPanel.getVideo().getTime());
						videoPanel.getPreview().repaint();

						if (videoPanel.getPreview().getMouseListeners() != null) {
							for (int a = 0; a < videoPanel.getPreview().getMouseListeners().length; a++) {
								Object listener = videoPanel.getPreview().getMouseListeners()[a];
								if (listener instanceof MouseLinkListener) {
									((MouseLinkListener) listener).setIndex(i);
								} else if (listener instanceof PopupTriggerListener) {
									((PopupTriggerListener) listener).setIndex(i);
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

					highlightsPanel.add(videoPanel.getPanel());
				}

				highlightsPanel.revalidate();
				highlightsPanel.repaint();
				break;

			case 1: // past broadcasts
				pastBroadcastsPanel.removeAll();

				pastBroadcastPanels.clear();

				JSONParser.getInstance().parseHighlights(ProfileTab.getInstance().getActiveUsername(), true);

				if (pastBroadcasts.size() <= 0) {
					pastBroadcastsPanel.setLayout(new BorderLayout());
					pastBroadcastsPanel.add(new JLabel("<html><i>This user has no available past broadcasts.</i></html>", JLabel.CENTER));
					pastBroadcastsPanel.revalidate();
					pastBroadcastsPanel.repaint();
					return;
				}

				if (pastBroadcastsPanel.getLayout() instanceof BorderLayout) {
					pastBroadcastsPanel.setLayout(new GridLayout(0, 4, 5, 5));
				}

				for (int i = 0; i < pastBroadcasts.size(); i++) {
					pastBroadcastPanels.add(new SearchVideoPanel(5, pastBroadcasts.get(i)));
				}

				for (int i = 0; i < pastBroadcasts.size(); i++) {
					SearchVideoPanel videoPanel = pastBroadcastPanels.get(i);

					videoPanel.getTitle().setText(TwitchBuddy.getImageConstants().truncate(240, videoPanel.getVideo().getTitle(), videoPanel.getTitle().getFontMetrics(videoPanel.getTitle().getFont())));

					videoPanel.getDescription().setText(TwitchBuddy.getImageConstants().truncate(240, videoPanel.getVideo().getDescription(), videoPanel.getDescription().getFontMetrics(videoPanel.getDescription().getFont())));

					videoPanel.getInformation().setText(TwitchBuddy.getImageConstants().truncate(240, "Created " + videoPanel.getVideo().getTimeAgo(), videoPanel.getInformation().getFontMetrics(videoPanel.getInformation().getFont())));

					try {
						BufferedImage image = ImageIO.read(new URL(videoPanel.getVideo().getPreview()));
						image = resize(image, 250, 140);

						videoPanel.getPreview().setOffline(false);
						videoPanel.getPreview().setIcon(new ImageIcon(image));
						videoPanel.getPreview().addMouseListener(new MouseLinkListener(5));
						videoPanel.getPreview().addMouseListener(new PopupTriggerListener());

						if (videoPanel.getPreview().getMouseListeners() != null) {
							for (int a = 0; a < videoPanel.getPreview().getMouseListeners().length; a++) {
								Object listener = videoPanel.getPreview().getMouseListeners()[a];
								if (listener instanceof MouseLinkListener) {
									((MouseLinkListener) listener).setIndex(i);
								} else if (listener instanceof PopupTriggerListener) {
									((PopupTriggerListener) listener).setIndex(i);
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

					pastBroadcastsPanel.add(videoPanel.getPanel());
				}

				pastBroadcastsPanel.revalidate();
				pastBroadcastsPanel.repaint();
				break;
		}
	}

	public BufferedImage resize(BufferedImage image, int width, int height) {
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);

		Graphics2D graphics = (Graphics2D) bufferedImage.createGraphics();
		graphics.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
		graphics.drawImage(image, 0, 0, width, height, null);
		graphics.dispose();

		return bufferedImage;
	}

	public ArrayList<Video> getHighlights() {
		return highlights;
	}

	public ArrayList<Video> getPastBroadcasts() {
		return pastBroadcasts;
	}

	public ArrayList<SearchVideoPanel> getHighlightPanels() {
		return highlightPanels;
	}

	public ArrayList<SearchVideoPanel> getPastBroadcastPanels() {
		return pastBroadcastPanels;
	}

	public static ProfileTabs getInstance() {
		if (instance == null) {
			instance = new ProfileTabs();
		}
		return instance;
	}

}