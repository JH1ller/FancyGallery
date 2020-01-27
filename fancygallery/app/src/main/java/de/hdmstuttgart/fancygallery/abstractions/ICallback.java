package de.hdmstuttgart.fancygallery.abstractions;

/**
 * Used for callbacks
 * @param <TResult> type of result
 */
public interface ICallback<TResult> {
    void onCallback(TResult result);
}
