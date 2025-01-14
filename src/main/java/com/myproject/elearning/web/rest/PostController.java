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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<PostAddRes> addPost(@Valid @RequestBody PostCreateReq request) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        PostAddRes post = postService.addPost(curUserId, request);
        return successRes("Tạo bài viết thành công", post);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (@resourceAccessService.isPostOwner(#id))")
    public ApiRes<PostUpdateRes> editPost(@PathVariable Long id, @Valid @RequestBody PostUpdateReq request) {
        PostUpdateRes post = postService.editPost(id, request);
        return successRes("Cập nhật bài viết thành công", post);
    }

    /**
     * anonymous users, the likedByCurrentUser field will be false in default
     * authenticated users, response will include whether they have liked the post
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    @PreAuthorize("isAnonymous() or isAuthenticated()")
    public ApiRes<PostGetRes> getPost(@PathVariable Long id) {
        PostGetRes post = postService.getPost(id);
        return successRes("Lấy bài viết thành công", post);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<PagedRes<PostListRes>> getPostsByUser(
            @PathVariable Long userId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PagedRes<PostListRes> posts = postService.getPostsByUser(userId, pageable);
        return successRes("Lấy danh sách bài viết thành công", posts);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<Void> likePost(@PathVariable Long id) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        postService.like(id, curUserId);
        return successRes("Đã thích bài viết", null);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/unlike")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<Void> unlikePost(@PathVariable Long id) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        postService.unlike(id, curUserId);
        return successRes("Đã bỏ thích bài viết", null);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (@resourceAccessService.isPostOwner(#id))")
    public ApiRes<Void> delPost(@PathVariable Long id) {
        postService.delPost(id);
        return successRes("Xóa bài viết thành công", null);
    }
}
