---
applyTo: "**/*Test.kt,**/*.kt"
---

# Testing Instructions

## Testing philosophy
- Prefer meaningful tests that validate behavior, not implementation details.
- Write tests for business rules, validation logic, view models, and important edge cases.
- Favor real behavior over mock-heavy tests when the real integration is feasible.

## Unit tests
- Cover predictable domain logic, edge conditions, and failure modes.
- Name tests clearly around the behavior being verified.
- Keep tests small and deterministic.

## UI tests
- Test real user flows when practical.
- Validate state transitions, navigation, and visible outcomes.
- Keep UI tests targeted and not overly brittle.

## TDD approach
- When adding significant logic, write a failing test before implementing the fix.
- Use the smallest scenario that captures the bug or feature requirement.
- Verify the fix with a narrow, relevant test run before broad validation.

## Quality gates
- Ensure new behavior does not break existing flows.
- Validate edge cases: empty states, errors, permissions, retries, and cancellation.
- Never claim a fix is complete without running the relevant validation.

## This project
- Test PDF-viewer logic, state transitions, and error handling where applicable.
- Prefer behavior-focused tests around file handling, rendering decisions, and UI state changes.
