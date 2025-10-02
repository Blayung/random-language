package me.teamaster.randomlang;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;

import java.util.Random;
import java.util.Map;

public class RandomLang implements ClientModInitializer {
    private Random random;

    private KeyBinding randomLanguageKeybind;
    private KeyBinding returnToNativeKeybind;

    public void onInitializeClient() {
        random = new Random();

        KeyBinding.Category keyBindingCategory = KeyBinding.Category.create(Identifier.of("randomlang:main"));
        randomLanguageKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("Randomize Your Language", GLFW.GLFW_KEY_COMMA, keyBindingCategory));
        returnToNativeKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("Return To Native", GLFW.GLFW_KEY_PERIOD, keyBindingCategory));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (randomLanguageKeybind.wasPressed()) {
                LanguageManager languageManager = client.getLanguageManager();
                Object[] allLanguages = languageManager.getAllLanguages().entrySet().toArray();
                Map.Entry<String, LanguageDefinition> language = (Map.Entry<String, LanguageDefinition>) allLanguages[random.nextInt(allLanguages.length)];
                client.player.sendMessage(Text.literal("[Random Language] Changing the language to: ").append(language.getValue().getDisplayText()), true);
                languageManager.setLanguage(language.getKey());
                languageManager.reload(client.getResourceManager());
            }

            if (returnToNativeKeybind.wasPressed()) {
                LanguageManager languageManager = client.getLanguageManager();
                client.player.sendMessage(Text.literal("[Random Language] Changing the language to: ").append(languageManager.getLanguage(client.options.language).getDisplayText()), true);
                languageManager.setLanguage(client.options.language);
                languageManager.reload(client.getResourceManager());
            }
        });
    }
}
