package com.qiniu.noveltoscript.dto;

public record ChapterDto(
        String id,
        int index,
        String title,
        String content,
        int charCount
) {
    public ChapterDto preview(int previewLength) {
        if (content == null || content.length() <= previewLength) {
            return this;
        }
        String snippet = content.substring(0, previewLength) + "...";
        return new ChapterDto(id, index, title, snippet, charCount);
    }
}
