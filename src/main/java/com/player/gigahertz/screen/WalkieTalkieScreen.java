package com.player.gigahertz.screen;

import com.player.gigahertz.network.ModNetwork;
import com.player.gigahertz.network.SendMessagePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class WalkieTalkieScreen extends Screen {

    private EditBox messageBox;

    private static final int PANEL_W = 220;
    private static final int PANEL_H = 80;

    public WalkieTalkieScreen() {
        super(Component.literal("1G Walkie-Talkie"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;

        messageBox = new EditBox(
                this.font,
                cx - (PANEL_W / 2) + 5, cy - 10,
                PANEL_W - 10, 20,
                Component.literal("Type a message...")
        );
        messageBox.setMaxLength(256);
        messageBox.setHint(Component.literal("Type a message..."));
        messageBox.setFocused(true);
        this.addRenderableWidget(messageBox);

        this.addRenderableWidget(
                Button.builder(Component.literal("Send"), btn -> sendMessage())
                        .bounds(cx - 40, cy + 20, 80, 20)
                        .build()
        );
    }

    private void sendMessage() {
        String msg = messageBox.getValue().strip();
        if (!msg.isEmpty()) {
            ModNetwork.CHANNEL.sendToServer(new SendMessagePacket(msg));
        }
        this.onClose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        int cx = this.width / 2;
        int cy = this.height / 2;
        int x0 = cx - PANEL_W / 2;
        int y0 = cy - PANEL_H / 2;

        // Dark translucent panel
        graphics.fill(x0, y0, x0 + PANEL_W, y0 + PANEL_H, 0xCC111111);
        // Green border (retro radio feel)
        graphics.fill(x0,              y0,              x0 + PANEL_W, y0 + 1,         0xFF22BB22);
        graphics.fill(x0,              y0 + PANEL_H - 1, x0 + PANEL_W, y0 + PANEL_H, 0xFF22BB22);
        graphics.fill(x0,              y0,              x0 + 1,       y0 + PANEL_H,  0xFF22BB22);
        graphics.fill(x0 + PANEL_W - 1, y0,            x0 + PANEL_W, y0 + PANEL_H,  0xFF22BB22);

        // Title
        graphics.drawCenteredString(this.font, this.title, cx, y0 + 8, 0x22FF22);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            sendMessage();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        // Game keeps running while walkie-talkie is open
        return false;
    }
}