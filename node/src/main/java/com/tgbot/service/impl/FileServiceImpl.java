package com.tgbot.service.impl;

import com.tgbot.entity.AppDocument;
import com.tgbot.entity.AppPhoto;
import com.tgbot.entity.BinaryContent;
import com.tgbot.exception.UploadFileException;
import com.tgbot.repository.AppDocumentRepository;
import com.tgbot.repository.AppPhotoRepository;
import com.tgbot.repository.BinaryContentRepository;
import com.tgbot.service.FileService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Value("${bot.token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    private final AppDocumentRepository appDocumentRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final AppPhotoRepository appPhotoRepository;

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        String fileId = telegramMessage.getDocument().getFileId();
        ResponseEntity<String> response = getFileDataFromTelegram(fileId);
        if (HttpStatus.OK.equals(response.getStatusCode())) {
            BinaryContent binaryContent = getBinaryContent(response);
            Document telegramDoc = telegramMessage.getDocument();
            AppDocument appDocument = buildTransientAppDocument(telegramDoc, binaryContent);
            return appDocumentRepository.save(appDocument);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        //todo если отправить картинки пачкой, обработана будет только первая
        PhotoSize photo = telegramMessage.getPhoto().get(0);
        String fileId = photo.getFileId();
        ResponseEntity<String> response = getFileDataFromTelegram(fileId);
        if (HttpStatus.OK.equals(response.getStatusCode())) {
            BinaryContent binaryContent = getBinaryContent(response);
            AppPhoto appPhoto = buildTransientAppPhoto(binaryContent, photo);
            return appPhotoRepository.save(appPhoto);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    private static AppPhoto buildTransientAppPhoto(BinaryContent binaryContent, PhotoSize photo) {
        return AppPhoto.builder()
                .binaryContent(binaryContent)
                .telegramFileId(photo.getFileId())
                .size(photo.getFileSize())
                .build();
    }

    private AppDocument buildTransientAppDocument(Document telegramDoc, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFileId(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .size(telegramDoc.getFileSize())
                .build();
    }

    /**
     * Осуществляет HTTP-GET запрос к серверу телеграма
     *
     * @param fileId
     * @return данные объекта, пришедшие из телеграма
     */
    private ResponseEntity<String> getFileDataFromTelegram(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token,
                fileId
        );
    }

    private BinaryContent getBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFileAsByteArray(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentRepository.save(transientBinaryContent);
    }

    private static String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }

    private byte[] downloadFileAsByteArray(String filePath) {
        String fullUri = fileStorageUri.replace("{token}", token).replace("{filePath}", filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e.getCause());
        }
        try (InputStream inputStream = urlObj.openStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }
    }
}
