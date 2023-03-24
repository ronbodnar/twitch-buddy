package org.mron.twitch.launcher.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class NewsPanel extends JPanel {
	
	private JPanel top, mid, bot;
	
	private JLabel dateLabel, titleLabel, readMoreLabel, contentLabel;
	
	private static final long serialVersionUID = 1L;
	
	public NewsPanel() {
		dateLabel = new JLabel();
		dateLabel.setFont(new Font("Arial", Font.ITALIC, 10));
		dateLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
		
		titleLabel = new JLabel();
		titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));
		
		contentLabel = new JLabel();
		contentLabel.setFont(new Font("Arial", Font.PLAIN, 10));
		
		readMoreLabel = new JLabel();
		readMoreLabel.setFont(new Font("Arial", Font.ITALIC, 10));
		readMoreLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
	}
	
	public NewsPanel getPanel() {
		setLayout(new BorderLayout());
		
		add(top(), BorderLayout.NORTH);
		add(mid(), BorderLayout.CENTER);
		add(bot(), BorderLayout.SOUTH);
		
		setPreferredSize(new Dimension(550, 85));
		
		setBackground(Color.blue);
		
		return this;
	}
	
	public JPanel top() {
		top = new JPanel(new BorderLayout());
		top.add(titleLabel, BorderLayout.WEST);
		top.add(dateLabel, BorderLayout.EAST);
		top.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		
		return top;
	}
	
	public JPanel mid() {	
		mid = new JPanel(new FlowLayout(FlowLayout.CENTER));
		mid.add(contentLabel);
		
		return mid;
	}
	
	public JPanel bot() {
		bot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bot.add(readMoreLabel);
		
		return bot;
	}

	public JLabel getDateLabel() {
		return dateLabel;
	}

	public JLabel getTitleLabel() {
		return titleLabel;
	}

	public JLabel getReadMoreLabel() {
		return readMoreLabel;
	}

	public JLabel getContentLabel() {
		return contentLabel;
	}

}