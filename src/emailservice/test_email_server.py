import unittest
from unittest.mock import Mock, patch, MagicMock
import grpc
from concurrent import futures
import threading
import time

# Mock the email_server module
class MockEmailService:
    def SendOrderConfirmation(self, request, context):
        # Mock successful email sending
        if not request.email or not request.order:
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            context.set_details('Invalid email or order')
            return {}
        
        return {}
    
    def Check(self, request, context):
        return Mock(status='SERVING')

class TestEmailService(unittest.TestCase):
    
    def setUp(self):
        self.service = MockEmailService()
    
    def test_send_order_confirmation_success(self):
        """Test successful email sending"""
        request = Mock()
        request.email = 'test@example.com'
        request.order = Mock()
        request.order.order_id = 'order-123'
        request.order.items = []
        
        context = Mock()
        
        result = self.service.SendOrderConfirmation(request, context)
        
        # Should not set error codes for valid request
        context.set_code.assert_not_called()
        context.set_details.assert_not_called()
    
    def test_send_order_confirmation_missing_email(self):
        """Test email sending with missing email"""
        request = Mock()
        request.email = None
        request.order = Mock()
        
        context = Mock()
        
        result = self.service.SendOrderConfirmation(request, context)
        
        # Should set error for missing email
        context.set_code.assert_called_with(grpc.StatusCode.INVALID_ARGUMENT)
        context.set_details.assert_called_with('Invalid email or order')
    
    def test_send_order_confirmation_missing_order(self):
        """Test email sending with missing order"""
        request = Mock()
        request.email = 'test@example.com'
        request.order = None
        
        context = Mock()
        
        result = self.service.SendOrderConfirmation(request, context)
        
        # Should set error for missing order
        context.set_code.assert_called_with(grpc.StatusCode.INVALID_ARGUMENT)
        context.set_details.assert_called_with('Invalid email or order')
    
    def test_health_check(self):
        """Test health check endpoint"""
        request = Mock()
        context = Mock()
        
        result = self.service.Check(request, context)
        
        self.assertEqual(result.status, 'SERVING')

class TestEmailTemplating(unittest.TestCase):
    """Test email template functionality"""
    
    def test_order_confirmation_template(self):
        """Test order confirmation email template"""
        order_data = {
            'order_id': 'ORDER-12345',
            'items': [
                {'name': 'Product A', 'quantity': 2, 'price': 29.99},
                {'name': 'Product B', 'quantity': 1, 'price': 49.99}
            ],
            'total': 109.97,
            'shipping_address': {
                'street': '123 Main St',
                'city': 'Anytown',
                'state': 'CA',
                'zip': '12345'
            }
        }
        
        # Mock template rendering
        template = "Order #{order_id} confirmed"
        rendered = template.format(order_id=order_data['order_id'])
        
        self.assertEqual(rendered, "Order #ORDER-12345 confirmed")
    
    def test_email_validation(self):
        """Test email address validation"""
        valid_emails = [
            'test@example.com',
            'user.name@domain.co.uk',
            'test+tag@example.org'
        ]
        
        invalid_emails = [
            'invalid-email',
            '@example.com',
            'test@',
            ''
        ]
        
        import re
        email_regex = r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$'
        
        for email in valid_emails:
            self.assertTrue(re.match(email_regex, email), f"{email} should be valid")
        
        for email in invalid_emails:
            self.assertFalse(re.match(email_regex, email), f"{email} should be invalid")

if __name__ == '__main__':
    unittest.main()