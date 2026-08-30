package net.eymenwsmc;

import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.internal.EnumCursorType;
import net.lax1dude.eaglercraft.sp.gui.GuiScreenLANConnect;
import net.lax1dude.eaglercraft.sp.gui.GuiScreenLANInfo;
import net.lax1dude.eaglercraft.sp.gui.GuiScreenLANNotSupported;
import net.lax1dude.eaglercraft.sp.lan.LANServerController;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;

public class GuiFAQ extends GuiScreen {
    private final GuiScreen parent;

    private List<FAQItem> items = new ArrayList<FAQItem>();
    private int selectedIndex = 0;
    private int scrollOffset = 0;

    private static final int BTN_BACK = 0;

    public GuiFAQ(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.items = new ArrayList<FAQItem>(FAQData.items());

        if (selectedIndex < 0 && !items.isEmpty()) selectedIndex = 0;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (!button.enabled) return;
        if (button.id == BTN_BACK) {
            this.mc.displayGuiScreen(parent);
            return;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        // Handle selecting an item from the left list
        int listX = 12;
        int listY = 58;
        int listW = this.width / 3 - 24;
        int rowH = 28;

        if (mouseX >= listX && mouseX <= listX + listW && mouseY >= listY) {
            int idx = (mouseY - listY) / rowH + scrollOffset;
            if (idx >= 0 && idx < items.size()) {
                selectedIndex = idx;
            }
        }
        String text = I18n.format("Back");
		int w = mc.fontRenderer.getStringWidth(text);
		if (mouseX > 2 && mouseY > 2 && mouseX < (w * 3 / 4) + 5 && mouseY < 12) {
			mc.displayGuiScreen(parent);
			mc.getSoundHandler().playSound(
					PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
		}
    }

    @Override
    public void updateScreen() {
        // no-op for read-only
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Frequently Asked Questions", this.width / 2, 18, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, "Browse common questions and answers.", this.width / 2, 32, 0xAAAAAA);

        int leftPanelWidth = this.width / 3;

        // Left panel background
        drawRect(8, 50, leftPanelWidth - 8, this.height - 36, 0x66000000);

        // Right panel background
        drawRect(leftPanelWidth + 8, 50, this.width - 8, this.height - 36, 0x33000000);

        // Left list
        int y = 58;
        int rowH = 28;
        int listW = leftPanelWidth - 24;
        int visibleRows = (this.height - 36 - y) / rowH;
        int maxOffset = Math.max(0, items.size() - visibleRows);
        if (scrollOffset > maxOffset) scrollOffset = maxOffset;
        if (scrollOffset < 0) scrollOffset = 0;

        for (int i = 0; i < visibleRows && (i + scrollOffset) < items.size(); i++) {
            int idx = i + scrollOffset;
            int top = y + i * rowH;
            int left = 12;
            int right = left + listW;
            int bottom = top + rowH - 4;
            int bg = (idx == selectedIndex) ? 0xFF2E7D32 : 0x88212121; // green selected, dark default
            drawRect(left, top, right, bottom, bg);

            String q = items.get(idx).question;
            if (q == null) q = "";
            String clipped = this.fontRendererObj.trimStringToWidth(q, listW - 10);
            this.fontRendererObj.drawString(clipped, left + 6, top + 8, 0xFFFFFF);
        }
        
		String text = I18n.format("Back");
		int w = mc.fontRenderer.getStringWidth(text);
		boolean hover = mouseX > 1 && mouseY > 1 && mouseX < (w * 3 / 4) + 7 && mouseY < 12;
	

		drawString(mc.fontRenderer, EnumChatFormatting.UNDERLINE + text, 5, 5, hover ? 0xFFEEEE22 : 0xFFCCCCCC);

        // Right viewer labels and content
        int rightX = leftPanelWidth + 16;
        this.fontRendererObj.drawString("Question", rightX, 48, 0xCCCCCC);
        String qText = (selectedIndex >= 0 && selectedIndex < items.size()) ? items.get(selectedIndex).question : "";
        if (qText == null) qText = "";
        int contentWidth = this.width - rightX - 20;
        int questionY = 64;
        this.fontRendererObj.drawSplitString(qText, rightX, questionY, contentWidth, 0xFFFFFF);

        int qLines = this.fontRendererObj.listFormattedStringToWidth(qText, contentWidth).size();
        int answerLabelY = questionY + qLines * this.fontRendererObj.FONT_HEIGHT + 10;
        this.fontRendererObj.drawString("Answer", rightX, answerLabelY, 0xCCCCCC);
        String aText = (selectedIndex >= 0 && selectedIndex < items.size()) ? items.get(selectedIndex).answer : "";
        if (aText == null) aText = "";
        int answerY = answerLabelY + 16;
        this.fontRendererObj.drawSplitString(aText, rightX, answerY, contentWidth, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int d = Mouse.getEventDWheel();
        if (d != 0) {
            if (d < 0) scrollOffset += 1;
            if (d > 0) scrollOffset -= 1;
        }
    }
}
