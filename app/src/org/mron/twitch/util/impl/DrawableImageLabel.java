package org.mron.twitch.util.impl;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.mron.twitch.TwitchBuddy;
import org.mron.twitch.ui.tabs.HomeTab;
import org.mron.twitch.util.ImageConstants;

public class DrawableImageLabel extends JLabel {

	private Graphics2D graphics2D;

	private String length, views;

	private static final long serialVersionUID = 1L;

	private boolean offline, hovered;

	private int tab;

	public void setLength(int length) {
		int hours = length / 3600;
		int minutes = (length % 3600) / 60;
		int seconds = length % 60;

		StringBuilder string = new StringBuilder();

		if (hours > 0) {
			string.append(hours + ":");
		}
		if (minutes > 0) {
			string.append((minutes < 10 ? (hours <= 0 ? "" : "0") : "") + minutes + ":");
		}
		string.append(((minutes <= 0 && hours <= 0) ? "0:" : "") + (seconds < 10 ? "0" : "") + seconds);

		this.length = string.toString();
	}

	public void setViews(String views) {
		this.views = views;
	}

	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	public boolean isOffline() {
		return offline;
	}

	public DrawableImageLabel(int tab) {
		super();
		this.tab = tab;
	}

	public DrawableImageLabel(String text, ImageIcon icon) {
		super(text, icon, JLabel.CENTER);
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		graphics2D = (Graphics2D) getGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		switch (tab) {
			case 0: // home
			case 3: // clear history
			case 5: // past broadcasts
				break;

			case 1: // live channels
				drawPlusIcon(graphics);
				break;

			case 2: // game highlights
			case 4: // channel highlights
				drawHighlight(graphics);
				break;
		}
	}

	public void drawPlusIcon(Graphics graphics) {
		if (graphics == null || HomeTab.getInstance().channelExists(getName())) {
			return;
		}
		((Graphics2D) graphics).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (hovered ? 0.5f : 1f)));
		graphics.drawImage(TwitchBuddy.getImageConstants().getImage(ImageConstants.ADD_CHANNEL_ICON), 220, 110, null);
	}

	public void drawHighlight(Graphics graphics) {
		if (graphics == null) {
			return;
		}
		graphics.setFont(new Font("Arial", Font.PLAIN, 12));

		FontMetrics fontMetrics = graphics.getFontMetrics();

		graphics.setColor(new Color(0f, 0f, 0f, 0.7f));
		
		graphics.fillRect(3, 4, 22 + fontMetrics.stringWidth(views), 17); // views
		graphics.fillRect((getWidth() - fontMetrics.stringWidth(length)) - 30, 4, fontMetrics.stringWidth(length) + 22, 17); // length

		graphics.drawImage(TwitchBuddy.getImageConstants().getImage(ImageConstants.EYE_ICON), 5, 5, null);
		graphics.drawImage(TwitchBuddy.getImageConstants().getImage(ImageConstants.CLOCK_ICON), (getWidth() - fontMetrics.stringWidth(length)) - 28, 5, null);

		graphics.setColor(Color.WHITE);
		graphics.drawString(views, 23, 17);
		graphics.drawString(length, (getWidth() - fontMetrics.stringWidth(length)) - 10, 17);
	}

	public boolean isHovered() {
		return hovered;
	}

	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}

}