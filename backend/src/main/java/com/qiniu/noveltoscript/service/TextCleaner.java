package com.qiniu.noveltoscript.service;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class TextCleaner {

    private static final Pattern MULTI_BLANK_LINE = Pattern.compile("\\n{3,}");
    private static final Pattern TRAILING_SPACES = Pattern.compile("[ \\t]+\\n");
    private static final Pattern ZERO_WIDTH = Pattern.compile("[\\u200B-\\u200D\\uFEFF]");
    private static final Pattern AD_LINES = Pattern.compile(
            "(?m)^.*(本书.*首发|更新最快|手机.*阅读|^[\\s]*www\\.|笔趣阁|起点中文|纵横中文|qq阅读|龙空|追书神器|顶点小说|小说网|永久网址|最新章节|无弹窗|本章未完|请收藏|点击阅读|未完待续\\.\\.\\.[\\s]*$).*$"
    );

    public String cleanHtml(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        String text = Jsoup.clean(html, Safelist.none()
                .preserveRelativeLinks(false));
        text = text.replace("&nbsp;", " ").replace("&amp;", "&");
        return cleanPlain(text);
    }

    public String cleanPlain(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String text = raw.replace("\r\n", "\n").replace("\r", "\n");
        text = ZERO_WIDTH.matcher(text).replaceAll("");
        text = AD_LINES.matcher(text).replaceAll("");
        text = TRAILING_SPACES.matcher(text).replaceAll("\n");
        text = MULTI_BLANK_LINE.matcher(text).replaceAll("\n\n");
        return text.trim();
    }
}
