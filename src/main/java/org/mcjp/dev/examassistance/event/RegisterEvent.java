package org.mcjp.dev.examassistance.event;

import net.klnetwork.playerrolechecker.api.PlayerRoleCheckerAPI;
import net.klnetwork.playerrolechecker.api.enums.JoinEventType;
import net.klnetwork.playerrolechecker.api.event.connector.JoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.mcjp.dev.examassistance.util.DiscordUtil;
import org.mcjp.dev.examassistance.util.OtherUtil;


import javax.print.attribute.standard.MediaSize;

import static org.mcjp.dev.examassistance.ExamAssistance.jda;
import static org.mcjp.dev.examassistance.ExamAssistance.plugin;

public class RegisterEvent implements Listener {

    @EventHandler
    public void onSuccessRegister(JoinEvent e) {
        if (e.getType() == JoinEventType.SUCCESS) {
            long id = DiscordUtil.createChannel("s", e.getMember());
            jda.getTextChannelById(id).sendMessage("<@" + e.getMemberId() + ">");

            String uuid = null;
            String discordId = null;

            uuid = PlayerRoleCheckerAPI.getCheckerAPI().getPlayerData().getUUID(e.getMemberId()).toString();
            discordId = e.getMemberId();


            try  {
                if (!plugin.getConfig().getBoolean("Discord.ChangeNickName")) return;

                e.getMember().modifyNickname(OtherUtil.getName(uuid));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            try {
                DiscordUtil.createChannel(OtherUtil.getName(uuid), e.getMember());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        }
    }
}
