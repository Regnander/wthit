package mcp.mobius.waila.plugin.extra.provider;

import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipLine;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.api.component.ProgressArrowComponent;
import mcp.mobius.waila.api.data.ProgressData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ProgressProvider extends DataProvider<ProgressData> {

    public static final ProgressProvider INSTANCE = new ProgressProvider();

    private ProgressProvider() {
        super(ProgressData.ID, ProgressData.class, ProgressData::new);
    }

    @Override
    protected void appendBody(ITooltip tooltip, ProgressData progress, IPluginConfig config, ResourceLocation objectId) {
        if (progress.ratio() == 0f) return;

        ITooltipLine line = tooltip.setLine(ProgressData.ID);

        for (ItemStack stack : progress.input()) {
            line.with(new ItemComponent(stack));
        }

        line.with(new ProgressArrowComponent(progress.ratio()));

        for (ItemStack stack : progress.output()) {
            line.with(new ItemComponent(stack));
        }
    }

}
