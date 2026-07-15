# Design Tokens Reference

## Color Palette

### Primary Colors
```xml
<!-- Purple Gradient -->
<color name="primary_purple">#FF7C3AED</color>
<color name="primary_indigo">#FF6366F1</color>
<color name="primary_violet">#FF8B5CF6</color>
```

### Accent Colors
```xml
<!-- Dynamic Accents -->
<color name="accent_cyan">#FF06B6D4</color>
<color name="accent_blue">#FF0EA5E9</color>
<color name="accent_pink">#FFEC4899</color>
```

### Glass Effects
```xml
<!-- Glassmorphism -->
<color name="glass_light">#4DFFFFFF</color>        <!-- 30% white -->
<color name="glass_border">#80FFFFFF</color>       <!-- 50% white -->
<color name="glass_dark">#1A1A1A99</color>         <!-- For dark mode -->
<color name="glass_dark_border">#4D666666</color>  <!-- Dark border -->
```

### Text Colors
```xml
<!-- Semantic Text -->
<color name="text_primary">#FFFFFFFF</color>       <!-- Main text -->
<color name="text_secondary">#FFE0E0E0</color>     <!-- Supporting text -->
<color name="text_tertiary">#FFB0B0B0</color>      <!-- Tertiary text -->
```

### Neutral Colors
```xml
<!-- Fallbacks -->
<color name="black">#FF000000</color>
<color name="white">#FFFFFFFF</color>
<color name="off_white">#FFF5F5F7</color>
<color name="dark_bg">#FF0F0F0F</color>
<color name="dark_surface">#FF1A1A1A</color>
```

---

## Typography Scale

### Headings
| Size | Usage | Style |
|------|-------|-------|
| 32sp | App Title | Bold, 0.05em letter spacing |
| 24sp | Section Headers | Bold, 0.02em letter spacing |
| 20sp | Card Titles | Bold |

### Body Text
| Size | Usage | Style |
|------|-------|-------|
| 24sp | Quote Text | Italic, Serif, 10dp line spacing |
| 16sp | Primary Body | Regular |
| 15sp | Secondary Body | Regular, 0.02em letter spacing |
| 14sp | Labels & Hints | Regular, 0.01em letter spacing |
| 13sp | Tertiary Text | Regular, reduced opacity |

---

## Spacing System

### Padding
```
Extra Small: 8dp
Small:       12dp
Medium:      16dp
Large:       20dp
Extra Large: 24dp
XXL:         32dp
XXXL:        40dp
```

### Margins
```
Vertical:    4dp - 40dp (scales with content)
Horizontal:  0dp - 20dp (consistent)
```

### Gaps
```
Component Gap: 16dp - 20dp
Section Gap:   24dp - 28dp
```

---

## Component Sizing

### Buttons
```
Primary Button Height:   56dp
Primary Button Radius:   18dp
Secondary Button Height: 40dp
Secondary Button Radius: 12dp
```

### Icons
```
Icon Button Size:     56dp
Inline Icon Size:     24sp
Touch Target Min:     56dp
```

### Cards
```
Radius:      28dp
Elevation:   0dp (uses glass effect)
Padding:     40dp
Min Height:  wrap_content
```

---

## Shadow System

### Depth Levels
```xml
<!-- Level 1 (Subtle) -->
android:shadowColor="#20000000"
android:shadowDx="0"
android:shadowDy="2"
android:shadowRadius="8"

<!-- Level 2 (Medium) -->
android:shadowColor="#20000000"
android:shadowDx="0"
android:shadowDy="4"
android:shadowRadius="8"

<!-- Level 3 (Strong) -->
(Not commonly used - relying on glass effect)
```

---

## Glass Morphism Recipe

### Standard Glass Background
```xml
<shape>
    <solid android:color="@color/glass_light" />
    <corners android:radius="28dp" />
    <stroke
        android:width="1.5dp"
        android:color="@color/glass_border" />
</shape>
```

### Usage
- Main quote card background
- Reminder card background
- Widget backgrounds

---

## Gradient System

### Primary Gradient (Liquid)
```xml
<gradient
    android:angle="135"
    android:startColor="#FF7C3AED"     <!-- Purple -->
    android:centerColor="#FF6366F1"    <!-- Indigo -->
    android:endColor="#FF06B6D4"       <!-- Cyan -->
    android:type="linear" />
```

### Usage
- Screen background
- Widget background
- Dialog backgrounds

---

## Opacity Scale

### Text Opacity
```
Primary:     100% (1.0)
Secondary:   88%  (0.88)
Tertiary:    70%  (0.70)
Hint:        50%  (0.50)
Disabled:    38%  (0.38)
```

### Component Opacity
```
Icons (interactive):    90%  (0.90)
Icons (decorative):     60%  (0.60)
Disabled buttons:       50%  (0.50)
Loading state:          60%  (0.60)
```

---

## Accessibility Considerations

### Color Contrast
- Text on gradients: Minimum 4.5:1 contrast ratio
- Icons: Minimum 3:1 contrast ratio
- Interactive elements: 56dp minimum touch target

### Text Legibility
- Minimum font size: 13sp
- Line height: 1.4-1.6x (6-10dp extra)
- Letter spacing: 0-0.05em

---

## Animation & Transitions

### Standard Duration
```
Quick: 150ms
Normal: 300ms
Slow: 500ms
```

### Quote Fade-In
```
Duration: 500ms
Type: Alpha animation
Start opacity: 0%
End opacity: 100%
```

---

## Theme Configuration

### Material3 Theme Items
```xml
<item name="colorPrimary">@color/primary_purple</item>
<item name="colorPrimaryContainer">@color/primary_indigo</item>
<item name="colorOnPrimaryContainer">@color/text_primary</item>
<item name="colorSecondary">@color/accent_cyan</item>
<item name="colorTertiary">@color/accent_pink</item>
<item name="android:windowBackground">@drawable/liquid_gradient</item>
```

### Application in Both Themes
- Light theme (values/themes.xml)
- Dark theme (values-night/themes.xml)

---

## Usage Examples

### Using Color Tokens
```xml
<!-- ✅ Good - Using semantic tokens -->
android:textColor="@color/text_primary"
android:backgroundTint="@color/primary_purple"

<!-- ❌ Avoid - Hard-coded colors -->
android:textColor="#FFFFFF"
android:backgroundTint="#6C5CE7"
```

### Using Spacing
```xml
<!-- ✅ Good - Using scale -->
android:padding="20dp"
android:layout_marginTop="24dp"
android:layout_marginStart="16dp"

<!-- ❌ Avoid - Arbitrary values -->
android:padding="23dp"
android:layout_marginTop="27dp"
```

### Typography
```xml
<!-- ✅ Good - Consistent sizing -->
android:textSize="24sp"
android:textStyle="italic"
android:letterSpacing="0.02"
android:lineSpacingExtra="10dp"

<!-- ❌ Avoid - Inconsistent styles -->
android:textSize="22sp"
```

---

## Dark Mode Adaptations

All colors automatically adapt when `values-night/` colors are used:

```xml
<!-- Automatically switches based on system theme -->
android:textColor="@color/text_primary"    <!-- Always optimal contrast -->
android:background="@drawable/liquid_gradient"  <!-- Gradient adapts -->
```

---

## Future Customization

To add new color theme (e.g., sunrise theme):
1. Create new gradient drawable in `drawable/`
2. Add to theme selection in widget configuration
3. Follow existing color naming conventions
4. Maintain 4.5:1 contrast minimum

---

This design system provides a solid foundation for consistent, beautiful, and accessible Android interfaces across the Quotely app.
