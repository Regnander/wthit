package mcp.mobius.waila.plugin.vanilla.config;

import net.minecraft.resources.ResourceLocation;

public final class Options {

    // @formatter:off
    public static final ResourceLocation BREAKING_PROGRESS             = rl("breaking_progress.enabled");
    public static final ResourceLocation BREAKING_PROGRESS_COLOR       = rl("breaking_progress.color");
    public static final ResourceLocation BREAKING_PROGRESS_BOTTOM_ONLY = rl("breaking_progress.bottom_only");
    public static final ResourceLocation FURNACE_CONTENTS              = rl("furnace_contents");
    public static final ResourceLocation ITEM_ENTITY                   = rl("item_entity");
    public static final ResourceLocation OVERRIDE_INFESTED             = rl("override.infested");
    public static final ResourceLocation OVERRIDE_TRAPPED_CHEST        = rl("override.trapped_chest");
    public static final ResourceLocation OVERRIDE_POWDER_SNOW          = rl("override.powder_snow");
    public static final ResourceLocation PET_OWNER                     = rl("pet.owner");
    public static final ResourceLocation PET_HIDE_UNKNOWN_OWNER        = rl("pet.hide_unknown_owner");
    public static final ResourceLocation SPAWNER_TYPE                  = rl("spawner_type");
    public static final ResourceLocation CROP_PROGRESS                 = rl("crop_progress");
    public static final ResourceLocation REDSTONE_LEVER                = rl("redstone.lever");
    public static final ResourceLocation REDSTONE_REPEATER             = rl("redstone.repeater");
    public static final ResourceLocation REDSTONE_COMPARATOR           = rl("redstone.comparator");
    public static final ResourceLocation REDSTONE_LEVEL                = rl("redstone.level");
    public static final ResourceLocation JUKEBOX_RECORD                = rl("jukebox.record");
    public static final ResourceLocation PLAYER_HEAD_NAME              = rl("player_head.name");
    public static final ResourceLocation LEVEL_COMPOSTER               = rl("level.composter");
    public static final ResourceLocation LEVEL_HONEY                   = rl("level.honey");
    public static final ResourceLocation NOTE_BLOCK_TYPE               = rl("note_block.type");
    public static final ResourceLocation NOTE_BLOCK_NOTE               = rl("note_block.note");
    public static final ResourceLocation NOTE_BLOCK_INT_VALUE          = rl("note_block.int_value");
    public static final ResourceLocation TIMER_GROW                    = rl("timer.grow");
    public static final ResourceLocation TIMER_BREED                   = rl("timer.breed");
    // @formatter:on

    public enum NoteDisplayMode {
        SHARP, FLAT
    }

    private static ResourceLocation rl(String rl) {
        return new ResourceLocation(rl);
    }

}
