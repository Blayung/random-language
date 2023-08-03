[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/requires/fabric-api_vector.svg)](https://modrinth.com/mod/fabric-api) [![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/randomized-language) ![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/unsupported/forge_vector.svg)  
  
**This mod currently supports the 1.19.4 and 1.20.x versions of minecraft.**  
For older versions you can try to use the older releases.
# Randomized Language

## What is this mod?
This minecraft fabric mod allows you to randomize your game's language with just a single press of a key. You can also always return to your native language by pressing an another keybind.

## Configuration
You can configure that mod by editing the `.minecraft/config/randomized-language.json` file, that is generated automatically after you launch the game with that mod for the first time. The keybindings are configurable in the vanilla options menu.  
  
It is recommended to delete the config file when updating the mod to the following versions, since its syntax is confirmed to change on them, and this mod doesn't have a data-fixer-upper.  
**Known versions that will break the game if you don't delete the config:**  
- 1.2.0

## Downloads
You can download this mod on modrinth. [https://modrinth.com/mod/randomized-language](https://modrinth.com/mod/randomized-language)

## How to compile this mod? (assuming you're on linux)
0. Make sure you have jdk 17 and git installed. If not, check these links out: [https://git-scm.com/downloads](https://git-scm.com/downloads), [https://www.oracle.com/pl/java/technologies/downloads/#java17](https://www.oracle.com/pl/java/technologies/downloads/#java17). You can also try to install these programs with your distro's package manager.
1. Clone the repo: `git clone https://github.com/Blayung/randomized-language-mod.git; cd randomized-language-mod`
2. Compile the mod: `./gradlew build`
3. Now the mod jar file should be here: `./build/libs/randomized-language-1.2.7.jar`
