# Consumption Stats APIs Implementation Guide

## Overview

This document provides a complete implementation guide for the Consumption Stats APIs (Phase 4 & partial Phase 7), including CRUD operations for consumption records, comprehensive statistics endpoints, and UniApp frontend integration.

## Architecture Components

### Backend Implementation (Java Spring Boot)

#### 1. **Data Transfer Objects (DTOs)**

- `ConsumptionRecordDTO.java` - Response DTO for consumption records with customer info
- `ConsumptionStatsDTO.java` - Comprehensive statistics DTO with nested data structures
- `CreateConsumptionRequest.java` - Request DTO for creating new consumption records
- `ConsumptionRecordFilter.java` - Filter parameters for querying consumption records

#### 2. **Repository Layer**

**Enhanced `TransactionRepository.java`** with additional queries:
- `findConsumptionRecords()` - Filtered pagination support
- `getDailySummaries()` - Daily aggregated statistics
- `getWeeklySummaries()` - Weekly aggregated statistics  
- `getMonthlySummaries()` - Monthly aggregated statistics
- `getSourceDistribution()` - Customer source distribution analysis
- `getTopSpenders()` - Highest spending customers with VIP scores
- `getTopFrequentCustomers()` - Most active customers
- `getProjectTypeBreakdown()` - Project type performance analysis
- `getVisitFrequencyAnalysis()` - Customer visit pattern analysis
- `calculateVipScore()` - Customer VIP scoring algorithm

#### 3. **Service Layer**

**`ConsumptionService.java`** - Main business logic:
- CRUD operations for consumption records
- Comprehensive statistics aggregation
- VIP score calculation and batch updates
- Account balance management
- Transaction validation and business rules

#### 4. **Controller Layer**

**`ConsumptionController.java`** - REST API endpoints:
- 15+ endpoints for consumption management
- Authentication and authorization
- Parameter validation
- Error handling and response formatting

### Frontend Implementation (UniApp/Vue)

#### 1. **Service Layer**

**`consumptionService.js`** - API client:
- Authenticated HTTP requests
- Token management
- Error handling and redirects
- Promise-based API calls

#### 2. **Pages**

**`history.vue`** - Consumption History Page:
- Paginated consumption records
- Advanced filtering (date range, type, project type)
- Real-time summary statistics
- Add new consumption records
- Responsive UI with modal dialogs

**`stats.vue`** - Statistics Dashboard:
- Tabbed interface (Overview, Trends, Customers, Projects)
- Interactive charts and visualizations
- Date range filtering
- Top customers and spenders analysis
- Project type breakdown

## API Endpoints

### CRUD Operations

#### POST `/api/consumption/records`
Create a new consumption record.

**Request:**
```json
{
  "customerId": 1,
  "amount": 99.99,
  "projectType": "POND_ARTICLES",
  "notes": "Premium article purchase"
}
```

**Response:**
```json
{
  "id": 123,
  "customerId": 1,
  "customerName": "张三",
  "customerPhone": "13800138000",
  "source": "weixin",
  "type": "SPEND",
  "amount": 99.99,
  "projectType": "POND_ARTICLES",
  "projectTypeDisplay": "池塘文章",
  "metadata": "notes:Premium article purchase",
  "createdAt": "2024-12-16T10:30:00",
  "formattedAmount": "￥99.99",
  "formattedDate": "2024-12-16"
}
```

#### GET `/api/consumption/records`
Get consumption records with filtering and pagination.

**Query Parameters:**
- `customerId` (optional) - Filter by customer
- `type` (optional) - Filter by transaction type (SPEND, RECHARGE, REFUND, ADJUSTMENT)
- `projectType` (optional) - Filter by project type
- `startDate` (optional) - Start date (YYYY-MM-DD)
- `endDate` (optional) - End date (YYYY-MM-DD)
- `page` (default: 0) - Page number
- `size` (default: 20) - Page size
- `sortBy` (default: createdAt) - Sort field
- `sortDir` (default: desc) - Sort direction

**Response:** Paginated ConsumptionRecordDTO list

### Statistics Endpoints

#### GET `/api/consumption/stats`
Comprehensive consumption statistics for date range.

**Query Parameters:**
- `startDate` (optional) - Start date
- `endDate` (optional) - End date

**Response:**
```json
{
  "totalRevenue": 15000.50,
  "totalSpend": 8500.25,
  "totalTransactions": 245,
  "uniqueCustomers": 89,
  "averageTransactionAmount": 61.43,
  "dailySummaries": [...],
  "weeklySummaries": [...],
  "monthlySummaries": [...],
  "sourceDistribution": [...],
  "topSpenders": [...],
  "topFrequentCustomers": [...],
  "projectTypeBreakdown": [...],
  "visitFrequency": {
    "oneTimeCustomers": 45,
    "frequentCustomers": 12,
    "regularCustomers": 32,
    "averageVisitsPerCustomer": 2.75
  }
}
```

#### GET `/api/consumption/stats/daily`
Daily aggregated statistics for charts.

#### GET `/api/consumption/stats/weekly`
Weekly aggregated statistics for charts.

#### GET `/api/consumption/stats/monthly`
Monthly aggregated statistics for charts.

#### GET `/api/consumption/stats/sources`
Customer source distribution analysis.

#### GET `/api/consumption/stats/top-spenders`
Top spending customers with VIP scores.

#### GET `/api/consumption/stats/top-frequent`
Most frequent customers.

#### GET `/api/consumption/stats/vip-score/{customerId}`
Get VIP score for specific customer.

#### POST `/api/consumption/stats/update-vip-scores`
Update VIP scores for all customers (admin function).

#### GET `/api/consumption/stats/project-types`
Project type breakdown analysis.

