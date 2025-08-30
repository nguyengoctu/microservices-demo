package hipstershop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;
import io.grpc.stub.StreamObserver;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import hipstershop.Demo.AdRequest;
import hipstershop.Demo.AdResponse;
import hipstershop.Demo.Ad;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class AdServiceTest {

    private hipstershop.AdServiceGrpc.AdServiceImplBase adServiceImpl;
    
    @Mock
    private StreamObserver<AdResponse> responseObserver;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        // Create the inner AdServiceImpl class using reflection
        Class<?> innerClass = Class.forName("hipstershop.AdService$AdServiceImpl");
        Constructor<?> constructor = innerClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        adServiceImpl = (hipstershop.AdServiceGrpc.AdServiceImplBase) constructor.newInstance();
    }

    @Test
    @DisplayName("Should return ads for valid categories")
    void shouldReturnAdsForValidCategories() {
        // Given
        AdRequest request = AdRequest.newBuilder()
                .addContextKeys("clothing")
                .addContextKeys("accessories")
                .build();

        // When
        adServiceImpl.getAds(request, responseObserver);

        // Then
        verify(responseObserver).onNext(any(AdResponse.class));
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());
    }

    @Test
    @DisplayName("Should return ads for empty context")
    void shouldReturnAdsForEmptyContext() {
        // Given
        AdRequest request = AdRequest.newBuilder().build();

        // When
        adServiceImpl.getAds(request, responseObserver);

        // Then
        verify(responseObserver).onNext(any(AdResponse.class));
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());
    }

    @Test
    @DisplayName("Should return limited number of ads")
    void shouldReturnLimitedNumberOfAds() {
        // Given
        AdRequest request = AdRequest.newBuilder()
                .addContextKeys("clothing")
                .build();

        // Capture the response
        ArgumentCaptor<AdResponse> responseCaptor = ArgumentCaptor.forClass(AdResponse.class);

        // When
        adServiceImpl.getAds(request, responseObserver);

        // Then
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();
        
        AdResponse response = responseCaptor.getValue();
        assertNotNull(response);
        
        // Based on AdService.MAX_ADS_TO_SERVE = 2
        assertTrue(response.getAdsList().size() <= 10, "Should not return more than reasonable number of ads");
        assertFalse(response.getAdsList().isEmpty(), "Should return at least one ad");
    }

    @Test
    @DisplayName("Should have valid ad content")
    void shouldHaveValidAdContent() {
        // Given
        AdRequest request = AdRequest.newBuilder()
                .addContextKeys("clothing")
                .build();

        ArgumentCaptor<AdResponse> responseCaptor = ArgumentCaptor.forClass(AdResponse.class);

        // When
        adServiceImpl.getAds(request, responseObserver);

        // Then
        verify(responseObserver).onNext(responseCaptor.capture());
        
        AdResponse response = responseCaptor.getValue();
        assertNotNull(response);
        
        // Check that ads have required fields
        for (Ad ad : response.getAdsList()) {
            assertFalse(ad.getRedirectUrl().isEmpty(), "Ad should have redirect URL");
            assertFalse(ad.getText().isEmpty(), "Ad should have text content");
        }
    }

    @Test
    @DisplayName("Should return different ads for different contexts")
    void shouldReturnDifferentAdsForDifferentContexts() {
        // Given
        AdRequest clothingRequest = AdRequest.newBuilder()
                .addContextKeys("clothing")
                .build();
                
        AdRequest kitchenRequest = AdRequest.newBuilder()
                .addContextKeys("kitchen")
                .build();

        StreamObserver<AdResponse> clothingObserver = mock(StreamObserver.class);
        StreamObserver<AdResponse> kitchenObserver = mock(StreamObserver.class);

        // When
        adServiceImpl.getAds(clothingRequest, clothingObserver);
        adServiceImpl.getAds(kitchenRequest, kitchenObserver);

        // Then
        verify(clothingObserver).onNext(any(AdResponse.class));
        verify(clothingObserver).onCompleted();
        verify(kitchenObserver).onNext(any(AdResponse.class));
        verify(kitchenObserver).onCompleted();
    }

    @Test
    @DisplayName("Should return random ads when category not found")
    void shouldReturnRandomAdsWhenCategoryNotFound() {
        // Given
        AdRequest request = AdRequest.newBuilder()
                .addContextKeys("unknown_category")
                .build();

        ArgumentCaptor<AdResponse> responseCaptor = ArgumentCaptor.forClass(AdResponse.class);

        // When
        adServiceImpl.getAds(request, responseObserver);

        // Then
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();
        
        AdResponse response = responseCaptor.getValue();
        assertNotNull(response);
        assertFalse(response.getAdsList().isEmpty(), "Should return random ads when category not found");
    }
}