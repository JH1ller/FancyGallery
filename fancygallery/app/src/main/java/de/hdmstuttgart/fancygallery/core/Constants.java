package de.hdmstuttgart.fancygallery.core;

import de.hdmstuttgart.fancygallery.model.OrderBy;

/**
 * Provides various constants used in multiple classes
 */
public final class Constants {

    private Constants(){
        // prevent constructor usage
    }

    public static final String DATABASE_BLACKLIST_NAME = "Blacklist";

    public static final String PREFS = "prefs";

    public static final int FOLDER_LIST_MAX_SPAN_COUNT = 5;
    public static final int FOLDER_LIST_MIN_SPAN_COUNT = 2;
    public static final String FOLDER_LIST_SPAN_COUNT_PORTRAIT ="folder_list_span_count";
    public static final int FOLDER_LIST_SPAN_COUNT_PORTRAIT_DEFAULT = 2;
    public static final String FOLDER_LIST_SPAN_COUNT_LANDSCAPE ="folder_list_span_count_landscape";
    public static final int FOLDER_LIST_SPAN_COUNT_LANDSCAPE_DEFAULT = 4;
    public static final String FOLDER_LIST_ORDER_BY ="folder_list_order_by";
    public static final int FOLDER_LIST_ORDER_BY_DEFAULT = OrderBy.NAME_DESCENDING;


    public static final int IMAGE_LIST_MAX_SPAN_COUNT = 8;
    public static final int IMAGE_LIST_MIN_SPAN_COUNT = 2;
    public static final String IMAGE_LIST_SPAN_COUNT_PORTRAIT ="image_list_span_count";
    public static final int IMAGE_LIST_SPAN_COUNT_PORTRAIT_DEFAULT = 3;
    public static final String IMAGE_LIST_SPAN_COUNT_LANDSCAPE ="image_list_span_count_landscape";
    public static final int IMAGE_LIST_SPAN_COUNT_LANDSCAPE_DEFAULT = 5;
}
