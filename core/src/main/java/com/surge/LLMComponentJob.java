package com.surge.aem.llmconnector.jobs;

import com.adobe.acs.commons.mcp.JobImpl;
import com.adobe.acs.commons.mcp.model.GenericReport;
import com.surge.aem.llmconnector.utils.ComponentBuilderUtil;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.jcr.Session;
import java.io.InputStream;
import java.util.Scanner;

public class LLMComponentJob extends JobImpl {

    private String jobName = "LLM-Driven Component Creator";

    @Override
    public void buildProcess(ProcessInstance instance, ResourceResolver resolver) throws Exception {
        instance.defineCriticalAction("Parse and Create Components", rr -> {
            InputStream inputStream = getClass().getResourceAsStream("/llm-prompt.json");
            String jsonText = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
            JSONArray components = new JSONArray(jsonText);

            for (int i = 0; i < components.length(); i++) {
                JSONObject comp = components.getJSONObject(i);
                ComponentBuilderUtil.createComponent(rr, comp);
            }
        });
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public GenericReport getReport() {
        return null;
    }
}
