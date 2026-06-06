package com.qiniu.noveltoscript.util;

import java.util.UUID;

public final class IdGenerator {
    private IdGenerator() {}

    public static String sessionId() {
        return "ns_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public static String chapterId(int index) {
        return String.format("ch_%03d", index);
    }
}
