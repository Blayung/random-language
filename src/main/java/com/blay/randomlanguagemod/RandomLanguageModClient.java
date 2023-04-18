package com.blay.randomlanguagemod;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import com.mojang.logging.LogUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.util.crash.CrashReport;

import org.lwjgl.glfw.GLFW;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.Random;
import java.util.Map;

public class RandomLanguageModClient implements ClientModInitializer {
    private Random random;
    private JsonConfig config;
    public KeyBinding randomLanguageKeybind;
    public KeyBinding returnToNativeKeybind;

    private class JsonConfig{
        public String native_language="en_us";
    }

    private class SetConfigThread extends Thread {
        public void run() {
            while(true) {
                if((MinecraftClient.getInstance().getLanguageManager() != null) && (MinecraftClient.getInstance().getLanguageManager().getAllLanguages().size() > 1)){
                    try{
                        File configFile = FabricLoader.getInstance().getConfigDir().resolve("random-language-mod.json").toFile();

                        if(!(configFile.exists() && configFile.isFile())){
                            configFile.delete();
                            configFile.getParentFile().mkdirs();
                            configFile.createNewFile();

                            FileWriter fileWriter=new FileWriter(configFile);

                            String allLanguagesString="";
                            for(String languageCode : MinecraftClient.getInstance().getLanguageManager().getAllLanguages().keySet()){
                                allLanguagesString += languageCode + ", ";
                            }
                            allLanguagesString=allLanguagesString.substring(0, allLanguagesString.length()-2);

                            fileWriter.write("{\n    \"native_language\": \"en_us\",\n    \"native_language_comment\": \"Available options: "+allLanguagesString+"\"\n}");
                            fileWriter.close();
                        }

                        FileReader fileReader=new FileReader(configFile);
                        config = new Gson().fromJson(fileReader, JsonConfig.class);
                        fileReader.close();
                    }
                    catch(Exception e)
                    {
                        CrashReport crashReport = MinecraftClient.getInstance().addDetailsToCrashReport(new CrashReport("Random language mod config error", e));
                        LogUtils.getLogger().error(LogUtils.FATAL_MARKER, "Unreported exception thrown!", e);
                        MinecraftClient.getInstance().cleanUpAfterCrash();
                        MinecraftClient.getInstance().printCrashReport(crashReport);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onInitializeClient() {
        random = new Random();

        new SetConfigThread().start();

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

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(randomLanguageKeybind.wasPressed()) {
                LanguageManager languageManager = MinecraftClient.getInstance().getLanguageManager();
                Object[] allLanguages = languageManager.getAllLanguages().entrySet().toArray();
                int choosenLanguage = random.nextInt(allLanguages.length);
                LanguageDefinition language = ((Map.Entry<String,LanguageDefinition>) allLanguages[choosenLanguage]).getValue();
                client.player.sendMessage(Text.literal("[Random Language Mod] Changing language to: " + language.name() + " (" + language.region() + ")"),true);
                languageManager.setLanguage(((Map.Entry<String,LanguageDefinition>) allLanguages[choosenLanguage]).getKey());
                languageManager.reload(MinecraftClient.getInstance().getResourceManager());
            }

            if(returnToNativeKeybind.wasPressed()) {
                LanguageManager languageManager = MinecraftClient.getInstance().getLanguageManager();
                LanguageDefinition language = languageManager.getLanguage(config.native_language);
                client.player.sendMessage(Text.literal("[Random Language Mod] Changing language to: " + language.name() + " (" + language.region() + ")"),true);
                languageManager.setLanguage(config.native_language);
                languageManager.reload(MinecraftClient.getInstance().getResourceManager());
            }
        });
    }
}
