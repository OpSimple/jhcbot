package org.simplelabz.jhcbot.command.svc;

import org.simplelabz.jhcbot.Chat;
import org.simplelabz.jhcbot.Chat.ParsedData;


/**
 *
 * @author simple
 */
public interface ServiceAdapter
{
    boolean onServe(Chat parent, Chat chat, ParsedData cmdData, ParsedData data);
}
