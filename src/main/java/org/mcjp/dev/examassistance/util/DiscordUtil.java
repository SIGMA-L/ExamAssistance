package org.mcjp.dev.examassistance.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.klnetwork.playerrolechecker.api.utils.CommonUtils;

import java.time.OffsetDateTime;
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


        jda.getCategoryById(plugin.getConfig().getLong("Setting.CategoryId"))
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

    private static MessageEmbed createEmbedMessage(String path, String uuid, String discordId) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(CommonUtils.getColor(plugin.getConfig().getString(path + ".color")))
                .setTitle(addString(plugin.getConfig().getString(path + ".title"), uuid, discordId))
                .setDescription(addString(plugin.getConfig().getString(path + ".description"), uuid, discordId))
                .setThumbnail(addString(plugin.getConfig().getString(path + ".image"), uuid, discordId))
                .setTimestamp(plugin.getConfig().getBoolean(path + ".timestamp") ? OffsetDateTime.now() : null);

        return (splitBuilder(builder, path + ".message", uuid, discordId)).build();
    }

    public static MessageCreateData createEmbedMessage(String path, String uuid, String discordId, boolean bedrock) {
        return createEmbedMessage("Setting.Message.ExamStart", uuid, discordId, false);
    }

    public static EmbedBuilder TicketembedBuilder(String configPath, OffsetDateTime offsetDateTime, String uuid, String discordID, Member member) {
        EmbedBuilder ticketembedBuilder = (new EmbedBuilder()).setColor(OtherUtil.ColorFromString(plugin.getConfig().getString(configPath + ".color"))).setTitle(replaceString(plugin.getConfig().getString(configPath + ".title"), uuid, discordID)).setDescription(replaceString(plugin.getConfig().getString(configPath + ".description"), uuid, discordID)).setThumbnail(member.getAvatarUrl()).setTimestamp(plugin.getConfig().getBoolean(configPath + ".timestamp") ? offsetDateTime : null);
        return ticketembedBuilder;
    }

    public static EmbedBuilder splitBuilder(EmbedBuilder embedBuilder, String configPath, String uuid, String discordID) {
        plugin.getConfig().getStringList(configPath).forEach(i -> {
            i = replaceString(i, uuid, discordID);
            String[] split = i.split("\\|");
            embedBuilder.addField(split[0], split[1], Boolean.parseBoolean(split[2]));
        });
        return embedBuilder;
    }

    public static String replaceString(String string, String uuid, String discordID) {
        if (string != null) {
            if (uuid != null)
                string = string.replaceAll("%uuid%", uuid);
            if (discordID != null)
                string = string.replaceAll("%discordid%", discordID);
        }
        return string;
    }

    public static void removeNickName(Member member) {
        if (!plugin.getConfig().getBoolean("Discord.ChangeNickName"))
            return;
        if (member != null)
            return;
        member.modifyNickname("");
    }

    public static void createTicketChannel(Guild guild, Member member, String name) {
        if (!plugin.getConfig().getBoolean("Discord.CreateTicketChannel"))
            return;
        Category category = guild.getCategoryById(plugin.getConfig().getLong("Discord.ChannelCategory"));
        Role everyoneRole = guild.getPublicRole();
        Role judgeRole = guild.getRoleById(plugin.getConfig().getInt("Setting.JudgeRoleId"));
        Role adviserRole = guild.getRoleById(plugin.getConfig().getInt("Setting.AdviserRoleId"));
        long everyone = everyoneRole.getIdLong();
        long judge = judgeRole.getIdLong();
        long adviser = adviserRole.getIdLong();
        long all = Permission.ALL_CHANNEL_PERMISSIONS;
        long allow = Permission.MESSAGE_HISTORY.getRawValue() | Permission.MESSAGE_SEND.getRawValue() | Permission.VIEW_CHANNEL.getRawValue() | Permission.MESSAGE_ATTACH_FILES.getRawValue();
        long non = 0L;
        guild.createTextChannel(name, category)
                .addPermissionOverride((IPermissionHolder) member, allow, non)
                .addRolePermissionOverride(everyone, non, all)
                .addRolePermissionOverride(adviser, all, non)
                .addRolePermissionOverride(judge, all, non)
                .queue(channel -> {
                    String msg = plugin.getConfig().getString("sendMessage.message");
                    channel.sendMessage("<@" + member.getId() + ">\n" + msg).queue();
                });
    }

}
