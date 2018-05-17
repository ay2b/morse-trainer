package org.queue.morseTrainer.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Extend Jackson's ObjectMapper by setting preferred defaults in this object's constructor.  Without
 * CustomObjectMapper, typical usage is:
 * <pre>
 *     private static final ObjectMapper MAPPER = new ObjectMapper();
 *
 *     static
 *     {
 *         MAPPER.registerModule(new JodaModule());
 *         ...
 *     }
 * </pre>
 *
 * With CustomObjectMapper, this becomes simply:
 * <code>private static final ObjectMapper MAPPER = new CustomObjectMapper()</code>
 */
public class CustomObjectMapper extends ObjectMapper
{
    /**
     * Constructor.  This registers JodaModule, which handles Joda objects (eg org.joda.time.DateTime), and
     * JavaTimeModule, which handles java.time.* objects.  It configures java.time.LocalData objects to be written as
     * dates only, not date + time.  It sets fields with null values to be ignored during serialization.
     * NOTE: This does not call setVisibility(), which means that fields without getters may not be serialized.
     * See the CustomObjectMapper(boolean) to enable that.
     */
    public CustomObjectMapper()
    {
        this(false);
    }

    /**
     * Constructor.  This registers JodaModule, which handles Joda objects (eg org.joda.time.DateTime), and
     * JavaTimeModule, which handles java.time.* objects.  It configures java.time.LocalData objects to be written as
     * dates only, not date + time.  It sets fields with null values to be ignored during serialization.  Optionally,
     * this can also set visibility such that all fields will be serialized, even those without getters.
     *
     * @param includeAllFields if set, even fields without getters will be serialized
     */
    public CustomObjectMapper(final boolean includeAllFields)
    {
        super();

        // handle Joda objects (eg DateTime) as nicely formatted string
        registerModule(new JodaModule());
        // handle java.time.* objects
        registerModule(new JavaTimeModule());
        // write java.time.LocalDate objects as dates only (not date + time)
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // only output non-null fields
        setSerializationInclusion(JsonInclude.Include.NON_NULL);

        if (includeAllFields)
        {
            // This ensures that all fields get serialized, not just those with @Getter
            // (without this, the "data" field of EIPReport was not getting published)
            setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
            setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        }
    }
}
