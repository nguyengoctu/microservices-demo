import unittest
from unittest.mock import Mock, patch, MagicMock
import random

class MockRecommendationService:
    def __init__(self):
        # Mock product catalog
        self.product_catalog = [
            'L9ECAV7KIM',  # Loafers
            '2ZYFJ3GM2N',  # Hairdryer  
            '0PUK6V6EV0',  # Candle Holder
            '1YMWWN1N4O',  # Watch
            '66VCHSJNUP',  # Tank Top
            '6E92ZMYYFZ',  # Mug
            '9SIQT8TOJO',  # Bamboo Glass Jar
            'LS4PSXUNUM',  # Salt & Pepper Shakers
            'OLJCESPC7Z'   # Sunglasses
        ]
    
    def ListRecommendations(self, request, context):
        """Return product recommendations"""
        user_id = request.user_id
        product_ids = list(request.product_ids) if request.product_ids else []
        
        # Mock recommendation logic
        # Filter out current products and recommend others
        available_products = [p for p in self.product_catalog if p not in product_ids]
        
        # Return up to 4 recommendations
        max_responses = min(4, len(available_products))
        if max_responses == 0:
            return Mock(product_ids=[])
        
        recommendations = random.sample(available_products, max_responses)
        return Mock(product_ids=recommendations)
    
    def Check(self, request, context):
        return Mock(status='SERVING')

class TestRecommendationService(unittest.TestCase):
    
    def setUp(self):
        self.service = MockRecommendationService()
        # Seed random for consistent testing
        random.seed(42)
    
    def test_get_recommendations_empty_cart(self):
        """Test recommendations with empty cart"""
        request = Mock()
        request.user_id = 'test-user-1'
        request.product_ids = []
        
        context = Mock()
        
        result = self.service.ListRecommendations(request, context)
        
        # Should return some recommendations
        self.assertIsInstance(result.product_ids, list)
        self.assertLessEqual(len(result.product_ids), 4)
    
    def test_get_recommendations_with_existing_products(self):
        """Test recommendations when user has products in cart"""
        request = Mock()
        request.user_id = 'test-user-2'
        request.product_ids = ['L9ECAV7KIM', '2ZYFJ3GM2N']  # Loafers, Hairdryer
        
        context = Mock()
        
        result = self.service.ListRecommendations(request, context)
        
        # Should not recommend products already in cart
        for product_id in result.product_ids:
            self.assertNotIn(product_id, request.product_ids)
        
        # Should return up to 4 recommendations
        self.assertLessEqual(len(result.product_ids), 4)
    
    def test_get_recommendations_with_all_products_in_cart(self):
        """Test recommendations when all products are in cart"""
        request = Mock()
        request.user_id = 'test-user-3'
        request.product_ids = self.service.product_catalog  # All products
        
        context = Mock()
        
        result = self.service.ListRecommendations(request, context)
        
        # Should return empty list when no products to recommend
        self.assertEqual(result.product_ids, [])
    
    def test_get_recommendations_different_users(self):
        """Test that recommendations can vary (in real implementation)"""
        request1 = Mock()
        request1.user_id = 'user-1'
        request1.product_ids = []
        
        request2 = Mock()
        request2.user_id = 'user-2' 
        request2.product_ids = []
        
        context = Mock()
        
        # In a real system, recommendations might differ based on user preferences
        # For this mock, we'll just verify the service responds consistently
        result1 = self.service.ListRecommendations(request1, context)
        result2 = self.service.ListRecommendations(request2, context)
        
        self.assertIsInstance(result1.product_ids, list)
        self.assertIsInstance(result2.product_ids, list)
    
    def test_health_check(self):
        """Test health check endpoint"""
        request = Mock()
        context = Mock()
        
        result = self.service.Check(request, context)
        
        self.assertEqual(result.status, 'SERVING')

class TestRecommendationLogic(unittest.TestCase):
    """Test recommendation algorithm logic"""
    
    def test_product_filtering(self):
        """Test that products are properly filtered"""
        all_products = ['A', 'B', 'C', 'D', 'E']
        user_products = ['B', 'D']
        
        available = [p for p in all_products if p not in user_products]
        
        self.assertEqual(set(available), {'A', 'C', 'E'})
        self.assertNotIn('B', available)
        self.assertNotIn('D', available)
    
    def test_recommendation_limit(self):
        """Test recommendation count limit"""
        all_products = list(range(20))  # 20 products
        user_products = [1, 2, 3]       # User has 3 products
        
        available = [p for p in all_products if p not in user_products]
        max_recommendations = min(4, len(available))
        
        # Should limit to 4 recommendations max
        self.assertEqual(max_recommendations, 4)
        
        # Test with fewer available products
        all_products_small = [1, 2, 3, 4, 5]
        user_products_small = [1, 2, 4]
        available_small = [p for p in all_products_small if p not in user_products_small]
        max_recommendations_small = min(4, len(available_small))
        
        # Should return only available products (2 in this case)
        self.assertEqual(max_recommendations_small, 2)

if __name__ == '__main__':
    unittest.main()