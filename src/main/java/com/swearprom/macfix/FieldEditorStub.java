package com.swearprom.macfix;

import ca.weblite.objc.Proxy;
import com.mojang.blaze3d.platform.Window;
import com.sun.jna.Callback;
import com.sun.jna.CallbackReference;
import com.sun.jna.Pointer;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFWNativeCocoa;
import org.lwjgl.system.macosx.ObjCRuntime;

import java.util.WeakHashMap;

/**
 * Installs a no-op {@code windowWillReturnFieldEditor:toObject:} on GLFW's
 * NSWindow delegate so AppKit styleMask changes on undecorated windows do not
 * crash with an unrecognized selector on macOS 26+.
 *
 * <p>Adapted from Cubes Without Borders (MIT), Kira NT —
 * {@code MinecraftWindow.MacOS.registerWindowWillReturnFieldEditorStub}.
 */
public final class FieldEditorStub {
    private static final WeakHashMap<Window, Callback> INSTALLED = new WeakHashMap<>();

    private FieldEditorStub() {
    }

    public static void install(Window window) {
        if (!Minecraft.ON_OSX || window == null || INSTALLED.containsKey(window)) {
            return;
        }

        long cocoaWindow = GLFWNativeCocoa.glfwGetCocoaWindow(window.getWindow());
        if (cocoaWindow == 0L) {
            MacFix.LOGGER.warn("macfix: GLFW returned no Cocoa window");
            return;
        }

        Proxy nsWindow = new Proxy(new Pointer(cocoaWindow));
        Proxy nsWindowDelegate = nsWindow.sendProxy("delegate");
        if (nsWindowDelegate == null) {
            MacFix.LOGGER.warn("macfix: NSWindow has no delegate yet");
            return;
        }
        if (nsWindowDelegate.sendBoolean("respondsToSelector:", "windowWillReturnFieldEditor:toObject:")) {
            return;
        }

        Callback callback = new Callback() {
            @SuppressWarnings("unused")
            public Pointer windowWillReturnFieldEditor(
                    Pointer self,
                    Pointer sel,
                    Pointer sender,
                    Pointer client
            ) {
                return null;
            }
        };

        boolean success = ObjCRuntime.class_addMethod(
                ObjCRuntime.object_getClass(Pointer.nativeValue(nsWindowDelegate.getPeer())),
                ObjCRuntime.sel_registerName("windowWillReturnFieldEditor:toObject:"),
                Pointer.nativeValue(CallbackReference.getFunctionPointer(callback)),
                "@@:@@"
        );

        if (success) {
            INSTALLED.put(window, callback);
            MacFix.LOGGER.info("macfix: installed windowWillReturnFieldEditor stub");
        } else {
            MacFix.LOGGER.warn("macfix: failed to add windowWillReturnFieldEditor stub");
        }
    }
}
