package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class ScreenChatOptions extends GuiScreen {
	private static final GameSettings.Options[] CHAT_OPTIONS = new GameSettings.Options[] {
			GameSettings.Options.CHAT_VISIBILITY, GameSettings.Options.CHAT_COLOR, GameSettings.Options.CHAT_LINKS,
			GameSettings.Options.CHAT_OPACITY, GameSettings.Options.CHAT_LINKS_PROMPT, GameSettings.Options.CHAT_SCALE,
			GameSettings.Options.CHAT_HEIGHT_FOCUSED, GameSettings.Options.CHAT_HEIGHT_UNFOCUSED,
			GameSettings.Options.CHAT_WIDTH,
			GameSettings.Options.REDUCED_DEBUG_INFO, /* GameSettings.Options.NARRATOR */ };
	private final GuiScreen parentScreen;
	private final GameSettings game_settings;
	private String chatTitle;
	private GuiOptionButton field_193025_i;

	public ScreenChatOptions(GuiScreen parentScreenIn, GameSettings gameSettingsIn) {
		this.parentScreen = parentScreenIn;
		this.game_settings = gameSettingsIn;
	}

	public void initGui() {
		this.chatTitle = I18n.format("options.chat.title");
		int i = 0;

		for (GameSettings.Options gamesettings$options : CHAT_OPTIONS) {
			if (gamesettings$options.getEnumFloat()) {
				this.buttonList.add(new GuiOptionSlider(gamesettings$options.returnEnumOrdinal(),
						this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), gamesettings$options));
			} else {
				GuiOptionButton guioptionbutton = new GuiOptionButton(gamesettings$options.returnEnumOrdinal(),
						this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), gamesettings$options,
						this.game_settings.getKeyBinding(gamesettings$options));
				this.buttonList.add(guioptionbutton);

				/*
				 * if (gamesettings$options == GameSettings.Options.NARRATOR) {
				 * this.field_193025_i = guioptionbutton; guioptionbutton.enabled =
				 * NarratorChatListener.field_193643_a.func_193640_a(); }
				 */
			}

			++i;
		}

		this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 144, I18n.format("gui.done")));
	}

	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1) {
			this.mc.gameSettings.saveOptions();
		}

		super.keyTyped(typedChar, keyCode);
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			if (button.id < 100 && button instanceof GuiOptionButton) {
				GameSettings.Options option = ((GuiOptionButton) button).returnEnumOptions();
				this.game_settings.setOptionValue(option, 1);
				button.displayString = this.game_settings.getKeyBinding(option);
			}

			if (button.id == 200) {
				this.mc.gameSettings.saveOptions();
				this.mc.displayGuiScreen(this.parentScreen);
			}
		}
	}

	public void confirmClicked(boolean result, int id) {
		if (result && id < 100) {
			GameSettings.Options option = GameSettings.Options.getEnumOptions(id);
			this.game_settings.setOptionValue(option, 1);

			for (GuiButton button : this.buttonList) {
				if (button instanceof GuiOptionButton && ((GuiOptionButton) button).returnEnumOptions() == option) {
					button.displayString = this.game_settings.getKeyBinding(option);
					break;
				}
			}
		}

		this.mc.displayGuiScreen(this);
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, this.chatTitle, this.width / 2, 20, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void func_193024_a() {
		this.field_193025_i.displayString = this.game_settings
				.getKeyBinding(GameSettings.Options.getEnumOptions(this.field_193025_i.id));
	}
}