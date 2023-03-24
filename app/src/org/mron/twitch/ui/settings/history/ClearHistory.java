package org.mron.twitch.ui.settings.history;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.mron.twitch.ui.TwitchBuddyUI;
import org.mron.twitch.ui.settings.SettingPanel;
import org.mron.twitch.util.IOHandler;
import org.mron.twitch.util.impl.MouseLinkListener;

public class ClearHistory implements SettingPanel {

	private JPanel panel;

	private JButton clearHistoryButton;

	private static ClearHistory instance;

	@Override
	public JPanel getPanel() {
		panel = new JPanel(new BorderLayout());
		panel.add(getHeaderPanel(), BorderLayout.NORTH);
		panel.add(getContentPanel(), BorderLayout.CENTER);
		panel.add(getButtonPanel(), BorderLayout.SOUTH);

		return panel;
	}

	@Override
	public JPanel getHeaderPanel() {
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.add(new JLabel("<html><font size=\"20\">Clear History</font></html>", JLabel.CENTER), BorderLayout.NORTH);
		headerPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);

		return headerPanel;
	}

	@Override
	public JPanel getButtonPanel() {
		clearHistoryButton = new JButton("Clear History");
		clearHistoryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				reset();
			}

		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(clearHistoryButton);

		return buttonPanel;
	}

	@Override
	public JPanel getContentPanel() {
		JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		JLabel label = new JLabel("<html><body style='width: 270px'>Clearing your search history will remove every stream or VOD that you've ever loaded.<br /><br />You can view your search history here: <a href='http://history'>searches.txt</a></body></html>");
		label.addMouseListener(new MouseLinkListener(3));
		contentPanel.add(label);

		return contentPanel;
	}

	@Override
	public void save() {
		// no save option here
	}

	@Override
	public void reset() {
		int confirmation = JOptionPane.showConfirmDialog(panel, "This will remove all recent stream and VOD history.\n\nWould you like to continue?", "Clear History", JOptionPane.YES_NO_OPTION);
		if (confirmation == 0) {
			IOHandler.getInstance().clearSearches(false);
			TwitchBuddyUI.getInstance().getStreams().removeAllItems();
		}
	}

	public static ClearHistory getInstance() {
		if (instance == null) {
			instance = new ClearHistory();
		}
		return instance;
	}

}