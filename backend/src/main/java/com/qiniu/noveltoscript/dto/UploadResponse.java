package com.qiniu.noveltoscript.dto;

import java.util.List;

public record UploadResponse(
        String sessionId,
        String title,
        String author,
        int totalChars,
        int chapterCount,
        List<ChapterDto> chapters
) {}
