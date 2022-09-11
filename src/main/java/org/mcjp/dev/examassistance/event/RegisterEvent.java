package org.mcjp.dev.examassistance.event;

import net.klnetwork.playerrolechecker.api.enums.JoinEventType;
import net.klnetwork.playerrolechecker.api.event.connector.JoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.mcjp.dev.examassistance.util.DiscordUtil;

import static org.mcjp.dev.examassistance.ExamAssistance.jda;

public class RegisterEvent implements Listener {

    @EventHandler
    public void onSuccessRegister(JoinEvent e) {
        if (e.getType() == JoinEventType.SUCCESS) {
            long id = DiscordUtil.createChannel("s", e.getMember());
            jda.getTextChannelById(id).sendMessage("<@" + e.getMemberId() + ">");

            String uuid = null;
            String discordId = null;

            jda.getTextChannelById(id).sendMessage(DiscordUtil.createEmbedMessage("Custom.ExamAssistance.Message.ExamStart",uuid,discordId,false));

             

        }
    }
}
