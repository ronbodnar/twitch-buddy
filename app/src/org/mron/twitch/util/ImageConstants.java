package org.mron.twitch.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;

import org.mron.twitch.Constants;

public class ImageConstants {

	private Font font;

	private BufferedImage[] images;

	public static String IMAGE_DIRECTORY = Constants.SETTINGS_DIRECTORY + "assets" + File.separator + "images" + File.separator;

	public static String ICON_DIRECTORY = Constants.SETTINGS_DIRECTORY + "assets" + File.separator + "images" + File.separator + "icons" + File.separator;

	public static final int FAVICON = 0, EYE_ICON = 1, CLOCK_ICON = 2, VIEWER_ICON = 3, SEARCH_ICON = 4, NULL_CHANNEL_ICON = 5, AJAX_LOADER = 6, ADD_CHANNEL_ICON = 7;

	/*
	 * Images
	 */
	public static final String FAVICON_IMAGE_URL = IMAGE_DIRECTORY + "favicon.png";
	public static final String AJAX_LOADER_URL = IMAGE_DIRECTORY + "ajax-loader.gif";
	
	/*
	 * Icons
	 */
	public static final String EYE_ICON_URL = ICON_DIRECTORY + "eye.png";
	public static final String USER_ICON_URL = ICON_DIRECTORY + "user.png";
	public static final String CLOCK_ICON_URL = ICON_DIRECTORY + "clock.png";
	public static final String SEARCH_ICON_URL = ICON_DIRECTORY + "search.png";
	public static final String NULL_CHANNEL_ICON_URL = ICON_DIRECTORY + "404_user.png";
	public static final String ADD_CHANNEL_ICON_URL = ICON_DIRECTORY + "add-channel.png";

	public ImageConstants() {
		try {
			images = new BufferedImage[8];
			/*
			 * Images
			 */
			images[FAVICON] = ImageIO.read(new File(FAVICON_IMAGE_URL));
			images[AJAX_LOADER] = ImageIO.read(new File(AJAX_LOADER_URL));
			
			/*
			 * Icons
			 */
			images[EYE_ICON] = ImageIO.read(new File(EYE_ICON_URL));
			images[CLOCK_ICON] = ImageIO.read(new File(CLOCK_ICON_URL));
			images[VIEWER_ICON] = ImageIO.read(new File(USER_ICON_URL));
			images[SEARCH_ICON] = ImageIO.read(new File(SEARCH_ICON_URL));
			images[ADD_CHANNEL_ICON] = ImageIO.read(new File(ADD_CHANNEL_ICON_URL));
			images[NULL_CHANNEL_ICON] = ImageIO.read(new File(NULL_CHANNEL_ICON_URL));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getImage(int index) {
		return images[index];
	}

	public Font getFont() {
		if (font == null) {
			font = new Font("Arial", Font.PLAIN, 12);
		}
		return font;
	}

	public String truncate(int maxLength, String text, FontMetrics fontMetrics) {
		if (fontMetrics.stringWidth(text) > maxLength) {
			int width = 0;
			StringBuilder builder = new StringBuilder();
			for (char c : text.toCharArray()) {
				int w = fontMetrics.charWidth(c);
				width += w;
				if (width > maxLength) {
					break;
				}
				builder.append(c);
			}
			builder.append("...");
			return builder.toString();
		}
		return text;
	}

	public BufferedImage resize(BufferedImage image, int width, int height) {
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);

		Graphics2D graphics = (Graphics2D) bufferedImage.createGraphics();
		graphics.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
		graphics.drawImage(image, 0, 0, width, height, null);
		graphics.dispose();

		return bufferedImage;
	}

}