package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.Client
import com.example.data.CollectionIssue
import com.example.ui.EcoViewModel
import com.example.ui.components.*
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.EcoGreenSecondary
import com.example.ui.theme.EcoGreenTertiary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EcoMainScreen(
    viewModel: EcoViewModel,
    modifier: Modifier = Modifier
) {
    val clients by viewModel.clients.collectAsStateWithLifecycle()
    val allStamps by viewModel.allStamps.collectAsStateWithLifecycle()
    val allIssues by viewModel.allIssues.collectAsStateWithLifecycle()

    val isCollectorMode by viewModel.isCollectorMode.collectAsStateWithLifecycle()
    val currentClientCode by viewModel.currentClientCode.collectAsStateWithLifecycle()
    val currentClient by viewModel.currentClient.collectAsStateWithLifecycle()
    val currentClientStamps by viewModel.currentClientStamps.collectAsStateWithLifecycle()
    val currentClientIssues by viewModel.currentClientIssues.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    // Modals Control
    var showReportDialog by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }
    var showResolveDialogForIssue by remember { mutableStateOf<CollectionIssue?>(null) }

    // Dropdown list switcher of clients for Client View
    var clientSelectorExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("eco_main_scaffold"),
        topBar = {
            HeaderSection(
                isCollectorMode = isCollectorMode,
                onToggleCollectorMode = { viewModel.toggleCollectorMode() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (!isCollectorMode) {
                    // ==========================================
                    // CLIENT MODE VIEW
                    // ==========================================
                    // Hero Image Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("client_hero_box")
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_eco_banner),
                            contentDescription = "Eco Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                                    )
                                )
                        )
                        Text(
                            text = "Serviço Porta-a-Porta",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        )
                    }

                    // Client Selection Row (To easily check different households on simulation)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "A Visualizar Cartão de:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier
                                    .clickable { clientSelectorExpanded = true }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = currentClient?.name ?: "Nenhum Cliente Selecionado",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EcoGreenPrimary
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Escolher Cliente",
                                    tint = EcoGreenPrimary
                                )
                            }
                            DropdownMenu(
                                expanded = clientSelectorExpanded,
                                onDismissRequest = { clientSelectorExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.6f)
                            ) {
                                clients.forEach { c ->
                                    DropdownMenuItem(
                                        text = { Text("${c.clientCode} - ${c.name}") },
                                        onClick = {
                                            viewModel.setClientCode(c.clientCode)
                                            clientSelectorExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Code badge
                        Surface(
                            shape = CircleShape,
                            color = EcoGreenPrimary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Main Pass rendering
                    currentClient?.let { client ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            BoardPassCard(
                                client = client,
                                stamps = currentClientStamps
                            )
                        }

                        // Client Actions and stats
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Mini stats board
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                StatCard(
                                    title = "Selo de Recolha",
                                    value = "${currentClientStamps.size} / 25",
                                    subtext = "Passos validados",
                                    icon = Icons.Default.Refresh,
                                    modifier = Modifier.weight(1f)
                                )

                                StatCard(
                                    title = "Subscrição",
                                    value = client.feeType,
                                    subtext = "${client.feeAmount.toInt()} AOA",
                                    icon = Icons.Default.Home,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // CTA: Report Problem
                            Button(
                                onClick = { showReportDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("btn_report_issue"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Warning, contentDescription = null)
                                    Text(
                                        text = "Reportar falha ou lixo pendente",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }

                            // Historical Issues Section
                            Text(
                                text = "Histórico de Incidências",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            if (currentClientIssues.isEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Tudo limpo! Não tem ocorrências pendentes de momento.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    currentClientIssues.forEach { issue ->
                                        IssueItemRow(issue = issue)
                                    }
                                }
                            }
                        }
                    } ?: run {
                        EmptyStateLabel(message = "Por favor adicione um cliente para simular.")
                    }

                } else {
                    // ==========================================
                    // COLLECTOR / CATADOR MODE VIEW
                    // ==========================================
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Rota de Trabalho",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = EcoGreenPrimary
                                )
                                Text(
                                    text = "Gestão de recolha de resíduos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // CTA add and register new household on path
                            Button(
                                onClick = { showRegisterDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_register_new_client")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text("Novo Cliente", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        // Quick Stats counters for route
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val activeClients = clients.filter { it.active }.size
                            val pendingRouteIssues = allIssues.filter { it.status == "Pendente" }.size

                            StatCard(
                                title = "Famílias Cobertas",
                                value = "$activeClients",
                                subtext = "Recolha Ativa",
                                icon = Icons.Default.Home,
                                modifier = Modifier.weight(1.1f)
                            )

                            StatCard(
                                title = "Pendentes na Rota",
                                value = "$pendingRouteIssues",
                                subtext = "Falhas urgentes",
                                icon = Icons.Default.Warning,
                                modifier = Modifier.weight(0.9f),
                                contentColor = if (pendingRouteIssues > 0) MaterialTheme.colorScheme.error else EcoGreenPrimary
                            )
                        }

                        // Search Client Filter
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Pesquisar morada, nome ou código (e.g. EB011)...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("collector_search_bar"),
                            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Client Route List
                        val filteredClients = clients.filter {
                            it.name.contains(searchQuery, ignoreCase = true) ||
                            it.clientCode.contains(searchQuery, ignoreCase = true) ||
                            it.address.contains(searchQuery, ignoreCase = true)
                        }

                        Text(
                            text = "Lista de Clientes de Recolha (${filteredClients.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (filteredClients.isEmpty()) {
                            EmptyStateLabel(message = "Nenhum cliente coincide com a pesquisa.")
                        } else {
                            // Column representation of customers
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                filteredClients.forEach { c ->
                                    val isSelected = c.clientCode == currentClientCode
                                    val clientStamps = allStamps.filter { it.clientCode.uppercase() == c.clientCode.uppercase() }
                                    val clientPendingIssues = allIssues.filter { it.clientCode.uppercase() == c.clientCode.uppercase() && it.status == "Pendente" }

                                    ClientRouteListItem(
                                        client = c,
                                        stampsCount = clientStamps.size,
                                        pendingIssuesCount = clientPendingIssues.size,
                                        isSelected = isSelected,
                                        onClick = { viewModel.setClientCode(c.clientCode) }
                                    )

                                    // If selected, expand details right on the spot! Inside a gorgeous nested Card
                                    if (isSelected) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(
                                                    width = 2.dp,
                                                    color = EcoGreenPrimary,
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                                .background(
                                                    MaterialTheme.colorScheme.surface,
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "CARTÃO DE CONTROLO ATIVO",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Black,
                                                    color = EcoGreenPrimary
                                                )
                                                TextButton(
                                                    onClick = { viewModel.clearAllStamps(c.clientCode) }
                                                ) {
                                                    Text("Limpar Cartão", color = MaterialTheme.colorScheme.error)
                                                }
                                            }

                                            // Interactive Board Pass
                                            BoardPassCard(
                                                client = c,
                                                stamps = clientStamps,
                                                onStampClick = { idx ->
                                                    val exists = clientStamps.any { it.stampIndex == idx }
                                                    if (exists) {
                                                        viewModel.removeStampCollection(c.clientCode, idx)
                                                    } else {
                                                        viewModel.stampCollection(c.clientCode, idx, "João Catador")
                                                    }
                                                }
                                            )

                                            // Easy auto action button
                                            Button(
                                                onClick = {
                                                    // Find next unused stamp index from 0 to 24
                                                    val takenIndices = clientStamps.map { it.stampIndex }.toSet()
                                                    val nextIndex = (0 until 25).firstOrNull { !takenIndices.contains(it) }
                                                    if (nextIndex != null) {
                                                        viewModel.stampCollection(c.clientCode, nextIndex, "João Catador")
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = EcoGreenSecondary),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(44.dp)
                                                    .testTag("btn_quick_stamp"),
                                                enabled = clientStamps.size < 25
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                                                    Text("Validar Próxima Recolha Hoje", fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            // Resolve reports on the route Dialog triggers
                                            if (clientPendingIssues.isNotEmpty()) {
                                                Text(
                                                    text = "Incidentes Reportados por este Cliente:",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                                clientPendingIssues.forEach { issue ->
                                                    Card(
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                                                        ),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(12.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                    text = issue.issueType,
                                                                    style = MaterialTheme.typography.bodyMedium,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = MaterialTheme.colorScheme.onErrorContainer
                                                                )
                                                                Text(
                                                                    text = issue.description,
                                                                    style = MaterialTheme.typography.bodySmall,
                                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                                )
                                                            }
                                                            Button(
                                                                onClick = { showResolveDialogForIssue = issue },
                                                                colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                                            ) {
                                                                Text("Resolver", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // MODAL DIALOGS
            if (showReportDialog) {
                ReportIssueDialog(
                    clientCode = currentClientCode,
                    onDismiss = { showReportDialog = false },
                    onSubmit = { type, desc ->
                        viewModel.reportIssue(currentClientCode, type, desc)
                        showReportDialog = false
                    }
                )
            }

            if (showRegisterDialog) {
                // Calculate next ID code
                val nextSuggested = "EB" + String.format("%03d", clients.size + 11)
                RegisterClientDialog(
                    nextSuggestedId = nextSuggested,
                    onDismiss = { showRegisterDialog = false },
                    onSubmit = { code, n, ph, adr, plan, price ->
                        viewModel.registerNewClient(code, n, ph, adr, plan, price)
                        showRegisterDialog = false
                    }
                )
            }

            showResolveDialogForIssue?.let { issue ->
                ResolveIssueDialog(
                    issueType = issue.issueType,
                    onDismiss = { showResolveDialogForIssue = null },
                    onSubmit = { notes ->
                        viewModel.resolveIssue(issue.id, notes)
                        showResolveDialogForIssue = null
                    }
                )
            }
        }
    }
}

// ==========================================
// DETAILED COMPOSE SUBCOMPOENTS
// ==========================================

@Composable
fun HeaderSection(
    isCollectorMode: Boolean,
    onToggleCollectorMode: () -> Unit
) {
    Surface(
        color = EcoGreenPrimary,
        tonalElevation = 4.dp,
        modifier = Modifier.testTag("app_header_bar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 12.dp) // notch safe padding
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title Brand
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reciclagem",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "EcoBeta Lixo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                // Smooth styled Persona Switch Pill button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier
                        .clickable { onToggleCollectorMode() }
                        .testTag("btn_toggle_mode")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isCollectorMode) Icons.Default.Build else Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isCollectorMode) "Modo Coletor" else "Modo Cliente",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentColor: Color = EcoGreenPrimary
) {
    Card(
        modifier = modifier.testTag("stat_card_$title"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = contentColor
            )
            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun IssueItemRow(issue: CollectionIssue) {
    val isResolved = issue.status == "Resolvido"
    val cardColor = if (isResolved) {
        Color(0xFFE8F5E9) // soft Light green
    } else {
        Color(0xFFFFEBEE) // soft Light red
    }
    val contentColor = if (isResolved) EcoGreenPrimary else MaterialTheme.colorScheme.error

    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedDate = sdf.format(Date(issue.reportedAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("issue_row_${issue.id}"),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = issue.issueType,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )

                // Badge Status
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isResolved) EcoGreenPrimary else MaterialTheme.colorScheme.error,
                ) {
                    Text(
                        text = issue.status.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = issue.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Reportado em: $formattedDate",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

            if (isResolved && issue.resolutionNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = EcoGreenPrimary.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EcoGreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Column {
                        Text(
                            text = "Resposta do Coletor:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary
                        )
                        Text(
                            text = issue.resolutionNotes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClientRouteListItem(
    client: Client,
    stampsCount: Int,
    pendingIssuesCount: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("client_item_${client.clientCode}")
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) EcoGreenPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 1.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) EcoGreenPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = client.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Zona: ${client.address.split(",").lastOrNull()?.trim() ?: client.address}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = EcoGreenPrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = client.clientCode,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = EcoGreenPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stamp mini indicator progress
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = EcoGreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "$stampsCount / 25 recolhas",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // If they have pending issues, show a screaming badge
                if (pendingIssuesCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "$pendingIssuesCount Alerta",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EcoGreenSecondary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Pago: Plano ${client.feeType}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateLabel(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
