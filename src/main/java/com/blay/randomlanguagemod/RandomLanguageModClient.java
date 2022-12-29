package com.blay.randomlanguagemod;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.resource.language.LanguageDefinition;

import java.util.Random;

public class RandomLanguageModClient implements ClientModInitializer {
    private static MinecraftClient minecraftClient;
    private static LanguageManager languageManager;
    private static LanguageDefinition language;
    private static LanguageDefinition[] allLanguages;
    private static Random random;

    public static KeyBinding randomLanguageKeybind;
    public static KeyBinding returnToEnglishKeybind;

    @Override
    public void onInitializeClient() {
        minecraftClient=MinecraftClient.getInstance();
        random = new Random();

        randomLanguageKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "random-language-mod.key.random",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_COMMA,
            "random-language-mod.key.category"
        ));
        returnToEnglishKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "random-language-mod.key.english",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_PERIOD,
            "random-language-mod.key.category"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(randomLanguageKeybind.wasPressed()) {
                languageManager=minecraftClient.getLanguageManager();
                allLanguages=languageManager.getAllLanguages().toArray(new LanguageDefinition[0]);
                language=allLanguages[random.nextInt(allLanguages.length)];
                client.player.sendMessage(Text.literal("[Random Language Mod] Changing language to: "+language.getName()+" ("+language.getRegion()+")"));
                languageManager.setLanguage(language);
                languageManager.reload(minecraftClient.getResourceManager());
            }
            if(returnToEnglishKeybind.wasPressed()){
                languageManager=minecraftClient.getLanguageManager();
                language=languageManager.getLanguage("en_us");
                client.player.sendMessage(Text.literal("[Random Language Mod] Changing language to: "+language.getName()+" ("+language.getRegion()+")"));
                languageManager.setLanguage(language);
                languageManager.reload(minecraftClient.getResourceManager());
            }
        });
    }
}
