package main;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
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

public class Step1 extends JFrame {

	private static final long serialVersionUID = 1L;
	private JList<String> fileList;
	private DefaultListModel<String> listModel;

	public Step1(String folderPath) {
		super("Who to write?");

		try {
			UnpackExe.unpackCDTBTranslator();
			UnpackExe.unpackRitobin();
		} catch (IOException e) {
			e.printStackTrace();
		}

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

		JButton continueButton = new JButton("Continue");
		continueButton.addActionListener(e -> {
			String selected = fileList.getSelectedValue();
			if (selected != null && !selected.startsWith("[")) {
				// Switch to Step2 UI
				dispose();
				SwingUtilities.invokeLater(() -> new Step2(folderPath, selected));
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

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Step1("D:\\Riot Games\\League of Legends\\Game\\DATA\\FINAL\\Champions")); // current
																														// folder
																														// by
																														// default
	}
}
