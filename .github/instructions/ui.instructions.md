---
applyTo: "**/*.kt,**/*.kts"
---

# Material 3 UI Constitution

## 1. PRIMARY DESIGN SYSTEM
The application UI MUST use Google's Material 3 design system as the default design language.

Primary references:

- Material Design 3: https://m3.material.io/
- Android Material 3 Compose documentation: https://developer.android.com/develop/ui/compose/designsystems/material3
- Material 3 Compose API reference: https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary
- Official Jetpack Compose samples: https://github.com/android/compose-samples

Use these references as the source of truth for UI decisions.

Do NOT invent arbitrary UI patterns when Material 3 already provides an appropriate component.

---

## 2. UI MUST START WITH THE THEME
Before building substantial UI, establish the application's Material 3 theme.

The theme MUST define and centralize:

- ColorScheme
- Dynamic colors
- Light theme
- Dark theme
- Typography
- Shapes
- Material theme configuration
- System UI integration where appropriate

Use `MaterialTheme(...)` and centralize tokens in the theme.

Do NOT scatter arbitrary colors, typography sizes, or corner radii throughout the UI.

---

## 3. DYNAMIC COLOR IS THE DEFAULT
For Android 12+ applications, support Material You dynamic color.

Use `dynamicLightColorScheme(context)` and `dynamicDarkColorScheme(context)` when dynamic color is available.

The standard strategy is:

- Android 12+ -> Dynamic Light/Dark Color Scheme
- Older Android -> App-defined Light/Dark Color Scheme

Dynamic color should be the default unless the product explicitly requires a fixed brand palette.

---

## 4. USE OFFICIAL MATERIAL COMPONENTS FIRST
Before implementing a UI component manually, search the Material 3 Compose API.

If a Material 3 component already matches the requirement, use it.

For example, if the user asks for a standard list or settings row, prefer `ListItem` instead of making a custom row.

---

## 5. LIST RULE
If the user asks for a list, settings list, menu-like list, account list, contacts list, preference list, or selectable list row, FIRST consider `androidx.compose.material3.ListItem`.

Do not automatically create a custom `Row` with arbitrary padding and shapes when the Material component already provides the required behavior.

---

## 6. CUSTOM COMPONENTS ONLY WHEN NEEDED
Custom components are allowed when:

- Material 3 does not provide the required component
- the product has a genuinely unique interaction
- a reusable combination of Material components deserves a wrapper

When creating a custom component:

- build it from Material 3 primitives where possible
- inherit `MaterialTheme`
- use Material color roles, shape, and typography
- support light/dark themes and accessibility
- avoid reinventing native Material patterns

---

## 7. THEME-FIRST RULE
A new app should start with a Material 3 theme rather than raw colors and ad hoc layout styling.

Prefer:

- `MaterialTheme.colorScheme`
- `MaterialTheme.typography`
- `MaterialTheme.shapes`
- semantic color roles

Do not start by scattering `Color(0xFF...)` values all over the screen.

---

## 8. ACCESSIBILITY AND QUALITY
Every interactive element should respect Material semantics and accessibility.

- keep touch targets large enough
- use semantic labels where needed
- respect contrast and readable hierarchy
- support light/dark themes and dynamic color
- avoid random shadows, arbitrary cards, or inconsistent spacing

---

## 9. GOLDEN UI RULE
When Material 3 already knows how to build it, do not reinvent it.

Use the official Material 3 component first.
Customize only when necessary.
