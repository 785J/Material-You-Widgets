## applyTo: "**/*.kt"

# Material 3 UI Constitution

## 1. PRIMARY DESIGN SYSTEM

The application UI MUST use Google's Material 3 design system as the default design language.

Primary references:

* Material Design 3:
  https://m3.material.io/

* Android Material 3 Compose documentation:
  https://developer.android.com/develop/ui/compose/designsystems/material3

* Material 3 Compose API reference:
  https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary

* Official Jetpack Compose samples:
  https://github.com/android/compose-samples

Use these references as the source of truth for UI decisions.

Do NOT invent arbitrary UI patterns when Material 3 already provides an appropriate component.

---

# 2. UI MUST START WITH THE THEME

Before building substantial UI, establish the application's Material 3 theme.

The theme MUST define and centralize:

* ColorScheme
* Dynamic colors
* Light theme
* Dark theme
* Typography
* Shapes
* Material theme configuration
* System UI integration where appropriate

Use:

```kotlin
MaterialTheme(
    colorScheme = ...,
    typography = ...,
    shapes = ...
) {
    ...
}
```

Material 3's core theme consists of color, typography and shape systems. Changes to these theme values should flow automatically into Material components.

Do NOT scatter arbitrary colors, typography sizes or corner radii throughout the UI.

---

# 3. DYNAMIC COLOR IS THE DEFAULT

For Android 12+ applications, support Material You dynamic color.

Use:

```kotlin
dynamicLightColorScheme(context)
dynamicDarkColorScheme(context)
```

when dynamic color is available.

The standard strategy is:

```text
Android 12+
    ↓
Dynamic Light/Dark Color Scheme

Older Android
    ↓
App-defined Light/Dark Color Scheme
```

Dynamic color should be the default unless the product explicitly requires a fixed brand palette.

The app must still have deterministic fallback colors for devices without dynamic color support.

Do not manually sample the wallpaper.

Do not recreate Google's dynamic color algorithm.

Use the official Material 3 APIs.

---

# 4. NEVER START A NEW APP WITH RANDOM COLORS

Do NOT begin a project with:

```kotlin
Color(0xFF...)
```

scattered across Composables.

Instead establish:

```text
ui/
└── theme/
    ├── Theme.kt
    ├── Color.kt
    ├── Type.kt
    └── Shape.kt
```

or the equivalent structure used by the existing project.

Use semantic Material roles:

```kotlin
MaterialTheme.colorScheme.primary
MaterialTheme.colorScheme.onPrimary
MaterialTheme.colorScheme.primaryContainer
MaterialTheme.colorScheme.onPrimaryContainer

MaterialTheme.colorScheme.secondary
MaterialTheme.colorScheme.secondaryContainer

MaterialTheme.colorScheme.tertiary
MaterialTheme.colorScheme.tertiaryContainer

MaterialTheme.colorScheme.background
MaterialTheme.colorScheme.surface
MaterialTheme.colorScheme.surfaceVariant
MaterialTheme.colorScheme.onSurface
MaterialTheme.colorScheme.onSurfaceVariant

MaterialTheme.colorScheme.outline
MaterialTheme.colorScheme.error
```

Prefer semantic roles over raw colors.

---

# 5. COLOR ROLE RULE

Never choose a random color because it "looks good".

Ask:

"What Material color role is appropriate for this UI element?"

Examples:

Primary action:

```text
primary / onPrimary
```

Secondary tonal action:

```text
secondaryContainer / onSecondaryContainer
```

Supporting surface:

```text
surfaceContainer / onSurface
```

Error:

```text
error / onError
```

Border:

```text
outline
```

Secondary text:

```text
onSurfaceVariant
```

Use the semantic role that matches the component.

Do not create a new color for every screen.

---

# 6. USE OFFICIAL MATERIAL COMPONENTS FIRST

Before implementing a UI component manually, search the Material 3 Compose API.

If Material 3 provides a component that matches the requirement, USE IT.

Examples include:

