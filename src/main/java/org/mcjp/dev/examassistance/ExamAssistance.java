package org.mcjp.dev.examassistance;

import net.dv8tion.jda.api.JDA;
import net.klnetwork.playerrolechecker.api.PlayerRoleCheckerAPI;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcjp.dev.examassistance.event.RegisterEvent;

public final class ExamAssistance extends JavaPlugin {

    public static JDA jda;
    public static Plugin plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        jda = PlayerRoleCheckerAPI.getChecker().getJDA();

        plugin = this;

        getServer().getPluginManager().registerEvents(new RegisterEvent(),this);


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
