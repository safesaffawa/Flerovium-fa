# Flerovium (Fabric)

Makes rendering faster — a Fabric port of [Flerovium](https://github.com/MoePus/Flerovium) for **Minecraft 26.2**.

Port based on the unofficial NeoForge 26.1.2 build (`flerovium-neoforge-26.1.2-1.0.18`), targeting the Fabric loader
with Sodium 0.9.1+ for MC 26.2.

## Features

- **Entity back-face culling** — view-space culling that skips entity cuboid faces pointing away
  from the camera before they reach the GPU (requires Sodium; correct reimplementation of the
  original mod's culling)
- **Item back-face culling**
- **Reduce terrain particles** — culls destroy-block particles behind/away from the camera
- **Particle light caching** — caches `SingleQuadParticle` light coords per tick
- **Campfire smoke fix** — only enables physics when smoke is under a roof
- **Sound distance culling** — skips out-of-range non-streaming sounds
- **Sound channel limit** — drops non-streaming sounds once the static channel pool is exhausted

## Requirements

- Minecraft 26.2
- Fabric Loader >= 0.19.3
- Java >= 25
- Sodium >= 0.9.1 (Fabric)

## Config

Configuration is written to `config/flerovium.json` in the game directory on first launch.

## Setup (development)

For setup instructions, please see the [Fabric Documentation page](https://docs.fabricmc.net/develop/getting-started/creating-a-project#setting-up)
related to the IDE that you are using.

## License

LGPL-3.0
