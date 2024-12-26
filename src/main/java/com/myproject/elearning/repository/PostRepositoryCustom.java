package com.myproject.elearning.repository;

import com.myproject.elearning.dto.response.post.PostGetRes;
import java.util.Optional;

public interface PostRepositoryCustom {
    Optional<PostGetRes> findPostGetResById(Long id);
}
