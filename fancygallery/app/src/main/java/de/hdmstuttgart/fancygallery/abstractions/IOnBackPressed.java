package de.hdmstuttgart.fancygallery.abstractions;

/**
 * Used to forward a back press from activity to a fragment
 */
public interface IOnBackPressed {
    /**
     * Handles a back press received by an activity.
     * @return true if back press has been handled, else false
     */
    boolean onBackPressed();
}
