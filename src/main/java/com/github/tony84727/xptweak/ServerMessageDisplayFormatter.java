package com.github.tony84727.xptweak;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class ServerMessageDisplayFormatter {
    public static ITextComponent format(DiscordMessage message) {
        return new StringTextComponent(TextFormatting.BLUE + "[Discord]" + TextFormatting.WHITE + String.format(" <%s> 說：%s", message.member, message.message));
    }
}
