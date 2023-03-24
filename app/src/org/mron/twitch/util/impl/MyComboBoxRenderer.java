package org.mron.twitch.util.impl;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.mron.twitch.util.Util;

public class MyComboBoxRenderer extends BasicComboBoxRenderer {

	private int index;

	private static final long serialVersionUID = 2746090194775905713L;

	public MyComboBoxRenderer(int index) {
		this.index = index;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
			if ((-1 < index) && (Util.getInstance().getGames().size() > index)) {
				if (Util.getInstance().getGames().size() >= index) {
					list.setToolTipText("<html><img src=\"" + Util.getInstance().getGames().get(index).getLogo() + "\" /></html>");
				}
			}
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setFont(list.getFont());
		setText((value == null) ? "" : value.toString());
		return this;
	}

	public int getIndex() {
		return index;
	}

}