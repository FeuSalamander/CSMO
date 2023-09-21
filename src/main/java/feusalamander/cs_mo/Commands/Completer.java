package feusalamander.cs_mo.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Completer implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1){
            if(sender.isOp())return List.of("play", "reload", "addelo");
            return List.of("play");
        }
        if(args.length == 2){
            if(args[1].equalsIgnoreCase("addelo")){
                return Collections.singletonList(" ");
            }
            return List.of("");
        }
        if(args.length == 3){
            if(args[1].equalsIgnoreCase("addelo")){
                return List.of("1", "5", "10");
            }
            return List.of("");
        }
        return List.of("");
    }
}
