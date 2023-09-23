package feusalamander.cs_mo.Commands;

import feusalamander.cs_mo.CS_MO;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
@SuppressWarnings("deprecation")
public class Command implements CommandExecutor {
    CS_MO main = CS_MO.main;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 0)return false;
        if(sender instanceof Player p&&args[0].equalsIgnoreCase("play")){
            Objects.requireNonNull(main.getPlayGui().getItem(18)).setLore(List.of("§dElo: "+main.getPlayerData().getElo(p.getUniqueId())));
            p.openInventory(main.getPlayGui());
        }
        if(args[0].equalsIgnoreCase("reload")){
            main.reloadConfig();
            sender.sendMessage("§cThe config file has been reloaded");
        }
        if(sender.isOp()&&args[0].equalsIgnoreCase("addelo")){
            if(args.length != 3){
                sender.sendMessage("§cCorrect usage: addelo #player #number");
                return false;
            }else {
                Player p = Bukkit.getPlayer(args[1]);
                if(p == null){
                    sender.sendMessage("§cThe player is not online");
                    return false;
                }
                main.getPlayerData().addElo(p.getUniqueId(), Integer.parseInt(args[2]));
                sender.sendMessage("§aAdded "+args[2]+" elo to "+p.getName());
            }
        }
        return false;
    }
}
