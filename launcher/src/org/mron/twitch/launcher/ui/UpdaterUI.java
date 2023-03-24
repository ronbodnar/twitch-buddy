package org.mron.twitch.launcher.ui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.mron.twitch.launcher.NewsItem;
import org.mron.twitch.launcher.TwitchBuddyLauncher;
import org.mron.twitch.launcher.Updater;

public class UpdaterUI {

	private String action;

	private boolean hovered;

	private String message = "A new version of Twitch Buddy is available!";
	
	private JComponent footerPanel;

	private Updater updater;

	private JFrame frame;

	private JLabel headerLabel;

	private JScrollPane scrollPane;

	private JPanel panel, headerPanel, newsPanel;

	private long percentage;

	// RGB/255 = RGB float value
	private final Color TWITCHBUDDY_RED = new Color(0.547f, 0f, 0f, 0.7f);
	private final Color TWITCHBUDDY_RED_HOVER = new Color(0.547f, 0f, 0f, 0.4f);

	private final Color SUNFLOWER_YELLOW = new Color(0.901f, 0.870f, 0.164f, 0.7f);
	private final Color SUNFLOWER_YELLOW_HOVER = new Color(0.901f, 0.870f, 0.164f, 0.4f);

	public void constructFrame(Updater updater) {
		this.updater = updater;

		frame = new JFrame("Twitch Buddy Launcher");
		try {
			frame.setIconImage(ImageIO.read(new File(System.getProperty("user.home") + File.separator + ".mron" + File.separator + "twitch-buddy" + File.separator + "assets" + File.separator + "images" + File.separator + "favicon.png")));
		} catch (IOException e) {
			try {
				frame.setIconImage(ImageIO.read(new URL("http://cdn.mron.co/images/buddy-favicon.png")));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(getPanel());
		frame.pack();

		centerWindow(frame);
	}

	public JPanel getPanel() {
		panel = new JPanel(new BorderLayout());
		panel.add(getHeaderPanel(), BorderLayout.NORTH);
		panel.add(getNewsPanel(), BorderLayout.CENTER);
		panel.add(getFooterPanel(), BorderLayout.SOUTH);
		panel.setPreferredSize(new Dimension(600, 400));

		return panel;
	}

	public JPanel getHeaderPanel() {
		headerLabel = new JLabel("Release Notes");
		headerLabel.setFont(new Font("Arial", Font.PLAIN, 20));

		headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		headerPanel.add(headerLabel);

		return headerPanel;
	}

	public JScrollPane getNewsPanel() {
		newsPanel = new JPanel(new GridLayout(10, 1));

		for (NewsItem news : TwitchBuddyLauncher.getIOManager().getNewsItems()) {
			NewsPanel np = new NewsPanel();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < news.getContent().length(); i++) {
				if (i > 0 && (i % 115 == 0)) {
					sb.append("<br />");
				}
				if (i > 0 && (i % 200 == 0)) {
					sb.append("...");
					break;
				}
				sb.append(news.getContent().charAt(i));
			}

			np.getDateLabel().setText("<html>Published by <font color='blue'>@</font><font color='green'><i>" + news.getAuthor() + "</i></font> on <i>" + news.getDate() + "</i></html>");
			np.getTitleLabel().setText("<html><b>" + news.getTitle() + "</b></html>");
			np.getContentLabel().setText("<html>" + sb.toString() + "</html>");
			np.getReadMoreLabel().setText("Read more...");

			newsPanel.add(np.getPanel());
			System.out.println("added news");
		}

		scrollPane = new JScrollPane(newsPanel);
		scrollPane.setPreferredSize(new Dimension(500, 300));
		scrollPane.getVerticalScrollBar().setUnitIncrement(5);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		return scrollPane;
	}

	public JComponent getFooterPanel() {
		footerPanel = new JComponent() {

			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);

				Graphics2D graphics = (Graphics2D) g;
				graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

				/*
				 * Loading bar
				 */
				graphics.setColor(new Color(0f, 0f, 0f, 1f));
				graphics.drawRect(10, 5, 480, 28);

				graphics.setColor(new Color(0f, 0f, 0f, 0.2f));
				graphics.fillRect(11, 6, (int) ((479 * percentage) / 100), 27);

				/*
				 * Action button
				 */
				graphics.setColor(hovered ? new Color(0f, 0f, 0f, 0.8f) : Color.BLACK);
				graphics.drawRect(505, 5, 80, 28);

				graphics.setColor(!updater.isUpdateAvailable() ? hovered ? SUNFLOWER_YELLOW_HOVER : SUNFLOWER_YELLOW : hovered ? TWITCHBUDDY_RED_HOVER : TWITCHBUDDY_RED);
				graphics.fillRect(506, 6, 79, 27);

				/*
				 * Loading bar text
				 */
				graphics.setColor(new Color(0f, 0f, 0f, 0.8f));
				graphics.setFont(new Font("Arial", Font.PLAIN, 11));
				graphics.drawString(getMessage(), 240 - (graphics.getFontMetrics().stringWidth(getMessage()) / 2), 23);

				/*
				 * Action button text
				 */
				graphics.setColor(new Color(0f, 0f, 0f, 0.8f));
				graphics.setFont(new Font("Arial", Font.PLAIN, 12));

				action = updater.isUpdateAvailable() ? "Update" : "Start";

				graphics.drawString(action, 545 - (graphics.getFontMetrics().stringWidth(action) / 2), 23);
			}

		};
		footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		footerPanel.setPreferredSize(new Dimension(600, 40));
		footerPanel.setOpaque(false);
		footerPanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent event) {
				Point point = event.getPoint();
				if ((point.getX() >= 505 && point.getX() <= 584) && (point.getY() >= 5 && point.getY() <= 35)) { // action button
					if (updater.isUpdateAvailable()) {
						new SwingWorker<Boolean, Void>() {

							@Override
							protected Boolean doInBackground() throws Exception {
								setMessage(0, "Updating Twitch Raffle to the current version - please wait.");
								footerPanel.repaint();
								updater.downloadUpdates();
								return true;
							}

							@Override
							protected void done() {
								setMessage(0, "Twitch Buddy is up to date!");
								footerPanel.repaint();
							}

						}.execute();
					} else {
						updater.start();
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent event) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseExited(MouseEvent event) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mousePressed(MouseEvent event) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseReleased(MouseEvent event) {
				// TODO Auto-generated method stub
			}

		});
		footerPanel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				Point point = arg0.getPoint();
				boolean temp = hovered;
				if ((point.getX() >= 505 && point.getX() <= 584) && (point.getY() >= 5 && point.getY() <= 35)) { // action button
					hovered = true;
				} else {
					hovered = false;
				}
				if (temp != hovered) {
					footerPanel.repaint();
				}
			}

		});

		return footerPanel;
	}

	public void centerWindow(Window frame) {
		GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = graphicsDevice.getDisplayMode().getWidth();
		int height = graphicsDevice.getDisplayMode().getHeight();

		int x = (int) ((width - frame.getWidth()) / 2);
		int y = (int) ((height - frame.getHeight()) / 2);
		frame.setLocation(x, y);
		frame.setVisible(true);
	}

	public JFrame getFrame() {
		return frame;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(long percentage, String message) {
		this.message = message;
		this.percentage = percentage;
		if (footerPanel != null) {
			footerPanel.repaint();
		}
	}

}