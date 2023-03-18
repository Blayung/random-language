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

import com.blay.randomlanguagemod.SimpleConfig;

import java.util.Random;
import java.util.Map;

class setSimpleConfigThread extends Thread {
    public void run() {
        while(true) {
            if(MinecraftClient.getInstance().getLanguageManager() != null){
                if(MinecraftClient.getInstance().getLanguageManager().getAllLanguages().size() > 1){
                    RandomLanguageModClient.getInstance().setSimpleConfig();
                    break;
                }
            }
        }
    }
}

public class RandomLanguageModClient implements ClientModInitializer {
    private static RandomLanguageModClient instance;

    private MinecraftClient minecraftClient;
    private Random random;
    private SimpleConfig simpleConfig;
    private String nativeLanguage;
    public KeyBinding randomLanguageKeybind;
    public KeyBinding returnToNativeKeybind;
    private LanguageManager languageManager;
    private Object[] allLanguages;
    private String allLanguagesString;
    private int choosenLanguage;
    private LanguageDefinition language;

    private String defaultConfigProvider(String filename) {
        allLanguagesString="";
        for(String languageCode : minecraftClient.getLanguageManager().getAllLanguages().keySet()){
            allLanguagesString = allLanguagesString + "\n# " + languageCode;
        }
        return "#  Random Language Mod config\n# ============================\nnative-language=en_us\n# In above option you can choose from:" + allLanguagesString;
    }

    public void setSimpleConfig() {
        simpleConfig = SimpleConfig.of("random-language-mod").provider(this::defaultConfigProvider).request();
        nativeLanguage = simpleConfig.getOrDefault("native-language","en_us");
    }

    public static RandomLanguageModClient getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        instance = this;

        random = new Random();

        minecraftClient = MinecraftClient.getInstance();

        randomLanguageKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "random-language-mod.key.random",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_COMMA,
            "random-language-mod.key.category"
        ));
        returnToNativeKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "random-language-mod.key.native",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_PERIOD,
            "random-language-mod.key.category"
        ));

        new setSimpleConfigThread().start();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(randomLanguageKeybind.wasPressed()) {
                languageManager = minecraftClient.getLanguageManager();
                allLanguages = languageManager.getAllLanguages().entrySet().toArray();
                choosenLanguage = random.nextInt(allLanguages.length);
                language = ((Map.Entry<String,LanguageDefinition>) allLanguages[choosenLanguage]).getValue();
                client.player.sendMessage(Text.literal("[Random Language Mod] Changing language to: " + language.name() + " (" + language.region() + ")"),true);
                languageManager.setLanguage(((Map.Entry<String,LanguageDefinition>) allLanguages[choosenLanguage]).getKey());
                languageManager.reload(minecraftClient.getResourceManager());
            }
            if(returnToNativeKeybind.wasPressed()) {
                languageManager = minecraftClient.getLanguageManager();
                language = languageManager.getLanguage(nativeLanguage);
                client.player.sendMessage(Text.literal("[Random Language Mod] Changing language to: " + language.name() + " (" + language.region() + ")"),true);
                languageManager.setLanguage(nativeLanguage);
                languageManager.reload(minecraftClient.getResourceManager());
            }
        });
    }
}
