package com.tgbot.service.impl;

import com.tgbot.entity.AppDocument;
import com.tgbot.entity.AppPhoto;
import com.tgbot.entity.BinaryContent;
import com.tgbot.repository.AppDocumentRepository;
import com.tgbot.repository.AppPhotoRepository;
import com.tgbot.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final AppDocumentRepository appDocumentRepository;
    private final AppPhotoRepository appPhotoRepository;

    @Override
    public AppDocument getDocument(String docId) {
        var id = Long.parseLong(docId);
        return appDocumentRepository.findById(id).orElse(null); //todo add exception throw
    }

    @Override
    public AppPhoto getPhoto(String photoId) {
        var id = Long.parseLong(photoId);
        return appPhotoRepository.findById(id).orElse(null); //todo add exception throw
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try {
            File temp = File.createTempFile("tempFile", ".bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(temp);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
