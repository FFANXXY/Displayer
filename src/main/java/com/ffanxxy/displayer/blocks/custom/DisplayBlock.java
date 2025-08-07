package com.ffanxxy.displayer.blocks.custom;

import com.ffanxxy.displayer.blocks.blockEntity.custom.DisplayBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class DisplayBlock extends BlockWithEntity {
    public DisplayBlock() {
        super(Settings.create().strength(4f));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DisplayBlockEntity(pos, state);
    }
}
