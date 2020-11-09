package io.geewit.web.convert.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.util.Date;

/**
 * @author geewit
 */
public class DateConverter implements Converter<String, Date> {
    private final static Logger logger = LoggerFactory.getLogger(DateConverter.class);

    private static final String[] PATTERNS = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "HH:mm:ss"};

    @Override
    public Date convert(String source) {
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDateStrictly(source, PATTERNS);
        } catch (ParseException e) {
            logger.warn(e.getMessage(), e);
            return null;
        }
    }
}
