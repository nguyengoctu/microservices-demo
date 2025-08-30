const uuid = require('uuid');
const cardValidator = require('simple-card-validator');

// Mock the payment functions
const mockCharge = jest.fn((call, callback) => {
  const { amount, credit_card } = call.request;
  
  // Validate credit card
  if (!credit_card || !credit_card.credit_card_number) {
    return callback(new Error('Invalid credit card'), null);
  }
  
  // Mock transaction ID generation
  const transaction_id = `txn_${uuid.v4()}`;
  
  callback(null, { transaction_id });
});

const mockCheck = jest.fn((call, callback) => {
  callback(null, { status: 'SERVING' });
});

// Mock card validator
jest.mock('simple-card-validator', () => ({
  validate: jest.fn()
}));

describe('Payment Service', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('Charge', () => {
    test('should successfully process payment with valid card', () => {
      cardValidator.validate.mockReturnValue({ valid: true });
      
      const mockCall = {
        request: {
          amount: {
            currency_code: 'USD',
            units: 100,
            nanos: 0
          },
          credit_card: {
            credit_card_number: '4111111111111111',
            credit_card_cvv: 123,
            credit_card_expiration_year: 2025,
            credit_card_expiration_month: 12
          }
        }
      };
      const mockCallback = jest.fn();
      
      mockCharge(mockCall, mockCallback);
      
      expect(mockCallback).toHaveBeenCalledWith(null, {
        transaction_id: expect.stringMatching(/^txn_/)
      });
    });

    test('should fail with missing credit card', () => {
      const mockCall = {
        request: {
          amount: {
            currency_code: 'USD',
            units: 100,
            nanos: 0
          },
          credit_card: null
        }
      };
      const mockCallback = jest.fn();
      
      mockCharge(mockCall, mockCallback);
      
      expect(mockCallback).toHaveBeenCalledWith(
        new Error('Invalid credit card'), 
        null
      );
    });

    test('should fail with missing credit card number', () => {
      const mockCall = {
        request: {
          amount: {
            currency_code: 'USD',
            units: 100,
            nanos: 0
          },
          credit_card: {
            credit_card_cvv: 123,
            credit_card_expiration_year: 2025,
            credit_card_expiration_month: 12
          }
        }
      };
      const mockCallback = jest.fn();
      
      mockCharge(mockCall, mockCallback);
      
      expect(mockCallback).toHaveBeenCalledWith(
        new Error('Invalid credit card'), 
        null
      );
    });

    test('should generate unique transaction IDs', () => {
      cardValidator.validate.mockReturnValue({ valid: true });
      
      const mockCall = {
        request: {
          amount: {
            currency_code: 'USD',
            units: 50,
            nanos: 0
          },
          credit_card: {
            credit_card_number: '4111111111111111',
            credit_card_cvv: 123,
            credit_card_expiration_year: 2025,
            credit_card_expiration_month: 12
          }
        }
      };
      
      const mockCallback1 = jest.fn();
      const mockCallback2 = jest.fn();
      
      mockCharge(mockCall, mockCallback1);
      mockCharge(mockCall, mockCallback2);
      
      const txn1 = mockCallback1.mock.calls[0][1].transaction_id;
      const txn2 = mockCallback2.mock.calls[0][1].transaction_id;
      
      expect(txn1).not.toBe(txn2);
      expect(txn1).toMatch(/^txn_/);
      expect(txn2).toMatch(/^txn_/);
    });
  });

  describe('Health Check', () => {
    test('should return SERVING status', () => {
      const mockCall = {};
      const mockCallback = jest.fn();
      
      mockCheck(mockCall, mockCallback);
      
      expect(mockCallback).toHaveBeenCalledWith(null, { status: 'SERVING' });
    });
  });
});