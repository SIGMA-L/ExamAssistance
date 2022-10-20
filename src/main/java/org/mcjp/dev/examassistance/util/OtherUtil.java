package org.mcjp.dev.examassistance.util;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OtherUtil {

    public static java.awt.Color ColorFromString(String str) {
        java.awt.Color color;
        try {
            Field field = Class.forName("java.awt.Color").getField(str);
            color = (Color) field.get(null);
        } catch (Exception e) {
            color = null;
        }
        return color;
    }

    public static String getName(String uuid) throws Exception {
        String content = streamToString((new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid)).openStream());
        JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(content);
        return UUIDObject.get("name").toString();
    }

    public static String streamToString(InputStream stream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = stream.read(buffer)) != -1)
            result.write(buffer, 0, length);
        return result.toString(StandardCharsets.UTF_8.name());
    }



}
