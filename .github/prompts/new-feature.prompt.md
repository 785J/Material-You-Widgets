# New Feature Prompt

Use this prompt when creating or extending a feature in the app.

## Goal
Describe the feature, expected behavior, and user value clearly.

## Required analysis
Before implementation, identify:
- the relevant module or feature area
- existing patterns and reusable code
- the responsible architectural layer
- files to create or modify
- required tests or validation

## Implementation checklist
- Keep the feature aligned to the current architecture.
- Prefer small, focused files and clear responsibilities.
- Use existing UI, state, and data patterns where possible.
- Validate edge cases, loading states, and failure handling.
- Run the smallest relevant verification command after implementation.

## Acceptance criteria
- The feature works as described.
- The code follows project conventions and layering rules.
- Important states are handled explicitly.
- Tests or verification cover the meaningful behavior.
