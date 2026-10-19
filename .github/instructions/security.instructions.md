---
applyTo: "**/*.kt,**/*.kts,**/AndroidManifest.xml,**/*.pro,**/*.gradle.kts"
---

# Security Instructions

## Security must-haves
- Never hardcode secrets, API keys, tokens, passwords, signing material, or private configuration values.
- Do not commit environment files that contain sensitive data.
- Validate untrusted input before use, especially data from user actions, bundles, intents, files, or network responses.
- Minimize the use of broad permissions and request only what the feature requires.

## Handling data safely
- Do not log sensitive user or configuration data.
- Treat file paths, user content, and external content as untrusted input.
- Sanitize or validate file references and display values before using them in UI or storage.
- Prefer secure, explicit handling for any web, network, or storage integrations.

## Android-specific safety
- Keep security-sensitive logic out of composables and activities.
- Validate app state transitions and permission flows before performing privileged actions.
- Review file access, storage, sharing, and export functionality for misuse or leakage risks.

## This project
- Maintain secure handling for PDF and file processing workflows.
- Ensure document access and rendering logic does not expose unsafe paths or unvalidated data.
- Follow the app’s existing secure practices and avoid introducing shortcuts that bypass validation.
