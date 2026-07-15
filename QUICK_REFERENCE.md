# Design System Quick Reference

## 🎨 Color Tokens

### Primary Brand
```
primary_purple    #FF7C3AED  ◼️ Main brand color
primary_indigo    #FF6366F1  ◼️ Secondary variant
primary_violet    #FF8B5CF6  ◼️ Tertiary variant
```

### Accents
```
accent_cyan       #FF06B6D4  ◼️ Dynamic highlights
accent_blue       #FF0EA5E9  ◼️ Professional accents
accent_pink       #FFEC4899  ◼️ Warm touches
```

### Text
```
text_primary      #FFFFFFFF  ◼️ Main content
text_secondary    #FFE0E0E0  ◼️ Supporting text
text_tertiary     #FFB0B0B0  ◼️ De-emphasized
```

### Glass
```
glass_light       #4DFFFFFF  ◼️ 30% white
glass_border      #80FFFFFF  ◼️ 50% white border
```

---

## 📏 Spacing Scale

```
xs: 8dp     sm: 12dp    md: 16dp    lg: 20dp    xl: 24dp    xxl: 32dp    xxxl: 40dp
```

### Usage Patterns
```
Screen padding:     20dp (lg)
Card padding:       40dp (xxxl)
Component gap:      16dp (md) or 20dp (lg)
Section margin:     24dp (xl)
Icon spacing:       20dp (lg)
Button height:      56dp
Icon size:          56dp
```

---

## 🔤 Typography Quick Guide

| Element | Size | Weight | Line Height | Letter Spacing |
|---------|------|--------|-------------|-----------------|
| App Title | 32sp | Bold | 1.5 | 0.05em |
| Section Header | 24sp | Bold | 1.5 | 0.02em |
| Quote Text | 24sp | Italic | 1.4 | 0 |
| Body Text | 16sp | Regular | 1.5 | 0 |
| Label | 14sp | Regular | 1.4 | 0.01em |
| Subtitle | 13sp | Regular | 1.4 | 0 |

---

## 🎯 Component Sizing

```
Button Height:       56dp
Icon Button:         56dp
Secondary Button:    40dp
Card Radius:         28dp
Small Radius:        20dp
Tiny Radius:         12dp
Border Width:        1.5dp
Shadow Blur:         2-8px
```

---

## 🎨 Color Usage

### Buttons
```
Primary Button:
  Background: @color/text_primary (white)
  Text: @color/primary_purple
  
Secondary Button:
  Background Tint: @color/primary_purple
  Text: @color/text_primary
```

### Text
```
Headings:        @color/text_primary
Body:            @color/text_primary
Supporting:      @color/text_secondary
Labels:          @color/text_secondary
Hints:           @color/glass_border
```

### Backgrounds
```
Main Screen:     @drawable/liquid_gradient
Cards:           @drawable/glass_background
Overlays:        @color/glass_light
```

---

## 📐 Common Layouts

### Quote Card Pattern
```xml
<CardView app:cardCornerRadius="28dp">
    <LinearLayout android:background="@drawable/glass_background"
                  android:padding="40dp">
        <TextView android:textSize="24sp"
                  android:fontFamily="serif"
                  android:textColor="@color/text_primary" />
    </LinearLayout>
</CardView>
```

### Action Button Pattern
```xml
<Button android:height="56dp"
        android:backgroundTint="@color/text_primary"
        android:textColor="@color/primary_purple"
        app:cornerRadius="18dp" />
```

### Glass Card Pattern
```xml
<CardView app:cardCornerRadius="20dp">
    <LinearLayout android:background="@drawable/glass_background"
                  android:padding="16dp" />
</CardView>
```

---

## ✨ Shadow Quick Reference

### Text Shadow (Subtle)
```xml
android:shadowColor="#20000000"
android:shadowRadius="2"
```

### Element Shadow (Medium)
```xml
android:shadowColor="#20000000"
android:shadowDx="0"
android:shadowDy="4"
android:shadowRadius="8"
```

---

## 🎭 Glassmorphism Recipe

The signature glass effect is created using:
- **Glass Background Drawable**: `@drawable/glass_background`
- **Transparency**: 30% white (#4DFFFFFF)
- **Border**: 1.5dp white (80% opacity)
- **Radius**: 28dp for main elements, 20dp for secondary

---

## 🌙 Dark Mode

Automatically handled by Material3:
- Light colors defined in `values/`
- Dark colors in `values-night/`
- System switches automatically

No code changes needed - just use color tokens!

---

## 🎯 Most Used Attributes

### Colors
```
android:textColor="@color/text_primary"
android:backgroundTint="@color/primary_purple"
android:background="@drawable/glass_background"
```

### Spacing
```
android:padding="20dp"
android:layout_margin="24dp"
android:layout_marginStart="16dp"
```

### Text
```
android:textSize="24sp"
android:textStyle="bold"
android:letterSpacing="0.02"
android:lineSpacingExtra="10dp"
```

### Layout
```
android:layout_height="56dp"
app:cornerRadius="18dp"
app:cardCornerRadius="28dp"
android:layout_gravity="center"
```

---

## ⚡ Pro Tips

✨ **For Quotes**: Use serif font with 24sp italic
```xml
android:textSize="24sp"
android:textStyle="italic"
android:fontFamily="serif"
```

✨ **For Headers**: Use bold with letter spacing
```xml
android:textStyle="bold"
android:letterSpacing="0.05"
```

✨ **For Depth**: Use appropriate shadow blur
```xml
android:shadowRadius="8"  <!-- For elevation -->
android:shadowRadius="2"  <!-- For text -->
```

✨ **For Touch Targets**: Minimum 56dp
```xml
android:layout_height="56dp"
android:layout_width="56dp"
```

✨ **For Cards**: Use glass background
```xml
android:background="@drawable/glass_background"
android:padding="40dp"
app:cardCornerRadius="28dp"
```

---

## 🔗 File Locations

```
colors.xml              → /values/colors.xml
themes.xml              → /values/themes.xml
dark themes.xml         → /values-night/themes.xml
glass_background        → /drawable/glass_background.xml
liquid_gradient         → /drawable/liquid_gradient.xml
main layout             → /layout/activity_main.xml
widget layout           → /layout/widget_layout.xml
widget config           → /layout/widget_configure.xml
```

---

## 📚 Documentation

- **UI_IMPROVEMENTS.md** - Detailed changes
- **DESIGN_TOKENS.md** - Token reference
- **DESIGN_CHANGES_SUMMARY.md** - Before/after
- **DESIGN_IMPLEMENTATION_GUIDE.md** - How-to guide
- **QUICK_REFERENCE.md** - This file!

---

## 🚀 Implementation Checklist

New component checklist:
- [ ] Using semantic color tokens
- [ ] Following spacing scale
- [ ] Correct typography sizing
- [ ] 56dp minimum touch targets
- [ ] Proper shadows for depth
- [ ] Tested in light & dark modes

---

**Need help? Check the detailed guides or review existing components!** 🎨