```text
Button
FilledTonalButton
OutlinedButton
TextButton

Card
ElevatedCard
OutlinedCard

ListItem

TextField
OutlinedTextField

Checkbox
RadioButton
Switch

AssistChip
FilterChip
InputChip
SuggestionChip

FloatingActionButton
SmallFloatingActionButton
LargeFloatingActionButton

NavigationBar
NavigationRail
NavigationDrawer

TopAppBar
CenterAlignedTopAppBar
MediumTopAppBar
LargeTopAppBar

ModalBottomSheet
BottomSheetScaffold

AlertDialog
BasicAlertDialog

DropdownMenu
DropdownMenuItem

Tab
TabRow
PrimaryTabRow
SecondaryTabRow

Snackbar
SnackbarHost

SearchBar

DatePicker
DateRangePicker

TimePicker

ProgressIndicator
CircularProgressIndicator
LinearProgressIndicator
```

The current Material 3 Compose API reference should be checked before creating a custom replacement.

---

# 7. LIST RULE

If the user asks for:

* list
* settings list
* menu-like list
* account list
* contacts list
* preference list
* selectable list item
* list row

FIRST consider:

```kotlin
androidx.compose.material3.ListItem
```

Do not automatically create:

```kotlin
Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(...)
)
```

with manually invented spacing and shapes.

Material 3 `ListItem` already supports:

* leading content
* headline content
* supporting content
* overline content
* trailing content
* enabled state
* selected state
* click behavior
* long click behavior
* shapes
* colors
* elevation
* content padding

Use the appropriate official overload.

For a standard list:

```kotlin
LazyColumn {
    items(items) { item ->
        ListItem(
            leadingContent = { ... },
            supportingContent = { ... },
            trailingContent = { ... },
            content = {
                Text(item.title)
            }
        )
    }
}
```

Use `HorizontalDivider()` only when the Material specification or visual design calls for separation.

---

# 8. DO NOT RECREATE MATERIAL COMPONENTS

Do NOT manually recreate:

* buttons
* list items
* chips
* switches
* checkboxes
* dialogs
* navigation bars
* text fields
* app bars
* FABs
* snackbars
* bottom sheets

when an official Material 3 component exists.

Bad:

```text
CustomRoundedButton.kt
```

containing manually chosen:

* corner radius
* elevation
* colors
* pressed state
* typography
* padding

when a Material 3 Button already provides the correct behavior.

Prefer:

```kotlin
Button(...)
```

or the appropriate Material 3 variant.

---

# 9. CUSTOM COMPONENT RULE

Custom components are allowed when:

1. Material 3 does not provide the required component.
2. The product has a genuinely unique interaction.
3. A reusable combination of Material components deserves its own component.
4. A domain-specific component is required.

When creating a custom component:

* build it FROM Material 3 primitives where possible
* inherit `MaterialTheme`
* use Material color roles
* use Material typography
* use Material shapes
* respect Material spacing and interaction patterns
* support accessibility
* support light/dark themes
* support dynamic color

Do not create a completely independent visual language.

---

# 10. SHAPES

Shapes are part of the Material theme.

Do not randomly apply:

```kotlin
RoundedCornerShape(8.dp)
RoundedCornerShape(12.dp)
RoundedCornerShape(16.dp)
RoundedCornerShape(24.dp)
```

throughout the project.

Prefer:

```kotlin
MaterialTheme.shapes
```

or official component defaults.

If custom shapes are required, define them centrally in the theme.

The UI should have a consistent shape language.

---

# 11. TYPOGRAPHY

Do not randomly choose:

```kotlin
fontSize = 14.sp
fontSize = 17.sp
fontSize = 22.sp
```

for every screen.

Use:

```kotlin
MaterialTheme.typography
```

with the appropriate Material type role.

Examples:

```text
displayLarge
displayMedium
displaySmall

headlineLarge
headlineMedium
headlineSmall

titleLarge
titleMedium
titleSmall

bodyLarge
bodyMedium
bodySmall

labelLarge
labelMedium
labelSmall
```

Only create custom typography values when there is a real design requirement.

---

# 12. SPACING

Use a consistent spacing system.

Do not invent random spacing values throughout every screen.

