package app.jfxgg.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import app.jfxgg.dto.SettingDTO;
import app.jfxgg.utils.sec.Encryption;
import app.jfxgg.utils.text.StringUtils;

public class SettingService {

	public static final String PROPERTY_FILE = "jfx_gg_wrapper.ini";
	public static final String SHORTCUT_DEFAULT_NAME = "Go-Global";

	public static final String PROPERTY_EXECUTABLE_KEY = "executable";
	public static final String PROPERTY_HOST_KEY = "host";
	public static final String PROPERTY_CREDENTIALS_KEY = "credentials_hash";
	public static final String PROPERTY_DEFAULT_APP_KEY = "default_app";
	public static final String PROPERTY_SHORTCUT_NAME_KEY = "shortcut_name";
	public static final String PROPERTY_APP_ID_KEY = "app_id";

	private static Integer APP_ID_PREFIX_LENGTH = 12;
	private static Integer APP_ID_SUFFIX_LENGTH = 6;
	private static String APP_ID_SEPARATOR = "\\.";
	private static final String CREDENTIAL_SEPARATOR = "@";
	private static File propertyFile = new File(PROPERTY_FILE);

	public static void save(SettingDTO dto) throws IOException, InvalidKeyException, NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException,
			IllegalBlockSizeException, InvalidKeySpecException {

		SettingDTO loadedDto = load();

		String appId = loadedDto.getAppId() == null || loadedDto.getAppId().isEmpty() ? generateAppId()
				: loadedDto.getAppId();

		String credentials = protectCredentials(dto.getUsername(), dto.getPassword(), appId);

		Properties p = new Properties();
		p.put(PROPERTY_EXECUTABLE_KEY, dto.getExecutable());
		p.put(PROPERTY_HOST_KEY, dto.getHost());
		p.put(PROPERTY_CREDENTIALS_KEY, credentials);
		p.put(PROPERTY_DEFAULT_APP_KEY, dto.getDefaultApp());
		p.put(PROPERTY_SHORTCUT_NAME_KEY,
				loadedDto.getShortcutName() == null || loadedDto.getShortcutName().isEmpty() ? SHORTCUT_DEFAULT_NAME
						: loadedDto.getShortcutName());
		p.put(PROPERTY_APP_ID_KEY, appId);

		savePropertiesFile(p);
	}

	public static SettingDTO load() throws IOException, InvalidKeyException, NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException,
			IllegalBlockSizeException, InvalidKeySpecException {

		if (!propertyFile.exists()) {
			return SettingDTO.of("", "", "", "", "", "");
		}

		Properties p = new Properties();
		loadPropertiesFile(p);

		String appId = p.getProperty(PROPERTY_APP_ID_KEY);
		String executable = p.getProperty(PROPERTY_EXECUTABLE_KEY);
		String host = p.getProperty(PROPERTY_HOST_KEY);
		String credentials = p.getProperty(PROPERTY_CREDENTIALS_KEY);
		String defaultApp = p.getProperty(PROPERTY_DEFAULT_APP_KEY);

		String username = extractUsernameFromProtectedCredentials(credentials, appId);
		String pass = extractPasswordFromProtectedCredentials(credentials, appId);
		String shortcutName = p.getProperty(PROPERTY_SHORTCUT_NAME_KEY) != null
				? p.getProperty(PROPERTY_SHORTCUT_NAME_KEY)
				: SHORTCUT_DEFAULT_NAME;

		return SettingDTO.of(appId, executable, host, username, pass, defaultApp, shortcutName);
	}

	private static void savePropertiesFile(Properties p) throws IOException {

		FileOutputStream fr = new FileOutputStream(propertyFile);
		p.store(fr, "Properties");
		fr.close();
	}

	private static void loadPropertiesFile(Properties p) throws IOException {

		FileInputStream fi = new FileInputStream(propertyFile);
		p.load(fi);
		fi.close();
	}

	private static String protectCredentials(String username, String password, String appId) throws InvalidKeyException,
			NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException,
			IllegalBlockSizeException, InvalidKeySpecException {

		String[] arrAppId = appId.split(APP_ID_SEPARATOR);
		if (arrAppId.length != 2) {
			throw new IllegalArgumentException("Invalid app id pattern");
		}

		String prefix = arrAppId[0];
		String suffix = arrAppId[1];

		String protectedUsername = Encryption.encrypt(username, prefix, suffix);
		String protectedPassword = Encryption.encrypt(password, prefix, suffix);

		return protectedUsername.concat(CREDENTIAL_SEPARATOR).concat(protectedPassword);
	}

	private static String extractUsernameFromProtectedCredentials(String credentialsHash, String appId)
			throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException,
			InvalidKeySpecException {

		String[] arrAppId = appId.split(APP_ID_SEPARATOR);
		if (arrAppId.length != 2) {
			throw new IllegalArgumentException("Invalid app id pattern");
		}

		String prefix = arrAppId[0];
		String suffix = arrAppId[1];

		String[] arrCredHash = credentialsHash.split(CREDENTIAL_SEPARATOR);
		return Encryption.decrypt(arrCredHash[0], prefix, suffix);
	}

	private static String extractPasswordFromProtectedCredentials(String credentialsHash, String appId)
			throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException,
			InvalidKeySpecException {

		String[] arrAppId = appId.split(APP_ID_SEPARATOR);
		if (arrAppId.length != 2) {
			throw new IllegalArgumentException("Invalid app id pattern");
		}

		String prefix = arrAppId[0];
		String suffix = arrAppId[1];

		String[] arrCredHash = credentialsHash.split(CREDENTIAL_SEPARATOR);
		if (arrCredHash.length != 2) {
			throw new IllegalArgumentException("Invalid credentials hash");
		}

		return Encryption.decrypt(arrCredHash[1], prefix, suffix);
	}

	private static String generateAppId() {

		String prefix = StringUtils.generateRandomString(APP_ID_PREFIX_LENGTH);
		String suffix = StringUtils.generateRandomString(APP_ID_SUFFIX_LENGTH);
		return prefix.concat(APP_ID_SEPARATOR).concat(suffix);
	}
}
