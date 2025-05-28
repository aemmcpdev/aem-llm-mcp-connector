package com.surge.aem.llmconnector.utils;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.PersistenceException;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

public class ComponentBuilderUtil {

    public static void createComponent(ResourceResolver resolver, JSONObject compDef) throws PersistenceException {
        String name = compDef.getString("componentName");
        JSONArray fields = compDef.getJSONArray("fields");

        String basePath = "/apps/llmconnector/components/" + name;

        // Create component node
        Resource compFolder = ResourceUtil.getOrCreateResource(resolver, basePath, "cq:Component", "nt:unstructured", true);

        // Add metadata
        ModifiableValueMap vm = compFolder.adaptTo(ModifiableValueMap.class);
        if (vm != null) {
            vm.put("jcr:title", name);
            vm.put("componentGroup", "LLM Generated");
        }

        // Create dialog structure
        Resource dialog = ResourceUtil.getOrCreateResource(resolver, basePath + "/cq:dialog", "nt:unstructured", null, true);
        Resource content = ResourceUtil.getOrCreateResource(resolver, dialog.getPath() + "/content", "nt:unstructured", null, true);

        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            String fieldName = field.getString("name");
            String type = field.getString("type");

            Map<String, Object> props = new HashMap<>();
            props.put("jcr:primaryType", "nt:unstructured");
            props.put("sling:resourceType", "granite/ui/components/coral/foundation/form/" + type);
            props.put("name", "./" + fieldName);
            props.put("fieldLabel", fieldName);

            ResourceUtil.getOrCreateResource(resolver, content.getPath() + "/" + fieldName, props, "nt:unstructured", true);
        }

        // Commit AEM nodes
        resolver.commit();

        // Write HTL file to filesystem (MCP context only)
        String absPath = System.getProperty("user.dir") + "/target/htl-output/" + name;
        HTLWriterUtil.writeHTL(absPath, name, fields);
    }
}
