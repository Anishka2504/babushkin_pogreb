package com.tgbot.repository;

import com.tgbot.entity.AppDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppDocumentRepository extends JpaRepository<AppDocument, Long> {
}
