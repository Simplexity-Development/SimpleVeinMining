# Simple Vein Mining

A simple vein-miner plugin for paper and forks of paper servers. 
There's a lot of configuration options for better customization.

## Default Config

### Allowed Blocks
- **Description:** Which blocks are affected by vein mining
- **Config Value:** Block List
- **Default values:**
```yml
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
```

### Allowed blocks is blacklist

- **Description:** This option will turn the 'allowed blocks' list into a blacklist instead of a whitelist.
  Probably not what you want unless you want every single block to be able to be broken at once, but I'm not your mom, I don't tell you what to do
- **Config Value:** Boolean (true/false)
- **Default value:** `false`

### Same Type
- **Description:** Whether vein mining should only affect blocks of the 'same type' or not. Types are defined underneath. 
- **Config Value:** Boolean (true/false)
- **Default value:** `true`

###  Types
- **Description:** What groups of blocks should be broken together when 'same type' is set to true
- **Config Value:** Groups of block lists
- **Default Value:** 
```yml
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
```    
### Works in creative
- **Description:** Whether vein-mining should work in creative mode. The other requirements will still be applied such as tool and block type
- **Config Value:** Boolean (true/false)
- **Default Value:** `false`

### Run Effects
- **Description:** Whether sound effects and particles should play for block-breaking. 
- **Config Value:** Boolean (true/false)
- **Default Value:** `true`

### Damage Tool
- **Description:** Whether all vein-mined blocks should apply damage to the tool as if they were mined normally
#### Damage Tool - Enabled
- **Description:** Whether this functionality is enabled or not
- **Config Value:** Boolean (true/false)
- **Default Value:** `true`
#### Damage Tool - Prevent Breaking
- **Description:** Whether this plugin should prevent vein mining if doing so would break the tool, this sends an alert to the player that their tool is nearly broken and that vein mining could not be accomplished
- **Config Value:** Boolean (true/false)
- **Default Value:** `true`
#### Damage Tool - Respect Unbreaking Enchant 
- **Description:** Whether damage should be calculated based on the unbreaking enchant (less damage to the tool with unbreaking III than unbreaking I for example)
- **Config Value:** Boolean (true/false)
- **Default Value:** `true`

### Require Proper Tool
- **Description:** Whether vein mining should work only when a 'proper tool' (one that can drop the item) is used
- **Config Value:** Boolean (true/false)
- **Default Value:** `true`

### Require Lore
- **Description:** Whether vein mining should require a tool with specific lore on it
#### Require Lore - Enabled
- **Description:** Whether this functionality is enabled or not
- **Config Value:** Boolean (true/false)
- **Default Value:** `false`
#### Require Lore - Lore
- **Description:** The lore to require if this functionality is enabled
- **Config Value:** String, uses [MiniMessage](https://webui.advntr.dev/) formatting for colors
- **Default Value:** "<white>Vein Mining</white>"
### Require Item Model
- **Description:** Whether specific item models should be required for a tool to have vein mining work
#### Require Item Model - Enabled
- **Description:** Whether this functionality is enabled or not
- **Config Value:** Boolean (true/false)
- **Default Value:** `false`
#### Require Item Model - Valid Models
- **Description:** Valid item models allowed when this feature is enabled
- **Config Value:** String list of namespaced key locations of item models
- **Default Value:** `"minecraft:stick"`

### Max Blocks To Break
- **Description:** Max blocks to break at one time, the largest ore blobs that can spawn in vanilla are 52 blocks in size
- **Config Value:** Integer (whole number)
- **Default Value:** `64`

### Ignore Claim Protections
- **Description:** Whether blocks should be broken regardless of claim protections, or if you don't use a claim plugin, this just shuts up the notification in console
- **Config Value:** Boolean (true/false)
- **Default Value:** `false`

### Crouch Prevents Vein Mining
- **Description:** Whether crouch should prevent vein mining or not
- **Config Value:** Boolean (true/false)
- **Default Value:** `false`

## Permissions and commands

| Permission          | Command     | Default | Description                                               |
|---------------------|-------------|---------|-----------------------------------------------------------|
| `veinmining.mining` | N/A         | op      | Allows the player to use the vein mining functionality    |
| `veinmining.reload` | `/vmreload` | op      | Allows the player to reload the plugin                    |
| `veinmining.toggle` | `/vmtoggle` | op      | Allows player to toggle vein mining off/on for themselves |
