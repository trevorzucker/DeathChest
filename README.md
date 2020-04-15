# DeathChest
Plugin for Bukkit server

## What is this?
This is another plugin I was commissioned to make.

It makes players drop their items on death into an ender chest at their death location.

## Features

- Configurable owner-only-access lock-out time.

- Persistent data across plugin reloads, server restarts, or even server changes

- Admin command (/deathchest) to view all current deathchests, and teleport to them

- Configurable lang file

- Lockout time is handled asynchronously, with the system clock. Not a timer that runs every second.

## Commands

/deathchest [name]
 - Command to either view all currently active death chests or teleport to a user-specific chest
 - Permission: DeathChest.tp
