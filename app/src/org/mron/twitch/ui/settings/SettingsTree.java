package org.mron.twitch.ui.settings;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import org.mron.twitch.util.impl.JTreeSelectionListener;

public class SettingsTree {

	private JTree tree;

	private DefaultMutableTreeNode root;

	public JTree getSettingsTree() {
		root = new DefaultMutableTreeNode("General");

		for (SettingPanels setting : SettingPanels.values()) {
			if (setting.getName().equals("General")) {
				continue;
			}
			root.add(new DefaultMutableTreeNode(setting.getName()));
		}

		tree = new JTree(root);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new JTreeSelectionListener());

		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);

		return tree;
	}

	private static SettingsTree instance;

	public static SettingsTree getInstance() {
		if (instance == null) {
			instance = new SettingsTree();
		}
		return instance;
	}

}