Prefer a project-level spacing system if one exists.

If no custom spacing system exists, use sensible Material-compatible spacing and keep it centralized where repeated.

Avoid visual inconsistency such as:

```text
Screen A: 13dp
Screen B: 17dp
Screen C: 19dp
Screen D: 23dp
```

without a reason.

---

# 13. ICONS

Prefer official Material icons when an appropriate icon exists.

Use meaningful semantic icons.

Do not use text characters as icons:

```text
"<"
">"
"⋮"
"✓"
"×"
```

when a proper icon is available.

Prefer:

```kotlin
Icons.AutoMirrored.Filled.ArrowBack
Icons.Default.MoreVert
Icons.Default.Check
Icons.Default.Close
```

Use Material Icons or another properly licensed icon library when the Material icon does not exist.

Do not download random SVG icons from unknown websites.

---

# 14. COMPONENT SELECTION PROTOCOL

When the user requests a UI element:

STEP 1:

Identify what the element actually is.

Example:

"settings list"

means:

```text
ListItem + appropriate supporting/trailing controls
```

"primary action"

means:

```text
Button
```

"secondary action"

may mean:

```text
FilledTonalButton
OutlinedButton
TextButton
```

depending on emphasis.

"floating action"

means:

```text
FloatingActionButton
```

STEP 2:

Search the Material 3 API.

STEP 3:

Check the Material 3 specification.

STEP 4:

Use the official component.

STEP 5:

Customize through supported Material APIs rather than rebuilding the component.

---

# 15. MATERIAL COMPONENT API RESEARCH

When uncertain about how to implement a component, inspect:

1. Material Design specification
2. Android Developers Material 3 documentation
3. Material 3 Compose API reference
4. Official Android Compose samples
5. AndroidX source code when necessary

Do not rely on random tutorial code as the primary authority.

---

# 16. OFFICIAL REFERENCE PRIORITY

Reference priority:

### Tier 1 — Source of truth

```text
m3.material.io
developer.android.com
AndroidX source
```

### Tier 2 — Official examples

```text
github.com/android/compose-samples
github.com/android/nowinandroid
```

The official Compose samples include examples covering Material 3, dynamic color, adaptive UI, custom design systems, animation, and the Material component catalog.

### Tier 3 — Community repositories

Use GitHub community projects for inspiration and implementation patterns.

Do not treat community code as the Material specification.

---

# 17. MATERIAL CATALOG

When looking for an example of a Material component, inspect the official Material Catalog / AndroidX samples.

The official Compose samples specifically point to a Material Catalog that demonstrates Material Design components and their implementation and behavior.

Use it as a visual/API reference before creating custom components.

---

# 18. GOOGLE-STYLE UI

When the user asks for:

* Pixel-like UI
* Google-style UI
* clean Android UI
* modern Android UI
* native Android look
* Material You UI

interpret this as:

```text
Material 3
+
Material You
+
Dynamic Color
+
Material typography
+
Material shapes
+
Material components
+
proper Android navigation
+
appropriate motion
+
accessibility
```

Do not interpret "Google-like" as copying a specific Google application.

Use Material design principles rather than copying proprietary screens.

---

# 19. ELEVATION

Do not add shadows everywhere.

Material 3 uses tonal surfaces and elevation intentionally.

Prefer Material component defaults.

Do not do this automatically:

```kotlin
shadow(...)
elevation = 20.dp
```

unless the design actually calls for it.

Prefer the elevation model provided by Material components.

---

# 20. SURFACES

Prefer Material surface roles and containers over arbitrary background colors.

Use the appropriate:

```text
background
surface
surfaceContainer
surfaceVariant
surfaceContainerHigh
surfaceContainerLow
```

or the roles supported by the project's Material 3 version.

Do not create dozens of custom background colors.

---

# 21. DARK MODE

Every screen must work in:

* light theme
* dark theme
* dynamic light theme
* dynamic dark theme

Do not assume:

```text
white background
black text
gray border
```

will remain correct.

Use Material semantic colors.

---

# 22. ACCESSIBILITY

Every interactive component must be usable with accessibility services.

