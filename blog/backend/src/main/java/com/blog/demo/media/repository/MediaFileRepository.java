package com.blog.demo.media.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blog.demo.media.entity.MediaFile;

public interface MediaFileRepository extends JpaRepository<MediaFile, String> {

    List<MediaFile> findByPostId(String postId);
}
