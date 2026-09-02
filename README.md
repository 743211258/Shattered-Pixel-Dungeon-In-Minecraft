# Shattered Pixel Dungeon in Minecraft (SPDIM)

SPDIM, or Shattered Pixel Dungeon in Minecraft, is a Minecraft mod based on an open-source traditional roguelike dungeon game called Shattered Pixel Dungeon, developed by [Evan Debenham](https://github.com/00-Evan) under GNU GPLv3 license. This Mod aims to mimic or redesign items from the Shattered Pixel Dungeon for Minecraft gameplay.

Source code of Shattered Pixel Dungeon: [Shattered Pixel Dungeon](https://github.com/00-Evan/shattered-pixel-dungeon)

Recommended Forge version is 1.20.1 - 47.4.10. Other Forge versions (1.20.1 to 1.20.5) may work, but they haven't been tested and are not guaranteed to work properly.

## List of items implemented from Shattered Pixel Dungeon
* Wands
  * Wand of Blast Wave
  * Wand of Lightning
  * Wand of Fireblast
  * Wand of Regrowth
* Artifacts:
  * Timekeeper's Hourglass
  * Chalice of Blood
  * Dried Rose
  * Master Thieves' Armband
* Glyphs:
  * Glyph of Viscosity

## Documentation

I am planning to develop either an in-game wiki or a wiki hosted on Fandom. Updates will be announced as the mod progresses.

## Playing SPDIM

### Requirements
- Any Minecraft Launcher capable of running Forge

### Installation
1. Download Minecraft Forge 1.20.1 - 47.4.10 installer from [here](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html).

2. Run the Forge installer by either clicking on it or run 

```bash
# The exact jar file name may vary
java -jar forge-1.20.1-47.4.10-installer.jar
```

3. Select Install Client and click OK.

4. Once the installation is complete, open your preferred Minecraft launcher, and Minecraft Forge 1.20.1 should appear on the installation list.

5. Install Forge 1.20.1.

6. Download the latest SPDIM release from the release section of [the Github repo](https://github.com/743211258/Shattered-Pixel-Dungeon-In-Minecraft)

7. Place the jar file under .minecraft/mods folder.

8. Run Forge 1.20.1 and you can start playing.

## Build the source code

### Requirements
- JDK 17+
- Git

Run the following code
```bash
git clone https://github.com/743211258/Shattered-Pixel-Dungeon-In-Minecraft.git
cd Shattered-Pixel-Dungeon-In-Minecraft
./gradlew build
```

After the build is successful, you may follow the installation guide, or run
```bash
./gradlew runClient
```
for singleplayer, and
```bash
./gradlew runServer
```
for multiplayer, and then
 ```bash
./gradlew runClient
```
for clients to join.

## Contribution
I welcome all forms of contributions (Bug report, pull requests, fork and make your own, etc), but please follow the guidelines below.

- Please provide a rundown of your contributions so I can go over them before making any changes.
- If you decide to contribute to source code, please keep your patch properly sized and no more than 5000 lines of changes.  
- As I am not planning on implementing a modder inside my mod, you might have to fork my repo and make your own stuffs, or incorporate items from other mods. If you choose to do so, please comply with GPLv3 license, my advice is to release your source code and license it under GPLv3 as well.

## Compatibility
Unfortunately, in order to implement some mechanisms, mixin injections were used extensively, which may cause serious compatibility issue with other mods. I am fully aware of this and will try my best to minimize it.

## How to use those items
A super brief description of the first six items can be found in document.txt.

## Release
Official releases are in the release section of the repository. However, I will only release major updates. Currently the mod version is 0.0.3.15, and I am wrapping up some stuff.
