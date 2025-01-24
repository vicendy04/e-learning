package com.myproject.elearning.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class FileService {
    @Value(value = "${file.folder_uri}")
    String folderURI;

    public String store(MultipartFile file, String folderName) throws IOException {
        String encodedFileName =
                URLEncoder.encode(Objects.requireNonNull(file.getOriginalFilename()), StandardCharsets.UTF_8);
        String finalName = System.currentTimeMillis() + "-" + encodedFileName;
        Path directoryPath = Paths.get(folderURI, folderName);
        Path filePath = directoryPath.resolve(finalName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        return finalName;
    }
}
