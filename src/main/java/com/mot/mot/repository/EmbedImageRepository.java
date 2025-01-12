package com.mot.mot.repository;


import com.mot.mot.model.entity.EmbedImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmbedImageRepository extends JpaRepository<EmbedImage, Long> {


    @Query("SELECT ei.url FROM EmbedImage ei WHERE ei.relatedId = :classPostId and ei.relatedTable = 'class_post'")
    public List<String> findUrlsByClassPostId(Long classPostId);

}

