package com.ffanxxy.displayer.blocks.blockEntity.custom;

import com.ffanxxy.displayer.Displayer;
import com.ffanxxy.displayer.blocks.blockEntity.ModBlockEntityTypes;
import com.ffanxxy.displayer.utils.net.NetImagesHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;

public class DisplayBlockEntity extends BlockEntity {

    private String URL = "https://fabricmc.net/assets/logo.png";
    private byte[] imageData = new byte[]{};
    private Boolean textureDirty = true;
    private Identifier textureId;

    private int facing = 1;

    public DisplayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.DISPLAY_BLOCK_ENTITY, pos, state);
    }

    public void setURL(String URL) {
        this.URL = URL;
        markDirty();
    }
    public String getURL() {
        return this.URL;
    }
    public byte[] getImageData() {
        return this.imageData;
    }
    public Boolean isTextureDirty() {
        return this.textureDirty;
    }
    public void setImageData(byte[] data) {
        this.imageData = data;
        this.textureDirty = true;
        markDirty();

        // 通知客户端更新
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }
    public void markTextureClean() {
        textureDirty = false;
    }
    @Nullable
    public Identifier getTextureId() {
        return textureId;
    }
    public void setTextureId(Identifier id) {
        this.textureId = id;
    }
    public void setFacing(Direction direction) {
        if(direction == Direction.NORTH) {
            facing = 1;
        } else if (direction == Direction.SOUTH) {
            facing = 2;
        } else if (direction == Direction.WEST) {
            facing = 3;
        } else if (direction == Direction.EAST) {
            facing = 4;
        }
    }
    public Direction getDirection() {
        return switch (facing) {
            case 2 -> Direction.SOUTH;
            case 3 -> Direction.WEST;
            case 4 -> Direction.EAST;
            default -> Direction.NORTH;
        };

    }

    // 序列化方块实体
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("URL", this.URL);
        if (imageData != null) {
            nbt.putByteArray("imageData", imageData);
        }
        nbt.putInt("facing", this.facing);
        nbt.putBoolean("textureDirty", textureDirty);
    }
    // 反序列化方块实体
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        URL = nbt.getString("URL");
        if (nbt.contains("imageData", NbtElement.BYTE_ARRAY_TYPE)) {
            imageData = nbt.getByteArray("imageData");
            textureDirty = true;
        }
        facing = nbt.getInt("facing");
        textureDirty = nbt.getBoolean("textureDirty");
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

    /*
    *
    * 下面的都是为了渲染的逻辑所操控的数据
    *
    *  */

    public void ImageDownAndLoad() {
        try {
            this.imageData = NetImagesHelper.downloadImage(this.URL).get();
            this.textureDirty = true;
        }catch (ExecutionException | InterruptedException e) {
            Displayer.LOGGER.error("Error when download and load image {} , \n Cause by {}", e.getMessage(), e.getCause());
            e.fillInStackTrace();
        }
    }
}
