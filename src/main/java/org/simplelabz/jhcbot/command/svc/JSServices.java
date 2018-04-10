package org.simplelabz.jhcbot.command.svc;

import java.io.PrintStream;
import org.simplelabz.jhcbot.Chat;
import org.simplelabz.jhcbot.command.io.BotOutputSctream;

/**
 *
 * @author simple
 */
public class JSServices
{
    private final Chat chat;
    private final PrintStream out;
    private JSServices(Chat chat)
    {
        this.chat = chat;
        out = new java.io.PrintStream(new BotOutputSctream(chat));
    }
    
    public static JSServices getJSServices(Chat chat)
    {
        return new JSServices(chat);
    }
    
    public void print(String text)
    {
        out.print(text);
        out.flush();
    }
    public void println(String text)
    {
        out.println(text);
        out.flush();
    }
    public void append(String text)
    {
        out.append(text);
    }
    public void flush()
    {
        out.flush();
    }
    public String name()
    {
        return chat.getNick();
    }
}
