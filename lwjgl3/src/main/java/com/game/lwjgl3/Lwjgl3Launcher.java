package com.game.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.game.Main;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    private static boolean fullscreen = false; // Flag for fullscreen mode, default is windowed.

    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("AngryBirds");

        //// Vsync helps eliminate screen tearing, but can be disabled if you want to test max FPS.
        configuration.useVsync(true);

        //// Toggle between fullscreen and windowed mode based on the `fullscreen` flag
        if (fullscreen) {
            configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode()); // Fullscreen
        } else {
            configuration.setWindowedMode(800, 500); // Windowed mode
        }

        //// FPS is capped based on the monitor's refresh rate
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);

        //// Set window icons
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        return configuration;
    }

    // Optional: Method to toggle fullscreen mode dynamically at runtime
    public static void toggleFullscreenMode() {
        fullscreen = !fullscreen; // Toggle the fullscreen flag

        // Recreate the application with the updated fullscreen setting
        createApplication();
    }
}
