package com.blay.randomlanguagemod;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

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
    private static RandomLanguageModClient instance;
    public static RandomLanguageModClient getInstance() {
        return instance;
    }

    private MinecraftClient minecraftClient;
    private Random random;
    private Gson gson;
    public CrashReport crashReport;
    public KeyBinding randomLanguageKeybind;
    public KeyBinding returnToNativeKeybind;
    private LanguageManager languageManager;
    private Object[] allLanguages;
    private String allLanguagesString;
    private int choosenLanguage;
    private LanguageDefinition language;
    private File configFile;
    private FileWriter fileWriter;
    private FileReader fileReader;
    private JsonConfig config;

    private class JsonConfig{ // My json config structure
        public String native_language="en_us";
    }

    private class SetConfigThread extends Thread { // Sets and reads the config only after minecraft resource manager has initialized. 
        public void run() {
            while(true) {
                if((MinecraftClient.getInstance().getLanguageManager() != null) && (MinecraftClient.getInstance().getLanguageManager().getAllLanguages().size() > 1)){
                    RandomLanguageModClient.getInstance().setConfig();
                    break;
                }
            }
        }
    }

    public void setConfig() { // Creates a new config if it's not found and then reads it
        try{
            configFile = FabricLoader.getInstance().getConfigDir().resolve("random-language-mod.json").toFile();

            if(!(configFile.exists() && configFile.isFile())){
                configFile.delete();
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();

                fileWriter=new FileWriter(configFile);

                allLanguagesString="";
                for(String languageCode : minecraftClient.getLanguageManager().getAllLanguages().keySet()){
                    allLanguagesString += languageCode + ", ";
                }
                allLanguagesString=allLanguagesString.substring(0, allLanguagesString.length()-2);

                fileWriter.write("{\n  \"native_language\": \"en_us\",\n  \"native_language_comment\": \"Available options: "+allLanguagesString+"\"\n}");
                fileWriter.close();
            }

            fileReader=new FileReader(configFile);
            config = gson.fromJson(fileReader, JsonConfig.class);
            fileReader.close();
        }
        catch(Exception e)
        {
            crashReport = minecraftClient.addDetailsToCrashReport(new CrashReport("Unexpected error", e));
            LogUtils.getLogger().error(LogUtils.FATAL_MARKER, "Unreported exception thrown!", e);
            minecraftClient.cleanUpAfterCrash();
            minecraftClient.printCrashReport(crashReport);
        }
    }

    @Override
    public void onInitializeClient() {
        instance = this;

        random = new Random();
        gson = new Gson();

        minecraftClient = MinecraftClient.getInstance();

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
            if(randomLanguageKeybind.wasPressed()) { // Randomizes the language
                languageManager = minecraftClient.getLanguageManager();
                allLanguages = languageManager.getAllLanguages().entrySet().toArray();
                choosenLanguage = random.nextInt(allLanguages.length);
                language = ((Map.Entry<String,LanguageDefinition>) allLanguages[choosenLanguage]).getValue();
                client.player.sendMessage(Text.literal("[Random Language Mod] Changing language to: " + language.name() + " (" + language.region() + ")"),true);
                languageManager.setLanguage(((Map.Entry<String,LanguageDefinition>) allLanguages[choosenLanguage]).getKey());
                languageManager.reload(minecraftClient.getResourceManager());
            }
            if(returnToNativeKeybind.wasPressed()) { // Returns to native language
                languageManager = minecraftClient.getLanguageManager();
                language = languageManager.getLanguage(config.native_language);
                client.player.sendMessage(Text.literal("[Random Language Mod] Changing language to: " + language.name() + " (" + language.region() + ")"),true);
                languageManager.setLanguage(config.native_language);
                languageManager.reload(minecraftClient.getResourceManager());
            }
        });
    }
}
