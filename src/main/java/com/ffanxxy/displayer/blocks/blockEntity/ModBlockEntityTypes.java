package com.ffanxxy.displayer.blocks.blockEntity;

import com.ffanxxy.displayer.Displayer;
import com.ffanxxy.displayer.blocks.ModBlocks;
import com.ffanxxy.displayer.blocks.blockEntity.custom.DisplayBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntityTypes {
    public static <T extends BlockEntityType<?>> T register(String path, T blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Displayer.MOD_ID, path), blockEntityType);
    }

    public static final BlockEntityType<DisplayBlockEntity> DISPLAY_BLOCK_ENTITY = register(
            "display_block_entity",

            FabricBlockEntityTypeBuilder.create(DisplayBlockEntity::new, ModBlocks.DISPLAY_BLOCK).build(null)
    );

    public static void initialize() {
    }
}
