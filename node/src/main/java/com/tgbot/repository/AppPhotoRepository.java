package com.tgbot.repository;

import com.tgbot.entity.AppPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppPhotoRepository extends JpaRepository<AppPhoto, Long> {
}
