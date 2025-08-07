package com.ffanxxy.displayer.blocks.custom;

import com.ffanxxy.displayer.blocks.blockEntity.custom.DisplayBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DisplayBlock extends BlockWithEntity {
    public DisplayBlock() {
        super(Settings.create().strength(4f));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DisplayBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DisplayBlockEntity displayBlockEntity) {
            displayBlockEntity.ImageDownAndLoad();
        }
        return ActionResult.SUCCESS;
    }
}
