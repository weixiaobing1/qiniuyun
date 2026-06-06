package com.qiniu.noveltoscript.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextCleanerTest {

    private final TextCleaner cleaner = new TextCleaner();

    @Test
    void shouldStripHtmlTagsAndKeepText() {
        String html = "<p>第一段</p><script>alert(1)</script><div>第二段</div>";
        String cleaned = cleaner.cleanHtml(html);
        assertThat(cleaned).contains("第一段").contains("第二段");
        assertThat(cleaned).doesNotContain("<p>").doesNotContain("alert");
    }

    @Test
    void shouldNormalizeLineBreaks() {
        String raw = "第一行\r\n\r\n\r\n\r\n第二行  \n第三行";
        String cleaned = cleaner.cleanPlain(raw);
        assertThat(cleaned).doesNotContain("\r");
        assertThat(cleaned).doesNotContain("\n\n\n");
    }

    @Test
    void shouldRemoveCommonAdLines() {
        String raw = "正文一\n本书首发于起点中文网\n正文二\nwww.example.com\n正文三";
        String cleaned = cleaner.cleanPlain(raw);
        assertThat(cleaned).contains("正文一").contains("正文二").contains("正文三");
        assertThat(cleaned).doesNotContain("起点中文网");
    }

    @Test
    void shouldRemoveZeroWidthCharacters() {
        String raw = "正​文‌内‍容﻿";
        String cleaned = cleaner.cleanPlain(raw);
        assertThat(cleaned).isEqualTo("正文内容");
    }
}
