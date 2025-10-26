package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Test {
	public static void main(String[] args) throws IOException {
		Path a = Paths.get("D:\\wad9\\0work\\data\\characters\\ambessa\\skins\\skin3.py");
		Path b = Paths.get("D:\\wad9\\0work\\data\\characters\\aphelios\\skins\\skin0.py");
		Path c = Paths.get("D:\\wad9\\aph");

		// Get the relative path of 'a' after "dir234" (adjust depending on your needs)
		Path relative = a.subpath(2, a.getNameCount() - 1); // "dir1/dir2"
		Path targetDir = c.resolve(relative);

		// Make sure directories exist
		Files.createDirectories(targetDir);

		// Final target file (with same name as 'a')
		Path targetFile = targetDir.resolve(a.getFileName());

		// Copy 'b' to targetFile, replacing if exists
		Files.copy(b, targetFile, StandardCopyOption.REPLACE_EXISTING);

		System.out.println("Copied fileB to: " + targetFile);
	}
}
