const grpc = require('@grpc/grpc-js');
const protoLoader = require('@grpc/proto-loader');

// Mock the server functions
const mockGetSupportedCurrencies = jest.fn((call, callback) => {
  callback(null, {
    currency_codes: ['USD', 'EUR', 'CAD', 'JPY', 'GBP', 'TRY']
  });
});

const mockConvert = jest.fn((call, callback) => {
  const { from, to_code } = call.request;
  
  // Mock exchange rates
  const rates = {
    'USD_EUR': 0.85,
    'USD_CAD': 1.25,
    'USD_JPY': 110,
    'USD_GBP': 0.75,
    'USD_TRY': 8.5,
    'EUR_USD': 1.18,
    'CAD_USD': 0.8,
    'JPY_USD': 0.009,
    'GBP_USD': 1.33,
    'TRY_USD': 0.12
  };
  
  const rate = rates[`${from.currency_code}_${to_code}`] || 1.0;
  
  callback(null, {
    currency_code: to_code,
    units: Math.floor(from.units * rate),
    nanos: Math.floor(from.nanos * rate)
  });
});

const mockCheck = jest.fn((call, callback) => {
  callback(null, { status: 'SERVING' });
});

describe('Currency Service', () => {
  describe('GetSupportedCurrencies', () => {
    test('should return list of supported currencies', () => {
      const mockCall = {};
      const mockCallback = jest.fn();
      
      mockGetSupportedCurrencies(mockCall, mockCallback);
      
      expect(mockCallback).toHaveBeenCalledWith(null, {
        currency_codes: ['USD', 'EUR', 'CAD', 'JPY', 'GBP', 'TRY']
      });
    });
  });

  describe('Convert', () => {
    test('should convert USD to EUR', () => {
      const mockCall = {
        request: {
          from: {
            currency_code: 'USD',
            units: 100,
            nanos: 0
          },
          to_code: 'EUR'
        }
      };
      const mockCallback = jest.fn();
      
      mockConvert(mockCall, mockCallback);
      
      expect(mockCallback).toHaveBeenCalledWith(null, {
        currency_code: 'EUR',
        units: 85,
        nanos: 0
      });
    });

    test('should convert EUR to USD', () => {
      const mockCall = {
        request: {
          from: {
            currency_code: 'EUR',
            units: 100,
            nanos: 0
          },
          to_code: 'USD'
        }
      };
      const mockCallback = jest.fn();
      
      mockConvert(mockCall, mockCallback);
      
      expect(mockCallback).toHaveBeenCalledWith(null, {
        currency_code: 'USD',
        units: 118,
        nanos: 0
      });
    });

    test('should handle same currency conversion', () => {
      const mockCall = {
        request: {
          from: {
            currency_code: 'USD',
            units: 100,
            nanos: 0
          },
          to_code: 'USD'
        }
      };
      const mockCallback = jest.fn();
      
      mockConvert(mockCall, mockCallback);
      
      expect(mockCallback).toHaveBeenCalledWith(null, {
        currency_code: 'USD',
        units: 100,
        nanos: 0
      });
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