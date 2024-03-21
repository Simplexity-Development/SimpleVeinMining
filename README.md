# Simple Vein Mining

A simple vein-miner plugin for paper and forks of paper servers. 
There's a lot of configuration options for better customization.

## Default Config
```yml
# Which blocks should be affected by vein mining?
allowed-blocks:
  - "minecraft:coal_ore"
  - "minecraft:coal_block"
  - "minecraft:deepslate_coal_ore"
  - "minecraft:copper_ore"
  - "minecraft:deepslate_copper_ore"
  - "minecraft:raw_copper_block"
  - "minecraft:diamond_ore"
  - "minecraft:deepslate_diamond_ore"
  - "minecraft:emerald_ore"
  - "minecraft:deepslate_emerald_ore"
  - "minecraft:gold_ore"
  - "minecraft:raw_gold_block"
  - "minecraft:deepslate_gold_ore"
  - "minecraft:iron_ore"
  - "minecraft:raw_iron_block"
  - "minecraft:deepslate_iron_ore"
  - "minecraft:lapis_ore"
  - "minecraft:deepslate_lapis_ore"
  - "minecraft:redstone_ore"
  - "minecraft:deepslate_redstone_ore"
  - "minecraft:nether_gold_ore"
  - "minecraft:nether_quartz_ore"
  - "minecraft:ancient_debris"
# Should the block list be a blacklist instead of a whitelist?
allowed-blocks-is-blacklist: false
# Should only blocks of the same type be vein-mined together? For instance
## If a player mines iron and there's coal, should only the iron be mined?
#
same-type: true
types:
  coal:
    - "minecraft:coal_ore"
    - "minecraft:coal_block"
    - "minecraft:deepslate_coal_ore"
  copper:
    - "minecraft:copper_ore"
    - "minecraft:deepslate_copper_ore"
    - "minecraft:raw_copper_block"
  diamond:
    - "minecraft:diamond_ore"
    - "minecraft:deepslate_diamond_ore"
  emerald:
    - "minecraft:emerald_ore"
    - "minecraft:deepslate_emerald_ore"
  gold:
    - "minecraft:gold_ore"
    - "minecraft:raw_gold_block"
    - "minecraft:deepslate_gold_ore"
  iron:
    - "minecraft:iron_ore"
    - "minecraft:raw_iron_block"
    - "minecraft:deepslate_iron_ore"
  lapis:
    - "minecraft:lapis_ore"
    - "minecraft:deepslate_lapis_ore"
  redstone:
    - "minecraft:redstone_ore"
    - "minecraft:deepslate_redstone_ore"
# Should vein-mining work in creative mode
works-in-creative: false
# Should sound effects and particles play?
run-effects: true
# Should blocks that would normally drop xp still drop xp when vein-mined?
drop-xp: true
# Should mined blocks wear down the tool used?
damage-tool:
  enabled: true
  prevent-breaking: true
  respect-unbreaking-enchant: true
# Should players be required to use the proper tool for the block?
# NOTE: proper tool does not necessarily mean effective tool, for instance, a pickaxe is a proper tool for dirt since it will still drop the item.
# A shovel is not a proper tool for stone because it will not drop the item.
require-proper-tool: true
#max blocks to break in one pass. For reference, the largest ore blobs are 52 blocks in size
max-blocks-to-break: 64

```
## Permissions and commands

| Permission          | Command     | Default | Description                                               |
|---------------------|-------------|---------|-----------------------------------------------------------|
| `veinmining.mining` | N/A         | op      | Allows the player to use the vein mining functionality    |
| `veinmining.reload` | `/vmreload` | op      | Allows the player to reload the plugin                    |
| `veinmining.toggle` | `/vmtoggle` | op      | Allows player to toggle vein mining off/on for themselves |
