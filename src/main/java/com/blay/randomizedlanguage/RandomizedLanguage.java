package com.blay.randomizedlanguage;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;

import java.util.Random;
import java.util.Map;

public class RandomizedLanguage implements ClientModInitializer {
    private Random random;

    private KeyBinding randomLanguageKeybind;
    private KeyBinding returnToNativeKeybind;

    @Override
    public void onInitializeClient() {
        random = new Random();

        randomLanguageKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Randomize Your Language",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_COMMA,
            "Randomized Language"
        ));

        returnToNativeKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Return To Native",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_PERIOD,
            "Randomized Language"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (randomLanguageKeybind.wasPressed()) {
                LanguageManager languageManager = client.getLanguageManager();
                Object[] allLanguages = languageManager.getAllLanguages().entrySet().toArray();
                Map.Entry<String,LanguageDefinition> language = (Map.Entry<String,LanguageDefinition>) allLanguages[random.nextInt(allLanguages.length)];
                client.player.sendMessage(Text.literal("[Randomized Language] Changing the language to: ").append(language.getValue().getDisplayText()), true);
                languageManager.setLanguage(language.getKey());
                languageManager.reload(client.getResourceManager());
            }

            if (returnToNativeKeybind.wasPressed()) {
                LanguageManager languageManager = client.getLanguageManager();
                client.player.sendMessage(Text.literal("[Randomized Language] Changing the language to: ").append(languageManager.getLanguage(client.options.language).getDisplayText()), true);
                languageManager.setLanguage(client.options.language);
                languageManager.reload(client.getResourceManager());
            }
        });
    }
}
