package org.queue.morseTrainer.audio;

import org.queue.morseTrainer.data.Element;
import org.queue.morseTrainer.data.Timings;

import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Play
{
    public static void playData(final int sampleRate, final byte[] data) throws LineUnavailableException
    {
        AudioFormat    af  = new AudioFormat(sampleRate, 8, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();
        sdl.write(data, 0, data.length);
        sdl.drain();
        sdl.stop();
        sdl.close();
    }

    public static void playElements(
            final Timings timings,
            final ToneGen toneGen,
            final float volume,
            List<Element> elements) throws LineUnavailableException
    {
        final byte[] dot  = toneGen.elementToToneArray(timings, Element.DOT, volume);
        final byte[] dash = toneGen.elementToToneArray(timings, Element.DASH, volume);

        AudioFormat    af  = new AudioFormat(toneGen.getSampleRate(), 8, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();

        for (Element e : elements)
        {
            switch (e)
            {
              case DOT:
                  playIt(sdl, dot);
                  break;
              case DASH:
                  playIt(sdl, dash);
                  break;
              case ELEMENT_BREAK:
                  sleep(timings.getElementBreakMs());
                  break;
              case CHARACTER_BREAK:
                  sleep(timings.getCharacterBreakMs());
                  break;
              case WORD_BREAK:
                  sleep(timings.getWordBreakMs());
                  break;
              default:
                  throw new IllegalArgumentException();
            }
        }

        sdl.stop();
        sdl.close();
    }

    private static void playIt(final SourceDataLine sdl, final byte[] data)
    {
        // sdl.start();
        sdl.write(data, 0, data.length);
        sdl.drain();
        // sdl.stop();
    }

    private static void sleep(final long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException ignore)
        {
            // ignored
        }
    }
}
