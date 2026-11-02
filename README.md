# Angel Ring

A NeoForge 1.21.1 mod that adds the **Angel Ring**: wear it and you can fly like in creative mode. Double-tap
jump to take off.

- **With [Curios](https://www.curseforge.com/minecraft/mc-mods/curios)** the ring goes in the Curios `ring` slot.
- **Without Curios** the ring works from anywhere in your inventory, including the off hand.

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

## Building and testing

```
gradlew build                          # mod jar in build/libs
gradlew runGameTestServer              # server-side tests, with Curios
gradlew runGameTestServer -PnoCurios   # same tests without Curios
gradlew clientTest                     # real client: double-tap flight in survival
gradlew clientTest -PnoCurios
```

Tests use [neoforge-mod-test-harness](https://github.com/peterbax117/neoforge-mod-test-harness), cloned next to
this repository. Without it the mod still builds; only the test tasks are missing.

## Credits

- The original Angel Ring is from **Extra Utilities** by RWTema. This recipe follows its 1.7.10 layout with
  vanilla substitutes.
- [Angel Ring](https://github.com/DenisMasterHerobrine/AngelRing) by DenisMasterHerobrine (MIT) was the reference
  for Curios integration. This mod is a separate implementation and shares no code with it.

## License

MIT
