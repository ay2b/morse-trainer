package org.queue.morseTrainer;

import org.queue.morseTrainer.audio.ToneGen;
import org.queue.morseTrainer.data.Code;
import org.queue.morseTrainer.data.Timings;
import org.queue.morseTrainer.util.CustomObjectMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Application
{
    private static final String       PROG_NAME = "morse-trainer";
    private static final ObjectMapper MAPPER    = new CustomObjectMapper();

    public static void sleepNoException(final long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException ignored)
        {
            // ignored
        }
    }

    private static final Options options;

    static
    {
        options = new Options();
        Option c = new Option("c", "cache-only", false, "only pre-cache (fetch from internet) sound files");
        options.addOption(c);
        Option d = new Option("d", "delay", true, "delay in seconds between words");
        d.setType(Integer.class);
        options.addOption(d);
        Option f = new Option("f", "hz", true, "tone frequency (Hz) -- 750Hz default");
        f.setType(Integer.class);
        options.addOption(f);
        Option l = new Option("l", "letters", false, "include letters");
        options.addOption(l);
        Option m = new Option("m", "min-word-length", true, "minimum word length");
        m.setType(Integer.class);
        options.addOption(m);
        Option n = new Option("n", "numbers", false, "include numbers");
        options.addOption(n);
        Option p = new Option("p", "punctuation", false, "include punctuation");
        options.addOption(p);
        Option s = new Option("s", "wpm", true, "words per minute");
        s.setType(Integer.class);
        options.addOption(s);
        Option v = new Option("v", "volume", true, "volume (0.0 to 1.0)");
        v.setType(Float.class);
        options.addOption(v);
        Option w = new Option("w", "word-list-file", true, "word list file");
        options.addOption(w);
        Option x = new Option("x", "max-word-length", true, "maximum word length");
        x.setType(Integer.class);
        options.addOption(x);
    }

    private static void helpAndExit(final int status)
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(PROG_NAME, options);
        System.exit(status);
    }

    private static CommandLine parseArgs(final String[] args)
    {
        if (0 == args.length)
        {
            helpAndExit(1);
        }

        CommandLineParser parser = new DefaultParser();
        try
        {
            return parser.parse(options, args);
        }
        catch (ParseException e)
        {
            System.out.println(e.getMessage());
            helpAndExit(1);
            return null;
        }
    }

    public static Integer getOptionOrDefault(final CommandLine cmd, final char opt, final int defaultValue)
    {
        return cmd.hasOption(opt) ? Integer.valueOf(cmd.getOptionValue(opt)) : defaultValue;
    }

    public static Float getOptionOrDefault(final CommandLine cmd, final char opt, final float defaultValue)
    {
        return cmd.hasOption(opt) ? Float.valueOf(cmd.getOptionValue(opt)) : defaultValue;
    }

    @Data
    @RequiredArgsConstructor
    public static class TaskObjects
    {
        private final boolean  cacheOnly;
        private final String[] wordArray;
        private final Timings  timings;
        private final ToneGen  toneGen;
        private final int      seconds;
        private final float    volume;
    }

    private static TaskObjects buildPrimaryDataFromArgs(final String[] args)
    {
        CommandLine cmd = parseArgs(args);

        final boolean cacheOnly = cmd.hasOption('c');

        final int delay = getOptionOrDefault(cmd, 'd', 300);
        if (delay < 1)
        {
            System.err.println("delay must be positive");
            System.exit(1);
        }

        final int wpm = getOptionOrDefault(cmd, 's', 18);
        if (wpm < 1)
        {
            System.err.println("speed (wpm) must be positive");
            System.exit(1);
        }

        final int hz = getOptionOrDefault(cmd, 'f', 750);
        if ((hz < 20) || (hz > 20000))
        {
            System.err.println(
                    "Frequency must be in range of 20 - 20,000 in order to be audible.  750Hz is recommended");
            System.exit(1);
        }

        Set<String> validWords = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (cmd.hasOption('p'))
        {
            validWords.addAll(Code.Punctuation.keySet());
        }
        if (cmd.hasOption('l'))
        {
            validWords.addAll(Code.Letters.keySet()
                    .parallelStream()
                    .filter(s -> s.equals(s.toUpperCase()))
                    .collect(Collectors.toSet()));
        }
        if (cmd.hasOption('n'))
        {
            validWords.addAll(Code.Numbers.keySet());
        }

        final Float   volume = getOptionOrDefault(cmd, 'v', 1.0f);
        final Integer minLen = getOptionOrDefault(cmd, 'm', 0);
        final Integer maxLen = getOptionOrDefault(cmd, 'x', Integer.MAX_VALUE);

        if (cmd.hasOption('w'))
        {
            final String fn   = cmd.getOptionValue('w');
            File         file = new File(fn);
            if (!file.exists() || !file.canRead())
            {
                System.err.println("word list file \"" + fn + "\" does not exist or is not readable.");
                System.exit(1);
            }
            try (BufferedReader in = new BufferedReader(new FileReader(file)))
            {
                in.lines().sequential()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(e -> !e.isEmpty())
                        .filter(e -> e.length() <= maxLen)
                        .filter(e -> e.length() >= minLen)
                        .forEach(validWords::add);
            }
            catch (IOException e)
            {
                System.err.println("Unable to open or read word list file \"" + fn + "\".");
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }

        if (0 == validWords.size())
        {
            System.out.println("Empty wordlist.  Must specify at least one set of characters (number, \n" +
                    "letters, punctuation) or wordlist");
            helpAndExit(1);
        }

        if (validWords.size() <= 100)
        {
            System.out.println("Word list (" + validWords.size() + " words):");
            validWords.stream().sorted().forEach(System.out::println);
        }
        else
        {
            System.out.println("Word list has " + validWords.size() + " words");
        }
        System.out.println("Tone is " + hz + "Hz");
        System.out.println("Speed is " + wpm + " WPM");
        System.out.println("Training rate is every " + Duration.ofSeconds(delay).toString().substring(2).toLowerCase());
        System.out.println("Volume is " + volume);

        final String[] wordArray = validWords.toArray(new String[0]);
        final Timings  timings   = new Timings(wpm);
        final ToneGen  toneGen   = new ToneGen(8000, hz);

        return new TaskObjects(cacheOnly, wordArray, timings, toneGen, delay, volume);
    }

    public static void main(final String[] args) throws Exception
    {
        final TaskObjects taskObjects = buildPrimaryDataFromArgs(args);

        Thread cacheThread = new Thread(new CacheFetchRunnable(taskObjects.getWordArray()));
        cacheThread.start();

        if (taskObjects.isCacheOnly())
        {
            System.out.println("Cache pre-fetch only mode. (expect this to take up to " +
                    Duration.ofSeconds(taskObjects.getWordArray().length / 20).toString().substring(2).toLowerCase() +
                    ")");
            cacheThread.join();
            return;
        }

        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(new PlayWordRunnable(taskObjects), 1, taskObjects.getSeconds(), TimeUnit.SECONDS);

        // java.awt.Toolkit.getDefaultToolkit().beep();
    }
}
