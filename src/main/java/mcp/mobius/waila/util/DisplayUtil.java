package mcp.mobius.waila.util;

import java.util.IllegalFormatException;
import java.util.Random;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.ITooltipComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public final class DisplayUtil {

    private static final Random RANDOM = new Random();

    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static void enable3DRender() {
        Lighting.setupFor3DItems();
        RenderSystem.enableDepthTest();
    }

    public static void enable2DRender() {
        Lighting.setupForFlatItems();
        RenderSystem.disableDepthTest();
    }

    public static void renderRectBorder(Matrix4f matrix, BufferBuilder buf, int x, int y, int w, int h, int s, int gradStart, int gradEnd) {
        if (s <= 0) {
            return;
        }

        // @formatter:off
        fillGradient(matrix, buf, x        , y        , w, s          , gradStart, gradStart);
        fillGradient(matrix, buf, x        , y + h - s, w, s          , gradEnd  , gradEnd);
        fillGradient(matrix, buf, x        , y + s    , s, h - (s * 2), gradStart, gradEnd);
        fillGradient(matrix, buf, x + w - s, y + s    , s, h - (s * 2), gradStart, gradEnd);
        // @formatter:on
    }

    public static void renderComponent(GuiGraphics ctx, ITooltipComponent component, int x, int y, int cw, float delta) {
        component.render(ctx, x, y, delta);

        if (WailaClient.showComponentBounds) {
            ctx.pose().pushPose();
            float scale = (float) Minecraft.getInstance().getWindow().getGuiScale();
            ctx.pose().scale(1 / scale, 1 / scale, 1);

            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buf = tesselator.getBuilder();
            buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            int bx = Mth.floor(x * scale + 0.5);
            int by = Mth.floor(y * scale + 0.5);
            int bw = Mth.floor((cw == 0 ? component.getWidth() : cw) * scale + 0.5);
            int bh = Mth.floor(component.getHeight() * scale + 0.5);
            int color = (0xFF << 24) + Mth.hsvToRgb(RANDOM.nextFloat(), RANDOM.nextFloat(), 1f);
            renderRectBorder(ctx.pose().last().pose(), buf, bx, by, bw, bh, 1, color, color);
            tesselator.end();

            ctx.pose().popPose();
        }
    }

    public static void fillGradient(Matrix4f matrix, BufferBuilder buf, int x, int y, int w, int h, int start, int end) {
        float sa = FastColor.ARGB32.alpha(start) / 255.0F;
        float sr = FastColor.ARGB32.red(start) / 255.0F;
        float sg = FastColor.ARGB32.green(start) / 255.0F;
        float sb = FastColor.ARGB32.blue(start) / 255.0F;

        float ea = FastColor.ARGB32.alpha(end) / 255.0F;
        float er = FastColor.ARGB32.red(end) / 255.0F;
        float eg = FastColor.ARGB32.green(end) / 255.0F;
        float eb = FastColor.ARGB32.blue(end) / 255.0F;

        buf.vertex(matrix, x, y, 0).color(sr, sg, sb, sa).endVertex();
        buf.vertex(matrix, x, y + h, 0).color(er, eg, eb, ea).endVertex();
        buf.vertex(matrix, x + w, y + h, 0).color(er, eg, eb, ea).endVertex();
        buf.vertex(matrix, x + w, y, 0).color(sr, sg, sb, sa).endVertex();
    }

    public static int getAlphaFromPercentage(int percentage) {
        return percentage == 100 ? 255 << 24 : percentage == 0 ? (int) (0.4F / 100.0F * 256) << 24 : (int) (percentage / 100.0F * 256) << 24;
    }

    public static String tryFormat(String format, Object... args) {
        try {
            return format.formatted(args);
        } catch (IllegalFormatException e) {
            return "FORMATTING ERROR";
        }
    }

    public static Button createButton(int x, int y, int width, int height, Component label, Button.OnPress pressAction) {
        return Button.builder(label, pressAction).bounds(x, y, width, height).build();
    }

}
