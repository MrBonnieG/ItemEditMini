# ItemEditMini

**ItemEditMini** is a simple and lightweight in-game item editor for changing item names and lore with full **MiniMessage support** and a **permission-based system**.

---

## ✨ Features

- 🪄 Edit item name in real time
- 📜 Edit item lore (add, set, remove, reset)
- 🎨 Full MiniMessage support (colors, gradients, formatting)
- 🔐 Permission-based access control
- ⚡ Lightweight and fast
- 🧩 Easy-to-use commands

---

## 📦 Commands

| Command | Description |
|:---------------------|:------------------------------|
| `/ie name <text>` | Changes the item display name |
| `/ie name reset` | Resets the item name to default |
| `/ie lore add <text>` | Adds a new line to the item lore |
| `/ie lore set <line number> <text>` | Sets or replaces a specific lore line |
| `/ie lore remove <line number>` | Removes a specific lore line |
| `/ie lore reset` | Clears all item lore |
| `/ie reload` | Reloads the plugin configuration |

---

## 📋 Permissions

| Permission | Description |
|:---------------------|:------------------------------|
| `itemedit.use` | Allows using name and lore commands |
| `itemedit.reload` | Allows reloading configuration |

---

## 🎨 MiniMessage support

You can use full MiniMessage formatting:

- `<red>Red text`
- `<green>Green text`
- `<bold>Bold text`
- `<gradient:red:blue>Gradient text</gradient>`
- `<#ff0000>Hex colors`

---

## ⚙️ Configuration

All messages are fully configurable in `config.yml` and support MiniMessage formatting.
