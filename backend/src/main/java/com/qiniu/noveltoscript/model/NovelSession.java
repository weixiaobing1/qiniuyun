package com.qiniu.noveltoscript.model;

import com.qiniu.noveltoscript.dto.ChapterDto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public record NovelSession(
        String sessionId,
        String title,
        String author,
        int totalChars,
        List<ChapterDto> chapters,
        Instant createdAt
) implements Serializable {}
