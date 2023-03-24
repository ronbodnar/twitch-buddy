package org.mron.twitch.ui.tabs;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.FocusManager;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.mron.twitch.TwitchBuddy;
import org.mron.twitch.controller.Channel;
import org.mron.twitch.ui.TwitchBuddyUI;
import org.mron.twitch.ui.tabs.impl.ProfileTabs;
import org.mron.twitch.util.IOHandler;
import org.mron.twitch.util.ImageConstants;
import org.mron.twitch.util.JSONParser;
import org.mron.twitch.util.Util;

public class ProfileTab {

	private int index;

	private String activeUsername;

	private Channel activeProfile;

	private JLabel label, loadingLabel, profileLogoLabel;

	private JButton searchButton;

	private JTextPane textPane;

	private JTextField searchField;

	private JTabbedPane tabbedPane;

	private JPanel cardPanel, loadingPanel, searchPanel, mainMenuPanel, resultsPanel, profilePanel, profileInformationPanel;

	private StringBuilder profileHeader;

	private JComboBox<String> searchBox;

	private boolean favoritesHovered;

	private static ProfileTab instance;

	public JPanel getPanel() {
		if (cardPanel == null) {
			cardPanel = new JPanel(new CardLayout() {

				private static final long serialVersionUID = 1L;

				@Override
				public void show(Container parent, String name) {
					if (name.equals("loading")) {
						loadingLabel.setText("Loading " + getActiveUsername() + "'s channel - please wait");
					}
					super.show(parent, name);
				}

			});
			cardPanel.add(getMainMenuPanel(), "main");
			cardPanel.add(getLoadingPanel(), "loading");
			cardPanel.add(getResultsPanel(), "results");
		}

		return cardPanel;
	}

