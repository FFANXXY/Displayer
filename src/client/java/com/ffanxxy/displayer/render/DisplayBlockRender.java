package com.ffanxxy.displayer.render;

import com.ffanxxy.displayer.Displayer;
import com.ffanxxy.displayer.blocks.blockEntity.custom.DisplayBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.DynamicTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import java.awt.image.renderable.RenderContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DisplayBlockRender implements BlockEntityRenderer<DisplayBlockEntity> {

    public DisplayBlockRender(BlockEntityRendererFactory.Context ctx) {
    }

    private static final float PLANE_DEPTH = 0.0625f; // 1/16 方块深度
    private static final float OFFSET = 0.005f; // 避免深度冲突


    @Override
    public void render(DisplayBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getImageData() == null || entity.getImageData().length == 0) return;

        // 获取或创建纹理
        Identifier textureId = getOrCreateTexture(entity);
        if (textureId == null) return;

        matrices.push();

        // 获取方块朝向
        Direction facing = entity.getDirection();

        // 调整位置和旋转
        setupTransform(matrices, facing);

        // 获取顶点消费者
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(textureId));

        // 创建平面模型
        renderSignPlane(matrices, vertexConsumer, light, overlay);

        matrices.pop();
    }

    private void setupTransform(MatrixStack matrices, Direction facing) {
        // 移动到方块中心
        matrices.translate(0.5, 0.5, 0.5);

        // 根据朝向旋转
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        // 移动到平面位置（轻微突出）
        matrices.translate(0.0, 0.0, 0.5 + OFFSET);

        // 缩放为平面
        matrices.scale(1.0f, 1.0f, PLANE_DEPTH);

        // 移回原点
        matrices.translate(-0.5, -0.5, -0.5);
    }

    private void renderSignPlane(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        // 前平面
        // 左下
        vertexConsumer.vertex(matrix, 0, 0, 0)
                .color(1f, 1f, 1f, 1f)
                .texture(0, 1)
                .overlay(overlay)
                .light(light)
                .normal(0, 0, 1)
                .next();

        // 左上
        vertexConsumer.vertex(matrix, 0, 1, 0)
                .color(1f, 1f, 1f, 1f)
                .texture(0, 0)
                .overlay(overlay)
                .light(light)
                .normal(0, 0, 1)
                .next();

        // 右上
        vertexConsumer.vertex(matrix, 1, 1, 0)
                .color(1f, 1f, 1f, 1f)
                .texture(1, 0)
                .overlay(overlay)
                .light(light)
                .normal(0, 0, 1)
                .next();

        // 右下
        vertexConsumer.vertex(matrix, 1, 0, 0)
                .color(1f, 1f, 1f, 1f)
                .texture(1, 1)
                .overlay(overlay)
                .light(light)
                .normal(0, 0, 1)
                .next();

        // 后平面（双面渲染）
        // 右下
        vertexConsumer.vertex(matrix, 1, 0, PLANE_DEPTH)
                .color(1f, 1f, 1f, 1f)
                .texture(1, 1)
                .overlay(overlay)
                .light(light)
                .normal(0, 0, -1)
                .next();

        // 右上
        vertexConsumer.vertex(matrix, 1, 1, PLANE_DEPTH)
                .color(1f, 1f, 1f, 1f)
                .texture(1, 0)
                .overlay(overlay)
                .light(light)
                .normal(0, 0, -1)
                .next();

        // 左上
        vertexConsumer.vertex(matrix, 0, 1, PLANE_DEPTH)
                .color(1f, 1f, 1f, 1f)
                .texture(0, 0)
                .overlay(overlay)
                .light(light)
                .normal(0, 0, -1)
                .next();

        // 左下
        vertexConsumer.vertex(matrix, 0, 0, PLANE_DEPTH)
                .color(1f, 1f, 1f, 1f)
                .texture(0, 1)
                .overlay(overlay)
                .light(light)
                .normal(0, 0, -1)
                .next();
    }

    private Identifier getOrCreateTexture(DisplayBlockEntity entity) {
        if (entity.isTextureDirty() || entity.getTextureId() == null) {
            createTextureForEntity(entity);
        }
        return entity.getTextureId();
    }

    private void createTextureForEntity(DisplayBlockEntity entity) {
        try {
            // 从字节数组创建纹理
            NativeImage image = NativeImage.read(new ByteArrayInputStream(entity.getImageData()));

            // 创建动态纹理ID
            Identifier textureId = new Identifier("dynamic_texture_" + entity.getPos().asLong());

            // 注册纹理
            MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, new NativeImageBackedTexture(image));

            // 更新实体状态
            entity.setTextureId(textureId);
            entity.markTextureClean();

            // 释放NativeImage内存
            image.close();
        } catch (IOException e) {
            Displayer.LOGGER.error("Failed to create dynamic texture", e);
        }
    }

}
