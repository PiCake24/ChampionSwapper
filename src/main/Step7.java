package main;

import java.awt.BorderLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

class Step7 extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextArea logArea;

	public Step7(Map<String, String> mappings, String chosen1, String chosen2) {
		super("Step 7 - Process Files");

		logArea = new JTextArea();
		logArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(logArea);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		setSize(600, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);

// Run processing in another thread
		new Thread(() -> {
			try {
				processMappings(mappings, chosen2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void log(String message) {
		SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
	}

	private void processMappings(Map<String, String> mappings, String champ2) throws IOException {
		log("Starting processing...");
		for (Map.Entry<String, String> entry : mappings.entrySet()) {
			String original = entry.getKey();
			String chosen = entry.getValue();
			Path target = Paths.get("D:\\wad9\\" + champ2);

			// Determine which file to use
			Path chosPath = chosen.isEmpty() ? Paths.get(original) : Paths.get(chosen);

			// Compute relative structure of original file, excluding the root part
			Path origRelative = Paths.get(original).subpath(2, Paths.get(original).getNameCount() - 1);

			System.out.println(origRelative);
			System.out.println(target);
			System.out.println(chosen);

			// Create the target directory mirroring the original structure
			Path targetDir = target.resolve(origRelative);
			Files.createDirectories(targetDir);

			// Create the target file path, renaming it to the original file name
			Path targetFile = targetDir.resolve(Paths.get(original).getFileName());

			// Copy chosen file to the target path, replacing if it exists
			Files.copy(chosPath, targetFile, StandardCopyOption.REPLACE_EXISTING);
		}
		log("Processing complete.");
		log("You may now close this window");
	}
}