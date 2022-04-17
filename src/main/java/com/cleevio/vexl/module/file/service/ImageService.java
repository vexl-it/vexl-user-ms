package com.cleevio.vexl.module.file.service;

import com.cleevio.vexl.module.file.dto.request.ImageRequest;
import com.cleevio.vexl.module.file.exception.FileWriteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {

    @Value("${content.path}")
    private String contentPath;

    /**
     * Store file to the storage
     *
     * @return Destination path
     * @throws FileWriteException File could not be stored
     */
    public String save(@NotNull ImageRequest imageRequest) throws FileWriteException {
        try {
            File dir = new File(this.contentPath);

            if (!dir.exists() && !dir.mkdirs()) {
                throw new FileWriteException();
            }

            String fileName = UUID.randomUUID().toString();

            final File destination = new File(dir, fileName + "." + imageRequest.getExtension());

            try (FileOutputStream stream = new FileOutputStream(destination)) {
                stream.write(Base64.getDecoder().decode(imageRequest.getData()));
            }

            log.info("created file {}", destination.getPath());

            return destination.getPath();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileWriteException();
        }
    }

    public void removeAvatar(@NotNull String destination) {
        try {
            Files.deleteIfExists(
                    Paths.get(destination));
        } catch (NoSuchFileException e) {
            log.error("No such file/directory exists");
        } catch (DirectoryNotEmptyException e) {
            log.error("Directory is not empty.");
        } catch (IOException e) {
            log.error("Invalid permissions.");
        }

        log.warn("Deletion successful.");

    }
}
