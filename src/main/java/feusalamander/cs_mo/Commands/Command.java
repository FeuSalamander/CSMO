package feusalamander.cs_mo.Commands;

import feusalamander.cs_mo.CS_MO;
import feusalamander.cs_mo.Managers.Data;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static feusalamander.cs_mo.CS_MO.main;

@SuppressWarnings("deprecation")
public class Command implements CommandExecutor {
    CS_MO main = CS_MO.main;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 0)return false;
        if(sender instanceof Player p&&args[0].equalsIgnoreCase("play")){
            Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
                Objects.requireNonNull(main.getPlayGui().getItem(18)).setLore(List.of("§dElo: "+ Data.getElo(p.getUniqueId())));
            });
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
                Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
                    Data.addElo(p.getUniqueId(), Integer.parseInt(args[2]));
                    sender.sendMessage("§aAdded "+args[2]+" elo to "+p.getName());
                });
            }
        }
        if(sender.isOp()&&args[0].equalsIgnoreCase("import")){
            
        }
        if(sender.isOp()&&args[0].equalsIgnoreCase("export")){

        }
        return false;
    }
}
