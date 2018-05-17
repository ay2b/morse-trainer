package org.queue.morseTrainer;

import org.queue.morseTrainer.data.Code;
import org.queue.morseTrainer.data.Element;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Encode
{
    private Encode() {}

    public static List<String> encodeToPrintableMorse(final String message)
    {
        return message.chars()
                .mapToObj(c -> Code.Characters.getOrDefault(String.valueOf((char)c), " "))
                .collect(Collectors.toList());
    }

    public static List<Element> encodeToElements(final String message)
    {
        LinkedList<Element> elements = new LinkedList<>();

        Element lastElem = null;
        for (char c : message.toCharArray())
        {
            final String morse = Code.Characters.getOrDefault(String.valueOf(c), " ");
            if (" ".equals(morse))
            {
                lastElem = Element.WORD_BREAK;
                elements.add(lastElem);
                continue;
            }

            if (Element.WORD_BREAK != lastElem)
            {
                lastElem = Element.CHARACTER_BREAK;
                elements.add(lastElem);
            }

            for (char e : morse.toCharArray())
            {
                if ((Element.CHARACTER_BREAK != lastElem) && (Element.WORD_BREAK != lastElem))
                {
                    lastElem = Element.ELEMENT_BREAK;
                    elements.add(lastElem);
                }

                switch (e)
                {
                  case '.':
                      lastElem = Element.DOT;
                      break;
                  case '-':
                      lastElem = Element.DASH;
                      break;
                  default:
                      throw new IllegalStateException("Unknown morse character '" + String.valueOf(c) + "'");
                }

                elements.add(lastElem);
            }
        }

        if (Element.CHARACTER_BREAK == elements.peekFirst())
        {
            elements.removeFirst();
        }

        return elements;
    }
}
