package com.qiniu.noveltoscript.service;

import com.qiniu.noveltoscript.dto.ChapterDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChapterSplitterTest {

    private final ChapterSplitter splitter = new ChapterSplitter();

    @Test
    void shouldSplitClassicChineseChapters() {
        String text = repeat("正文段落。", 60)
                + "\n\n第一章 开始\n\n" + repeat("第一章内容。", 60)
                + "\n\n第二章 中段\n\n" + repeat("第二章内容。", 60)
                + "\n\n第三章 结尾\n\n" + repeat("第三章内容。", 60);

        List<ChapterDto> chapters = splitter.split(text);

        assertThat(chapters).hasSize(4);
        assertThat(chapters.get(0).title()).isEqualTo("前言");
        assertThat(chapters.get(1).title()).startsWith("第一章");
        assertThat(chapters.get(2).title()).startsWith("第二章");
        assertThat(chapters.get(3).title()).startsWith("第三章");
        assertThat(chapters).allSatisfy(c -> assertThat(c.charCount()).isGreaterThan(100));
    }

    @Test
    void shouldRecognizeChapterEnglishKeyword() {
        String text = "Chapter 1 Beginning\n\n" + repeat("plot a. ", 50)
                + "\n\nChapter 2 Middle\n\n" + repeat("plot b. ", 50);
        List<ChapterDto> chapters = splitter.split(text);
        assertThat(chapters).hasSize(2);
        assertThat(chapters.get(0).title()).startsWith("Chapter 1");
    }

    @Test
    void shouldFallbackToLengthSplitWhenNoHeader() {
        String text = repeat("一段没有任何章节标题的纯文本。", 1500);
        List<ChapterDto> chapters = splitter.split(text);
        assertThat(chapters.size()).isGreaterThanOrEqualTo(2);
        assertThat(chapters.get(0).title()).startsWith("片段");
    }

    @Test
    void shouldReturnEmptyOnBlankInput() {
        assertThat(splitter.split("")).isEmpty();
        assertThat(splitter.split("   \n  \n")).isEmpty();
    }

    private static String repeat(String s, int n) {
        return String.valueOf(s).repeat(n);
    }
}
