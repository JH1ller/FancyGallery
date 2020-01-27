package de.hdmstuttgart.fancygallery.abstractions;

/**
 * Callback class which implements the {@link ICallback} interface which throws
 * an exception when called.
 *
 * This is used to make sure that a callback is set (or else) the app crashes.
 * @param <TResult> The type of result
 */
public class CallbackNotSet<TResult> implements ICallback<TResult> {
    @Override
    public void onCallback(TResult tResult) {
        throw new RuntimeException( "onCallback has not been set but is required.");
    }
}
