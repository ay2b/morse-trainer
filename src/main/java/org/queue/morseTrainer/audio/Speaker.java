package org.queue.morseTrainer.audio;

import org.queue.morseTrainer.util.FileDataCache;

import com.darkprograms.speech.synthesiser.SynthesiserV2;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

public class Speaker
{
    // This key came from one of the sound examples.  Googling for the key shows that it's been out in the public since
    // at least 2014, and as of 2018 is still working.  It is used by multiple different projects across multiple
    // different Google APIs.  There are some reports that near the end of the day, requests using this key get
    // throttled due to an apparent daily quota being reached.
    private static final SynthesiserV2 synthesizer = new SynthesiserV2("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");

    @Getter
    private FileDataCache fileDataCache;
    private boolean       synchronous;

    public Speaker(final boolean synchronous)
    {
        this(synchronous, new File(Paths.get(".").toAbsolutePath().normalize().toFile(), "cache"));
    }

    public Speaker(final boolean synchronous, final File cacheDir)
    {
        this.synchronous = synchronous;

        if (!cacheDir.exists())
        {
            cacheDir.mkdirs();
        }

        fileDataCache = new FileDataCache(cacheDir);
    }

    public void prefetch(final String text) throws IOException
    {
        if (fileDataCache.exists(text + ".mp3"))
        {
            return;
        }

        synthesizer.setLanguage("en");
        if (1 == text.length())
        {
            synthesizer.setSpeed(1.0);
        }
        else
        {
            synthesizer.setSpeed(0.75);
        }
        InputStream mp3Data = synthesizer.getMP3Data(text);
        fileDataCache.store(mp3Data, text + ".mp3");
    }

    public boolean prefetchIgnoreException(final String text)
    {
        try
        {
            prefetch(text);
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    private InputStream getMP3(final String text) throws IOException
    {
        prefetch(text);
        return fileDataCache.get(text + ".mp3");
    }

    public void speak(final String text) throws IOException
    {
        InputStream mp3Data = getMP3(text);

        try
        {
            CountDownLatch latch = new CountDownLatch(1);
            PlaybackListener listener = new PlaybackListener()
            {
                @Override
                public void playbackStarted(final PlaybackEvent evt)
                {
                }

                @Override
                public void playbackFinished(final PlaybackEvent evt)
                {
                    latch.countDown();
                }
            };

            AdvancedPlayer player = new AdvancedPlayer(mp3Data);
            player.setPlayBackListener(listener);
            player.play();
            if (synchronous)
            {
                latch.await();
            }
        }
        catch (JavaLayerException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
