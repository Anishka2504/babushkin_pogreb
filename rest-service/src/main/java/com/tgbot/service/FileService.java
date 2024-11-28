package com.tgbot.service;

import com.tgbot.entity.AppDocument;
import com.tgbot.entity.AppPhoto;
import com.tgbot.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {

    AppDocument getDocument(String hashId);

    AppPhoto getPhoto(String hashId);

    FileSystemResource getFileSystemResource(BinaryContent binaryContent);


}
