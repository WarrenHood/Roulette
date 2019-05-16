package com.nullbyte001.roulette;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BetCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		// TODO Auto-generated method stub
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can use this command!");
			return false;
		}
		Player player = (Player) sender;
		ItemStack hand = player.getInventory().getItemInMainHand();
		if(hand == null || hand.getType() == Material.AIR) {
			player.sendMessage("You need to be holding an item to bet with it!");
			return true;
		}
		Random random = new Random();
		int winningNumber = random.nextInt(36);
		int payrate = 35;
		double payout = 0;
		if(args.length == 0) {
			player.sendMessage("Please place your bet!");
			return false;
		}
		
		if ("even".contentEquals(args[0])) {
			if(winningNumber % 2 == 0) {
				payout = hand.getAmount();
				payrate = 1;
			}
		}
		else if ("odd".contentEquals(args[0])) {
			if(winningNumber % 2 == 1) {
				payout = hand.getAmount();
				payrate = 1;
			}
		}
		else for(String number : args) {
			if (number.equals(Integer.toString(winningNumber))) {
				payout = hand.getAmount()/args.length;
				break;
			}
		}
		player.sendMessage(ChatColor.YELLOW+"The payout is "+Integer.toString(payrate)+" times amount set on a number (total is divided equally amongst your bets).");
		player.sendMessage(ChatColor.GREEN+"The winning number is "+Integer.toString(winningNumber));
		String betItem = hand.getType().toString();
		if(hand.getItemMeta().hasDisplayName()) {
			betItem = hand.getItemMeta().getDisplayName();
		}
		int itemsBet = hand.getAmount();
		int itemsWon = (int) Math.floor(payout*payrate);
		ItemStack winnings = new ItemStack(hand.getType(),itemsWon);
		winnings.setItemMeta(hand.getItemMeta());
		int profit = itemsWon - itemsBet;
		if(itemsWon >= 0) {
			// Keep some items
			itemsWon -= itemsBet;
			itemsWon += (int) Math.floor(payout);
			hand.setAmount((int) Math.floor(payout));
			
		}
		else {
			hand.setAmount(0);
		}
		if (itemsWon >= 0) {
			HashMap<Integer, ItemStack> excess = player.getInventory().addItem(winnings);
			for (Map.Entry<Integer, ItemStack> me : excess.entrySet()) {
				player.getWorld().dropItem(player.getLocation(), me.getValue());
			}
			Bukkit.broadcastMessage(ChatColor.BLUE+""+player.getDisplayName()+" has won "+Integer.toString(itemsWon)+" "+betItem+"s by playing roulette!");
		}
		else {
			Bukkit.broadcastMessage(ChatColor.RED+""+player.getDisplayName()+" has gambled away "+itemsBet+" "+betItem+"s by playing roulette!");
		}
		return true;
	}

}
