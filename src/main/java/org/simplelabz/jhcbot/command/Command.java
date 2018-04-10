package org.simplelabz.jhcbot.command;

import org.simplelabz.jhcbot.command.event.CommandEvent;

/**
 * How to create commands?
 * <ul>
 * <li>Create a class with its name in all upper case ex: HELLO.java</li>
 * <li>Implement this interface and put appropriate values for {@link #name()},
 * {@link #call()}, {@link #desc()}, {@link #isHidden()} and {@link #listens()}</li>
 * <li>Insert your command name in all lowercase (ex: hello) in the array {@code org.simplelabz.jhcbot.Bot.COMMANDS}
 * or put this into config file's entry EXTCMDS.
 * </li>
 * </ul>
 * @author simple
 */
public interface Command
{
    /**
     * Returns name of the command
     * @return name
     */
    String name();
    
    /**
     * Returns call for the command
     * @return call
     */
    String call();
    
    /**
     * Returns a description for the command
     * @return description
     */
    String desc();
    
    /**
     * Returns the command is to be hidden
     * @return true if hidden
     */
    boolean isHidden();
    
    /**
     * Returns if the command is a listener
     * @return true if listens
     */
    boolean listens();
    
    /**
     * Command's action
     * @param event 
     */
    void action(CommandEvent event);
    
    /**
     * Command's listening action
     * @param event 
     */
    void listen(CommandEvent event);
}
