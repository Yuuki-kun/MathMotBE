package com.mot.mot.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "embed_image")

public class EmbedImage {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String url;

    private String type;

    private Long size;

    private String deleteHash;

    private String storageWeb;
    private Float width;
    private Float height;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "answer_id")
    private Answer answer;

    private Long entityId;

    private boolean uploaded;

    public EmbedImage(EmbedImage embedImageToSave) {
        this.name = embedImageToSave.getName();
        this.url = embedImageToSave.getUrl();
        this.type = embedImageToSave.getType();
        this.size = embedImageToSave.getSize();
        this.deleteHash = embedImageToSave.getDeleteHash();
        this.storageWeb = embedImageToSave.getStorageWeb();
        this.width = embedImageToSave.getWidth();
        this.height = embedImageToSave.getHeight();
        this.question = embedImageToSave.getQuestion();
        this.answer = embedImageToSave.getAnswer();
        this.uploaded = embedImageToSave.isUploaded();
        this.file = embedImageToSave.getFile();
        this.id = embedImageToSave.getId();
        }

    @Override
    public String toString() {
        return "EmbedImage{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", size=" + size +
                ", deleteHash='" + deleteHash + '\'' +
                ", storageWeb='" + storageWeb + '\'' +
                '}';
    }

    @Transient
    private byte[] file;
}
