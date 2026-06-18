package com.blog.demo.common.util;

import java.util.regex.Pattern;

/**
 * Plain-text helpers, e.g. building a short excerpt from Markdown content.
 */
public final class TextUtil {

    private static final Pattern CODE_FENCE = Pattern.compile("(?s)```.*?```");
    private static final Pattern IMAGE = Pattern.compile("!\\[[^\\]]*\\]\\([^)]*\\)");
    private static final Pattern LINK = Pattern.compile("\\[([^\\]]*)\\]\\([^)]*\\)");
    private static final Pattern MD_TOKENS = Pattern.compile("[#>*_`~\\-]+");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private TextUtil() {
    }

    /**
     * Strips Markdown formatting and truncates to {@code maxLength} characters,
     * appending an ellipsis when truncated.
     */
    public static String excerpt(String markdown, int maxLength) {
        if (markdown == null || markdown.isBlank()) {
            return "";
        }
        String text = CODE_FENCE.matcher(markdown).replaceAll(" ");
        text = IMAGE.matcher(text).replaceAll(" ");
        text = LINK.matcher(text).replaceAll("$1");
        text = MD_TOKENS.matcher(text).replaceAll(" ");
        text = WHITESPACE.matcher(text).replaceAll(" ").trim();

        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength).trim() + "…";
    }
}
