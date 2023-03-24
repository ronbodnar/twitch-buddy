package org.mron.twitch.ui.tabs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.mron.twitch.Constants;
import org.mron.twitch.TwitchBuddy;
import org.mron.twitch.controller.Channel;
import org.mron.twitch.ui.TwitchBuddyUI;
import org.mron.twitch.ui.tabs.impl.ChannelPanel;
import org.mron.twitch.util.ImageConstants;
import org.mron.twitch.util.JSONParser;
import org.mron.twitch.util.JSONParser.JsonElements;
import org.mron.twitch.util.impl.DrawableImageLabel;
import org.mron.twitch.util.impl.MouseLinkListener;
import org.mron.twitch.util.impl.PopupTriggerListener;

public class LiveChannelsTab {

	private int index;

	private JLabel loadingLabel;

	private JScrollPane videoScrollPane;

	private static LiveChannelsTab instance;

	private JPanel cardPanel, videoPanel, loadingPanel;

	private ArrayList<Channel> channels = new ArrayList<Channel>();

	private ArrayList<ChannelPanel> channelPanels = new ArrayList<ChannelPanel>();

	public static LiveChannelsTab getInstance() {
		if (instance == null) {
			instance = new LiveChannelsTab();
		}
		return instance;
	}

	public JPanel getPanel() {
		cardPanel = new JPanel(new CardLayout() {

			private static final long serialVersionUID = 1L;

			@Override
			public void show(Container parent, String name) {
				if (name.equals("Loading")) {
					loadingLabel.setText("Loading top streams for " + ((String) TwitchBuddyUI.getInstance().getFeaturedGames().getSelectedItem()).replaceAll("\\(.*?\\)", "") + " - please wait");
				}
				super.show(parent, name);
			}

		});
		cardPanel.add(getLoadingPanel(), "Loading");
		cardPanel.add(getVideoScrollPane(), "Channels");

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

	public void buildChannelList(final boolean refresh) {
		((CardLayout) cardPanel.getLayout()).show(cardPanel, "Loading");

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				final SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

					@Override
					protected Boolean doInBackground() throws Exception {
						if (refresh) {
							videoPanel.removeAll();

							channels.clear();

							channelPanels.clear();

							JSONParser.getInstance().parseJSONData(JsonElements.FEATURED_CHANNELS);
						}

						for (int i = 0; i < channels.size(); i++) {
							channelPanels.add(new ChannelPanel(channels.get(i)));
						}

						for (int i = 0; i < channels.size(); i++) {
							final ChannelPanel channelPanel = channelPanels.get(i);

							channelPanel.getChannel().setOnline(true);

							String truncatedTitle = TwitchBuddy.getImageConstants().truncate(245, channelPanel.getChannel().getStatus(), channelPanel.getTitle().getFontMetrics(channelPanel.getTitle().getFont()));

							channelPanel.getTitle().setToolTipText(channelPanel.getChannel().getStatus());
							channelPanel.getTitle().setText("<html>" + truncatedTitle + "</html>");

							channelPanel.getInformation().setText("<html>" + NumberFormat.getInstance().format(channelPanel.getChannel().getViewerCount()) + "</html>");

							channelPanel.getUsername().setText("<html><a href=''><font color='#238E23'>" + channelPanel.getChannel().getDisplayName() + "</font></a></html>");
							channelPanel.getUsername().addMouseListener(new MouseLinkListener(1));

							if (channelPanel.getUsername().getMouseListeners() != null) {
								for (int a = 0; a < channelPanel.getUsername().getMouseListeners().length; a++) {
									Object listener = channelPanel.getUsername().getMouseListeners()[a];
									if (listener instanceof MouseLinkListener) {
										((MouseLinkListener) listener).setIndex(i);
									}
								}
							}
							try {
								channelPanel.setPreview(new DrawableImageLabel(1));
								channelPanel.getPreview().setName(channelPanel.getChannel().getName());
								channelPanel.getPreview().setOffline(false);
								channelPanel.getPreview().setIcon(new ImageIcon(new URL("http://static-cdn.jtvnw.net/previews-ttv/live_user_" + channelPanel.getChannel().getName() + "-250x140.jpg")));
								channelPanel.getPreview().addMouseListener(new PopupTriggerListener());
								channelPanel.getPreview().addMouseListener(new MouseLinkListener(1));
								channelPanel.getPreview().addMouseMotionListener(new MouseMotionListener() {

									@Override
									public void mouseDragged(MouseEvent e) {
										// TODO Auto-generated method stub

									}

									@Override
									public void mouseMoved(MouseEvent e) {
										Point point = e.getPoint();
										boolean temp = channelPanel.getPreview().isHovered();
										if ((point.getX() >= 223.0 && point.getX() <= 242.0) && (point.getY() >= 115.0 && point.getY() <= 133.0)) {
											channelPanel.getPreview().setHovered(true);
										} else {
											channelPanel.getPreview().setHovered(false);
										}
										if (temp != channelPanel.getPreview().isHovered()) {
											videoPanel.revalidate();
											videoPanel.repaint();
										}
									}

								});

								if (channelPanel.getPreview().getMouseListeners() != null) {
									for (int a = 0; a < channelPanel.getPreview().getMouseListeners().length; a++) {
										Object listener = channelPanel.getPreview().getMouseListeners()[a];
										if (listener instanceof MouseLinkListener) {
											((MouseLinkListener) listener).setIndex(i);
										} else if (listener instanceof PopupTriggerListener) {
											((PopupTriggerListener) listener).setIndex(i);
										}
									}
								}
							} catch (MalformedURLException e) {
								e.printStackTrace();
								return false;
							}

							videoPanel.add(channelPanel.getPanel());
						}
						videoPanel.revalidate();
						videoPanel.repaint();

						return true;
					}

					@Override
					protected void done() {
						((CardLayout) cardPanel.getLayout()).show(cardPanel, "Channels");
					}

				};
				worker.execute();
			}

		});
	}

	public void addChannel(String name) {
		addChannel(getLiveChannelData(name));
	}

	public void addChannel(Channel channel) {
		if (channel == null) {
			if (TwitchBuddy.isDebugMode()) {
				Constants.LOGGER.log(Level.INFO, "Attempting to add null channel.");
			}
			return;
		}
		channels.add(channel);
	}

	public Channel getLiveChannelData(String name) {
		for (Channel channel : getChannels()) {
			if (channel.getName().equalsIgnoreCase(name)) {
				return channel;
			}
		}
		return null;
	}

	public ArrayList<Channel> getChannels() {
		return channels;
	}

	public ArrayList<ChannelPanel> getChannelPanels() {
		return channelPanels;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}