# Debug Prompt

Use this prompt when diagnosing a bug or unexpected behavior.

## Workflow
1. Reproduce the issue and identify the exact failing behavior.
2. Trace the relevant code path from input to output.
3. Check the app’s architecture boundaries for the root cause.
4. Validate the smallest hypothesis before broad changes.
5. Fix the root cause, not just the symptom.

## Required checks
- Confirm whether the bug is in UI state, data flow, domain logic, or platform integration.
- Review similar patterns or existing implementations for consistent handling.
- Ensure error states and lifecycle handling are correctly managed.
- Validate with a targeted test or app-level verification when possible.

## Completion standard
- The root cause is identified and addressed.
- The fix preserves expected behavior elsewhere.
- Validation demonstrates the issue is resolved.
