package main;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

class Step5 extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextArea logArea;

	static final String PATH = "D:\\wad9\\0work\\data\\characters\\";
	static final String SKINS = "\\skins\\";

	public Step5(String chosen1, String chosen2, String chosenSkin) {
		super("Step 5 - Unpack Skin");

		logArea = new JTextArea();
		logArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(logArea);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		setSize(500, 300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);

		// Run unpackSkin in another thread
		new Thread(() -> {
			try {
				String parentSkin = unpackSkin(chosen1, chosen2, chosenSkin);
				dispose();
				SwingUtilities.invokeLater(() -> new Step6(chosen1, chosen2, parentSkin));
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
				log("An error occurred during unpacking.");
				Thread.currentThread().interrupt();
			}
		}).start();
	}

	private void log(String message) {
		SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
	}

	private String unpackSkin(String chosen1, String chosen2, String skin) throws IOException, InterruptedException {
		convertWad(chosen1, skin);
		convertWad(chosen2, "skin0");
		Thread.sleep(4000);

		String[] data1 = extractData(chosen1, skin);
		String[] data2 = extractData(chosen2, "skin0");
		// dann kopiere die dateien nach wad9, und zwar in gleicher struktur
		for (int i = 0; i < 3; i++) {
			String path1 = data1[i];
			String path2 = data2[i];
			copyOver(chosen2, path1, path2);
		}
		if (data1[3] == null) {
			return skin;
		} else {
			return "skin" + data1[3];
		}
	}

	private void copyOver(String champ2, String path1, String path2) throws IOException {
		Path b = Paths.get("D:\\wad9\\0work\\" + path1);
		Path a = Paths.get("D:\\wad9\\0work\\" + path2);
		Path c = Paths.get("D:\\wad9\\" + champ2);

		// Get the relative path of 'a' after "dir234" (adjust depending on your needs)
		Path relative = a.subpath(2, a.getNameCount() - 1); // "dir1/dir2"
		Path targetDir = c.resolve(relative);

		// Make sure directories exist
		Files.createDirectories(targetDir);

		// Final target file (with same name as 'a')
		Path targetFile = targetDir.resolve(a.getFileName());

		// Copy 'b' to targetFile, replacing if exists
		Files.copy(b, targetFile, StandardCopyOption.REPLACE_EXISTING);

		log("Copied fileB to: " + targetFile);

	}

	private String[] extractData(String champion, String skin) {
		String[] results = new String[4]; // [skeleton, simpleSkin, texture]
		boolean[] found = new boolean[4]; // Track if we already found them

		String filePath = PATH + champion + SKINS + skin + ".py";

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (!found[0] && line.contains("skeleton: string = \"")) {
					results[0] = extractValue(line, "skeleton: string = \"");
					found[0] = true;
				} else if (!found[1] && line.contains("simpleSkin: string = \"")) {
					results[1] = extractValue(line, "simpleSkin: string = \"");
					found[1] = true;
				} else if (!found[2] && line.contains("texture: string = \"")) {
					results[2] = extractValue(line, "texture: string = \"");
					found[2] = true;
				} else if (!found[3] && line.contains("skinParent: i32 =")) {
					String prefix = "skinParent: i32 =";
					int start = line.indexOf(prefix) + prefix.length();
					int end = start + 2;
					results[3] = line.substring(start, end).strip();
					found[3] = true;
				}

				// If all found, stop reading
				if (found[0] && found[1] && found[2]) {
					break;
				}
			}
		} catch (

		IOException e) {
			e.printStackTrace();
		}

		return results;
	}

	private static String extractValue(String line, String prefix) {
		int start = line.indexOf(prefix) + prefix.length();
		int end = line.indexOf("\"", start);
		if (end > start) {
			return line.substring(start, end);
		}
		return null;
	}

	private boolean convertWad(String champion, String skin) throws IOException, InterruptedException {
		String cliPath = UnpackExe.getUnpackedRitobin().toString();
		List<String> l = new ArrayList<>();
		l.add(cliPath);
		l.add(PATH + champion + SKINS + skin + ".bin");
		l.add(PATH + champion + SKINS + skin + ".py");
		Process process = new ProcessBuilder(l).start();
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
