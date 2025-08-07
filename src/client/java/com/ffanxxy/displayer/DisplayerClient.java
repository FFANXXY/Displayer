package com.ffanxxy.displayer;

import com.ffanxxy.displayer.blocks.blockEntity.ModBlockEntityTypes;
import com.ffanxxy.displayer.blocks.blockEntity.custom.DisplayBlockEntity;
import com.ffanxxy.displayer.render.DisplayBlockRender;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public class DisplayerClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistry.register(ModBlockEntityTypes.DISPLAY_BLOCK_ENTITY, DisplayBlockRender::new);
	}
}