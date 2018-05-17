package org.queue.morseTrainer.data;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Code
{
    public static final ImmutableMap<String, String> Letters;
    public static final ImmutableMap<String, String> Numbers;
    public static final ImmutableMap<String, String> Punctuation;
    public static final ImmutableMap<String, String> LettersAndNumbers;
    public static final ImmutableMap<String, String> Characters;

    private Code() {}

    static
    {
        Map<String, String> letters = new HashMap<>();
        letters.put("A", ".-");
        letters.put("B", "-...");
        letters.put("C", "-.-.");
        letters.put("D", "-..");
        letters.put("E", ".");
        letters.put("F", "..-.");
        letters.put("G", "--.");
        letters.put("H", "....");
        letters.put("I", "..");
        letters.put("J", ".---");
        letters.put("K", "-.-");
        letters.put("L", ".-..");
        letters.put("M", "--");
        letters.put("N", "-.");
        letters.put("O", "---");
        letters.put("P", ".--.");
        letters.put("Q", "--.-");
        letters.put("R", ".-.");
        letters.put("S", "...");
        letters.put("T", "-");
        letters.put("U", "..-");
        letters.put("V", "...-");
        letters.put("W", ".--");
        letters.put("X", "-..-");
        letters.put("Y", "-.--");
        letters.put("Z", "--..");

        List<Map.Entry<String, String>> map = new LinkedList<>();
        map.addAll(letters.entrySet());
        for (Map.Entry<String, String> e : map)
        {
            letters.put(e.getKey().toLowerCase(), e.getValue());
        }

        Map<String, String> numbers = new HashMap<>();
        numbers.put("0", "-----");
        numbers.put("1", ".----");
        numbers.put("2", "..---");
        numbers.put("3", "...--");
        numbers.put("4", "....-");
        numbers.put("5", ".....");
        numbers.put("6", "-....");
        numbers.put("7", "--...");
        numbers.put("8", "---..");
        numbers.put("9", "----.");

        Map<String, String> punctuation = new HashMap<>();
        punctuation.put(".", ".-.-.-");
        punctuation.put(",", "--..--");
        punctuation.put("?", "..--..");
        punctuation.put("'", ".----.");
        punctuation.put("!", "-.-.--");
        punctuation.put("/", "-..-.");
        punctuation.put("(", "-.--.");
        punctuation.put(")", "-.--.-");
        punctuation.put("&", ".-...");
        punctuation.put(":", "---...");
        punctuation.put(";", "-.-.-.");
        punctuation.put("=", "-...-");
        punctuation.put("+", ".-.-.");
        punctuation.put("-", "-....-");
        punctuation.put("_", "..--.-");
        punctuation.put("\"", ".-..-.");
        punctuation.put("$", "...-..-");
        punctuation.put("@", ".--.-.");

        punctuation.put("*", "...-.-");
        punctuation.put("\n", "-.-..."); // paragraph

        Letters = ImmutableMap.copyOf(letters);
        Numbers = ImmutableMap.copyOf(numbers);
        Punctuation = ImmutableMap.copyOf(punctuation);

        LettersAndNumbers = ImmutableMap.<String, String>builder().putAll(letters).putAll(numbers).build();
        Characters = ImmutableMap.<String, String>builder().putAll(letters).putAll(numbers).putAll(punctuation).build();
    }
}
