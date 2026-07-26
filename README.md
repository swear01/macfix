# macfix

Client-only Minecraft mod that collects **macOS-specific bug fixes**.

**0.1.0** targets **NeoForge 1.21.1** only. See below for why one universal jar is not realistic.

## Can one jar cover every version + Forge / NeoForge / Fabric?

**No — not as a single jar for all Minecraft versions and all loaders.**

| Goal | Reality |
|---|---|
| One jar, all Minecraft versions | Not realistic for mixin mods. Class/mappings change every release; you need per-version builds (or a heavy multiversion monorepo that still outputs many jars). |
| One jar, Fabric + NeoForge on the **same** MC version | Possible: shared code → two builds, optionally **Forgix-merged** into one “universal” jar for that version. Still not “all versions”. |
| Forge vs NeoForge on 1.21.1 | Players on 1.21.1 use **NeoForge**. Legacy Forge is a separate line (older MC). |
| Fabric | Needs its own `fabric.mod.json` / entrypoint; can share the same mixin logic via MultiLoader/Architectury. |

**0.1.0 ships NeoForge 1.21.1.** Fabric (and optional merged jar) can be added next if you want Modrinth/CF to list both loaders for 1.21.1.

## Fixes in 0.1.0

### Quit / `styleMask` crash on macOS 26+

**Symptom:** Abrupt abort (often while pressing **Command-Q** in F11 / borderless fullscreen). Crash report shows:

`-[… windowWillReturnFieldEditor:toObject:]: unrecognized selector`

inside `-[NSWindow setStyleMask:]` via `libglfw`.

**Cause:** On macOS 26+, AppKit may call optional `NSWindowDelegate` method `windowWillReturnFieldEditor:toObject:` when changing `styleMask` on undecorated windows. GLFW’s delegate does not implement it → process abort.

**Fix:** At window creation (and again at `Minecraft.stop`), install a runtime Objective-C method on the GLFW NSWindow delegate that returns `nil`. Default AppKit behavior is preserved.

Adapted from [Cubes Without Borders](https://github.com/Kira-NT/cubes-without-borders) (MIT) — see `NOTICE`.

## Requirements (0.1.0)

- Minecraft **1.21.1**
- **NeoForge** 21.1.x
- macOS (loads elsewhere but no-ops)

## Build

```bash
cd /Users/swear/Documents/diy_minecraft_mods/macfix
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
./gradlew build
```

Jar: `build/libs/macfix-0.1.0.jar`  
Icon: `icon.png` (also `assets/macfix/icon.png`)

## Publish checklist

### Modrinth
1. https://modrinth.com/dashboard → Create project  
2. Slug: `macfix`  
3. Type: Mod · Client · NeoForge · 1.21.1 · MIT  
4. Icon: `icon.png`  
5. Summary + description: copy from `docs/store-listing.md`  
6. Upload `macfix-0.1.0.jar`

### CurseForge
1. https://authors.curseforge.com/ → Create project  
2. Game: Minecraft · Category: Mods · NeoForge · 1.21.1  
3. Same icon + description from `docs/store-listing.md`  
4. Upload jar; wait for file moderation  

## License

MIT. Cubes Without Borders attribution in `NOTICE`.
