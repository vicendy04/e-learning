package com.myproject.elearning.rest.content;

import static com.myproject.elearning.rest.utils.ResponseUtils.errorRes;
import static com.myproject.elearning.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.post.PostCreateReq;
import com.myproject.elearning.dto.request.post.PostUpdateReq;
import com.myproject.elearning.dto.response.post.PostGetRes;
import com.myproject.elearning.dto.response.post.PostListRes;
import com.myproject.elearning.dto.response.post.PostUpdateRes;
import com.myproject.elearning.exception.problemdetails.AnonymousUserEx;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.PostService;
import com.myproject.elearning.service.redis.PostRedisService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/posts")
@RestController
public class PostController {
    PostService postService;
    PostRedisService postRedisService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/top-like")
    @PreAuthorize("isAnonymous() or isAuthenticated()")
    public ApiRes<List<PostGetRes>> getTopPosts() {
        Set<Long> topPostIds = postRedisService.getTopPostIds(0, 100);
        if (topPostIds.isEmpty()) return errorRes("Không có bài viết nào hiện tại", null);
        var topPosts = postRedisService.getTopPosts(topPostIds);
        return successRes("Lay bai viet thanh cong", topPosts);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<PostGetRes> addPost(@Valid @RequestBody PostCreateReq request) {
        Long userId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        var res = postService.addPost(userId, request);
        postRedisService.set(res.getId(), res);
        return successRes("Tạo bài viết thành công", res);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (@resourceAccessService.isPostOwner(#id))")
    public ApiRes<PostUpdateRes> editPost(@PathVariable Long id, @Valid @RequestBody PostUpdateReq request) {
        var editedPost = postService.editPost(id, request);
        postRedisService.evict(id);
        return successRes("Cập nhật bài viết thành công", editedPost);
    }

    /**
     * anonymous users, the likedByCurrentUser field will be false in default
     * authenticated users, response will include whether they have liked the post
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    @PreAuthorize("isAnonymous() or isAuthenticated()")
    public ApiRes<PostGetRes> getPost(@PathVariable Long id) {
        var res = postRedisService.getAside(id);
        return successRes("Lấy bài viết thành công", res);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<PagedRes<PostListRes>> getPostsByUser(
            @PathVariable Long userId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        var posts = postService.getPostsByUser(userId, pageable);
        return successRes("Lấy danh sách bài viết thành công", posts);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<Void> likePost(@PathVariable Long id) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        postRedisService.like(id, curUserId);
        return successRes("Đã thích bài viết", null);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/unlike")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<Void> unlikePost(@PathVariable Long id) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        postRedisService.unlike(id, curUserId);
        return successRes("Đã bỏ thích bài viết", null);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (@resourceAccessService.isPostOwner(#id))")
    public ApiRes<Void> delPost(@PathVariable Long id) {
        postService.delPost(id);
        postRedisService.evict(id);
        return successRes("Xóa bài viết thành công", null);
    }
}
