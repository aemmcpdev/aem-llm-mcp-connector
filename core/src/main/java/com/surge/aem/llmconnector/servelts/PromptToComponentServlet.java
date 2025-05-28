package com.surge.aem.llmconnector.servlets;

import com.surge.aem.llmconnector.utils.ComponentBuilderUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component(service = Servlet.class,
           property = {
               "sling.servlet.methods=POST",
               "sling.servlet.paths=/bin/llm/generate-component"
           })
public class PromptToComponentServlet extends SlingAllMethodsServlet {

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        // Read POST body
        StringBuilder body = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }

        JSONObject input = new JSONObject(body.toString());
        String prompt = input.getString("prompt");

        // Create JSON definition based on keywords
        JSONObject component = new JSONObject();
        String componentName = "cmp-" + UUID.randomUUID().toString().substring(0, 8);
        component.put("componentName", componentName);

        JSONArray fields = new JSONArray();
        if (prompt.toLowerCase().contains("title")) {
            fields.put(new JSONObject().put("name", "title").put("type", "text"));
        }
        if (prompt.toLowerCase().contains("subtitle")) {
            fields.put(new JSONObject().put("name", "subtitle").put("type", "text"));
        }
        if (prompt.toLowerCase().contains("image")) {
            fields.put(new JSONObject().put("name", "backgroundImage").put("type", "image"));
        }
        if (prompt.toLowerCase().contains("cta") || prompt.toLowerCase().contains("link")) {
            fields.put(new JSONObject().put("name", "cta").put("type", "link"));
        }
        component.put("fields", fields);

        try {
            // Use service user to write into JCR
            ResourceResolver rr = resolverFactory.getServiceResourceResolver(
                Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "datawrite"));
            ComponentBuilderUtil.createComponent(rr, component);
            rr.close();
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write(new JSONObject().put("error", e.getMessage()).toString());
            return;
        }

        // Send response
        JSONObject result = new JSONObject();
        result.put("componentPath", "/apps/llmconnector/components/" + componentName);
        result.put("previewPath", "/content/llm-preview-page.html");
        response.getWriter().write(result.toString());
    }
}
