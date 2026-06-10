package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.IPLog
import com.example.data.repository.IPRepository
import com.example.data.api.IPResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IPAgentViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = IPRepository(db.ipDao())

    // Core states
    private val _currentIP = MutableStateFlow("185.156.172.42")
    val currentIP: StateFlow<String> = _currentIP.asStateFlow()

    private val _city = MutableStateFlow("Stockholm")
    val city: StateFlow<String> = _city.asStateFlow()

    private val _country = MutableStateFlow("SE")
    val country: StateFlow<String> = _country.asStateFlow()

    private val _provider = MutableStateFlow("Cloudflare")
    val provider: StateFlow<String> = _provider.asStateFlow()

    private val _latency = MutableStateFlow(12L)
    val latency: StateFlow<Long> = _latency.asStateFlow()

    private val _protocol = MutableStateFlow("UDP-256")
    val protocol: StateFlow<String> = _protocol.asStateFlow()

    private val _isRotating = MutableStateFlow(false)
    val isRotating: StateFlow<Boolean> = _isRotating.asStateFlow()

    private val _autoRotation = MutableStateFlow(true)
    val autoRotation: StateFlow<Boolean> = _autoRotation.asStateFlow()

    private val _selectedTab = MutableStateFlow("home")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResult = MutableStateFlow<IPResponse?>(null)
    val searchResult: StateFlow<IPResponse?> = _searchResult.asStateFlow()

    val logs: StateFlow<List<IPLog>> = repository.allLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private var autoRotateJob: Job? = null

    init {
        fetchActiveIP()
        setupAutoRotation()
    }

    fun selectTab(tab: String) {
        _selectedTab.value = tab
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateProtocol(newProtocol: String) {
        _protocol.value = newProtocol
    }

    fun toggleAutoRotation() {
        _autoRotation.value = !_autoRotation.value
        setupAutoRotation()
    }

    private fun setupAutoRotation() {
        autoRotateJob?.cancel()
        if (_autoRotation.value) {
            autoRotateJob = viewModelScope.launch {
                while (true) {
                    delay(300000) // Rotate or refresh every 5 mins automatically
                    rotateIP()
                }
            }
        }
    }

    fun fetchActiveIP() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val (details, ping) = repository.fetchMyIPDetails()
                _currentIP.value = details.ip ?: "185.156.172.42"
                _city.value = details.city ?: "Stockholm"
                _country.value = details.countryCode ?: "SE"
                _provider.value = details.org?.substringBefore(" ") ?: "Cloudflare"
                _latency.value = if (ping > 0) ping else 12L

                // Save to logs
                repository.saveLog(
                    IPLog(
                        ip = _currentIP.value,
                        country = _country.value,
                        city = _city.value,
                        provider = _provider.value,
                        latency = _latency.value,
                        protocol = _protocol.value
                    )
                )
            } catch (e: Exception) {
                // If offline or rate limited, degrade gracefully
                _errorMessage.value = "Failed to fetch live IP. Using secure fallback."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun rotateIP() {
        viewModelScope.launch {
            _isRotating.value = true
            delay(1500) // Simulate proxy reconnect

            // Simple selection of proxies for exciting simulation
            val proxies = listOf(
                IPLog(ip = "45.138.89.12", country = "NL", city = "Amsterdam", provider = "Leaseweb", latency = 18L, protocol = _protocol.value),
                IPLog(ip = "109.201.154.22", country = "DE", city = "Frankfurt", provider = "DigitalOcean", latency = 24L, protocol = _protocol.value),
                IPLog(ip = "81.92.203.44", country = "UK", city = "London", provider = "Linode", latency = 15L, protocol = _protocol.value),
                IPLog(ip = "185.156.172.42", country = "SE", city = "Stockholm", provider = "Cloudflare", latency = 14L, protocol = _protocol.value),
                IPLog(ip = "13.234.113.8", country = "IN", city = "Mumbai", provider = "AWS Host", latency = 45L, protocol = _protocol.value),
                IPLog(ip = "192.241.130.5", country = "US", city = "New York", provider = "Vultr", latency = 78L, protocol = _protocol.value)
            )

            val nextProxy = proxies.random()
            _currentIP.value = nextProxy.ip
            _city.value = nextProxy.city
            _country.value = nextProxy.country
            _provider.value = nextProxy.provider
            _latency.value = nextProxy.latency

            repository.saveLog(nextProxy.copy(date = System.currentTimeMillis()))
            _isRotating.value = false
        }
    }

    fun performCustomLookup() {
        val query = _searchQuery.value.trim()
        if (query.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _searchResult.value = null
            try {
                val (details, ping) = repository.fetchCustomIPDetails(query)
                _searchResult.value = details

                // Save lookup to logs as manual test
                repository.saveLog(
                    IPLog(
                        ip = details.ip ?: query,
                        country = details.countryCode ?: "Unknown",
                        city = details.city ?: "External",
                        provider = details.org?.substringBefore(" ") ?: "IP Lookup",
                        latency = ping,
                        protocol = "LOOKUP"
                    )
                )
            } catch (e: Exception) {
                _errorMessage.value = "IP lookup failed or address not found. Check query."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }
}
