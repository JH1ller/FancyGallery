package de.hdmstuttgart.fancygallery.model;

/**
 * Defines order options
 */
public final class OrderBy {

    private OrderBy(){
        // prevent constructor usage
    }

    public static final int NAME_ASCENDING = 0;
    public static final int NAME_DESCENDING = 1;

    public static final int DATE_TAKEN_ASCENDING = 2;
    public static final int DATE_TAKEN_DESCENDING = 3;

    public static final int DATE_MODIFIED_ASCENDING = 4;
    public static final int DATE_MODIFIED_DESCENDING = 5;

    public static final int COUNT_ASCENDING = 6;
    public static final int COUNT_DESCENDING = 7;
}
