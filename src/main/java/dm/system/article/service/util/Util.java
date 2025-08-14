package dm.system.article.service.util;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
public class Util {

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static String detectContentType(String fileName) {
        try {
            String mimeType = Files.probeContentType(Paths.get(fileName));
            return mimeType != null ? mimeType : "application/octet-stream";
        } catch (Exception e) {
            log.error("Error detecting content type for file {}. Exception: {}", fileName, e);
            return "application/octet-stream";
        }
    }
}
