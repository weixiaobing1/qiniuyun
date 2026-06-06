package com.qiniu.noveltoscript.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasteTextRequest(
        @NotBlank(message = "文本内容不能为空")
        @Size(max = 500_000, message = "单次粘贴文本不超过 50 万字")
        String text,

        String title,
        String author
) {}
