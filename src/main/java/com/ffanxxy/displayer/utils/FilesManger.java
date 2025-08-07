package com.ffanxxy.displayer.utils;

import com.ffanxxy.displayer.Displayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

public class FilesManger {
    public Path modImagesDir;
    private final MinecraftServer server;

    // 图片最大大小
    public static int IMAGE_MAX_SIZE = 100 * 1024 * 1024;

    public FilesManger(MinecraftServer server) {
        this.server = server;
    }

    /**
     * 获取文件路径
     */
    private Path getFilePath(String fileName) {
        if (modImagesDir == null) {
            throw new IllegalStateException("Mod data directory not initialized");
        }
        return modImagesDir.resolve(fileName);
    }


    /**
     * 将图片写入
     * @param fileName
     * @param data
     * @return
     */
    public CompletableFuture<Boolean> storeImage(String fileName, byte[] data) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Path filePath = getFilePath(fileName);

        server.execute(() -> {
            try {
                Path tempFile = filePath.resolveSibling(filePath.getFileName() + ".tmp");
                Files.write(tempFile, data);

                Files.move(tempFile, filePath,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);

                Displayer.LOGGER.debug("Async saved {} bytes to: {}", data.length, filePath);
                future.complete(true);
            } catch (IOException e) {
                Displayer.LOGGER.error("Async failed to save bytes to: {}", filePath, e);
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    /**
     * 读取图片
     * @param fileName
     * @return
     */
    public CompletableFuture<byte[]> readBytesAsync(String fileName) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        Path filePath = getFilePath(fileName);

        server.execute(() -> {
            if (!Files.exists(filePath)) {
                Displayer.LOGGER.warn("Async file not found: {}", filePath);
                future.complete(null);
                return;
            }

            try {
                byte[] data = Files.readAllBytes(filePath);
                future.complete(data);
            } catch (IOException e) {
                Displayer.LOGGER.error("Async failed to read bytes from: {}", filePath, e);
                future.completeExceptionally(e);
            }
        });

        return future;
    }

}
