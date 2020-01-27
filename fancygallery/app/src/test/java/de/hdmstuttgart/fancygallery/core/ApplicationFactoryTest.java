package de.hdmstuttgart.fancygallery.core;

import org.junit.Test;

import de.hdmstuttgart.fancygallery.infrastructure.providers.CurrentImageListProvider;

import static org.junit.Assert.*;

/**
 * Apparently, most of the APP requires Android.Uri which can not be tested in Unit Tests
 * (Throws exceptions --> Need third party libraries to mock them)
 */
public class ApplicationFactoryTest {

    /**
     * getCurrentImageListProvider should return Singleton
     * --> Reference should be same calling it multiple times
     */
    @Test
    public void getCurrentImageListProvider() {
        CurrentImageListProvider provider = ApplicationFactory.getCurrentImageListProvider();
        CurrentImageListProvider provider2 = ApplicationFactory.getCurrentImageListProvider();
        assertSame(provider, provider2);
    }
}