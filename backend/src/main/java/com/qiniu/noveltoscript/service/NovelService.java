package com.qiniu.noveltoscript.service;

import com.qiniu.noveltoscript.config.AppProperties;
import com.qiniu.noveltoscript.dto.ChapterDto;
import com.qiniu.noveltoscript.dto.UploadResponse;
import com.qiniu.noveltoscript.exception.BizException;
import com.qiniu.noveltoscript.model.NovelSession;
import com.qiniu.noveltoscript.util.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NovelService {

    private final TextCleaner textCleaner;
    private final ChapterSplitter chapterSplitter;
    private final SessionStore sessionStore;
    private final Set<String> allowedExtensions;
    private final int maxChars;

    public NovelService(TextCleaner textCleaner,
                        ChapterSplitter chapterSplitter,
                        SessionStore sessionStore,
                        AppProperties properties) {
        this.textCleaner = textCleaner;
        this.chapterSplitter = chapterSplitter;
        this.sessionStore = sessionStore;
        this.allowedExtensions = Arrays.stream(properties.getUpload().getAllowedExtensions().split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        this.maxChars = properties.getUpload().getMaxChars();
    }

    public UploadResponse uploadFile(MultipartFile file, String title, String author) {
        if (file == null || file.isEmpty()) {
            throw new BizException("上传文件不能为空");
        }
        String filename = file.getOriginalFilename();
        String ext = extractExtension(filename);
        if (!allowedExtensions.contains(ext)) {
            throw new BizException("不支持的文件类型：" + ext + "，仅支持 " + allowedExtensions);
        }
        try {
            String raw = new String(file.getBytes(), StandardCharsets.UTF_8);
            String cleaned = isHtml(ext) ? textCleaner.cleanHtml(raw) : textCleaner.cleanPlain(raw);
            String resolvedTitle = (title == null || title.isBlank()) ? stripExtension(filename) : title;
            return process(cleaned, resolvedTitle, author);
        } catch (IOException e) {
            throw new BizException("读取文件失败：" + e.getMessage());
        }
    }

    public UploadResponse pasteText(String text, String title, String author) {
        if (text == null || text.isBlank()) {
            throw new BizException("粘贴文本不能为空");
        }
        String cleaned = textCleaner.cleanPlain(text);
        return process(cleaned, title, author);
    }

    private UploadResponse process(String cleaned, String title, String author) {
        if (cleaned.length() > maxChars) {
            throw new BizException("文本字数超过上限 " + maxChars + " 字");
        }
        List<ChapterDto> chapters = chapterSplitter.split(cleaned);
        if (chapters.isEmpty()) {
            throw new BizException("未识别到任何章节内容");
        }
        String sessionId = IdGenerator.sessionId();
        NovelSession session = new NovelSession(
                sessionId,
                safeTitle(title),
                safeAuthor(author),
                cleaned.length(),
                chapters,
                Instant.now()
        );
        sessionStore.save(session);

        List<ChapterDto> previews = chapters.stream()
                .map(c -> c.preview(120))
                .toList();
        return new UploadResponse(
                sessionId,
                session.title(),
                session.author(),
                session.totalChars(),
                chapters.size(),
                previews
        );
    }

    public NovelSession requireSession(String sessionId) {
        return sessionStore.load(sessionId)
                .orElseThrow(() -> new BizException(40400, "会话不存在或已过期"));
    }

    private String extractExtension(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf('.');
        return idx >= 0 ? filename.substring(idx + 1).toLowerCase() : "";
    }

    private String stripExtension(String filename) {
        if (filename == null) return "未命名小说";
        int idx = filename.lastIndexOf('.');
        return idx > 0 ? filename.substring(0, idx) : filename;
    }

    private boolean isHtml(String ext) {
        return "html".equals(ext) || "htm".equals(ext);
    }

    private String safeTitle(String title) {
        return (title == null || title.isBlank()) ? "未命名小说" : title.trim();
    }

    private String safeAuthor(String author) {
        return (author == null || author.isBlank()) ? "佚名" : author.trim();
    }
}
