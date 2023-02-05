package app.jfxgg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SettingDTO {

	private String appId;
	private String executable;
	private String host;
	private String username;
	private String password;
	private String defaultApp;
	private String shortcutName;
	
	public static SettingDTO of(String appId, String executable, String host, String username, String password, String defaultApp) {
		return new SettingDTO(appId, executable, host, username, password, defaultApp, null);
	}
	
	public static SettingDTO of(String appId, String executable, String host, String username, String password, String defaultApp, String shortcutName) {
		return new SettingDTO(appId, executable, host, username, password, defaultApp, shortcutName);
	}
	
	public static SettingDTO of(String executable, String host, String username, String password, String defaultApp) {
		return new SettingDTO(null, executable, host, username, password, defaultApp, null);
	}
}
