package mcp.mobius.waila.plugin.extra.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.GsonBuilder;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataReader;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.plugin.extra.WailaPluginExtra;
import mcp.mobius.waila.plugin.extra.config.ExtraBlacklistConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public abstract class DataProvider<T extends IData> implements IBlockComponentProvider, IEntityComponentProvider {

    public static final Map<ResourceLocation, DataProvider<?>> INSTANCES = new HashMap<>();

    private final ResourceLocation id;
    private final Class<T> type;
    private final IData.Serializer<T> serializer;
    private final ResourceLocation enabledOption;
    private final TagKey<Block> blockBlacklistTag;
    private final TagKey<BlockEntityType<?>> blockEntityBlacklistTag;
    private final TagKey<EntityType<?>> entityBlacklistTag;
    private final IJsonConfig<ExtraBlacklistConfig> blacklistConfig;

    protected DataProvider(ResourceLocation id, Class<T> type, IData.Serializer<T> serializer) {
        this.id = id;
        this.type = type;
        this.serializer = serializer;

        enabledOption = createConfigKey("enabled");

        ResourceLocation tagId = new ResourceLocation(WailaConstants.NAMESPACE, "extra/" + id.getPath() + "_blacklist");
        blockBlacklistTag = TagKey.create(Registries.BLOCK, tagId);
        blockEntityBlacklistTag = TagKey.create(Registries.BLOCK_ENTITY_TYPE, tagId);
        entityBlacklistTag = TagKey.create(Registries.ENTITY_TYPE, tagId);

        blacklistConfig = IJsonConfig.of(ExtraBlacklistConfig.class)
            .file(WailaConstants.NAMESPACE + "/extra/" + id.getPath() + "_blacklist")
            .gson(new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(ExtraBlacklistConfig.class, new ExtraBlacklistConfig.Adapter(tagId))
                .create())
            .build();

        blacklistConfig.save();

        INSTANCES.put(id, this);
    }

    public void register(IRegistrar registrar, int priority) {
        if (!WailaPluginExtra.BOOTSTRAPPED.contains(type)) return;

        registrar.addMergedSyncedConfig(enabledOption, true, false);
        register(registrar);
        registrar.addConfig(createConfigKey("blacklist"), blacklistConfig.getPath());

        registrar.addDataType(id, type, serializer);
        registrar.addComponent((IBlockComponentProvider) this, TooltipPosition.BODY, BlockEntity.class, priority);
        registrar.addComponent((IEntityComponentProvider) this, TooltipPosition.BODY, Entity.class, priority);
        registrar.addBlockData(new BlockDataProvider(), BlockEntity.class, 0);
        registrar.addEntityData(new EntityDataProvider(), Entity.class, 0);
    }

    protected final ResourceLocation createConfigKey(String path) {
        return new ResourceLocation(WailaConstants.NAMESPACE + "x", id.getPath() + "." + path);
    }

    protected void register(IRegistrar registrar) {
    }

    protected abstract void appendBody(ITooltip tooltip, T t, IPluginConfig config, ResourceLocation objectId);

    protected void appendBody(ITooltip tooltip, IDataReader reader, IPluginConfig config, ResourceLocation objectId) {
        T data = reader.get(type);
        if (data == null) return;
        if (!config.getBoolean(enabledOption)) return;

        appendBody(tooltip, data, config, objectId);
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        BlockEntityType<?> blockEntityType = Objects.<BlockEntity>requireNonNull(accessor.getBlockEntity()).getType();
        if (blacklistConfig.get().blockEntityTypes.contains(blockEntityType)) return;

        appendBody(tooltip, accessor.getData(), config, BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntityType));
    }

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        EntityType<?> entityType = accessor.getEntity().getType();
        if (blacklistConfig.get().entityTypes.contains(entityType)) return;

        appendBody(tooltip, accessor.getData(), config, BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
    }

    private class BlockDataProvider implements IDataProvider<BlockEntity> {

        @Override
        public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
            if (!config.getBoolean(enabledOption)
                || blacklistConfig.get().blockEntityTypes.contains(accessor.getTarget().getType())
                || accessor.getTarget().getBlockState().is(blockBlacklistTag)
                || BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(accessor.getTarget().getType()).is(blockEntityBlacklistTag)) {
                data.add(type, IDataWriter.Result::block);
            }
        }

    }

    private class EntityDataProvider implements IDataProvider<Entity> {

        @Override
        public void appendData(IDataWriter data, IServerAccessor<Entity> accessor, IPluginConfig config) {
            if (!config.getBoolean(enabledOption)
                || blacklistConfig.get().entityTypes.contains(accessor.getTarget().getType())
                || accessor.getTarget().getType().is(entityBlacklistTag)) {
                data.add(type, IDataWriter.Result::block);
            }
        }

    }

}