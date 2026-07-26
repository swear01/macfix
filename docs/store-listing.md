# Store listing (Modrinth + CurseForge)

Copy/paste for both platforms. Version **0.1.0**.

## Title

macfix

## Summary (short)

Client-only macOS bugfixes for Minecraft. 0.1.0 fixes the macOS 26+ Command-Q / borderless styleMask crash.

## Description

**macfix** is a small **client-only** mod that patches macOS-specific Minecraft / GLFW / AppKit bugs.

### Supported in 0.1.0

- Minecraft **1.21.1**
- **NeoForge**
- Environment: **Client**
- Other OSes: loads safely, does nothing

### Fix: Quit / borderless crash on macOS 26+

On macOS 26 (Tahoe) and newer, quitting or changing window decorations while Minecraft is in an undecorated / borderless / F11-style window can abort the Java process.

Typical crash:

```text
-[… windowWillReturnFieldEditor:toObject:]: unrecognized selector sent to instance …
```

Stack usually includes `libglfw` → `-[NSWindow setStyleMask:]`.

**What goes wrong:** AppKit started calling the optional NSWindowDelegate method `windowWillReturnFieldEditor:toObject:` during `styleMask` updates on undecorated windows. GLFW’s Cocoa delegate does not implement that selector, so the process aborts (`NSInvalidArgumentException` / SIGABRT). A common player trigger is **Command-Q while still in F11 / borderless fullscreen**.

**What macfix does:** When the Minecraft window is created (and again when the game begins stopping), macfix installs a tiny Objective-C stub on the GLFW NSWindow delegate that implements `windowWillReturnFieldEditor:toObject:` and returns `nil`. That matches AppKit’s default “no custom field editor” behavior and stops the unrecognized-selector abort.

This approach was documented / used by [Cubes Without Borders](https://github.com/Kira-NT/cubes-without-borders) (MIT). macfix is a dedicated fix-only mod so you do not need a full borderless-fullscreen feature pack just to survive quitting.

### Not included yet

- Fabric / Quilt builds
- Legacy Forge builds
- Other Minecraft versions
- Input fixes (Ctrl-click, trackpad, etc.) — see MacOS Input Fixes if you need those

### License

MIT. See the project `NOTICE` for Cubes Without Borders attribution.
