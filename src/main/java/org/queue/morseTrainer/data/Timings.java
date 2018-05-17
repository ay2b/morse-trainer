package org.queue.morseTrainer.data;

import lombok.Getter;

@Getter
public class Timings
{
    private final long dotMs;            // time in milliseconds of a dot
    private final long dashMs;           // time in milliseconds of a dash
    private final long elementBreakMs;   // time in milliseconds between character elements
    private final long characterBreakMs; // time in milliseconds between characters
    private final long wordBreakMs;      // time in milliseconds between words

    public static final int MIN_CHARACTER_WPM = 18;

    public Timings(final int wpm)
    {
        // per: http://www.arrl.org/files/file/Technology/x9004008.pdf
        if (wpm < 1)
        {
            throw new IllegalArgumentException("WPM must be at least 1");
        }
        if (wpm >= MIN_CHARACTER_WPM)
        {
            double u = 1200d / wpm;
            dotMs = Math.round(u);
            dashMs = Math.round(u * 3);
            elementBreakMs = Math.round(u);
            characterBreakMs = Math.round(u * 3);
            wordBreakMs = Math.round(u * 7);
        }
        else // at less than 18 WPM, characters are sent at 18 WPM but with extra spaces to slow overall rate
        {
            double charSpeed = MIN_CHARACTER_WPM;

            double u = 1200d / charSpeed;
            dotMs = Math.round(u);
            dashMs = Math.round(u * 3);
            elementBreakMs = Math.round(u);

            double a = (60d * charSpeed - 37.2 * wpm) / (charSpeed * wpm);
            characterBreakMs = Math.round(u * 3) + Math.round(3d * a / 19);
            wordBreakMs = Math.round(u * 7) + Math.round(7d * a / 19);
        }
    }

    public final long getElementTiming(final Element element)
    {
        switch (element)
        {
          case DOT:
              return getDotMs();
          case DASH:
              return getDashMs();
          case ELEMENT_BREAK:
              return getElementBreakMs();
          case CHARACTER_BREAK:
              return getCharacterBreakMs();
          case WORD_BREAK:
              return getWordBreakMs();
          default:
              throw new IllegalArgumentException();
        }
    }
}
