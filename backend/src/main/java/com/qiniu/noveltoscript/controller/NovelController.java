package com.qiniu.noveltoscript.controller;

import com.qiniu.noveltoscript.dto.ApiResponse;
import com.qiniu.noveltoscript.dto.ChapterDto;
import com.qiniu.noveltoscript.dto.PasteTextRequest;
import com.qiniu.noveltoscript.dto.UploadResponse;
import com.qiniu.noveltoscript.exception.BizException;
import com.qiniu.noveltoscript.model.NovelSession;
import com.qiniu.noveltoscript.service.NovelService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/novel")
public class NovelController {

    private final NovelService novelService;

    public NovelController(NovelService novelService) {
        this.novelService = novelService;
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ApiResponse<UploadResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "author", required = false) String author
    ) {
        return ApiResponse.ok(novelService.uploadFile(file, title, author));
    }

    @PostMapping(value = "/paste", consumes = "application/json")
    public ApiResponse<UploadResponse> paste(@Valid @RequestBody PasteTextRequest req) {
        return ApiResponse.ok(novelService.pasteText(req.text(), req.title(), req.author()));
    }

    @GetMapping("/session/{sessionId}")
    public ApiResponse<UploadResponse> getSession(@PathVariable String sessionId) {
        NovelSession session = novelService.requireSession(sessionId);
        List<ChapterDto> previews = session.chapters().stream()
                .map(c -> c.preview(120))
                .toList();
        UploadResponse resp = new UploadResponse(
                session.sessionId(),
                session.title(),
                session.author(),
                session.totalChars(),
                session.chapters().size(),
                previews
        );
        return ApiResponse.ok(resp);
    }

    @GetMapping("/session/{sessionId}/chapter/{chapterId}")
    public ApiResponse<ChapterDto> getChapter(
            @PathVariable String sessionId,
            @PathVariable String chapterId
    ) {
        NovelSession session = novelService.requireSession(sessionId);
        return ApiResponse.ok(session.chapters().stream()
                .filter(c -> c.id().equals(chapterId))
                .findFirst()
                .orElseThrow(() -> new BizException(40401, "章节不存在：" + chapterId)));
    }
}
