package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.post.PostCreateReq;
import com.myproject.elearning.dto.request.post.PostUpdateReq;
import com.myproject.elearning.dto.response.post.PostAddRes;
import com.myproject.elearning.dto.response.post.PostGetRes;
import com.myproject.elearning.dto.response.post.PostListRes;
import com.myproject.elearning.dto.response.post.PostUpdateRes;
import com.myproject.elearning.exception.problemdetails.AnonymousUserException;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.AuthzService;
import com.myproject.elearning.service.PostService;
import jakarta.validation.Valid;
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
    AuthzService authzService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiRes<PostAddRes> addPost(@Valid @RequestBody PostCreateReq request) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        PostAddRes post = postService.addPost(curUserId, request);
        return successRes("Tạo bài viết thành công", post);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<PostUpdateRes> editPost(@PathVariable Long id, @Valid @RequestBody PostUpdateReq request) {
        authzService.checkPostAccess(id);
        PostUpdateRes post = postService.editPost(id, request);
        return successRes("Cập nhật bài viết thành công", post);
    }

    /**
     * anonymous users, the likedByCurrentUser field will be false in default
     * authenticated users, response will include whether they have liked the post
     */
    @PreAuthorize("isAnonymous() or isAuthenticated()")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<PostGetRes> getPost(@PathVariable Long id) {
        PostGetRes post = postService.getPost(id);
        return successRes("Lấy bài viết thành công", post);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<PagedRes<PostListRes>> getPostsByUser(
            @PathVariable Long userId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PagedRes<PostListRes> posts = postService.getPostsByUser(userId, pageable);
        return successRes("Lấy danh sách bài viết thành công", posts);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/like")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<Void> likePost(@PathVariable Long id) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        postService.like(id, curUserId);
        return successRes("Đã thích bài viết", null);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/unlike")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<Void> unlikePost(@PathVariable Long id) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        postService.unlike(id, curUserId);
        return successRes("Đã bỏ thích bài viết", null);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiRes<Void> delPost(@PathVariable Long id) {
        authzService.checkPostAccess(id);
        postService.delPost(id);
        return successRes("Xóa bài viết thành công", null);
    }
}
