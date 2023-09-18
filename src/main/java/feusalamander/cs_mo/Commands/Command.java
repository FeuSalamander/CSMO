package feusalamander.cs_mo.Commands;

import feusalamander.cs_mo.CS_MO;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Command implements CommandExecutor {
    CS_MO main = CS_MO.main;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 0)return false;
        if(sender instanceof Player p&&args[0].equalsIgnoreCase("play"))p.openInventory(main.getPlaygui());
        return false;
    }
}
