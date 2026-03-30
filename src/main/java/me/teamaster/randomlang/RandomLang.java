package me.teamaster.randomlang;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.network.chat.Component;

import org.lwjgl.glfw.GLFW;

import java.util.Random;
import java.util.ArrayList;
import java.util.Map;

public class RandomLang implements ClientModInitializer {
    private final Random random = new Random();

    private KeyMapping randomLanguageKeymapping;
    private KeyMapping returnToNativeKeymapping;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category keyMappingCategory = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("randomlang", "main"));

        this.randomLanguageKeymapping = KeyMappingHelper.registerKeyMapping(new KeyMapping("Randomize Your Language", GLFW.GLFW_KEY_COMMA, keyMappingCategory));
        this.returnToNativeKeymapping = KeyMappingHelper.registerKeyMapping(new KeyMapping("Return To Native", GLFW.GLFW_KEY_PERIOD, keyMappingCategory));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (this.randomLanguageKeymapping.consumeClick()) {
                LanguageManager languageManager = client.getLanguageManager();
                ArrayList<Map.Entry<String, LanguageInfo>> languages = new ArrayList<>(languageManager.getLanguages().entrySet());
                Map.Entry<String, LanguageInfo> language = languages.get(this.random.nextInt(languages.size()));
                client.player.sendOverlayMessage(Component.literal("[Random Language] Changing the language to: ").append(language.getValue().toComponent()));
                languageManager.setSelected(language.getKey());
                languageManager.onResourceManagerReload(client.getResourceManager());
            }

            if (this.returnToNativeKeymapping.consumeClick()) {
                LanguageManager languageManager = client.getLanguageManager();
                client.player.sendOverlayMessage(Component.literal("[Random Language] Changing the language to: ").append(languageManager.getLanguage(client.options.languageCode).toComponent()));
                languageManager.setSelected(client.options.languageCode);
                languageManager.onResourceManagerReload(client.getResourceManager());
            }
        });
    }
}
