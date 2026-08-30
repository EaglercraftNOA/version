package net.eymenwsmc;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;

public class GuiWarning extends GuiScreen {
	private String warningTitle;
	private List<String> warningMessage;
	private final int MAX_LINE_WIDTH = 150;
	private final GuiScreen parent;

	public GuiWarning(GuiScreen parent, String title, String... messages) {
		this.warningTitle = title;
		this.warningMessage = new ArrayList<>();
		this.parent = parent;
		for (String msg : messages) {
			warningMessage.addAll(wrapText(msg));
		}
	}

	private List<String> wrapText(String text) {
		List<String> lines = new ArrayList<>();
		String[] words = text.split(" ");
		StringBuilder currentLine = new StringBuilder();

		for (String word : words) {
			if (word.isEmpty())
				continue;
			String testLine = (currentLine.length() == 0) ? word : currentLine.toString() + " " + word;
			if (fontRendererObj != null && fontRendererObj.getStringWidth(testLine) > MAX_LINE_WIDTH) {
				if (currentLine.length() > 0) {
					lines.add(currentLine.toString());
					currentLine = new StringBuilder();
				}
				if (fontRendererObj.getStringWidth(word) > MAX_LINE_WIDTH) {
					List<String> splitWords = splitWord(word);
					lines.addAll(splitWords);
				} else {
					currentLine.append(word);
				}
			} else {
				if (currentLine.length() > 0) {
					currentLine.append(" ");
				}
				currentLine.append(word);
			}
		}
		if (currentLine.length() > 0) {
			lines.add(currentLine.toString());
		}
		return lines;
	}

	private List<String> splitWord(String word) {
		List<String> segments = new ArrayList<>();
		StringBuilder segment = new StringBuilder();
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			if (fontRendererObj.getStringWidth(segment.toString() + c) > MAX_LINE_WIDTH) {
				segments.add(segment.toString() + "-");
				segment = new StringBuilder();
				segment.append(c);
			} else {
				segment.append(c);
			}
		}
		if (segment.length() > 0) {
			segments.add(segment.toString());
		}
		return segments;
	}

	@Override
	public void initGui() {
		buttonList.clear();
		buttonList.add(new GuiButton(0, width / 2 - 100, height - 160, "I Understand"));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, warningTitle, width / 2, 40, 0xff0000);

		int messageY = 60;
		for (int i = 0; i < warningMessage.size(); i++) {
			String numberedLine = warningMessage.get(i);
			drawCenteredString(fontRendererObj, numberedLine, width / 2, messageY, 0xffffff);
			messageY += fontRendererObj.FONT_HEIGHT + 2;
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 0) {
			mc.displayGuiScreen(parent);
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}
}