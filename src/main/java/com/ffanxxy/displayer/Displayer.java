package com.ffanxxy.displayer;


import com.ffanxxy.displayer.blocks.ModBlocks;
import com.ffanxxy.displayer.blocks.blockEntity.ModBlockEntityTypes;
import com.ffanxxy.displayer.utils.FilesManger;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Displayer implements ModInitializer {
	public static final String MOD_ID = "displayer";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.initialize();
		ModBlockEntityTypes.initialize();

//		获得文件存储路径
		ServerTickEvents.START_SERVER_TICK.register(Displayer::setFileStorePath);
	}

	public static FilesManger FileManger;

	public static void setFileStorePath(MinecraftServer server)  {
		FileManger = new FilesManger(server);
		Path worldDir = server.getSavePath(WorldSavePath.ROOT).toAbsolutePath();

		FileManger.modImagesDir = worldDir.resolve(MOD_ID).resolve("images");
		try {
			Files.createDirectories(FileManger.modImagesDir); // 确保目录存在
		}catch (IOException e) {
			LOGGER.error("[Displayer/IO] We have some problems when Creating Directories : {}", e.getMessage());
			e.fillInStackTrace();
			return;
		}
	}
}