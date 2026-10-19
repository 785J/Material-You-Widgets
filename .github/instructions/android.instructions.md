---
applyTo: "**/*.kt,**/*.kts,**/AndroidManifest.xml,**/res/**"
---

# Android Development Instructions

## Android-specific guidance
- Prefer the project’s existing architecture and package structure over introducing a new framework.
- Keep UI code focused on presentation, not domain logic or persistence.
- Respect Android lifecycle boundaries: activities, fragments, view models, and coroutine scopes should be managed intentionally.
- Avoid direct database or network access from composables, activities, or fragments.

## Lifecycle and state
- Use lifecycle-aware components where appropriate.
- Keep state ownership clear and avoid multiple competing sources of truth.
- Represent UI state explicitly with immutable state objects.
- Avoid storing transient UI state in global singletons or static fields.

## Resources and compatibility
- Prefer existing resources, themes, colors, dimensions, and string values before creating new ones.
- Keep resource names descriptive and consistent with Android conventions.
- Ensure compatibility across supported API levels and device configurations.

## App architecture
- Use a layered approach: UI, state, domain/business logic, and data access.
- Keep navigation logic centralized rather than scattered across screens.
- Connect UI to repositories, use cases, services, or view models instead of embedding logic directly in screen components.

## Performance and reliability
- Avoid unnecessary recomposition and keep composable functions efficient.
- Do not block the main thread with long-running work.
- Handle configuration changes, background execution, and restoration states intentionally.
- Fail gracefully when dependencies or resources are unavailable.

## Security and permissions
- Request only the permissions the feature requires.
- Never hardcode secrets, tokens, keys, or environment values in source.
- Validate input from Intents, Bundle data, and external sources before using it.
