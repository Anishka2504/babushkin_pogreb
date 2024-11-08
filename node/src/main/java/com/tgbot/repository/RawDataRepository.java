package com.tgbot.repository;

import com.tgbot.entity.RawData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawDataRepository extends JpaRepository<RawData, Long> {
}
