package com.blog.demo.common.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

/**
 * Renders stored Markdown into sanitized HTML and extracts a table of contents
 * from the headings (BR04.3, NFR11.1). Content is never stored as HTML; it is
 * rendered on read.
 */
@Service
public class MarkdownService {

    private final Parser parser;
    private final HtmlRenderer renderer;
    private final Cleaner cleaner;

    public MarkdownService() {
        List<org.commonmark.Extension> extensions = List.of(TablesExtension.create());
        this.parser = Parser.builder().extensions(extensions).build();
        this.renderer = HtmlRenderer.builder().extensions(extensions).build();
        this.cleaner = new Cleaner(buildSafelist());
    }

    /** A single heading extracted for the table of contents. */
    public record Heading(String id, String title, int level) {
    }

    /** Result of rendering: sanitized HTML + extracted headings. */
    public record RenderedMarkdown(String html, List<Heading> headings) {
    }

    public RenderedMarkdown render(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return new RenderedMarkdown("", List.of());
        }
        Node document = parser.parse(markdown);
        String rawHtml = renderer.render(document);

        Document parsed = Jsoup.parseBodyFragment(rawHtml);
        List<Heading> headings = assignHeadingIds(parsed);

        Document safe = cleaner.clean(parsed);
        safe.outputSettings().prettyPrint(false);
        return new RenderedMarkdown(safe.body().html(), headings);
    }

    /** Convenience for callers that only need the HTML. */
    public String toHtml(String markdown) {
        return render(markdown).html();
    }

    private List<Heading> assignHeadingIds(Document doc) {
        List<Heading> headings = new ArrayList<>();
        Set<String> usedIds = new HashSet<>();
        for (Element h : doc.select("h1, h2, h3, h4, h5, h6")) {
            String text = h.text();
            if (text.isBlank()) {
                continue;
            }
            String base = SlugUtil.toSlug(text);
            if (base.isBlank()) {
                base = "section";
            }
            String id = base;
            int counter = 1;
            while (!usedIds.add(id)) {
                id = base + "-" + counter++;
            }
            h.attr("id", id);
            int level = Integer.parseInt(h.tagName().substring(1));
            headings.add(new Heading(id, text, level));
        }
        return headings;
    }

    private Safelist buildSafelist() {
        // Build from scratch so we can omit addProtocols for img src —
        // Jsoup strips relative URLs (e.g. /uploads/posts/xxx.png) when
        // protocols are restricted, so we allow all src values here.
        return new Safelist()
                .addTags(
                        "a", "b", "blockquote", "br", "caption", "cite",
                        "code", "col", "colgroup", "dd", "del", "div", "dl", "dt",
                        "em", "h1", "h2", "h3", "h4", "h5", "h6", "hr", "i", "img",
                        "ins", "li", "ol", "p", "pre", "q", "small", "span", "strike",
                        "strong", "sub", "sup", "table", "tbody", "td", "tfoot", "th",
                        "thead", "tr", "u", "ul")
                .addAttributes("a", "href", "title")
                .addAttributes("blockquote", "cite")
                .addAttributes("col", "span", "width")
                .addAttributes("colgroup", "span", "width")
                .addAttributes("img", "src", "alt", "title", "width", "height")
                .addAttributes("ol", "start", "type")
                .addAttributes("table", "summary", "width")
                .addAttributes("td", "abbr", "axis", "colspan", "rowspan", "width")
                .addAttributes("th", "abbr", "axis", "colspan", "rowspan", "scope", "width")
                .addAttributes("ul", "type")
                .addAttributes(":all", "id", "class")
                .addProtocols("a", "href", "ftp", "http", "https", "mailto")
                .addProtocols("blockquote", "cite", "http", "https");
        // img src intentionally has no protocol restriction → allows relative /uploads/... paths
    }
}
