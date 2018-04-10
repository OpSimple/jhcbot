package org.simplelabz.jhcbot.command.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import org.simplelabz.jhcbot.Chat;

/**
 * A timer based OutputStream for JHCBot
 * @author simple
 */
public class BotOutputSctream extends OutputStream
{
    protected byte buf[];
    protected int count;
    protected String bufstring;
    protected int strcount;
    protected boolean close;

    public BotOutputSctream(Chat chat)
    {
        buf = new byte[8192];
        count = 0;
        bufstring="";
        strcount = 0;
        close = false;
        
        Timer time = new Timer();
        SendTask task = new SendTask(chat, this, time);
        time.schedule(task, 500, 1000);
    }

    protected void flushBuffer()
    {
        if(count>0)
        {
            bufstring = bufstring+ (new String(buf));
            ++strcount;
            Arrays.fill(buf, (byte)0);
            count = 0;
        }
    }

    @Override
    public void write(int b) throws IOException
    {
         if (count >= buf.length)
         {
            flushBuffer();
         }
         buf[count++] = (byte)b;
    }

    @Override
    public void flush() throws IOException
    {
        flushBuffer();
    }

    @Override
    public void close() throws IOException
    {
        close = true; 
        flushBuffer();
    }

    private class SendTask extends TimerTask
    {
        private Chat chat;
        private BotOutputSctream call;
        private Timer timer;
        protected int count;

        public SendTask(Chat chat, BotOutputSctream obj, Timer timer)
        {
            this.call = obj;
            this.chat = chat;
            this.timer = timer;
            count=0;
        }

        public String trimSend(String value)
        {
            String text = value.replaceAll("\0", "").replaceAll("\n", "\\\r");
            if(text.contains("\r"))
            {
                chat.send(text.substring(0,text.lastIndexOf("\r")));
                return text.substring(text.lastIndexOf("\r")+1);
            }
            else
            {
                chat.send(text);
                return "";
            }
        }

        @Override
        public void run()
        {
            String testbuf = call.bufstring.replaceAll("\0", "");
            if(testbuf.trim().isEmpty() || testbuf.equals("\r") || call.close)
            {
                if(count==1)
                {
                    timer.cancel();
                }
                count++;
            }
            String buff = trimSend(call.bufstring);
            call.bufstring = buff;
        }
    }
}
