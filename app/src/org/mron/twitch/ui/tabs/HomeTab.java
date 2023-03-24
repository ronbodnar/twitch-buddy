package org.mron.twitch.ui.tabs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.mron.twitch.Constants;
import org.mron.twitch.TwitchBuddy;
import org.mron.twitch.controller.Channel;
import org.mron.twitch.ui.tabs.impl.FavoritePanel;
import org.mron.twitch.util.IOHandler;
import org.mron.twitch.util.ImageConstants;
import org.mron.twitch.util.JSONParser;
import org.mron.twitch.util.impl.DrawableImageLabel;
import org.mron.twitch.util.impl.MouseLinkListener;
import org.mron.twitch.util.impl.PopupTriggerListener;

public class HomeTab {

	private int index;

	private static HomeTab instance;

	private JPanel cardPanel, emptyPanel, favoritesPanel, loadingPanel;

	private JScrollPane favoritesScrollPane;

	private JLabel informationLabel;

	private ArrayList<String> favoritesQueue = new ArrayList<String>();

	private ArrayList<Channel> homeChannels = new ArrayList<Channel>();

	private ArrayList<FavoritePanel> favoritePanels = new ArrayList<FavoritePanel>();

	public JPanel getPanel() {
		cardPanel = new JPanel(new CardLayout());
		cardPanel.add(getEmptyPanel(), "Empty");
		cardPanel.add(getLoadingPanel(), "Loading");
		cardPanel.add(getFavoritesScrollPane(), "Favorites");

		return cardPanel;
	}

	public JScrollPane getFavoritesScrollPane() {
		if (favoritesPanel == null || favoritesScrollPane == null) {
			favoritesPanel = new JPanel(new GridLayout(0, 8, 5, 5));

			favoritesScrollPane = new JScrollPane(favoritesPanel);
			favoritesScrollPane.getVerticalScrollBar().setUnitIncrement(10);
			favoritesScrollPane.setPreferredSize(new Dimension((int) favoritesPanel.getPreferredSize().getWidth() + 30, 500));
		}
		return favoritesScrollPane;
	}

	public JPanel getLoadingPanel() {
		if (loadingPanel == null) {
			loadingPanel = new JPanel(new BorderLayout());

			JLabel label = new JLabel("Loading your favorite channels - please wait", JLabel.CENTER);
			label.setIconTextGap(25);
			label.setFont(TwitchBuddy.getImageConstants().getFont().deriveFont(16));
			label.setIcon(new ImageIcon(TwitchBuddy.getImageConstants().getImage(ImageConstants.AJAX_LOADER)));
			label.setVerticalTextPosition(JLabel.BOTTOM);
			label.setHorizontalTextPosition(JLabel.CENTER);

			loadingPanel.add(label);

		}
		return loadingPanel;
	}

	public JPanel getEmptyPanel() {
		if (informationLabel == null) {
			informationLabel = new JLabel("<html>You can add to your home page by right clicking live channels or in the profile tab.</html>", JLabel.CENTER);
			informationLabel.setFont(TwitchBuddy.getImageConstants().getFont().deriveFont(16f));
		}
		if (emptyPanel == null) {
			emptyPanel = new JPanel(new BorderLayout());
			emptyPanel.add(informationLabel);
		}
		return emptyPanel;
	}

