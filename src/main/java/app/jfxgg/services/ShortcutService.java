package app.jfxgg.services;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.filechooser.FileSystemView;

import app.jfxgg.App;
import app.jfxgg.dto.SettingDTO;
import mslinks.ShellLink;

public class ShortcutService {

	public static void execute() throws IOException, InvalidKeyException, NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException,
			IllegalBlockSizeException, InvalidKeySpecException {

		SettingDTO dto = SettingService.load();

		List<String> params = new ArrayList<>();
		params.add(dto.getExecutable());
		params.add("-h");
		params.add(dto.getHost());
		params.add("-u");
		params.add(dto.getUsername());

		if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
			params.add("-p");
			params.add(dto.getPassword());
		}

		if (dto.getDefaultApp() != null && !dto.getDefaultApp().isEmpty()) {
			params.add("-a");
			params.add(dto.getDefaultApp());
		}

		new ProcessBuilder(params).start();
	}

	public static void create() throws IOException, InvalidKeyException, NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException,
			IllegalBlockSizeException, InvalidKeySpecException, URISyntaxException {

		SettingDTO dto = SettingService.load();
		String shortcutPath = getUserDesktopPath() + File.separator + dto.getShortcutName() + ".lnk";
		
		ShellLink sl = new ShellLink();
		sl.setTarget(getCurrentExecutableAppName());
		sl.setCMDArgs("-e");
		sl.saveTo(shortcutPath);
	}

	private static String getUserDesktopPath() {

		FileSystemView view = FileSystemView.getFileSystemView();
		File file = view.getHomeDirectory();
		return file.getPath();
	}
	
	private static String getCurrentExecutableAppName() throws URISyntaxException {
		
        String jarPath = App.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
                .getPath();
        System.out.println("JAR Path : " + jarPath);

        return jarPath.substring(jarPath.lastIndexOf("/") + 1);
	}
}
