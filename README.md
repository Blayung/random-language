[![fabric api badge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/requires/fabric-api_vector.svg)](https://modrinth.com/mod/fabric-api) [![modrinth badge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/random-language-mod)  
  
![**This mod currently supports the 1.19.4 and 1.20.x versions of minecraft.**](https://img.shields.io/static/v1?label=SUPPORTED%20MINECRAFT%20VERSIONS&message=1.19.4%20|%201.20%20|%201.20.1&color=yellowgreen&style=for-the-badge)  
For older versions you can try to use the older releases.
# Random Language Mod

## What is this mod?
This minecraft fabric mod allows you to randomize your game's language with just a single press of a key. You can also always return to your native language by pressing an another key.

## Configuration
You can configure that mod by editing the `.minecraft/config/random-language-mod.json` file, that is generated automatically after you launch the game with the mod for the first time.  
  
It is recommended to delete the config file when updating the mod to the following versions, since its syntax is confirmed to change on them, and this mod doesn't have a data-fixer-upper.  
**Known versions that WILL break the game if you don't delete the config:**  
- 1.2.0

## Downloads
You can download this mod on modrinth. [https://modrinth.com/mod/random-language-mod](https://modrinth.com/mod/random-language-mod)

## How to compile this mod? (assuming you're on linux)
0. Make sure you have jdk 17 and git installed. If not, check these links out: [https://git-scm.com/downloads](https://git-scm.com/downloads), [https://www.oracle.com/pl/java/technologies/downloads/#java17](https://www.oracle.com/pl/java/technologies/downloads/#java17)
1. Clone the repo: `git clone https://github.com/Blayung/random-language-mod.git; cd random-language-mod`
2. Compile the mod: `./gradlew build`
3. Now the mod jar file should be here: `./build/libs/random-language-mod-1.2.6.jar`
