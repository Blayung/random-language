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

import java.lang.Thread;
import java.util.Random;

class setSimpleConfigThread extends Thread {
    public void run() {
        while(true){
            if(MinecraftClient.getInstance().getLanguageManager()!=null){
                if(MinecraftClient.getInstance().getLanguageManager().getAllLanguages().toArray(new LanguageDefinition[0]).length>1){
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
    private LanguageManager languageManager;
    private LanguageDefinition language;
    private LanguageDefinition[] allLanguages;
    private String allLanguagesString;
    private SimpleConfig simpleConfig;
    private Random random;
    public KeyBinding randomLanguageKeybind;
    public KeyBinding returnToNativeKeybind;

    private String defaultConfigProvider(String filename) {
        allLanguagesString="";
        for(LanguageDefinition language:minecraftClient.getLanguageManager().getAllLanguages().toArray(new LanguageDefinition[0])){
            allLanguagesString=allLanguagesString+"\n# "+language.getCode();
        }
        return "#  Random Language Mod config\n# ============================\nnative-language=en_us\n# In above option you can choose from:"+allLanguagesString;
    }

    public void setSimpleConfig(){
        simpleConfig = SimpleConfig.of("random-language-mod").provider(this::defaultConfigProvider).request();
    }

    public static RandomLanguageModClient getInstance(){
        return instance;
    }

    @Override
    public void onInitializeClient() {
        instance=this;

        random = new Random();

        minecraftClient=MinecraftClient.getInstance();

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
                languageManager=minecraftClient.getLanguageManager();
                allLanguages=languageManager.getAllLanguages().toArray(new LanguageDefinition[0]);
                language=allLanguages[random.nextInt(allLanguages.length)];
                client.player.sendMessage(Text.literal("[Random Language Mod] Changing language to: "+language.getName()+" ("+language.getRegion()+")"),true);
                languageManager.setLanguage(language);
                languageManager.reload(minecraftClient.getResourceManager());
            }
            if(returnToNativeKeybind.wasPressed()){
                languageManager=minecraftClient.getLanguageManager();
                language=languageManager.getLanguage(simpleConfig.getOrDefault("native-language","en_us"));
                client.player.sendMessage(Text.literal("[Random Language Mod] Changing language to: "+language.getName()+" ("+language.getRegion()+")"),true);
                languageManager.setLanguage(language);
                languageManager.reload(minecraftClient.getResourceManager());
            }
        });
    }
}