#### GET `/api/consumption/stats/visit-frequency`
Visit frequency analysis.

#### GET `/api/consumption/customers/{customerId}/transactions`
Customer transaction history.

#### GET `/api/consumption/dashboard/stats`
Quick dashboard statistics.

## Database Schema

### Enhanced Indexes
```sql
-- Existing indexes on transactions table
CREATE INDEX idx_transaction_customer ON transactions(customer_id);
CREATE INDEX idx_transaction_account ON transactions(account_id);
CREATE INDEX idx_transaction_created ON transactions(created_at);
CREATE INDEX idx_transaction_type ON transactions(type);
CREATE INDEX idx_transaction_project_type ON transactions(project_type);
CREATE INDEX idx_transaction_customer_created ON transactions(customer_id, created_at);
```

### Transaction Entity Fields
- `id` - Primary key
- `customer_id` - Foreign key to customers
- `account_id` - Foreign key to accounts  
- `type` - ENUM: SPEND, RECHARGE, REFUND, ADJUSTMENT
- `amount` - DECIMAL(10,2)
- `project_type` - ENUM: GENERAL, POND_ARTICLES, DISCOUNT_CAMPAIGN, PROMOTION, SUBSCRIPTION, OTHER
- `metadata` - TEXT (for additional information)
- `created_at` - Timestamp
- `updated_at` - Timestamp

## VIP Score Algorithm

The VIP score is calculated using a weighted formula:
- **40%** - Total spending amount
- **30%** - Transaction frequency
- **30%** - Recency (activity within last 30 days)

```sql
SELECT 
  (spend_amount * 0.4) + 
  (transaction_count * 0.3) + 
  (CASE WHEN last_activity >= recent_date THEN 100 ELSE 0 END * 0.3)
FROM transactions 
WHERE customer_id = ?
```

## Authentication & Authorization

All endpoints require JWT authentication:
- `Authorization: Bearer <jwt_token>`
- Invalid/expired tokens return 401 Unauthorized
- Redirect to login page on frontend

## Performance Optimizations

### Database Level
- **Indexed queries** on all filtering fields
- **SQL aggregations** for summary statistics
- **Pagination** for large result sets
- **Eager loading** of customer relationships

### Application Level
- **Caching** strategies for frequently accessed data
- **Batch processing** for VIP score updates
- **Connection pooling** for database efficiency

## Frontend Features

### History Page (`history.vue`)
- **Real-time filtering** with immediate results
- **Modal dialogs** for adding new records
- **Summary cards** showing key metrics
- **Responsive design** for mobile devices
- **Pagination controls** for large datasets

### Statistics Page (`stats.vue`)
- **Tabbed interface** for different analysis views
- **Interactive charts** (placeholder for uCharts integration)
- **Date range pickers** for temporal filtering
- **Top customer rankings** with VIP scores
- **Project type breakdowns** with progress indicators

## Error Handling

### Backend
- **Validation errors** with detailed messages
- **Business rule violations** (insufficient balance, etc.)
- **Database constraint violations**
- **Authentication failures**

### Frontend
- **Network error handling** with retry logic
- **User-friendly error messages**
- **Loading states** for better UX
- **Automatic token refresh** handling

## Testing Strategy

### Backend Testing
- **Unit tests** for service layer logic
- **Integration tests** for API endpoints
- **Repository tests** with in-memory database
- **Mock services** for external dependencies

### Frontend Testing
- **Component testing** with Vue Test Utils
- **API integration testing** with mocked responses
- **User workflow testing** for critical paths

## Deployment Considerations

### Environment Configuration
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/core_db
spring.datasource.username=root
spring.datasource.password=password

# JWT
jwt.secret=${JWT_SECRET:your-secret-key}
jwt.expiration=86400000

# CORS
cors.allowed-origins=http://localhost:3000,https://yourdomain.com
```

### Production Checklist
- [ ] Database migrations applied
- [ ] Indexes created for performance
- [ ] Environment variables configured
- [ ] SSL certificates installed
- [ ] Monitoring and logging configured
- [ ] Backup strategy implemented
- [ ] Rate limiting configured
- [ ] API documentation generated

## API Response Examples

### Success Response
```json
{
  "status": "success",
  "data": {...},
  "timestamp": "2024-12-16T10:30:00Z"
}
```

### Error Response
```json
{
  "status": "error",
  "error": "Insufficient balance for spending transaction",
  "timestamp": "2024-12-16T10:30:00Z"
}
```

## Monitoring & Analytics

### Key Metrics to Track
- **API response times**
- **Database query performance**  
- **Error rates by endpoint**
- **User activity patterns**
- **VIP score distributions**
- **Transaction volume trends**

### Logging Strategy
- **Request/response logging** for debugging
- **Performance metrics** for optimization
- **Error tracking** for issue resolution
- **Business events** for analytics

## Future Enhancements

### Phase 5-7 Roadmap
1. **Real-time notifications** for large transactions
2. **Advanced analytics** with ML predictions
3. **Export functionality** for reports
4. **Multi-tenant support** for different merchants
5. **API versioning** for backward compatibility
6. **Advanced caching** with Redis
7. **WebSocket support** for live updates

## Support & Troubleshooting

### Common Issues
1. **Authentication errors** - Check JWT token validity
2. **Database timeouts** - Review query performance and indexes
3. **High memory usage** - Optimize batch processing
4. **Chart rendering issues** - Verify uCharts integration

### Debug Tools
- **Database query logging** for performance analysis
- **API testing** with Postman/Insomnia collections
- **Frontend debugging** with Vue DevTools
- **Network monitoring** for API latency

---

**Implementation Status:** ✅ Complete  
**Documentation Version:** 1.0  
**Last Updated:** December 2024