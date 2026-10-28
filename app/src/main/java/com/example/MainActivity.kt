package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.data.database.AppDatabase
import com.example.data.database.InitialDataSeeder
import com.example.data.repository.InvestigationRepository
import com.example.ui.AppScreen
import com.example.ui.InvestigationViewModel
import com.example.ui.components.AppHeaderBar
import com.example.ui.screens.AuditLogScreen
import com.example.ui.screens.CaseDetailScreen
import com.example.ui.screens.CreateCaseScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExportArchiveScreen
import com.example.ui.screens.LegalRequestWizardScreen
import com.example.ui.screens.LegalStandardsGuideScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var repository: InvestigationRepository
    private lateinit var viewModel: InvestigationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = AppDatabase.getDatabase(this)
        repository = InvestigationRepository(database)
        viewModel = InvestigationViewModel(repository)

        lifecycleScope.launch {
            InitialDataSeeder.seedDatabaseIfEmpty(database)
        }

        setContent {
            MyApplicationTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DigitalEvidenceApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun DigitalEvidenceApp(viewModel: InvestigationViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val activeOfficer by viewModel.activeOfficer.collectAsState()
    val selectedCaseId by viewModel.selectedCaseId.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    if (!isAuthenticated || currentScreen == AppScreen.LOGIN) {
        LoginScreen(
            viewModel = viewModel,
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
        )
        return
    }

    // Handle Android system back gestures gracefully
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        if (currentScreen == AppScreen.CREATE_LEGAL_REQUEST && selectedCaseId != null) {
            viewModel.navigateTo(AppScreen.CASE_DETAIL, selectedCaseId)
        } else {
            viewModel.navigateTo(AppScreen.DASHBOARD)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            AppHeaderBar(
                activeOfficer = activeOfficer,
                availableOfficers = viewModel.availableOfficers,
                onSelectOfficer = { viewModel.selectRole(it) },
                onNavigateToExport = { viewModel.navigateTo(AppScreen.EXPORT_ARCHIVE, selectedCaseId) },
                onLogout = { viewModel.logout() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.LOGIN -> {
                    LoginScreen(viewModel = viewModel)
                }
                AppScreen.DASHBOARD -> {
                    DashboardScreen(
                        viewModel = viewModel,
                        activeOfficer = activeOfficer
                    )
                }
                AppScreen.CASE_DETAIL -> {
                    CaseDetailScreen(
                        caseId = selectedCaseId ?: "",
                        viewModel = viewModel,
                        activeOfficer = activeOfficer,
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                }
                AppScreen.CREATE_CASE -> {
                    CreateCaseScreen(
                        viewModel = viewModel,
                        activeOfficer = activeOfficer,
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                }
                AppScreen.CREATE_LEGAL_REQUEST -> {
                    LegalRequestWizardScreen(
                        caseId = selectedCaseId ?: "",
                        viewModel = viewModel,
                        activeOfficer = activeOfficer,
                        onBack = {
                            if (selectedCaseId != null) {
                                viewModel.navigateTo(AppScreen.CASE_DETAIL, selectedCaseId)
                            } else {
                                viewModel.navigateTo(AppScreen.DASHBOARD)
                            }
                        }
                    )
                }
                AppScreen.AUDIT_LOGS -> {
                    AuditLogScreen(
                        viewModel = viewModel,
                        activeOfficer = activeOfficer,
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                }
                AppScreen.LEGAL_STANDARDS_GUIDE -> {
                    LegalStandardsGuideScreen(
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                }
                AppScreen.EXPORT_ARCHIVE -> {
                    ExportArchiveScreen(
                        caseId = selectedCaseId,
                        viewModel = viewModel,
                        activeOfficer = activeOfficer,
                        onBack = {
                            if (selectedCaseId != null) {
                                viewModel.navigateTo(AppScreen.CASE_DETAIL, selectedCaseId)
                            } else {
                                viewModel.navigateTo(AppScreen.DASHBOARD)
                            }
                        }
                    )
                }
            }
        }
    }
}
