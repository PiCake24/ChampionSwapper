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

public class Step2 extends JFrame {

	private static final long serialVersionUID = 1L;
	private JList<String> fileList;
	private DefaultListModel<String> listModel;

	public Step2(String folderPath, String selected1) {
		super("Who to overwrite?");

		// Create the list model and populate it with files
		listModel = new DefaultListModel<>();
		Set<String> uniqueNames = new HashSet<>();

		File folder = new File(folderPath);
		if (folder.exists() && folder.isDirectory()) {
			for (File f : folder.listFiles()) {
				String name = f.getName();
				int dotIndex = name.indexOf('.');
				if (dotIndex > 0) {
					name = name.substring(0, dotIndex);
				}
				if (!uniqueNames.contains(name)) {
					uniqueNames.add(name);
					listModel.addElement(name);
				}
			}
		} else {
			listModel.addElement("[Invalid folder path]");
		}

		// Create the JList
		fileList = new JList<>(listModel);
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(fileList);

		// Continue button
		JButton continueButton = new JButton("Continue");
		continueButton.addActionListener(e -> {
			String selected2 = fileList.getSelectedValue();
			if (selected2 != null && !selected2.startsWith("[")) {
				// Switch to Step2 UI
				dispose();
				SwingUtilities.invokeLater(() -> new Step3(selected1, selected2));
			} else {
				JOptionPane.showMessageDialog(this, "Please select a valid item first.");
			}
		});

		// Layout
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(continueButton, BorderLayout.SOUTH);

		// Window settings
		setSize(400, 300);
		setLocationRelativeTo(null); // center on screen
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
	}

}
