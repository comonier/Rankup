# 🏆 Rankup Plugin

A highly optimized rank progression system for Minecraft servers (Paper/Spigot), supporting versions from **1.21.1** up to **26.1.2**.

## 🌟 Features

*   📊 **100 Progressive Ranks:** Features a mathematical auto-fallback system if a rank is not manually configured.
*   🌍 **Multi-Language:** Native support for Portuguese, English, Spanish, and Russian (centralized in `.yml` files).
*   🗄️ **Database Support:** Built-in support for **SQLite** (local with automatic backups) and **MySQL** (optional for networks).
*   📱 **Bedrock Compatibility:** GUI menus optimized for mobile and console primary clicks (via Geyser).
*   🛡️ **Command Shield:** Prevents other plugins from "stealing" or overwriting `/rank` and `/rankup` commands.
*   🔗 **Deep Integration:** Native hooks with Vault, PlaceholderAPI, GriefPrevention, RedProtect, and Slimefun.

## 🚀 Commands


| Command | Description | Permission |
| :--- | :--- | :--- |
| `/rank` | Opens the visual GUI menu and shows progress in chat. | `rankup.player` |
| `/rankup` | Attempts to level up to the next rank. | `rankup.player` |
| `/rankreload` | Reloads all configurations and message files. | `rankup.admin` |

## 🛠️ Installation

1.  Ensure you are running **Java 21**.
2.  Place the `Rankup.jar` file into your server's `plugins/` folder.
3.  Restart the server to generate the configuration files.
4.  Configure requirements and rewards in the `ranks.yml` file.
5.  Set your preferred language in `config.yml`.

## 📦 Placeholders (PAPI)

Use these placeholders in your Chat or Tab plugins (VentureChat, TAB, etc.):
*   `%rankup_tag%` - Displays the current rank's short tag.
*   `%rankup_name%` - Displays the rank's full display name.

## ⚙️ Requirements

*   **Vault** (Mandatory for economy)
*   **PlaceholderAPI** (Recommended for tags)
*   **LuckPerms** (Recommended for permission management)

---
*Developed by comonier.*
