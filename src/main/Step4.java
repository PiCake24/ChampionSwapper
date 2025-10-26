package main;

import java.awt.BorderLayout;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

class Step4 extends JFrame {

	private static final long serialVersionUID = 1L;
	private JList<String> fileList;
	private DefaultListModel<String> listModel;

	public Step4(String chosen1, String chosen2) {
		super("Which Skin from " + chosen1 + "?");

		listModel = new DefaultListModel<>();
		Set<String> uniqueNames = new HashSet<>();

		// Folder path for skins
		String folderPath = "D:\\wad9\\0work\\data\\characters\\" + chosen1 + "\\skins";
		File folder = new File(folderPath);

		if (folder.exists() && folder.isDirectory()) {
			searchFolder(folder, uniqueNames);
		} else {
			listModel.addElement("[Invalid folder path or no skins found]");
		}

		fileList = new JList<>(listModel);
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(fileList);

		JButton continueButton = new JButton("Continue");
		continueButton.addActionListener(e -> {
			String selected = fileList.getSelectedValue();
			if (selected != null && !selected.startsWith("[")) {
				dispose();
				SwingUtilities.invokeLater(() -> new Step5(chosen1, chosen2, selected));
			} else {
				JOptionPane.showMessageDialog(this, "Please select a valid skin first.");
			}
		});

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(continueButton, BorderLayout.SOUTH);

		setSize(500, 300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void searchFolder(File folder, Set<String> uniqueNames) {
		for (File f : folder.listFiles()) {
			String name = f.getName();
			int dotIndex = name.indexOf('.');
			if (dotIndex > 0) {
				name = name.substring(0, dotIndex);
			}
			// Ignore duplicates and 'root'
			if (!uniqueNames.contains(name) && !name.equalsIgnoreCase("root")) {
				uniqueNames.add(name);
				listModel.addElement(name);
			}
		}

	}
}
