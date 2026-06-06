package com.qiniu.noveltoscript.service;

import com.qiniu.noveltoscript.dto.ChapterDto;
import com.qiniu.noveltoscript.util.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ChapterSplitter {

    /**
     * 章节标题识别：
     *   第X章/回/节 [可选标题]
     *   Chapter X [可选标题]
     *   楔子 / 序章 / 序言 / 引子 / 尾声 / 后记 (整行)
     */
    private static final Pattern CHAPTER_HEADER = Pattern.compile(
            "(?m)^[ \\t]*("
                    + "第[0-9零一二三四五六七八九十百千万0-9]+[章回节卷部篇][^\\n]{0,50}"
                    + "|Chapter\\s+[0-9IVXLCDM]+[^\\n]{0,50}"
                    + "|楔子[^\\n]{0,30}"
                    + "|序章[^\\n]{0,30}"
                    + "|序言[^\\n]{0,30}"
                    + "|引子[^\\n]{0,30}"
                    + "|尾声[^\\n]{0,30}"
                    + "|后记[^\\n]{0,30}"
                    + ")[ \\t]*$"
    );

    private static final int MIN_CHAPTER_CHARS = 200;

    public List<ChapterDto> split(String cleanText) {
        if (cleanText == null || cleanText.isBlank()) {
            return List.of();
        }

        List<int[]> headers = new ArrayList<>();
        Matcher matcher = CHAPTER_HEADER.matcher(cleanText);
        while (matcher.find()) {
            headers.add(new int[]{matcher.start(), matcher.end()});
        }

        if (headers.isEmpty()) {
            return splitByLength(cleanText);
        }

        List<ChapterDto> chapters = new ArrayList<>();
        if (headers.get(0)[0] > MIN_CHAPTER_CHARS) {
            String prologue = cleanText.substring(0, headers.get(0)[0]).trim();
            if (!prologue.isEmpty()) {
                chapters.add(buildChapter(chapters.size() + 1, "前言", prologue));
            }
        }

        for (int i = 0; i < headers.size(); i++) {
            int titleStart = headers.get(i)[0];
            int titleEnd = headers.get(i)[1];
            int chapterEnd = (i + 1 < headers.size()) ? headers.get(i + 1)[0] : cleanText.length();

            String title = cleanText.substring(titleStart, titleEnd).trim();
            String body = cleanText.substring(titleEnd, chapterEnd).trim();

            if (body.length() < MIN_CHAPTER_CHARS && i + 1 < headers.size()) {
                continue;
            }

            chapters.add(buildChapter(chapters.size() + 1, title, body));
        }

        return chapters;
    }

    private List<ChapterDto> splitByLength(String text) {
        List<ChapterDto> chapters = new ArrayList<>();
        int chunkSize = 5_000;
        int total = text.length();
        for (int i = 0, idx = 1; i < total; i += chunkSize, idx++) {
            String body = text.substring(i, Math.min(i + chunkSize, total));
            chapters.add(buildChapter(idx, "片段 " + idx, body));
        }
        return chapters;
    }

    private ChapterDto buildChapter(int index, String title, String content) {
        return new ChapterDto(
                IdGenerator.chapterId(index),
                index,
                title,
                content,
                content.length()
        );
    }
}
