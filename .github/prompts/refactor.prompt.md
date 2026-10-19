# Refactor Prompt

Use this prompt when restructuring existing code without changing intended behavior.

## Goal
Refactor for clarity, maintainability, and architectural consistency while preserving behavior.

## Guardrails
- Do not rewrite unrelated code.
- Keep the change focused and reviewable.
- Avoid changing public behavior unless required by the refactor.
- Do not introduce new abstractions without a clear benefit.

## Review checklist
- Is the code easier to understand?
- Are responsibilities better separated?
- Are duplicated patterns reduced without overengineering?
- Are imports, names, and package usage consistent?
- Has validation been run on the affected area?

## Output expectations
- Summarize the refactor intent.
- List the files touched.
- Note any architecture improvements or constraints.
- Include verification performed.
