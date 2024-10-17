# EnderdragonAttack



The Enderdragonattack plugin is a comprehensive Minecraft plugin designed to manage a unique game mode centered around the Ender Dragon. This plugin features a variety of systems, including leveling, perks, kits, and an economy, enhancing the overall gameplay experience.
Features
Level System: Players can gain experience and level up through various activities.
Perk System: Players can unlock and utilize special abilities (perks) to enhance their gameplay.
Kit System: Players can choose from different kits for combat, providing them with unique gear and abilities.
Economy System: An internal currency (coins) is used for transactions within the game.
Statistics Tracking: Player statistics are recorded and can be displayed or accessed at any time.


Commands Overview:

Level Commands
/level
Displays your current level and experience status.
Permission: enderdragonattack.use


Experience Commands

/addExperience <player> <amount>
Adds experience to a specified player.
Permission: enderdragonattack.admin

/setExperience <player> <amount>
Sets experience for a specified player.
Permission: enderdragonattack.admin

/removeExperience <player> <amount>
Removes experience from a specified player.
Permission: enderdragonattack.admin


Coin Commands

/setcoins <player> <amount>
Sets coins for a player.
Aliases: sc
Permission: enderdragonattack.playerdata

/addcoins <player> <amount>
Adds coins to a player.
Aliases: ac
Permission: enderdragonattack.playerdata

/removecoins <player> <amount>
Removes coins from a player.
Aliases: rc
Permission: enderdragonattack.playerdata


Game Management Commands

/enderdragon stop
Stops the game while it is running.
Aliases: ed
Permission: enderdragonattack.admin

/resetworld
Resets the GameWorld to its initial state.
Aliases: rw
Permission: enderdragonattack.admin

Teleportation Commands
/worldteleport <worldname>
Teleports to a specific world.
Aliases: wt
Permission: enderdragonattack.admin


Player Statistics Commands

/stats <playername>
Shows the stats of a player.
Permission: enderdragonattack.use


Perk Commands

/perk <give|remove> <player> <perkName>
Gives or removes a perk from a player.
Permission: enderdragonattack.admin

/perk
Opens the perk shop menu.
Permission: enderdragonattack.use


Leaderboard Commands

/top <Damage|Level|Games>
Shows the top leaderboards in one of three categories.
Permission: enderdragonattack.use


Additional Commands

/list
Lists players in the starting zone.
Permission: enderdragonattack.playerdata

/rank
Displays your current rank based on your level.
Permission: enderdragonattack.use

/kit
Opens the kit shop menu.
Permission: enderdragonattack.use

/kitstart
Opens the kit selection menu for the game start.
Permission: enderdragonattack.use



Installation

To use it, you also have to provide a Lobby and GameWorld. The GameWorld will be created by the Plugin, 
with copying the ExampleWorld, which has to be provided be the Server.

A Lobby and ExampleWorld is provided in the "World" Folder.
