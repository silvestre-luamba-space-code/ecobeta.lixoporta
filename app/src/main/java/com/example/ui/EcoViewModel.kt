package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EcoViewModel(application: Application) : AndroidViewModel(application) {

    private val database = EcoDatabase.getDatabase(application, viewModelScope)
    private val repository = EcoRepository(database.ecoDao())

    // UI state states
    val clients = repository.clients.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allStamps = repository.allStamps.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allIssues = repository.allIssues.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Mode Toggle: Client (false) or Collector (true)
    private val _isCollectorMode = MutableStateFlow(false)
    val isCollectorMode = _isCollectorMode.asStateFlow()

    // Current Client selection (for client view or details)
    private val _currentClientCode = MutableStateFlow("EB011")
    val currentClientCode = _currentClientCode.asStateFlow()

    // Active client derived from current client code
    val currentClient: StateFlow<Client?> = combine(clients, _currentClientCode) { clientList, code ->
        clientList.find { it.clientCode.equals(code, ignoreCase = true) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Stamps for the currently active client
    val currentClientStamps: StateFlow<List<CollectionStamp>> = combine(allStamps, _currentClientCode) { stampList, code ->
        stampList.filter { it.clientCode.equals(code, ignoreCase = true) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Issues for the currently active client
    val currentClientIssues: StateFlow<List<CollectionIssue>> = combine(allIssues, _currentClientCode) { issueList, code ->
        issueList.filter { it.clientCode.equals(code, ignoreCase = true) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Collector Mode search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Toggle Mode
    fun toggleCollectorMode() {
        _isCollectorMode.value = !_isCollectorMode.value
    }

    fun setClientCode(code: String) {
        _currentClientCode.value = code
    }

    // --- Actions ---

    fun stampCollection(clientCode: String, stampIndex: Int, collectorName: String) {
        viewModelScope.launch {
            val exists = allStamps.value.any { it.clientCode.equals(clientCode, ignoreCase = true) && it.stampIndex == stampIndex }
            if (!exists) {
                repository.insertStamp(
                    CollectionStamp(
                        clientCode = clientCode.uppercase(),
                        stampIndex = stampIndex,
                        collectorName = collectorName
                    )
                )
            }
        }
    }

    fun removeStampCollection(clientCode: String, stampIndex: Int) {
        viewModelScope.launch {
            repository.deleteStamp(clientCode.uppercase(), stampIndex)
        }
    }

    fun clearAllStamps(clientCode: String) {
        viewModelScope.launch {
            repository.clearStampsForClient(clientCode.uppercase())
        }
    }

    fun reportIssue(clientCode: String, issueType: String, description: String) {
        viewModelScope.launch {
            repository.insertIssue(
                CollectionIssue(
                    clientCode = clientCode.uppercase(),
                    issueType = issueType,
                    description = description,
                    status = "Pendente"
                )
            )
        }
    }

    fun resolveIssue(id: Int, notes: String) {
        viewModelScope.launch {
            repository.updateIssueStatus(id, "Resolvido", notes)
        }
    }

    fun registerNewClient(
        clientCode: String,
        name: String,
        phone: String,
        address: String,
        feeType: String,
        feeAmount: Double
    ) {
        viewModelScope.launch {
            val client = Client(
                clientCode = clientCode.uppercase(),
                name = name,
                phone = phone,
                address = address,
                feeType = feeType,
                feeAmount = feeAmount,
                active = true,
                registrationDate = System.currentTimeMillis()
            )
            repository.insertClient(client)
            // Automatically select the new client
            _currentClientCode.value = clientCode.uppercase()
        }
    }

    fun updateClientDetails(client: Client) {
        viewModelScope.launch {
            repository.updateClient(client)
        }
    }
}

class EcoViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EcoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EcoViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
