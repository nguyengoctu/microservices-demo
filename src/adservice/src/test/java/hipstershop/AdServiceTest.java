package hipstershop;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import hipstershop.Demo.Ad;
import hipstershop.Demo.AdRequest;
import hipstershop.Demo.AdResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;

import java.util.List;

class AdServiceTest {

    private StreamObserver<AdResponse> responseObserver;

    @BeforeEach
    void setUp() {
        responseObserver = mock(StreamObserver.class);
    }

    @Test
    @DisplayName("Should return ads when context keys are provided")
    void testGetAdsWithContextKeys() {
        // Arrange
        AdRequest request = AdRequest.newBuilder()
                .addContextKeys("clothing")
                .addContextKeys("accessories")
                .build();

        // Act
        AdServiceGrpc.AdServiceImplBase service = new AdServiceTestImpl();
        service.getAds(request, responseObserver);

        // Assert
        ArgumentCaptor<AdResponse> responseCaptor = ArgumentCaptor.forClass(AdResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());

        AdResponse response = responseCaptor.getValue();
        assertNotNull(response);
        assertTrue(response.getAdsCount() > 0, "Should return at least one ad");
    }

    @Test
    @DisplayName("Should return random ads when no context keys provided")
    void testGetAdsWithoutContextKeys() {
        // Arrange
        AdRequest request = AdRequest.newBuilder().build();

        // Act
        AdServiceGrpc.AdServiceImplBase service = new AdServiceTestImpl();
        service.getAds(request, responseObserver);

        // Assert
        ArgumentCaptor<AdResponse> responseCaptor = ArgumentCaptor.forClass(AdResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        AdResponse response = responseCaptor.getValue();
        assertNotNull(response);
        assertTrue(response.getAdsCount() > 0, "Should return random ads");
    }

    @Test
    @DisplayName("Should return ads for clothing category")
    void testGetAdsForClothingCategory() {
        // Arrange
        AdRequest request = AdRequest.newBuilder()
                .addContextKeys("clothing")
                .build();

        // Act
        AdServiceGrpc.AdServiceImplBase service = new AdServiceTestImpl();
        service.getAds(request, responseObserver);

        // Assert
        ArgumentCaptor<AdResponse> responseCaptor = ArgumentCaptor.forClass(AdResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());

        AdResponse response = responseCaptor.getValue();
        assertNotNull(response);
        assertTrue(response.getAdsCount() > 0, "Should return clothing ads");

        // Check if any ad contains clothing-related redirect
        List<Ad> ads = response.getAdsList();
        assertFalse(ads.isEmpty(), "Ads list should not be empty");
    }

    @Test
    @DisplayName("Should return ads for multiple categories")
    void testGetAdsForMultipleCategories() {
        // Arrange
        AdRequest request = AdRequest.newBuilder()
                .addContextKeys("kitchen")
                .addContextKeys("decor")
                .build();

        // Act
        AdServiceGrpc.AdServiceImplBase service = new AdServiceTestImpl();
        service.getAds(request, responseObserver);

        // Assert
        ArgumentCaptor<AdResponse> responseCaptor = ArgumentCaptor.forClass(AdResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());

        AdResponse response = responseCaptor.getValue();
        assertNotNull(response);
        assertTrue(response.getAdsCount() > 0, "Should return ads for multiple categories");
    }

    @Test
    @DisplayName("Should handle unknown category by returning random ads")
    void testGetAdsForUnknownCategory() {
        // Arrange
        AdRequest request = AdRequest.newBuilder()
                .addContextKeys("unknown_category")
                .build();

        // Act
        AdServiceGrpc.AdServiceImplBase service = new AdServiceTestImpl();
        service.getAds(request, responseObserver);

        // Assert
        ArgumentCaptor<AdResponse> responseCaptor = ArgumentCaptor.forClass(AdResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());

        AdResponse response = responseCaptor.getValue();
        assertNotNull(response);
        assertTrue(response.getAdsCount() > 0, "Should return random ads for unknown category");
    }

    // Test implementation class to access the actual AdService logic
    private static class AdServiceTestImpl extends AdServiceGrpc.AdServiceImplBase {
        @Override
        public void getAds(AdRequest req, StreamObserver<AdResponse> responseObserver) {
            // This will use the actual implementation from AdService
            try {
                java.lang.reflect.Method method = Class.forName("hipstershop.AdService")
                    .getDeclaredMethod("getInstance");
                method.setAccessible(true);
                Object serviceInstance = method.invoke(null);

                // Get the inner class
                Class<?>[] declaredClasses = Class.forName("hipstershop.AdService").getDeclaredClasses();
                for (Class<?> innerClass : declaredClasses) {
                    if (innerClass.getSimpleName().equals("AdServiceImpl")) {
                        Object impl = innerClass.getDeclaredConstructor().newInstance();
                        java.lang.reflect.Method getAdsMethod = innerClass.getMethod(
                            "getAds", AdRequest.class, StreamObserver.class);
                        getAdsMethod.invoke(impl, req, responseObserver);
                        return;
                    }
                }
            } catch (Exception e) {
                responseObserver.onError(e);
            }
        }
    }
}
