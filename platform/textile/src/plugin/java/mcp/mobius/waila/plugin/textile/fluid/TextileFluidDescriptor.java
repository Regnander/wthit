package mcp.mobius.waila.plugin.textile.fluid;

import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.api.data.FluidData.CauldronDescriptor;
import mcp.mobius.waila.api.data.FluidData.FluidDescription;
import mcp.mobius.waila.api.data.FluidData.FluidDescriptionContext;
import mcp.mobius.waila.api.data.FluidData.FluidDescriptor;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.CauldronFluidContent;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public enum TextileFluidDescriptor implements FluidDescriptor<Fluid>, CauldronDescriptor {

    INSTANCE;

    @Override
    public void describeFluid(FluidDescriptionContext<Fluid> ctx, FluidDescription desc) {
        FluidVariant variant = FluidVariant.of(ctx.fluid(), ctx.nbt());
        desc.name(FluidVariantAttributes.getName(variant));

        TextureAtlasSprite sprite = FluidVariantRendering.getSprite(variant);
        if (sprite != null) {
            desc.sprite(sprite)
                .tint(FluidVariantRendering.getColor(variant));
        }
    }

    @Override
    public @Nullable FluidData getCauldronFluidData(BlockState state) {
        CauldronFluidContent content = CauldronFluidContent.getForBlock(state.getBlock());
        if (content == null || content.fluid == Fluids.EMPTY) return null;

        double stored = (content.currentLevel(state) * content.amountPerLevel) / 81.0;
        double capacity = (content.maxLevel * content.amountPerLevel) / 81.0;
        return FluidData.of(1).add(content.fluid, null, stored, capacity);
    }

}
