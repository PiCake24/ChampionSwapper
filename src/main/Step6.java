package main;

import java.awt.BorderLayout;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

class Step6 extends JFrame {
	static final String PATH = "D:\\wad9\\0work\\assets\\characters\\";
	private static final long serialVersionUID = 1L;
	private Map<String, JTextField> fileMappings;

	public Step6(String chosen1, String chosen2, String parentSkin1) {
		super("Step 6 - Map Files");
		fileMappings = new HashMap<>();

		String skin1re = renameSkin(parentSkin1);

		String folderPath = PATH + chosen2 + "\\skins\\base\\animations";

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		File folder = new File(folderPath);
		if (folder.exists() && folder.isDirectory()) {
			for (File f : folder.listFiles()) {
				if (f.isFile()) {
					JPanel row = new JPanel(new BorderLayout());

					JLabel label = new JLabel(f.getName());
					JTextField textField = new JTextField();
					JButton button = new JButton("Choose File");

					fileMappings.put(f.getAbsolutePath(), textField);

					button.addActionListener(e -> {
						JFileChooser chooser = new JFileChooser(
								PATH + chosen1 + "\\skins\\" + skin1re + "\\animations");
						int result = chooser.showOpenDialog(this);
						if (result == JFileChooser.APPROVE_OPTION) {
							File chosenFile = chooser.getSelectedFile();
							textField.setText(chosenFile.getName());
						}
					});

					row.add(label, BorderLayout.WEST);
					row.add(textField, BorderLayout.CENTER);
					row.add(button, BorderLayout.EAST);

					panel.add(row);
				}
			}
		} else {
			panel.add(new JLabel("[Invalid folder path or no files found]"));
		}

		JScrollPane scrollPane = new JScrollPane(panel);

		JButton continueButton = new JButton("Continue");
		continueButton.addActionListener(e -> {
			Map<String, String> mappings = new HashMap<>();
			for (Map.Entry<String, JTextField> entry : fileMappings.entrySet()) {
				String originalFile = entry.getKey();
				String chosenFile = entry.getValue().getText().trim();
				String path = PATH + chosen1 + "\\skins\\" + skin1re + "\\animations\\";
				mappings.put(originalFile, chosenFile.isEmpty() ? "" : path + chosenFile);
			}
			dispose();
			SwingUtilities.invokeLater(() -> new Step7(mappings, chosen1, chosen2));
		});

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(continueButton, BorderLayout.SOUTH);

		setSize(600, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public static String renameSkin(String input) {
		if (!input.startsWith("skin")) {
			return input; // Not a skin string
		}

		String numberPart = input.substring(4); // Get part after "skin"

		if (numberPart.equals("0")) {
			return "base"; // Special case
		}

		try {
			int num = Integer.parseInt(numberPart);

			// Add leading zero only if it's a single digit
			if (num >= 1 && num <= 9) {
				return "skin0" + num;
			} else {
				return "skin" + num;
			}
		} catch (NumberFormatException e) {
			// If it's not a valid number, just return unchanged
			return input;
		}
	}
}