	public void buildChannelList(final boolean parse) {
		if (parse) {
			if (homeChannels.isEmpty() && favoritesQueue.isEmpty()) {
				((CardLayout) cardPanel.getLayout()).show(cardPanel, "Empty");
			} else {
				((CardLayout) cardPanel.getLayout()).show(cardPanel, "Loading");
			}
		}

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				final SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

					@Override
					protected Boolean doInBackground() throws Exception {
						processQueue();

						if (parse) {
							favoritesPanel.removeAll();

							favoritePanels.clear();

							for (int i = 0; i < homeChannels.size(); i++) {
								FavoritePanel p = new FavoritePanel(homeChannels.get(i));
								if (!favoritePanels.contains(p)) {
									favoritePanels.add(p);
								}
							}

							favoritePanels.add(new FavoritePanel(null));
						}

						for (int i = 0; i < favoritePanels.size(); i++) {
							FavoritePanel favoritePanel = favoritePanels.get(i);

							if (favoritePanel.getChannel() == null) { // add button
								favoritePanel.getStatus().setFont(TwitchBuddy.getImageConstants().getFont().deriveFont(10f));
								favoritePanel.getStatus().addMouseListener(new MouseLinkListener(0));
								favoritePanel.getStatus().setForeground(new Color(44, 181, 0));
								favoritePanel.getStatus().setText("Add channel");

								favoritePanel.setPreview(new DrawableImageLabel(0));
								favoritePanel.getPreview().setIcon(new ImageIcon(TwitchBuddy.getImageConstants().getImage(ImageConstants.ADD_CHANNEL_ICON)));

								favoritePanel.getPreview().addMouseListener(new MouseLinkListener(0));

								if (favoritePanel.getPreview().getMouseListeners() != null) {
									for (int a = 0; a < favoritePanel.getPreview().getMouseListeners().length; a++) {
										Object listener = favoritePanel.getPreview().getMouseListeners()[a];
										if (listener instanceof MouseLinkListener) {
											((MouseLinkListener) listener).setIndex(i);
										}
									}
								}

								if (favoritePanel.getStatus().getMouseListeners() != null) {
									for (int a = 0; a < favoritePanel.getStatus().getMouseListeners().length; a++) {
										Object listener = favoritePanel.getStatus().getMouseListeners()[a];
										if (listener instanceof MouseLinkListener) {
											((MouseLinkListener) listener).setIndex(i);
										}
									}
								}

								favoritesPanel.add(favoritePanel.getPanel());
							} else {
								favoritePanel.getStatus().setFont(TwitchBuddy.getImageConstants().getFont().deriveFont(10f));
								favoritePanel.getStatus().setText(favoritePanel.getChannel().isOnline() ? "Playing " + favoritePanel.getChannel().getGame() + "" : "Offline");

								favoritePanel.getUsernameLabel().setFont(TwitchBuddy.getImageConstants().getFont());
								favoritePanel.getUsernameLabel().addMouseListener(new MouseLinkListener(0));
								favoritePanel.getUsernameLabel().setText("<html><a href=''><font color='#238E23'>" + favoritePanel.getChannel().getDisplayName() + "</font></a></html>");

								try {
									favoritePanel.setPreview(new DrawableImageLabel(0));
									if (favoritePanel.getChannel().getLogo() != null) {
										BufferedImage image = ImageIO.read(new URL(favoritePanel.getChannel().getLogo()));
										image = resize(image, 75, 75);

										favoritePanel.getPreview().setIcon(new ImageIcon(image));
									} else {
										BufferedImage image = TwitchBuddy.getImageConstants().getImage(ImageConstants.NULL_CHANNEL_ICON);
										image = resize(image, 75, 75);

										favoritePanel.getPreview().setIcon(new ImageIcon(image));
									}
								} catch (MalformedURLException e) {
									e.printStackTrace();
								}

								favoritePanel.getPreview().addMouseListener(new PopupTriggerListener());
								favoritePanel.getPreview().addMouseListener(new MouseLinkListener(0));

								if (favoritePanel.getUsernameLabel().getMouseListeners() != null) {
									for (int a = 0; a < favoritePanel.getUsernameLabel().getMouseListeners().length; a++) {
										Object listener = favoritePanel.getUsernameLabel().getMouseListeners()[a];
										if (listener instanceof MouseLinkListener) {
											((MouseLinkListener) listener).setIndex(i);
										}
									}
								}

								if (favoritePanel.getPreview().getMouseListeners() != null) {
									for (int a = 0; a < favoritePanel.getPreview().getMouseListeners().length; a++) {
										Object listener = favoritePanel.getPreview().getMouseListeners()[a];
										if (listener instanceof MouseLinkListener) {
											((MouseLinkListener) listener).setIndex(i);
										} else if (listener instanceof PopupTriggerListener) {
											((PopupTriggerListener) listener).setIndex(i);
										}
									}
								}
								favoritesPanel.add(favoritePanel.getPanel());
							}
						}
						favoritesPanel.revalidate();
						favoritesPanel.repaint();

						return true;
					}

					@Override
					protected void done() {
						if (homeChannels.isEmpty() && favoritesQueue.isEmpty()) {
							((CardLayout) cardPanel.getLayout()).show(cardPanel, "Empty");
						} else {
							((CardLayout) cardPanel.getLayout()).show(cardPanel, "Favorites");
						}
					}

				};
				worker.execute();
			}

		});
	}

	public void removeChannel(Channel channel, boolean write) {
		homeChannels.remove(channel);
		if (write) {
			IOHandler.getInstance().writeFavorites();
		}
		for (FavoritePanel p : favoritePanels) {
			if (p.getChannel().getName().equals(channel.getName())) {
				favoritesPanel.remove(p.getPanel());
			}
		}
		if (favoritesPanel.getComponentCount() <= 0 && homeChannels.isEmpty() && favoritesQueue.isEmpty()) {
			((CardLayout) cardPanel.getLayout()).show(cardPanel, "Empty");
		}
		favoritesPanel.revalidate();
		favoritesPanel.repaint();
	}

	public void addChannelToQueue(String name) {
		favoritesQueue.add(name);
	}

	public void processQueue() {
		if (favoritesQueue.isEmpty()) {
			return;
		}
		for (String name : favoritesQueue) {
			addChannel(getChannelForName(name));
		}
		IOHandler.getInstance().writeFavorites();
	}

	public void addFavorite(String name) {
		addFavorite(getChannelForName(name));
	}

	public void addFavorite(Channel channel) {
		if (channel == null) {
			return;
		}
		HomeTab.getInstance().addChannelToQueue(channel.getName());
		HomeTab.getInstance().buildChannelList(true);

		JOptionPane.showMessageDialog(null, channel.getDisplayName() + " has been added to the Home Tab", "TwitchBuddy", JOptionPane.INFORMATION_MESSAGE);
	}

	public void addChannel(Channel channel) {
		addChannel(channel, false);
	}

	public void addChannel(Channel channel, boolean write) {
		boolean found = false;
		if (channel == null) {
			return;
		}
		for (Channel chan : homeChannels) {
			if (chan.getName().equals(channel.getName())) {
				found = true;
			}
		}
		if (!found) {
			homeChannels.add(channel);
			if (write) {
				IOHandler.getInstance().writeFavorites();
			}
		} else {
			if (TwitchBuddy.isDebugMode()) {
				Constants.LOGGER.log(Level.INFO, "Channel " + channel.getDisplayName() + " already exists on home page.");
			}
		}
	}

	public Channel getChannelForName(String name) {
		return JSONParser.getInstance().parseChannel(name);
	}

	public boolean channelExists(String name) {
		for (Channel channel : getHomeChannels()) {
			if (channel.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public BufferedImage resize(BufferedImage image, int width, int height) {
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);

		Graphics2D graphics = (Graphics2D) bufferedImage.createGraphics();
		graphics.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
		graphics.drawImage(image, 0, 0, width, height, null);
		graphics.dispose();

		return bufferedImage;
	}

	public ArrayList<FavoritePanel> getFavoritePanels() {
		return favoritePanels;
	}

	public ArrayList<Channel> getHomeChannels() {
		return homeChannels;
	}

	public static HomeTab getInstance() {
		if (instance == null) {
			instance = new HomeTab();
		}
		return instance;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}