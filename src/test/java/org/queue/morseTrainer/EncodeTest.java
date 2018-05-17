package org.queue.morseTrainer;

import org.queue.morseTrainer.data.Element;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.queue.morseTrainer.data.Element.CHARACTER_BREAK;
import static org.queue.morseTrainer.data.Element.DASH;
import static org.queue.morseTrainer.data.Element.DOT;
import static org.queue.morseTrainer.data.Element.ELEMENT_BREAK;
import static org.queue.morseTrainer.data.Element.WORD_BREAK;

@Tag("unit")
public class EncodeTest
{
    @Test
    public void doTestPrintable()
    {
        final String message  = "hello world";
        final String expected = ".... . .-.. .-.. --- \n.-- --- .-. .-.. -.. ";

        List<String> morse = Encode.encodeToPrintableMorse(message);

        String mc = "";

        for (String m : morse)
        {
            if (" ".equals(m))
            {
                mc = mc + "\n";
            }
            else
            {
                mc = mc + m + " ";
            }
        }

        Assertions.assertEquals(expected, mc);
    }

    @Test
    public void doTestElements()
    {
        final String  message  = "a ab abc";
        List<Element> elements = Encode.encodeToElements(message);
        Element[] expected = {
            DOT, ELEMENT_BREAK, DASH,
            WORD_BREAK,

            DOT, ELEMENT_BREAK, DASH,
            CHARACTER_BREAK,
            DASH, ELEMENT_BREAK, DOT, ELEMENT_BREAK, DOT, ELEMENT_BREAK, DOT,
            WORD_BREAK,

            DOT, ELEMENT_BREAK, DASH,
            CHARACTER_BREAK,
            DASH, ELEMENT_BREAK, DOT, ELEMENT_BREAK, DOT, ELEMENT_BREAK, DOT,
            CHARACTER_BREAK,
            DASH, ELEMENT_BREAK, DOT, ELEMENT_BREAK, DASH, ELEMENT_BREAK, DOT
        };

        Assertions.assertArrayEquals(expected, elements.toArray(new Element[0]));
    }
}
