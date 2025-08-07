package com.ffanxxy.displayer.blocks.blockEntity.custom;

import com.ffanxxy.displayer.blocks.blockEntity.ModBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class DisplayBlockEntity extends BlockEntity {

    private String URL;
    private byte imageData;

    public DisplayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.DISPLAY_BLOCK_ENTITY, pos, state);
    }

    // 序列化方块实体
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("URL", this.URL);
    }
    // 反序列化方块实体
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        URL = nbt.getString("URL");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return super.toInitialChunkDataNbt();
    }
}