Consider:

* content descriptions
* semantic labels
* touch target sizes
* text contrast
* enabled/disabled states
* selected states
* screen-reader meaning

Do not remove accessibility semantics just to make UI code shorter.

Material components provide a strong accessibility foundation; preserve their semantics instead of replacing them with custom primitives unnecessarily.

---

# 23. RESPONSIVE UI

Do not assume the application only runs on one phone size.

Consider:

* small phones
* large phones
* tablets
* foldables
* landscape
* desktop/windowed environments where supported

Use adaptive Material navigation and layout patterns when appropriate.

Do not hardcode screen widths.

---

# 24. SYSTEM UI

Respect the Android system UI.

Consider:

* status bar
* navigation bar
* edge-to-edge
* window insets
* gesture navigation
* display cutouts
* light/dark system bars

Do not blindly add large manual top padding.

Use appropriate window inset APIs.

---

# 25. MOTION

Use motion intentionally.

Prefer Material motion patterns and Compose animation APIs.

Animation should communicate:

* hierarchy
* state change
* navigation
* continuity
* feedback

Do not animate every element.

Avoid unnecessary:

```kotlin
delay(...)
```

or random animations added only to make the app look "cool."

---

# 26. LOADING STATES

Do not leave blank screens while data loads.

Use appropriate Material patterns such as:

* progress indicators
* loading content
* placeholders/skeletons when justified

Keep loading UI consistent across the app.

---

# 27. EMPTY STATES

An empty list should not simply render an empty `LazyColumn`.

Provide an intentional empty state when appropriate:

```text
icon/illustration
title
supporting explanation
optional action
```

Use Material typography and spacing.

---

# 28. ERROR STATES

Errors should have clear visual hierarchy.

Prefer:

```text
title
explanation
retry/action
```

where appropriate.

Use Material error roles.

Do not rely on arbitrary red colors.

---

# 29. FORMS

Use Material 3 text input components.

Prefer:

```text
TextField
OutlinedTextField
```

depending on the design.

Provide:

* labels
* supporting text
* validation state
* error state
* appropriate keyboard options
* accessibility semantics

Do not build text fields from raw `BasicTextField` unless there is a specific reason.

---

# 30. SETTINGS SCREENS

Settings screens should use Material patterns.

For a typical preference:

```text
ListItem
    leading icon
    headline
    supporting text
    trailing control
```

Examples:

```text
Switch
Checkbox
RadioButton
```

should be used as appropriate.

Do not manually draw preference rows.

---

# 31. DIALOGS

Prefer Material 3:

```text
AlertDialog
BasicAlertDialog
```

before creating a custom dialog.

Use custom dialogs only when the interaction genuinely requires a custom layout.

---

# 32. BOTTOM SHEETS

Prefer:

```text
ModalBottomSheet
BottomSheetScaffold
```

when appropriate.

Do not recreate bottom sheets using a `Column` at the bottom of the screen.

---

# 33. NAVIGATION

Use Material navigation components where appropriate.

For mobile:

```text
NavigationBar
```

For larger screens:

```text
NavigationRail
NavigationDrawer
```

Choose based on window size and information architecture.

Do not permanently force a bottom navigation bar onto tablet/desktop layouts when an adaptive Material pattern is more appropriate.

---

# 34. COMPONENT CUSTOMIZATION

When customizing a Material component:

First use:

```text
MaterialTheme
ComponentDefaults
```

and official component parameters.

Only create custom drawing or custom layouts when necessary.

Example:

Prefer:

```kotlin
Button(
    colors = ButtonDefaults.buttonColors(...)
)
```

over rebuilding the Button with:

```text
Row
Surface
clickable
padding
RoundedCornerShape
```

---

# 35. DESIGN SYSTEM STRUCTURE

For a serious application, prefer:

```text
ui/
└── theme/
    ├── Theme.kt
    ├── Color.kt
    ├── Type.kt
    ├── Shape.kt
    └── Dimens.kt

ui/
└── components/
    ├── AppButton.kt
    ├── AppCard.kt
    └── ...
```

But do NOT wrap every Material component unnecessarily.

