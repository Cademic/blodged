package com.gcu.controller.util;

/**
 * utilities that have to do with the post models/views
 */
public class PostUtil {
    /**
     * correct the content of long posts to not take up too much space in their previews <br>
     * limits to either 100 characters and/or 6 linebreaks
     * @param content the content to sanitize
     * @param postPage if the content is a preview or the whole post
     * @return sanitized content if previewed, raw content if post
     */
    public String sanitizePostContent(String content, boolean postPage) {
        if (postPage) return content;
        else {
            String[] lines = content.split("\n");
            if (content.length() > 100) {
                return content.substring(0, 101) + "...";
            }
            if (lines.length > 6) {
                StringBuilder cc = new StringBuilder();
                for (int i = 0; i < 6; i++) {
                    cc.append(lines[i]).append("\n");
                }
                return cc.toString();
            }
        }
        return content;
    }

}
