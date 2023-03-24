package org.mron.twitch.ui.tabs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.mron.twitch.Constants;
import org.mron.twitch.TwitchBuddy;
import org.mron.twitch.controller.Video;
import org.mron.twitch.ui.TwitchBuddyUI;
import org.mron.twitch.ui.tabs.impl.VideoPanel;
import org.mron.twitch.util.ImageConstants;
import org.mron.twitch.util.JSONParser;
import org.mron.twitch.util.JSONParser.JsonElements;
import org.mron.twitch.util.impl.DrawableImageLabel;
import org.mron.twitch.util.impl.MouseLinkListener;
import org.mron.twitch.util.impl.PopupTriggerListener;

public class HighlightsTab {

	private int index;

	private JLabel loadingLabel;

	private JPanel cardPanel, videoPanel, loadingPanel;

	private JScrollPane videoScrollPane;

	private ArrayList<Video> videos = new ArrayList<Video>();

	private ArrayList<VideoPanel> videoPanels = new ArrayList<VideoPanel>();

	private static HighlightsTab instance;

	public static HighlightsTab getInstance() {
		if (instance == null) {
			instance = new HighlightsTab();
		}
		return instance;
	}

	public JPanel getPanel() {
		cardPanel = new JPanel(new CardLayout() {

			private static final long serialVersionUID = 1L;

			@Override
			public void show(Container parent, String name) {
				if (name.equals("Loading")) {
					loadingLabel.setText("Loading top highlights for " + ((String) TwitchBuddyUI.getInstance().getFeaturedGames().getSelectedItem()).replaceAll("\\(.*?\\)", "") + " - please wait");
				}
				super.show(parent, name);
			}

		});
		cardPanel.add(getLoadingPanel(), "Loading");
		cardPanel.add(getVideoScrollPane(), "Highlights");

		return cardPanel;
	}

	public JScrollPane getVideoScrollPane() {
		if (videoPanel == null || videoScrollPane == null) {
			videoPanel = new JPanel(new GridLayout(0, 4, 5, 5));

			videoScrollPane = new JScrollPane(videoPanel);
			videoScrollPane.getVerticalScrollBar().setUnitIncrement(10);
			videoScrollPane.setPreferredSize(new Dimension((int) videoPanel.getPreferredSize().getWidth() + 30, 500));
		}
		return videoScrollPane;
	}

	public JPanel getLoadingPanel() {
		if (loadingLabel == null) {
			loadingLabel = new JLabel("", JLabel.CENTER);
			loadingLabel.setIconTextGap(25);
			loadingLabel.setFont(TwitchBuddy.getImageConstants().getFont().deriveFont(16));
			loadingLabel.setIcon(new ImageIcon(TwitchBuddy.getImageConstants().getImage(ImageConstants.AJAX_LOADER)));
			loadingLabel.setVerticalTextPosition(JLabel.BOTTOM);
			loadingLabel.setHorizontalTextPosition(JLabel.CENTER);
		}
		if (loadingPanel == null) {
			loadingPanel = new JPanel(new BorderLayout());
			loadingPanel.add(loadingLabel);
		}
		return loadingPanel;
	}