	public JPanel getResultsPanel() {
		if (resultsPanel == null) {
			resultsPanel = new JPanel(new BorderLayout());
			resultsPanel.add(getProfilePanel(), BorderLayout.CENTER);
			resultsPanel.add(getProfileTabs(), BorderLayout.SOUTH);
		}
		return resultsPanel;
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

	public JPanel getMainMenuPanel() {
		if (searchBox == null) {
			searchBox = new JComboBox<String>(new HashSet<String>(Arrays.asList(IOHandler.getInstance().getSearches(true))).toArray(new String[0]));
			searchBox.setPreferredSize(new Dimension(175, 22));
			searchBox.setEditable(true);
			searchBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent arg0) {
					if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
						if (searchBox.getSelectedItem() != null) {
							parseResults();
						} else {
							TwitchBuddyUI.getInstance().displayError("Search", "You must enter a valid name before searching!");
						}
					}
				}

			});
		}
		if (searchButton == null) {
			searchButton = new JButton("Search");
			searchButton.setPreferredSize(new Dimension(75, 22));
			searchButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (searchBox.getSelectedItem() != null) {
						parseResults();
					} else {
						TwitchBuddyUI.getInstance().displayError("Search", "You must enter a valid name before searching!");
					}
				}

			});
		}
		if (label == null) {
			label = new JLabel("Enter a profile to search for:");
			label.setFont(TwitchBuddy.getImageConstants().getFont().deriveFont(16f));
		}
		if (mainMenuPanel == null) {
			mainMenuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			mainMenuPanel.add(label);
			mainMenuPanel.add(searchBox);
			mainMenuPanel.add(searchButton);
			mainMenuPanel.setBorder(BorderFactory.createEmptyBorder(200, 0, 0, 0));
		}
		return mainMenuPanel;
	}

	public JPanel getProfilePanel() {
		textPane = new JTextPane();
		textPane.setOpaque(false);
		textPane.setEditable(false);
		textPane.setContentType("text/html");
		textPane.setText(getProfileHeader());
		if (getActiveProfile() != null) {
			textPane.setToolTipText(getActiveProfile().getBio());
		}
		textPane.setPreferredSize(new Dimension(950, 80));
		textPane.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent event) {
				if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					Util.getInstance().openBrowser(event.getURL().toString());
				}
			}

		});

		searchField = new JTextField(16) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				if (getText().isEmpty() && FocusManager.getCurrentKeyboardFocusManager().getFocusOwner() != this) {
					Graphics2D graphics = (Graphics2D) g.create();
					graphics.setColor(Color.GRAY.darker());
					graphics.setFont(new Font(TwitchBuddy.getImageConstants().getFont().getFamily(), Font.ITALIC, 12));
					graphics.drawString("Search channels", 20, 15);
					graphics.drawImage(TwitchBuddy.getImageConstants().getImage(ImageConstants.SEARCH_ICON), 5, 2, null); // search icon
				}
			}

		};
		searchField.setToolTipText("Enter a channel name to search for");
		searchField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					if (searchField.getText().length() > 0 && !searchField.getText().equals("")) {
						parseResults(searchField.getText());
						searchField.setText("");
					} else {
						TwitchBuddyUI.getInstance().displayError("Search", "You must enter a valid name before searching!");
					}
				}
			}

		});
		searchField.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				searchField.repaint();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				searchField.repaint();
			}

		});

		searchPanel = new JPanel(new BorderLayout()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);

				Graphics2D graphics = (Graphics2D) g.create();
				graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, favoritesHovered ? 0.4f : 0.9f));
				graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

				boolean favorited = false;
				if (!favorited) {
					graphics.drawString("Favorite", 0, 57);
				} else {
					graphics.drawString("Favorited", 0, 50);
				}
			}

		};
		searchPanel.setPreferredSize(new Dimension(150, 80));
		searchPanel.add(searchField, BorderLayout.NORTH);
		searchPanel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseMoved(MouseEvent e) {
				Point point = e.getPoint();
				boolean temp = favoritesHovered;
				if ((point.getX() >= 1.0 && point.getX() <= 80.0) && (point.getY() >= 54.0 && point.getY() <= 80.0)) {
					favoritesHovered = true;
				} else {
					favoritesHovered = false;
				}
				if (temp != favoritesHovered) {
					searchPanel.revalidate();
					searchPanel.repaint();
				}
			}

		});
		searchPanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseExited(MouseEvent arg0) {
				if (favoritesHovered) {
					favoritesHovered = false;
					searchPanel.revalidate();
					searchPanel.repaint();
				}
			}

		});

		profileLogoLabel = new JLabel();

		profileInformationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		profileInformationPanel.add(profileLogoLabel);
		profileInformationPanel.add(textPane);

		profilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		profilePanel.add(profileInformationPanel);
		profilePanel.add(searchPanel);

		return profilePanel;
	}

	public JTabbedPane getProfileTabs() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.add(ProfileTabs.getInstance().getHighlightsPanel(), "Highlights");
			tabbedPane.add(ProfileTabs.getInstance().getPastBroadcastsPane(), "Past Broadcasts");
			tabbedPane.setPreferredSize(new Dimension(1200, 430));
		}
		return tabbedPane;
	}

	public String getProfileHeader() {
		profileHeader = new StringBuilder();
		if (getActiveProfile() != null) {
			int bioLength = getActiveProfile().getBio().length();
			profileHeader.append("<html><p><font face='arial' color='black'>");
			profileHeader.append("<b>" + getActiveProfile().getDisplayName() + "</b>");
			profileHeader.append("<br />" + (bioLength < 183 ? "<br />" : ""));
			profileHeader.append("<i>" + getActiveProfile().getBio() + "</i>");
			profileHeader.append("</font></p></html>");
		}
		return profileHeader.toString();
	}

	public void parseResults() {
		parseResults((String) searchBox.getSelectedItem());
	}

	public void parseResults(final String username) {
		if (TwitchBuddyUI.getInstance().getTabbedPane().getSelectedIndex() != 3) {
			TwitchBuddyUI.getInstance().getTabbedPane().setSelectedIndex(3);
		}

		setActiveUsername(username);

		((CardLayout) cardPanel.getLayout()).show(cardPanel, "loading");

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				final SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

					@Override
					protected Boolean doInBackground() throws Exception {
						setActiveProfile(JSONParser.getInstance().parseChannel(username));

						for (int i = 0; i < 2; i++) {
							ProfileTabs.getInstance().buildVideos(i);
						}

						textPane.setText(getProfileHeader());

						profileLogoLabel.setText("<html><img src='" + (getActiveProfile().getLogo() == null ? ImageConstants.NULL_CHANNEL_ICON_URL : getActiveProfile().getLogo()) + "' width='64' height='64' /></html>");
						try {
							IOHandler.getInstance().writeSearch(true, username);
						} catch (IOException e) {
							e.printStackTrace();
						}
						return true;
					}

					@Override
					protected void done() {
						((CardLayout) cardPanel.getLayout()).show(cardPanel, "results");
					}

				};
				worker.execute();
			}

		});
	}

	public BufferedImage resize(BufferedImage image, int width, int height) {
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);

		Graphics2D graphics = (Graphics2D) bufferedImage.createGraphics();
		graphics.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
		graphics.drawImage(image, 0, 0, width, height, null);
		graphics.dispose();

		return bufferedImage;
	}

	public static ProfileTab getInstance() {
		if (instance == null) {
			instance = new ProfileTab();
		}
		return instance;
	}

	public JComboBox<String> getSearchBox() {
		return searchBox;
	}

	public Channel getActiveProfile() {
		return activeProfile;
	}

	public void setActiveProfile(Channel activeProfile) {
		this.activeProfile = activeProfile;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getActiveUsername() {
		return activeUsername;
	}

	public void setActiveUsername(String activeUsername) {
		this.activeUsername = activeUsername;
		this.searchBox.addItem(activeUsername);
		this.searchBox.setSelectedItem(activeUsername);
	}

	public JTextField getSearchField() {
		return searchField;
	}

	public void setSearchField(JTextField searchField) {
		this.searchField = searchField;
	}

}