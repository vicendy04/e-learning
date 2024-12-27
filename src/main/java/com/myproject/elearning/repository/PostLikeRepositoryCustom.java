package com.myproject.elearning.repository;

import com.myproject.elearning.dto.request.post.PostLikeData;
import java.util.Set;

public interface PostLikeRepositoryCustom {
    int[][] bulkInsertLikes(Set<PostLikeData> likes);

    int[][] bulkDeleteLikes(Set<PostLikeData> unlikes);
}
