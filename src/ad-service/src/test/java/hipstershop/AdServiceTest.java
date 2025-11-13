package hipstershop;

import static org.junit.jupiter.api.Assertions.*;

import hipstershop.Demo.Ad;
import hipstershop.Demo.AdRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

class AdServiceTest {

    @Test
    @DisplayName("Should create ad request with context keys")
    void testCreateAdRequestWithContextKeys() {
        AdRequest request = AdRequest.newBuilder()
                .addContextKeys("clothing")
                .addContextKeys("accessories")
                .build();

        assertNotNull(request);
        assertEquals(2, request.getContextKeysCount());
        assertEquals("clothing", request.getContextKeys(0));
        assertEquals("accessories", request.getContextKeys(1));
    }

    @Test
    @DisplayName("Should create ad request without context keys")
    void testCreateAdRequestWithoutContextKeys() {
        AdRequest request = AdRequest.newBuilder().build();

        assertNotNull(request);
        assertEquals(0, request.getContextKeysCount());
    }

    @Test
    @DisplayName("Should create ad with redirect URL and text")
    void testCreateAd() {
        Ad ad = Ad.newBuilder()
                .setRedirectUrl("/product/123")
                .setText("Test ad for sale")
                .build();

        assertNotNull(ad);
        assertEquals("/product/123", ad.getRedirectUrl());
        assertEquals("Test ad for sale", ad.getText());
    }

    @Test
    @DisplayName("Should build valid AdRequest with multiple context keys")
    void testMultipleContextKeys() {
        AdRequest request = AdRequest.newBuilder()
                .addContextKeys("kitchen")
                .addContextKeys("decor")
                .addContextKeys("footwear")
                .build();

        assertEquals(3, request.getContextKeysCount());
        assertTrue(request.getContextKeysList().contains("kitchen"));
        assertTrue(request.getContextKeysList().contains("decor"));
        assertTrue(request.getContextKeysList().contains("footwear"));
    }

    @Test
    @DisplayName("Should create ad with empty fields")
    void testAdWithEmptyFields() {
        Ad ad = Ad.newBuilder().build();

        assertNotNull(ad);
        assertEquals("", ad.getRedirectUrl());
        assertEquals("", ad.getText());
    }
}
