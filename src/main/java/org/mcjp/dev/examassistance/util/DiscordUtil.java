package org.mcjp.dev.examassistance.util;

import jdk.tools.jlink.resources.plugins;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.klnetwork.playerrolechecker.api.PlayerRoleCheckerAPI;
import net.klnetwork.playerrolechecker.api.utils.CommonUtils;
import org.bukkit.Bukkit;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mcjp.dev.examassistance.ExamAssistance.jda;
import static org.mcjp.dev.examassistance.ExamAssistance.plugin;

public class DiscordUtil {

    public static long createChannel(String name, Member member) {

        long userid = member.getIdLong();

        long allow = Permission.MESSAGE_HISTORY.getRawValue() | Permission.MESSAGE_SEND.getRawValue() | Permission.MESSAGE_ADD_REACTION.getRawValue() | Permission.MESSAGE_ATTACH_FILES.getRawValue();
        long deny = 0;

        String code = String.valueOf(CommonUtils.random(100000000, 999999999));

        long channelId = 0;


        jda.getCategoryById(plugin.getConfig().getLong("Custom.ExamAssistance.CategoryId"))
                .createTextChannel(name)
                .setTopic(code)
                .addMemberPermissionOverride(userid, allow, deny)
                .queue();

        List<TextChannel> list = jda.getTextChannelsByName(name, true);

        for (TextChannel channel : list) {
            if (channel.getTopic().equals(code)) {
                channelId = channel.getIdLong();
                break;
            }
        }
        return channelId;
    }

    public static EmbedBuilder embedBuilder(String configPath, OffsetDateTime offsetDateTime, String uuid, String discordID) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(CommonUtils.getColor(plugin.getConfig().getString(configPath + ".color")))
                .setTitle(addString(plugin.getConfig().getString(configPath + ".title"), uuid, discordID))
                .setDescription(addString(plugin.getConfig().getString(configPath + ".description"), uuid, discordID))
                .setThumbnail(plugin.getConfig().getString(configPath + ".image") != null ? addString(plugin.getConfig().getString(configPath + ".image"), uuid, discordID) : null)
                .setTimestamp(plugin.getConfig().getBoolean(configPath + ".timestamp") ? offsetDateTime : null);
        return (splitBuilder(embedBuilder, configPath + ".message", uuid, discordID));
    }

    public static String addString(String target, String uuid, String discordId) {
        if (target == null) {
            return null;
        }
        if (uuid != null) {
            target = target.replaceAll("%uuid%", uuid);
            target = target.replaceAll("%xuid%", ((Long) CommonUtils.getXUID(UUID.fromString(uuid))).toString());
        }
        if (discordId != null) {
            target = target.replaceAll("%discordid%", discordId);
        }

        return target;
    }

    public static EmbedBuilder splitBuilder(EmbedBuilder embedBuilder, String configPath, String uuid, String discordId) {
        for (String c : plugin.getConfig().getStringList(configPath)) {
            String[] strings = addString(c, uuid, discordId).split("\\|", 3);

            if (strings.length != 3) {
                throw new IllegalStateException("Illegal format=" + Arrays.toString(strings));
            }
            embedBuilder.addField(strings[0], strings[1], Boolean.parseBoolean(strings[2]));
        }
        return embedBuilder;
    }

    private static MessageEmbed createEmbedMessage(String path, String uuid, String discordId) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(CommonUtils.getColor(plugin.getConfig().getString(path + ".color")))
                .setTitle(addString(plugin.getConfig().getString(path + ".title"), uuid, discordId))
                .setDescription(addString(plugin.getConfig().getString(path + ".description"), uuid, discordId))
                .setThumbnail(addString(plugin.getConfig().getString(path + ".image"), uuid, discordId))
                .setTimestamp(plugin.getConfig().getBoolean(path + ".timestamp") ? OffsetDateTime.now() : null);

        return (splitBuilder(builder, path + ".message", uuid,discordId)).build();
    }

    public static MessageCreateData createEmbedMessage(String path, String uuid, String discordId, boolean bedrock) {
        return createEmbedMessage("Setting.Message.ExamStart",uuid, discordId,false);
    }
}
