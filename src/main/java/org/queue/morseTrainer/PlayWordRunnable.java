package org.queue.morseTrainer;

import org.queue.morseTrainer.audio.Play;
import org.queue.morseTrainer.audio.Speaker;
import org.queue.morseTrainer.data.Element;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import javax.sound.sampled.LineUnavailableException;

public class PlayWordRunnable implements Runnable
{
    final Application.TaskObjects taskObjects;
    final Speaker                 speaker;
    final Random                  rng;

    public PlayWordRunnable(final Application.TaskObjects taskObjects)
    {
        this.taskObjects = taskObjects;
        this.speaker = new Speaker(true);
        this.rng = new Random();
    }

    @Override
    public void run()
    {
        final int     idx      = rng.nextInt(taskObjects.getWordArray().length);
        final String  message  = taskObjects.getWordArray()[idx];
        List<Element> elements = Encode.encodeToElements(message);
        try
        {
            Play.playElements(taskObjects.getTimings(), taskObjects.getToneGen(), taskObjects.getVolume(),
                    elements);
            Application.sleepNoException(500);
            speaker.speak(message);
            final List<String> printableMorse = Encode.encodeToPrintableMorse(message);
            System.out.println(message + "\t" + String.join(" ", printableMorse));
            Application.sleepNoException(500);
            Play.playElements(taskObjects.getTimings(), taskObjects.getToneGen(), taskObjects.getVolume(),
                    elements);
        }
        catch (LineUnavailableException | IOException e)
        {
            e.printStackTrace();
        }
    }
}
