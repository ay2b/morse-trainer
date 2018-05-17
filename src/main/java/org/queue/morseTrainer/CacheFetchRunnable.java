package org.queue.morseTrainer;

import org.queue.morseTrainer.audio.Speaker;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CacheFetchRunnable implements Runnable
{
    private String[] wordList;

    public CacheFetchRunnable(final String[] wordList)
    {
        this.wordList = wordList;
    }

    @Override
    public void run()
    {
        Speaker      speaker      = new Speaker(true);
        List<String> shuffledList = Arrays.asList(wordList);
        Collections.shuffle(shuffledList);
        for (String w : shuffledList)
        {
            if (!speaker.getFileDataCache().exists(w + ".mp3"))
            {
                speaker.prefetchIgnoreException(w);
                Application.sleepNoException(5);
            }
        }
    }
}
