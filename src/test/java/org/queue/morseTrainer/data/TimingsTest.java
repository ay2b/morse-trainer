package org.queue.morseTrainer.data;

import org.queue.morseTrainer.util.CustomObjectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
public class TimingsTest
{
    private static final ObjectMapper MAPPER = new CustomObjectMapper();

    @Test
    public void doTest() throws JsonProcessingException
    {
        Timings timings20 = new Timings(20);
        Assertions.assertEquals(60, timings20.getDotMs());
        Timings timings18 = new Timings(18);
        Timings timings15 = new Timings(15);
        Timings timings05 = new Timings(5);

        Timings[] timings = new Timings[]{timings20, timings18, timings15, timings05};

        String json = MAPPER.writeValueAsString(timings);
        System.out.println(json);
    }
}
