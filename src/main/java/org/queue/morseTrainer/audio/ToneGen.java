package org.queue.morseTrainer.audio;

import org.queue.morseTrainer.data.Element;
import org.queue.morseTrainer.data.Timings;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

@Accessors(chain = true)
@Getter
@Setter
public class ToneGen
{
    private final int sampleRate;
    private       int frequencyHz;

    public ToneGen()
    {
        this(44000);
    }

    public ToneGen(final int sampleRate)
    {
        this(sampleRate, 750);
    }

    public ToneGen(final int sampleRate, final int frequencyHz)
    {
        this.sampleRate = sampleRate;
        this.frequencyHz = frequencyHz;
    }

    public byte[] gen8bit(
            final long durationMilliseconds,
            final float volume)
    {
        return gen8bit(this.sampleRate, this.frequencyHz, durationMilliseconds, volume);
    }

    public static byte[] gen8bit(
            final float sampleRate,
            final int frequencyHz,
            final long durationMilliseconds,
            final double volume)
    {
        byte[] data = new byte[Math.round((sampleRate * durationMilliseconds) / 1000f)];
        if ((0 == frequencyHz) || (0 == volume))
        {
            return data;
        }

        for (int i = 0; i < data.length; i++)
        {
            double angle = i / (sampleRate / frequencyHz) * 2.0 * Math.PI;
            data[i] = (byte) (Math.sin(angle) * 127.0 * volume);
        }

        return data;
    }

    public byte[] elementToToneArray(final Timings timings, final Element element, final float volume)
    {
        return gen8bit(timings.getElementTiming(element), volume);
    }

    public byte[] elementsToToneArray(final Timings timings, final List<Element> elements, final float volume)
    {
        List<byte[]> tones = elements.parallelStream()
                .map(e -> elementToToneArray(timings, e, volume))
                .collect(Collectors.toList());

        int    length   = tones.parallelStream().mapToInt(t -> t.length).sum();
        byte[] allTones = new byte[length];

        int idx = 0;
        for (byte[] a : tones)
        {
            System.arraycopy(a, 0, allTones, idx, a.length);
            idx += a.length;
        }

        return allTones;
    }
}
