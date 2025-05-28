package com.surge.aem.llmconnector.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HTLWriterUtil {

    public static void writeHTL(String componentPath, String componentName, JSONArray fields) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"cmp-").append(componentName).append("\">\n");

        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            String name = field.getString("name");
            String type = field.optString("type", "text");

            switch (type) {
                case "text":
                    sb.append("  <p>${properties.").append(name).append("}</p>\n");
                    break;
                case "image":
                    sb.append("  <img src=\"${properties.").append(name).append("}\" alt=\"Image\"/>\n");
                    break;
                case "link":
                    sb.append("  <a href=\"${properties.").append(name).append("}\">Click here</a>\n");
                    break;
                case "textarea":
                    sb.append("  <div>${properties.").append(name).append("}</div>\n");
                    break;
                default:
                    sb.append("  <div>${properties.").append(name).append("}</div>\n");
            }
        }

        sb.append("</div>");

        try {
            File dir = new File(componentPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            FileWriter writer = new FileWriter(new File(dir, componentName + ".html"));
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
