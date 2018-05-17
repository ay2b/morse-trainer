package org.queue.morseTrainer;

import org.queue.morseTrainer.audio.Play;
import org.queue.morseTrainer.audio.ToneGen;
import org.queue.morseTrainer.data.Element;
import org.queue.morseTrainer.data.Timings;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import javax.sound.sampled.LineUnavailableException;

@Tag("unit")
public class EverythingTest
{
    @Test
    public void doTest() throws LineUnavailableException
    {
        final String        message  = "test success";
        final List<Element> elements = Encode.encodeToElements(message);

        Timings timings = new Timings(20);
        ToneGen toneGen = new ToneGen(8000);
        Play.playElements(timings, toneGen, 1.0f, elements);
    }
}