Only create `AppButton`, `AppCard`, etc. when the application needs consistent product-specific behavior or styling.

Otherwise directly use Material 3 components.

---

# 36. DESIGN TOKENS

If the application has a custom brand, centralize:

* colors
* typography
* shapes
* spacing
* elevation
* motion

Do not distribute design tokens throughout feature files.

The UI should have one coherent visual language.

---

# 37. VISUAL QUALITY CHECK

Before declaring UI complete, inspect it for:

* inconsistent padding
* inconsistent corner radius
* incorrect typography hierarchy
* random colors
* excessive cards
* excessive borders
* excessive shadows
* poor contrast
* awkward empty space
* cramped content
* inconsistent icon sizes
* incorrect component choice
* broken dark theme
* broken dynamic theme
* poor accessibility
* poor tablet behavior

If any of these exist, improve the UI.

---

# 38. ANTI-PATTERNS

Avoid UI that looks like generic AI-generated Android UI.

Do NOT automatically produce:

```text
white background
+
purple button
+
RoundedCornerShape(16.dp)
+
Card around everything
+
random shadow
+
Row for every component
```

This is NOT a substitute for Material 3.

Use actual Material 3 components and their default visual language.

---

# 39. NO "CARD EVERYTHING"

Do not put every section inside a Card.

Material 3 supports hierarchy through:

* surface roles
* typography
* spacing
* containers
* tonal elevation
* dividers
* component shapes

A card should communicate grouping or hierarchy.

If everything is a card, nothing has hierarchy.

---

# 40. NO CUSTOM COMPONENT WITHOUT A REASON

Before creating:

```text
CustomButton
CustomList
CustomTextField
CustomSwitch
CustomDialog
CustomBottomSheet
CustomTopBar
```

ask:

"Does Material 3 already provide this?"

If yes, use the Material component.

If no, explain why a custom component is required.

---

# 41. UI IMPLEMENTATION PROTOCOL

For every significant UI request:

### Step 1

Understand the user's intent.

### Step 2

Identify the Material 3 component(s) that best match it.

### Step 3

Check the official Material 3 documentation/API.

### Step 4

Check official Compose samples if implementation details are unclear.

### Step 5

Use the Material component.

### Step 6

Connect it to the application's theme.

### Step 7

Verify light/dark/dynamic color.

### Step 8

Verify accessibility.

### Step 9

Verify responsive behavior.

### Step 10

Only then consider custom UI.

---

# 42. THEME-FIRST RULE

For a NEW Android application:

Do NOT start by building screens.

First create:

```text
Theme.kt
Color.kt
Type.kt
Shape.kt
```

and the root:

```kotlin
MaterialTheme(...)
```

Then establish:

```text
dynamic light theme
dynamic dark theme
fallback light theme
fallback dark theme
```

Then build the application UI.

The theme is the foundation of the application.

---

# 43. BRAND COLOR RULE

If the user provides a brand color:

Use it as the Material source/key color and derive the Material color roles appropriately.

Do not simply use the same hex value for:

```text
background
button
text
border
icon
```

Material color roles must determine where the color is used.

Use Material Theme Builder or equivalent official tooling when appropriate.

---

# 44. EXISTING APP RULE

If working on an existing application:

FIRST inspect:

```text
Theme.kt
Color.kt
Type.kt
Shape.kt
```

and existing Material components.

Do not introduce a second theme.

Do not create a second color system.

Do not replace existing design tokens without understanding the current design system.

---

# 45. FINAL UI RULE

The goal is NOT:

"make the UI colorful."

The goal is:

"make the UI feel like a coherent, polished Android application built using the Material design system."

Prioritize:

1. hierarchy
2. consistency
3. usability
4. accessibility
5. responsiveness
6. motion
7. Material semantics
8. visual polish

---

# GOLDEN UI RULE

WHEN MATERIAL 3 ALREADY KNOWS HOW TO BUILD IT, DO NOT REINVENT IT.

Use the official Material 3 component first.

Customize only when necessary.

Build custom UI from Material 3 primitives when the product genuinely requires something unique.
