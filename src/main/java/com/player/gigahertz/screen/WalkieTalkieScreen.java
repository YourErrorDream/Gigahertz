package com.player.gigahertz.screen;

import com.player.gigahertz.network.ModNetwork;
import com.player.gigahertz.network.RequestSignalPacket;
import com.player.gigahertz.network.SendMessagePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class WalkieTalkieScreen extends Screen {

    private EditBox messageBox;

    // Сигнал: -1 = ожидание ответа от сервера
    private int     signalLevel = -1;
    private boolean towerMode   = false;

    private static final int PANEL_W = 220;
    private static final int PANEL_H = 90; // чуть выше для строки сигнала

    public WalkieTalkieScreen() {
        super(Component.literal("1G Walkie-Talkie"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;

        messageBox = new EditBox(
                this.font,
                cx - (PANEL_W / 2) + 5, cy - 8,
                PANEL_W - 10, 20,
                Component.literal("Type a message...")
        );
        messageBox.setMaxLength(256);
        messageBox.setHint(Component.literal("Type a message..."));
        messageBox.setFocused(true);
        this.addRenderableWidget(messageBox);

        this.addRenderableWidget(
                Button.builder(Component.literal("Send"), btn -> sendMessage())
                        .bounds(cx - 40, cy + 22, 80, 20)
                        .build()
        );

        // Запрашиваем уровень сигнала у сервера сразу при открытии
        ModNetwork.CHANNEL.sendToServer(new RequestSignalPacket());
    }

    /** Вызывается из SignalResponsePacket на главном потоке. */
    public void updateSignal(int level, boolean tower) {
        this.signalLevel = level;
        this.towerMode   = tower;
    }

    private void sendMessage() {
        String msg = messageBox.getValue().strip();
        if (!msg.isEmpty()) {
            ModNetwork.CHANNEL.sendToServer(new SendMessagePacket(msg));
        }
        this.onClose();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);

        int cx = this.width / 2;
        int cy = this.height / 2;
        int x0 = cx - PANEL_W / 2;
        int y0 = cy - PANEL_H / 2;

        // Фон панели
        g.fill(x0, y0, x0 + PANEL_W, y0 + PANEL_H, 0xCC111111);

        // Зелёная рамка
        g.fill(x0,              y0,               x0 + PANEL_W, y0 + 1,          0xFF22BB22);
        g.fill(x0,              y0 + PANEL_H - 1, x0 + PANEL_W, y0 + PANEL_H,    0xFF22BB22);
        g.fill(x0,              y0,               x0 + 1,       y0 + PANEL_H,    0xFF22BB22);
        g.fill(x0 + PANEL_W - 1, y0,             x0 + PANEL_W, y0 + PANEL_H,    0xFF22BB22);

        // Заголовок
        g.drawCenteredString(this.font, this.title, cx, y0 + 7, 0x22FF22);

        // Индикатор сигнала (верхний правый угол панели)
        renderSignalBars(g, x0 + PANEL_W - 28, y0 + 4);

        // Режим — нижний левый угол
        String modeStr = signalLevel == -1
                ? "§7..."
                : (towerMode ? "§a⬆ Tower" : "§7~ Direct");
        g.drawString(this.font, modeStr, x0 + 6, y0 + PANEL_H - 11, 0xFFFFFF, false);

        super.render(g, mouseX, mouseY, partialTick);
    }

    /**
     * Рисует 4 столбика сигнала, как на телефоне.
     * x, y — верхний левый угол области (24×12 px).
     */
    private void renderSignalBars(GuiGraphics g, int x, int y) {
        // Высоты столбиков (от низкого к высокому)
        int[] heights = {4, 6, 8, 10};
        int barW = 4;
        int gap  = 1;
        int baseY = y + 11; // нижняя линия столбиков

        for (int i = 0; i < 4; i++) {
            int bh   = heights[i];
            int bx   = x + i * (barW + gap);
            int by   = baseY - bh;
            int color;

            if (signalLevel == -1) {
                // Ожидание — мигающий серый (используем время)
                long t = System.currentTimeMillis() / 400;
                color = (t % 2 == 0) ? 0xFF666666 : 0xFF333333;
            } else if (i < signalLevel) {
                // Активный столбик — цвет зависит от уровня
                color = switch (signalLevel) {
                    case 1 -> 0xFFFF3333; // красный — почти нет сигнала
                    case 2 -> 0xFFFFAA00; // оранжевый
                    case 3 -> 0xFFFFFF00; // жёлтый
                    case 4 -> 0xFF44FF44; // зелёный — отличный
                    default -> 0xFF888888;
                };
            } else {
                // Неактивный столбик
                color = 0xFF333333;
            }

            g.fill(bx, by, bx + barW, baseY, color);
        }

        // Цифровая подпись под столбиками
        String label = signalLevel == -1 ? "" : signalLevel + "/4";
        g.drawString(this.font, label, x, baseY + 2, 0x888888, false);
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
        return false;
    }
}