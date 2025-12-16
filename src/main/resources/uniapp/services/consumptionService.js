/**
 * Consumption Service - API client for consumption records and statistics
 */

const BASE_URL = 'http://localhost:8080/api/consumption'

// Get token from localStorage
function getAuthToken() {
    try {
        return uni.getStorageSync('auth_token') || ''
    } catch (error) {
        console.error('Error getting auth token:', error)
        return ''
    }
}

// Make authenticated request
function makeRequest(url, method = 'GET', data = null) {
    const token = getAuthToken()
    
    return new Promise((resolve, reject) => {
        uni.request({
            url: BASE_URL + url,
            method: method,
            data: data,
            header: {
                'Content-Type': 'application/json',
                'Authorization': token ? `Bearer ${token}` : ''
            },
            success: (res) => {
                if (res.statusCode === 200) {
                    resolve(res.data)
                } else if (res.statusCode === 401) {
                    // Redirect to login if unauthorized
                    uni.redirectTo({
                        url: '/pages/auth/login'
                    })
                    reject(new Error('Authentication required'))
                } else {
                    reject(new Error(res.data.error || 'Request failed'))
                }
            },
            fail: (err) => {
                console.error('Request failed:', err)
                reject(new Error('Network error'))
            }
        })
    })
}

/**
 * Create a new consumption record
 */
export function createConsumptionRecord(recordData) {
    return makeRequest('/records', 'POST', recordData)
}

/**
 * Get consumption records with filtering and pagination
 */
export function getConsumptionRecords(params = {}) {
    const queryString = Object.keys(params)
        .filter(key => params[key] !== null && params[key] !== undefined)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        .join('&')
    
    const url = '/records' + (queryString ? '?' + queryString : '')
    return makeRequest(url)
}

/**
 * Get comprehensive consumption statistics
 */
export function getConsumptionStatistics(params = {}) {
    const queryString = Object.keys(params)
        .filter(key => params[key] !== null && params[key] !== undefined)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        .join('&')
    
    const url = '/stats' + (queryString ? '?' + queryString : '')
    return makeRequest(url)
}

/**
 * Get daily summaries for charts
 */
export function getDailySummaries(params = {}) {
    const queryString = Object.keys(params)
        .filter(key => params[key] !== null && params[key] !== undefined)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        .join('&')
    
    const url = '/stats/daily' + (queryString ? '?' + queryString : '')
    return makeRequest(url)
}

/**
 * Get weekly summaries for charts
 */
export function getWeeklySummaries(params = {}) {
    const queryString = Object.keys(params)
        .filter(key => params[key] !== null && params[key] !== undefined)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        .join('&')
    
    const url = '/stats/weekly' + (queryString ? '?' + queryString : '')
    return makeRequest(url)
}

/**
 * Get monthly summaries for charts
 */
export function getMonthlySummaries(params = {}) {
    const queryString = Object.keys(params)
        .filter(key => params[key] !== null && params[key] !== undefined)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        .join('&')
    
    const url = '/stats/monthly' + (queryString ? '?' + queryString : '')
    return makeRequest(url)
}

/**
 * Get source distribution for charts
 */
export function getSourceDistribution(params = {}) {
    const queryString = Object.keys(params)
        .filter(key => params[key] !== null && params[key] !== undefined)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        .join('&')
    
    const url = '/stats/sources' + (queryString ? '?' + queryString : '')
    return makeRequest(url)
}

/**
 * Get top spenders with VIP scores
 */
export function getTopSpenders(params = {}) {
    const queryString = Object.keys(params)
        .filter(key => params[key] !== null && params[key] !== undefined)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        .join('&')
    
    const url = '/stats/top-spenders' + (queryString ? '?' + queryString : '')
    return makeRequest(url)
}

/**
 * Get top frequent customers with VIP scores
 */
export function getTopFrequentCustomers(params = {}) {
    const queryString = Object.keys(params)
        .filter(key => params[key] !== null && params[key] !== undefined)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        .join('&')
    
    const url = '/stats/top-frequent' + (queryString ? '?' + queryString : '')
    return makeRequest(url)
}

/**
 * Get customer VIP score
 */
export function getCustomerVipScore(customerId) {
    return makeRequest(`/stats/vip-score/${customerId}`)
}

/**
 * Get project type breakdown for charts
 */
export function getProjectTypeBreakdown(params = {}) {
    const queryString = Object.keys(params)
        .filter(key => params[key] !== null && params[key] !== undefined)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        .join('&')
    
    const url = '/stats/project-types' + (queryString ? '?' + queryString : '')
    return makeRequest(url)
}

/**
 * Get visit frequency analysis
 */
export function getVisitFrequencyAnalysis(params = {}) {
    const queryString = Object.keys(params)
        .filter(key => params[key] !== null && params[key] !== undefined)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        .join('&')
    
    const url = '/stats/visit-frequency' + (queryString ? '?' + queryString : '')
    return makeRequest(url)
}

/**
 * Get customer transaction history
 */
export function getCustomerTransactions(customerId, params = {}) {
    const queryString = Object.keys(params)
        .filter(key => params[key] !== null && params[key] !== undefined)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        .join('&')
    
    const url = `/customers/${customerId}/transactions` + (queryString ? '?' + queryString : '')
    return makeRequest(url)
}

/**
 * Get quick dashboard stats
 */
export function getQuickStats() {
    return makeRequest('/dashboard/stats')
}

/**
 * Update VIP scores for all customers (admin function)
 */
export function updateAllVipScores() {
    return makeRequest('/stats/update-vip-scores', 'POST')
}