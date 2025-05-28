# AEM LLM MCP Connector

This project allows Adobe Experience Manager developers to generate components, templates, dialogs, and configurations using natural language prompts powered by an LLM (Large Language Model).

## Features
- LLM-to-Component Generator via MCP
- Dialog and Sling Model auto-generator
- JSON-based intermediate format
- Fully extensible and secure

## Usage
1. Type a natural language prompt.
2. System maps to JSON structure.
3. MCP Job reads JSON and creates AEM nodes.

## Prompt Example
"Create a hero banner component with title, subtitle, background image, and CTA button."

## Output
- /apps/llmconnector/components/hero-banner
- /apps/llmconnector/templates/...
- Dialog fields and HTL included

---

## License
MIT
