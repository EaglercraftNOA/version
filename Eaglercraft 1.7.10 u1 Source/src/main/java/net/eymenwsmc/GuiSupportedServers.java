package net.eymenwsmc;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.lax1dude.eaglercraft.internal.PlatformOpenGL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;

public class GuiSupportedServers extends GuiScreen {
    private final GuiScreen parent;
    private final List<ServerData> supportedServers = new ArrayList<ServerData>();

    private static final int ROW_HEIGHT = 72;    private static final int START_Y = 58;
    private static final int MAX_CARD_WIDTH = 420;
    private static final int CARD_PADDING = 8;
    private static final int BOTTOM_RESERVED = 36; // reserve space for back button area

    // scrolling state
    private int scrollOffset = 0;
    private int maxScroll = 0;

    public GuiSupportedServers(GuiScreen parent) {
        this.parent = parent;
        supportedServers.add(new ServerData("Tuff Net", "wss://tuff.ws"));
        supportedServers.add(new ServerData("OPSPT", "wss://opspt.webmc.xyz"));
        supportedServers.add(new ServerData("Adderall MC", "wss://adderall.ir"));
    }

    @Override
    public void initGui() {
        this.buttonList.clear();

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height - 28, 200, 20, "Back"));

        // Pre-create buttons for each server entry; positions will be laid out dynamically
        for (int index = 0; index < supportedServers.size(); index++) {
            // Add to List button (odd id mapping as before)
            this.buttonList.add(new GuiButton(101 + index * 2, -1000, -1000, 110, 20, "Add to List"));
            // Connect button
            this.buttonList.add(new GuiButton(100 + index * 2, -1000, -1000, 110, 20, "Connect"));
        }
        layoutButtons();
    }

    private void layoutButtons() {
        // Compute layout params based on current width/height
        int cardWidth = Math.min(MAX_CARD_WIDTH, this.width - 40);
        boolean narrow = cardWidth < (2 * 110 + 12 + CARD_PADDING * 2);
        int rowHeight = narrow ? 96 : ROW_HEIGHT;

        int listTop = START_Y;
        int listBottom = this.height - BOTTOM_RESERVED; // keep area for back button
        int listArea = Math.max(0, listBottom - listTop);
        int contentHeight = supportedServers.size() * rowHeight;
        maxScroll = Math.max(0, contentHeight - listArea);
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;
        if (scrollOffset < 0) scrollOffset = 0;

        int cardLeft = this.width / 2 - cardWidth / 2;
        int btnWidth = 110;
        int btnSpacing = 12;
        int totalBtnWidth = btnWidth * 2 + btnSpacing;
        int btnStartX = this.width / 2 - (totalBtnWidth / 2);

        // Update back button position
        for (Object o : this.buttonList) {
            if (o instanceof GuiButton) {
                GuiButton b = (GuiButton) o;
                if (b.id == 0) {
                    b.buttonX = this.width / 2 - 100;
                    b.buttonY = this.height - 28;
                }
            }
        }

        // Update entry buttons
        for (int index = 0; index < supportedServers.size(); index++) {
            int baseId = 100 + index * 2;
            GuiButton connectBtn = getButtonById(baseId);
            GuiButton addBtn = getButtonById(baseId + 1);
            if (connectBtn == null || addBtn == null) continue;

            int yBase = START_Y + index * rowHeight - scrollOffset;
            int buttonsY = yBase + rowHeight - CARD_PADDING - 24;
            int cardTop = yBase;
            int cardBottom = yBase + rowHeight - 8;

            boolean visible = cardBottom > listTop && cardTop < listBottom;
            connectBtn.visible = visible;
            addBtn.visible = visible;

            if (!visible) {
                // park offscreen
                connectBtn.buttonX = addBtn.buttonX = -10000;
                connectBtn.buttonY = addBtn.buttonY = -10000;
                continue;
            }

            if (narrow) {
                // Stack vertically, keep fixed widths; center within card
                int btnX = this.width / 2 - (btnWidth / 2);
                addBtn.buttonX = btnX;
                addBtn.buttonY = buttonsY - 26;

                connectBtn.buttonX = btnX;
                connectBtn.buttonY = buttonsY;
            } else {
                addBtn.buttonX = btnStartX;
                addBtn.buttonY = buttonsY;

                connectBtn.buttonX = btnStartX + btnWidth + btnSpacing;
                connectBtn.buttonY = buttonsY;
            }
        }
    }

    private GuiButton getButtonById(int id) {
        for (Object o : this.buttonList) {
            if (o instanceof GuiButton) {
                GuiButton b = (GuiButton) o;
                if (b.id == id) return b;
            }
        }
        return null;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (!button.enabled) return;

        if (button.id == 0) {
            this.mc.displayGuiScreen(parent);
            return;
        }

        if (button.id >= 100) {
            int idx = (button.id - 100) / 2;
            boolean isAdd = ((button.id - 100) % 2) == 1;
            if (idx >= 0 && idx < supportedServers.size()) {
                ServerData dat = supportedServers.get(idx);
                if (isAdd) {
                    ServerList list = new ServerList(this.mc);
                    list.loadServerList();
                    boolean exists = false;
                    for (int i = 0; i < list.countServers(); i++) {
                        ServerData e = list.getServerData(i);
                        if (e.serverIP.equalsIgnoreCase(dat.serverIP)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        list.addServerData(new ServerData(dat.serverName, dat.serverIP));
                        list.saveServerList();
                    }
                } else {
                    this.mc.displayGuiScreen(new GuiConnecting(this.parent, Minecraft.getMinecraft(),
                            new ServerData(dat.serverName, dat.serverIP)));
                }
            }
        }
    }

    @Override
    public void onGuiClosed() {
        
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int dWheel = Mouse.getEventDWheel();
        if (dWheel != 0) {
            // Normalize direction and scale scrolling speed
            int step = dWheel > 0 ? -1 : 1;
            int cardWidth = Math.min(MAX_CARD_WIDTH, this.width - 40);
            boolean narrow = cardWidth < (2 * 110 + 12 + CARD_PADDING * 2);
            int rowHeight = narrow ? 96 : ROW_HEIGHT;
            scrollOffset += step * (rowHeight / 2);
            if (scrollOffset < 0) scrollOffset = 0;
            if (scrollOffset > maxScroll) scrollOffset = maxScroll;
            layoutButtons();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) { 
            this.mc.displayGuiScreen(parent);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        // Make the header scroll along with content
        this.drawCenteredString(this.fontRendererObj, "Supported Servers", this.width / 2, 18 - scrollOffset, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, "Join curated, 1.7-compatible servers.", this.width / 2, 32 - scrollOffset, 0xAAAAAA);

        int y = START_Y - scrollOffset;
        int cardWidth = Math.min(MAX_CARD_WIDTH, this.width - 40);
        int cardLeft = this.width / 2 - cardWidth / 2;
        boolean narrow = cardWidth < (2 * 110 + 12 + CARD_PADDING * 2);
        int rowHeight = narrow ? 96 : ROW_HEIGHT;
        // ensure button positions keep in sync with current layout
        layoutButtons();

        int listTop = START_Y;
        int listBottom = this.height - BOTTOM_RESERVED;
        int listLeft = cardLeft;
        int listRight = this.width / 2 + cardWidth / 2;

        // Enable scissor to clip list drawing region
        ScaledResolution sr = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        int scale = sr.getScaleFactor();
        int scX = listLeft * scale;
        int scY = (this.height - listBottom) * scale; // convert to bottom-left origin
        int scW = (listRight - listLeft) * scale;
        int scH = (listBottom - listTop) * scale;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        //PlatformOpenGL._wglScissor(scX, scY, scW, scH);

        for (int i = 0; i < supportedServers.size(); i++) {
            ServerData data = supportedServers.get(i);
            int cardRight = this.width / 2 + cardWidth / 2;
            int cardTop = y;
            int cardBottom = y + rowHeight - 8;

            // Skip drawing if outside list area for perf
            if (cardBottom >= listTop && cardTop <= listBottom) {
                drawRect(cardLeft, cardTop, cardRight, cardBottom, 0x88000000);
                drawRect(cardLeft, cardTop, cardRight, cardTop + 1, 0x33FFFFFF);
                drawRect(cardLeft, cardTop, cardLeft + 1, cardBottom, 0x33000000);
                drawRect(cardRight - 1, cardTop, cardRight, cardBottom, 0x33000000);
                drawRect(cardLeft, cardBottom - 1, cardRight, cardBottom, 0x33000000);

                int textX = cardLeft + CARD_PADDING;
                int nameY = cardTop + CARD_PADDING;
                int ipY = nameY + this.fontRendererObj.FONT_HEIGHT + 6;

                this.fontRendererObj.drawString(data.serverName, textX, nameY, 0xFFFFFF);
                this.fontRendererObj.drawString(data.serverIP, textX, ipY, 0xCCCCCC);
            }

            y += rowHeight;
        }

        // Disable scissor before drawing other UI (like the back button)
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        // Draw a simple scrollbar on the right edge of the list area
        int contentHeight = supportedServers.size() * rowHeight;
        int viewportHeight = listBottom - listTop;
        if (contentHeight > viewportHeight) {
            int barX = listRight + 6;
            int barW = 4;
            // track
            drawRect(barX, listTop, barX + barW, listBottom, 0x33000000);
            // thumb
            float fracVisible = viewportHeight / (float) contentHeight;
            int thumbH = Math.max(16, (int) (viewportHeight * fracVisible));
            float fracScroll = scrollOffset / (float) (contentHeight - viewportHeight);
            int thumbY = listTop + (int) ((viewportHeight - thumbH) * fracScroll);
            drawRect(barX, thumbY, barX + barW, thumbY + thumbH, 0x88FFFFFF);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
