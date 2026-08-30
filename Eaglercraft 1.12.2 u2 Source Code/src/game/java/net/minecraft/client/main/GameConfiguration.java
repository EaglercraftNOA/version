package net.minecraft.client.main;

import net.minecraft.util.Session;

public class GameConfiguration {
	public final GameConfiguration.UserInformation userInfo;
	public final GameConfiguration.DisplayInformation displayInfo;
	public final GameConfiguration.GameInformation gameInfo;

	public GameConfiguration(GameConfiguration.UserInformation userInfoIn,
			GameConfiguration.DisplayInformation displayInfoIn, GameConfiguration.GameInformation gameInfoIn) {
		this.userInfo = userInfoIn;
		this.displayInfo = displayInfoIn;
		this.gameInfo = gameInfoIn;
	}

	public static class DisplayInformation {
		public final int width;
		public final int height;
		public final boolean fullscreen;

		public DisplayInformation(int widthIn, int heightIn, boolean fullscreenIn) {
			this.width = widthIn;
			this.height = heightIn;
			this.fullscreen = fullscreenIn;
		}
	}

	public static class GameInformation {
		public final boolean isDemo;
		public final String version;

		public GameInformation(boolean isDemoIn, String versionIn) {
			this.isDemo = isDemoIn;
			this.version = versionIn;
		}
	}

	public static class UserInformation {
		public final Session session;

		public UserInformation(Session parSession) {
			this.session = parSession;
		}
	}
}