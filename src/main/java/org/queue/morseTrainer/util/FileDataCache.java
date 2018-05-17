package org.queue.morseTrainer.util;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileDataCache
{
    private File directory;

    public FileDataCache() throws IOException
    {
        this(Files.createTempDirectory(null));
    }

    public FileDataCache(final Path directory)
    {
        this(directory.toFile());
    }

    public FileDataCache(final File directory)
    {
        if (!directory.isDirectory())
        {
            throw new IllegalArgumentException();
        }
        this.directory = directory;
    }

    public boolean exists(final String filename)
    {
        return (new File(directory, filenameEncode(filename))).exists();
    }

    public InputStream get(final String filename)
    {
        File file = new File(directory, filenameEncode(filename));
        if (!file.exists())
        {
            return null;
        }

        try
        {
            return new FileInputStream(file);
        }
        catch (FileNotFoundException e)
        {
            return null;
        }
    }

    public void store(final InputStream in, final String filename) throws IOException
    {
        File         file = new File(directory, filenameEncode(filename));
        OutputStream out  = new FileOutputStream(file);
        IOUtils.copy(in, out);
    }

    private String filenameEncode(final String filename)
    {
        try
        {
            String fn = URLEncoder.encode(filename, "UTF-8");
            if (fn.startsWith("."))
            {
                fn = "%2E" + fn.substring(1);
            }
            return fn;
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
