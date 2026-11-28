package ru.practicum.main.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoConstants {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final int MAX_TITLE = 50;

    public static final int MIN_EVENT_TITLE = 3;
    public static final int MAX_EVENT_TITLE = 120;

    public static final int MIN_ANNOTATION = 20;
    public static final int MAX_ANNOTATION = 2000;

    public static final int MIN_DESCRIPTION = 20;
    public static final int MAX_DESCRIPTION = 7000;

    public static final int MIN_USERNAME = 2;
    public static final int MAX_USERNAME = 250;

    public static final int MIN_EMAIL = 6;
    public static final int MAX_EMAIL = 254;
}
