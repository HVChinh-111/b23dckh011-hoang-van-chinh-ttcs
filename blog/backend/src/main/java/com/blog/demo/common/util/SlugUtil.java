package com.blog.demo.common.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Slug helpers. A valid slug contains only lowercase letters, digits and
 * single hyphens between segments (BR09.1).
 */
public final class SlugUtil {

    /** ^[a-z0-9]+(?:-[a-z0-9]+)*$ */
    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
    private static final Pattern EDGE_DASHES = Pattern.compile("(^-+)|(-+$)");
    private static final Pattern MULTI_DASH = Pattern.compile("-{2,}");

    private SlugUtil() {
    }

    public static boolean isValid(String slug) {
        return slug != null && SLUG_PATTERN.matcher(slug).matches();
    }

    /**
     * Best-effort conversion of arbitrary text (including Vietnamese) into a
     * slug. Used as a fallback / helper; admin-supplied slugs are validated
     * against {@link #isValid(String)}.
     */
    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String noWhitespace = WHITESPACE.matcher(input.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replace('đ', 'd').replace('Đ', 'D');
        String slug = NON_LATIN.matcher(normalized).replaceAll("")
                .toLowerCase(Locale.ROOT);
        slug = MULTI_DASH.matcher(slug).replaceAll("-");
        return EDGE_DASHES.matcher(slug).replaceAll("");
    }
}
