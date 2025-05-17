package com.gcu.controller.util;


public class PostUtil {

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
