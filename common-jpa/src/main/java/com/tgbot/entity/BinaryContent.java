package com.tgbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BinaryContent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bin_content_seq_gen")
    @SequenceGenerator(name = "bin_content_seq_gen", sequenceName = "binary_content_sequence", allocationSize = 1)
    private Long id;
    private byte[] fileAsArrayOfBytes;
}
