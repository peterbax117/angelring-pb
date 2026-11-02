// Sample pack-maker script for the -Pcrafttweaker test run: replaces the Angel Ring recipe with a cheaper one.
// Checked by RingGameTests.craftTweakerScriptReplacesRecipe.
craftingTable.remove(<item:angelring_pb:angel_ring>);
craftingTable.addShapeless("easy_angel_ring", <item:angelring_pb:angel_ring>, [<item:minecraft:feather>, <item:minecraft:gold_block>]);
