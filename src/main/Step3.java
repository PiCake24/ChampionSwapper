package main;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

class Step3 extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextArea logArea;

	public Step3(String chosen1, String chosen2) {
		super("Step 3");

		logArea = new JTextArea();
		logArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(logArea);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		setSize(500, 300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);

		// Run unpackFiles in another thread
		new Thread(() -> {
			unpackFiles(chosen1, chosen2);
			SwingUtilities.invokeLater(() -> {
				dispose();
				new Step4(chosen1, chosen2);
			});
		}).start();
	}

	private void log(String message) {
		SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
	}

	private void unpackFiles(String c1, String c2) {
		try {
			boolean done1 = extractFile(c1);
			boolean done2 = extractFile(c2);

			if (done1 && done2) {
				log("Unpacking complete.");
				// todo copy vustom mods into dir
			} else {
				log("Unpacking had errors.");
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			log("An error occurred during unpacking.");
			Thread.currentThread().interrupt();
		}
	}

	private boolean extractFile(String champion) throws IOException, InterruptedException {
		String pythonScript = UnpackExe.getUnpackedCDTBTranslator().toString();
		String inputPath = "D:\\Riot Games\\League of Legends\\Game\\DATA\\FINAL\\Champions\\" + champion
				+ ".wad.client";
		String outputPath = "D:\\wad9\\0work";
		String pattern = "*characters/" + champion.toLowerCase() + "/skins*";

		if (!new File(inputPath).exists()) {
			if (champion.isEmpty()) {
				log("Champion could not be found, continuing with next champion");
				return true;
			}
			log("Champion file does not exist, trying to find parent file: "
					+ champion.substring(0, champion.length() - 1));
			return extractFile(champion.substring(0, champion.length() - 1));
		}

		log("Starting unpack for: " + champion);
		ProcessBuilder pb = new ProcessBuilder(pythonScript, "unpack_file", inputPath, outputPath, pattern);
		Process process = pb.start();

		// Wait for process to finish
		return checkprocess(process);
	}

	private boolean checkprocess(Process process) throws IOException, InterruptedException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			log(line);
		}
		int exitCode = process.waitFor();
		return exitCode == 0;
	}
}
