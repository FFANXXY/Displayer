package com.ffanxxy.displayer.utils.net;

import com.ffanxxy.displayer.Displayer;
import com.ffanxxy.displayer.utils.FilesManger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetImagesHelper {
    public static CompletableFuture<byte[]> downloadImage(String URL) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<byte[]> resultData = new CompletableFuture<>();

        executor.submit(() -> {
            try {
                URL url = new URL(URL);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(30 * 1000);
                connection.setReadTimeout(60 * 1000);    // 读取超时60秒
                connection.setInstanceFollowRedirects(true); // 跟随重定向

                // 检查HTTP响应码
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Displayer.LOGGER.error("Https错误{}", responseCode);
                    return;
                }

                String contentType = connection.getContentType();
                int contentLength = connection.getContentLength();
                String fileName = extractFileName(connection, url);
                String fileExtension = extractFileExtension(contentType);

                // 验证内容类型
                if (!isValidImageType(contentType)) {
                    Displayer.LOGGER.error("不支持的图片格式: {}", contentType);
                    return;
                }
                // 验证文件大小
                if (contentLength > FilesManger.IMAGE_MAX_SIZE) {
                    Displayer.LOGGER.error("文件过大: {} bytes", contentLength);
                    return;
                }

                // 安全读取数据
                InputStream inputStream = connection.getInputStream();
                byte[] data = readStream(inputStream, contentLength);

                resultData.complete(data);

                // 存储文件
                Displayer.FileManger.storeImage(fileName, data);

            } catch (Exception e) {
                e.fillInStackTrace();
                return;
            }
        });
        executor.shutdown();
        return resultData;
    }

    // 从Content-Disposition或URL中提取文件名
    private static String extractFileName(HttpURLConnection conn, URL url) {
        String fileName = null;

        // 尝试从Content-Disposition头获取文件名
        String disposition = conn.getHeaderField("Content-Disposition");
        if (disposition != null) {
            int index = disposition.indexOf("filename=");
            if (index > 0) {
                fileName = disposition.substring(index + 9)
                        .replace("\"", "")
                        .replace(";", "")
                        .trim();
            }
        }

        // 从URL路径获取文件名
        if (fileName == null) {
            String path = url.getPath();
            int lastSlash = path.lastIndexOf('/');
            if (lastSlash >= 0 && lastSlash < path.length() - 1) {
                fileName = path.substring(lastSlash + 1);
            }
        }

        // 默认文件名
        if (fileName == null || fileName.isEmpty()) {
            fileName = "image";
        }

        return fileName;
    }

    // 从Content-Type提取文件扩展名
    private static String extractFileExtension(String contentType) {
        if (contentType == null) return "bin";

        if (contentType.contains("jpeg")) return "jpg";
        if (contentType.contains("png")) return "png";
        if (contentType.contains("gif")) return "gif";
        if (contentType.contains("webp")) return "webp";
        if (contentType.contains("bmp")) return "bmp";

        return "bin";
    }

    // 验证是否是支持的图片类型
    private static boolean isValidImageType(String contentType) {
        return contentType != null && (
                contentType.startsWith("image/jpeg") ||
                        contentType.startsWith("image/png") ||
                        contentType.startsWith("image/gif") ||
                        contentType.startsWith("image/webp") ||
                        contentType.startsWith("image/bmp")
        );
    }

    // 安全读取流数据
    private static byte[] readStream(InputStream is, int expectedSize) throws IOException {
        // 创建合适大小的缓冲区
        int bufferSize = expectedSize > 0 ? expectedSize : 4096;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bufferSize);

        byte[] data = new byte[8192];
        int bytesRead;
        int totalRead = 0;

        while ((bytesRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
            totalRead += bytesRead;

            // 防止过大文件
            if (totalRead > FilesManger.IMAGE_MAX_SIZE) {
                throw new IOException("文件超过最大限制");
            }
        }

        return buffer.toByteArray();
    }
}
