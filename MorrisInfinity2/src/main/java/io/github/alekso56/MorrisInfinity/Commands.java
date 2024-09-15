package io.github.alekso56.MorrisInfinity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Please specify a command.");
            return false;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        if (args[0].equalsIgnoreCase("startgame")) {
            if (MorrisInfinity.getGame() != null) {
                Player gamePlayer = MorrisInfinity.getGame().getPlayer();
                Player gameOpponent = MorrisInfinity.getGame().getOpponent();

                if ((gamePlayer != null && gamePlayer.getUniqueId().equals(playerUUID)) ||
                    (gameOpponent != null && gameOpponent.getUniqueId().equals(playerUUID))) {
                    player.sendMessage("You already have an active game.");
                    return true;
                }
                player.sendMessage("Game already running, but it's not yours.");
                return true;
            }

            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("ai")) {
                    if (args.length < 3) { // Corrected index to 3, as 2 was leading to errors
                        player.sendMessage("Please specify an AI difficulty level.");
                        return true;
                    }
                    MorrisInfinity.StartPVAI(player, args[2]);
                    player.sendMessage(ChatColor.GOLD+"Starting a new game against AI with difficulty: " +ChatColor.RED+ args[2]);
                    player.sendMessage(ChatColor.GOLD+"Please select a spot to place down your piece, you have "+MorrisInfinity.getGame().getState().numberOfPieces+" pieces in this game.");
                } else if (args[1].equalsIgnoreCase("pvp")) {
                    if (args.length < 3) {
                        player.sendMessage(ChatColor.GOLD+"Please specify the player you want to play against.");
                        return true;
                    }
                    Player vs = sender.getServer().getPlayer(args[2]);
                    if (vs == null) {
                        player.sendMessage(ChatColor.GOLD+"The specified player is not online.");
                        return true;
                    }
                    MorrisInfinity.StartPVP(player, vs);
                    player.sendMessage(ChatColor.GOLD+"Starting a new PvP game against: " + vs.getDisplayName()+". It's your turn!");
                    vs.sendMessage(ChatColor.GOLD+player.getDisplayName()+" has challenged you to a game! It's their turn!");
                } else {
                    player.sendMessage(ChatColor.GOLD+"Invalid game type. Use 'ai' or 'pvp'.");
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.GOLD+"Please specify the game type (ai/pvp).");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("endgame")) {
            if (MorrisInfinity.getGame() == null) {
                player.sendMessage(ChatColor.GOLD+"No game instance found.");
                return true;
            }

            Player gamePlayer = MorrisInfinity.getGame().getPlayer();
            Player gameOpponent = MorrisInfinity.getGame().getOpponent();

            if ((gamePlayer != null && gamePlayer.getUniqueId().equals(playerUUID)) ||
                (gameOpponent != null && gameOpponent.getUniqueId().equals(playerUUID))) {

                if (gameOpponent != null) {
                    gameOpponent.sendMessage("Game ended.");
                }

                if (gamePlayer != null) {
                    gamePlayer.sendMessage("Game ended.");
                }

                // Reset or clear the game instance if necessary
                MorrisInfinity.setGame(null);
                player.sendMessage("Game has been ended.");
            } else {
                player.sendMessage("You are not part of any active game.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("setorigin")) {
            Location playerLocation = player.getLocation();
            Board.gameOrigin = playerLocation.subtract(0, 1, 0);
            player.sendMessage("Game origin set to your current location: " +
                    "X: " + playerLocation.getBlockX() + 
                    ", Y: " + (playerLocation.getBlockY()-1) + 
                    ", Z: " + playerLocation.getBlockZ());
            MorrisInfinity.instance.saveLocation("origin", Board.gameOrigin);
            return true;
        }

        player.sendMessage("Invalid command. Please check your input.");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("startgame");
            completions.add("endgame");
            completions.add("setorigin");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("startgame")) {
            completions.add("ai");
            completions.add("pvp");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("startgame") && args[1].equalsIgnoreCase("pvp")) {
            for (Player onlinePlayer : sender.getServer().getOnlinePlayers()) {
                completions.add(onlinePlayer.getName());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("startgame") && args[1].equalsIgnoreCase("ai")) {
        	completions.add("easy");
        	completions.add("medium");
        	completions.add("hard");
        }

        return completions.stream()
		          .filter(a -> a.startsWith(args[args.length - 1]))
		          .collect(Collectors.toList());
    }
}
