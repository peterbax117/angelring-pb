# Angel Ring Reborn

A NeoForge 1.21.1 mod that adds the **Angel Ring**: wear it and you can fly like in creative mode. Double-tap
jump to take off.

The ring works from anywhere you carry it: any inventory or hotbar slot, your main or off hand, or, with
[Curios](https://www.curseforge.com/minecraft/mc-mods/curios) installed, the Curios `ring` slot so it does not take
up inventory space.

Flight is free while the ring is worn. Remove it and you stop flying and fall.

## Recipe

```
Glass       Gold Ingot   Glass
Gold Ingot  Nether Star  Gold Ingot
Ghast Tear  Elytra       Ghast Tear
```

Glass and gold use the `c:glass_blocks` and `c:ingots/gold` tags, so modded variants work. Modpacks can replace
`data/angelring_pb/recipe/angel_ring.json`.

## How it works

The ring adds a modifier to NeoForge's `neoforge:creative_flight` attribute while worn, and removes it when it is
not. Because it is an attribute modifier, it stacks with creative mode and other mods that grant flight:
removing the ring never takes away flight that something else granted. No extra flight library is needed.

## Requirements

Minecraft 1.21.1, NeoForge 21.1.x. Curios 9.x is optional.

## Integrations

The mod works fully by itself; every integration only adds to it.

| Mod | Integration |
|---|---|
| Curios | The `ring` slot is one more place to wear the ring |
| JEI | Information page describing the ring |
| CraftTweaker, KubeJS | The recipe is a standard data recipe with common tags, so pack makers can replace or remove it |
| Backpacks, bundles, shulker boxes | A ring stored inside them does **not** grant flight (by design) |

## Building and testing

```
gradlew build                                 # mod jar in build/libs
gradlew runGameTestServer -Pstandalone        # server tests, no optional mods
gradlew runGameTestServer                     # with Curios and JEI
gradlew runGameTestServer -Pcrafttweaker      # plus CraftTweaker and a sample recipe-replacing script
gradlew clientTest -Pstandalone               # real client: double-tap flight in survival
gradlew clientTest                            # with Curios and JEI (also checks the JEI info page)
```

Tests use [neoforge-mod-test-harness](https://github.com/peterbax117/neoforge-mod-test-harness), cloned next to
this repository. Without it the mod still builds; only the test tasks are missing. CI runs the three setups as a
matrix.

## Releasing

Published on CurseForge as **Angel Ring Reborn**. One-time setup: set `curseforge_project_id` in
`gradle.properties` and add a `CURSEFORGE_TOKEN` repository secret (CurseForge account, API tokens).

To release, bump `mod_version`, commit, then push an annotated tag whose message is the changelog:

```
git tag -a v1.0.0 -m "First release"
git push origin v1.0.0
```

`.github/workflows/release.yml` runs the full test matrix, then `gradlew publishMods`. Locally, without a token,
`gradlew publishMods` is a dry run that writes the upload to `build/publishMods`.

## Credits

- The original Angel Ring is from **Extra Utilities** by RWTema. This recipe follows its 1.7.10 layout with
  vanilla substitutes. The item texture is an original design inspired by its small winged ring icon; no
  Extra Utilities assets are included.
- [Angel Ring](https://github.com/DenisMasterHerobrine/AngelRing) by DenisMasterHerobrine (MIT) was the reference
  for Curios integration. This mod is a separate implementation and shares no code with it.

## License

MIT