	public void buildVideoList(final boolean refresh) {
		((CardLayout) cardPanel.getLayout()).show(cardPanel, "Loading");

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				final SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

					@Override
					protected Boolean doInBackground() throws Exception {
						if (refresh) {
							videoPanel.removeAll();

							videos.clear();

							videoPanels.clear();

							JSONParser.getInstance().parseJSONData(JsonElements.FEATURED_VIDEOS);
						}

						for (int i = 0; i < videos.size(); i++) {
							videoPanels.add(new VideoPanel(videos.get(i)));
						}

						for (int i = 0; i < videos.size(); i++) {
							if (videos.get(i) == null) {
								continue;
							}
							VideoPanel tempVideoPanel = videoPanels.get(i);

							String truncatedTitle = TwitchBuddy.getImageConstants().truncate(240, tempVideoPanel.getVideo().getTitle(), tempVideoPanel.getTitle().getFontMetrics(tempVideoPanel.getTitle().getFont()));

							String truncatedDescription = TwitchBuddy.getImageConstants().truncate(240, tempVideoPanel.getVideo().getDescription(), tempVideoPanel.getTitle().getFontMetrics(tempVideoPanel.getDescription().getFont()));

							tempVideoPanel.getTitle().setToolTipText(tempVideoPanel.getVideo().getTitle());
							tempVideoPanel.getTitle().setText("<html>" + truncatedTitle + "</html>");

							tempVideoPanel.getDescription().setToolTipText(tempVideoPanel.getVideo().getDescription());
							tempVideoPanel.getDescription().setText("<html>" + truncatedDescription + "</html>");

							tempVideoPanel.getInformation().setFont(TwitchBuddy.getImageConstants().getFont());
							tempVideoPanel.getInformation().setText("<html>By <a href='http://twitch.tv/" + tempVideoPanel.getVideo().getDisplayName() + "'><font color='#238E23'>" + tempVideoPanel.getVideo().getDisplayName() + "</font></a> " + tempVideoPanel.getVideo().getTimeAgo());
							tempVideoPanel.getInformation().addMouseListener(new MouseLinkListener(2));

							if (tempVideoPanel.getInformation().getMouseListeners() != null) {
								for (int a = 0; a < tempVideoPanel.getInformation().getMouseListeners().length; a++) {
									Object listener = tempVideoPanel.getInformation().getMouseListeners()[a];
									if (listener instanceof MouseLinkListener) {
										((MouseLinkListener) listener).setIndex(i);
									}
								}
							}
							BufferedImage image = null;
							try {
								image = ImageIO.read(new URL(tempVideoPanel.getVideo().getPreview()));
								image = resize(image, 250, 140);
							} catch (IOException e) {
								// TODO: default image
								image = ImageIO.read(new URL("http://www.mobafire.com/images/champion/skins/landscape/lee-sin-muay-thai.jpg"));
								image = resize(image, 250, 140);
							}

							tempVideoPanel.setPreview(new DrawableImageLabel(2));
							tempVideoPanel.getPreview().setIcon(new ImageIcon(image));
							tempVideoPanel.getPreview().setViews(NumberFormat.getInstance().format(tempVideoPanel.getVideo().getViews()));
							tempVideoPanel.getPreview().setLength((int) tempVideoPanel.getVideo().getTime());
							tempVideoPanel.getPreview().addMouseListener(new PopupTriggerListener());
							tempVideoPanel.getPreview().addMouseListener(new MouseLinkListener(2));
							tempVideoPanel.getPreview().repaint();

							if (tempVideoPanel.getPreview().getMouseListeners() != null) {
								for (int a = 0; a < tempVideoPanel.getPreview().getMouseListeners().length; a++) {
									Object listener = tempVideoPanel.getPreview().getMouseListeners()[a];
									if (listener instanceof MouseLinkListener) {
										((MouseLinkListener) listener).setIndex(i);
									} else if (listener instanceof PopupTriggerListener) {
										((PopupTriggerListener) listener).setIndex(i);
									}
								}
							}
							videoPanel.add(tempVideoPanel.getPanel());
						}
						videoPanel.revalidate();
						videoPanel.repaint();

						return true;
					}

					@Override
					protected void done() {
						((CardLayout) cardPanel.getLayout()).show(cardPanel, "Highlights");
					}

				};
				worker.execute();
			}

		});
	}

	public void addVideo(Video video) {
		if (video == null) {
			if (TwitchBuddy.isDebugMode()) {
				Constants.LOGGER.log(Level.INFO, "Attempting to add an invalid highlight: " + video);
			}
			return;
		}
		videos.add(video);
	}

	public static BufferedImage resize(BufferedImage image, int width, int height) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		Graphics2D g2d = (Graphics2D) bi.createGraphics();
		g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
		g2d.drawImage(image, 0, 0, width, height, null);
		g2d.dispose();
		return bi;
	}

	public ArrayList<Video> getVideos() {
		return videos;
	}

	public ArrayList<VideoPanel> getVideoPanels() {
		return videoPanels;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}