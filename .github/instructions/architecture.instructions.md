---
applyTo: "**/*.kt,**/*.kts,**/*.gradle.kts"
---

# Architecture Instructions

## Core architecture principles
- Preserve the separation between app UI, business logic, and data access.
- Avoid placing network, database, or heavy transformation logic directly in composables or activities.
- Prefer a clear layer structure that matches the existing project architecture.
- Keep responsibilities isolated and make each file answer a single purpose.

## Recommended layering
- `presentation`: screens, view models, UI state, navigation-facing decisions
- `domain`: business rules, core models, repository contracts, use cases
- `data`: repositories, remote/local sources, mappers, API or storage implementations
- `core` or `common`: shared utilities and foundational abstractions

## File and module discipline
- Split code when responsibilities become mixed or a file becomes difficult to follow.
- Reuse existing components before creating new abstractions.
- Do not create generic `Utils` or `Helper` files unless they hold clearly named behavior.
- Use package names that reflect domain ownership rather than technical dumping grounds.

## Change management
- Before extending a feature, inspect related modules and patterns already in use.
- Prefer minimal, targeted changes over broad rewrites.
- If a refactor is necessary, keep it scoped and explain the architectural reason.

## This project
- Maintain a clear boundary between PDF backend logic and Android UI code.
- Avoid mixing native PDF engine behavior with screen rendering concerns.
- Keep viewer implementation choices explicit and modular so they remain maintainable.
