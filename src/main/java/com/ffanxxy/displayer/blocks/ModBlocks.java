package com.ffanxxy.displayer.blocks;

import com.ffanxxy.displayer.Displayer;
import com.ffanxxy.displayer.blocks.custom.DisplayBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block DISPLAY_BLOCK = register("display_block", new DisplayBlock());

    private static <T extends Block> T register(String path, T block) {
        Registry.register(Registries.BLOCK, Identifier.of(Displayer.MOD_ID, path), block);
        Registry.register(Registries.ITEM, Identifier.of(Displayer.MOD_ID, path), new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static void initialize() {
    }
}
