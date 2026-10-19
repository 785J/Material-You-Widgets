---
applyTo: "**/*.kt,**/*.kts"
---

# Kotlin Coding Standards

## Core rules
- Prefer Kotlin-first idioms over Java-style patterns.
- Prefer `val` over `var` unless mutation is required.
- Use immutable data models and avoid hidden state.
- Favor expression-bodied logic when it keeps code readable.
- Keep functions small and focused on one responsibility.
- Use explicit types when they improve clarity; avoid redundant type annotations when inference is obvious.

## Naming and structure
- Use clear, domain-driven names for classes, functions, and variables.
- Prefer `camelCase` for functions and variables, `PascalCase` for classes and types.
- Avoid generic names like `Manager`, `Helper`, `Utils`, `Thing`, or `Data` unless they are truly domain-specific.
- Keep files aligned to a single responsibility and package structure.

## Null safety
- Treat `null` as an explicit state and handle it intentionally.
- Prefer safe calls and null checks over forced unwraps.
- Avoid swallowing exceptions or converting nulls into ambiguous default values without justification.

## Collections and data classes
- Use `List`, `Set`, and `Map` interfaces with explicit implementations only when needed.
- Prefer `data class` for model objects that represent values.
- Use `when` expressions for exhaustive branching.
- Avoid deep nesting and overly clever one-liners when readability suffers.

## Coroutines and async work
- Use structured concurrency and cancelable coroutine scopes.
- Do not launch untracked background work from UI code without a clear lifecycle boundary.
- Prefer `Dispatchers.IO` for blocking or I/O work, and `Dispatchers.Main` for UI state updates.
- Avoid arbitrary delays or polling loops unless the feature explicitly requires them.

## Error handling
- Handle failures at the boundary where they matter most.
- Convert low-level exceptions into meaningful domain or UI errors.
- Log only non-sensitive information.
- Avoid empty catch blocks.

## Conventions for this project
- Keep business logic outside composables and view layers.
- Use repository, use case, or service abstractions for data access and orchestration.
- Match the existing package and architectural conventions already used in this codebase.
