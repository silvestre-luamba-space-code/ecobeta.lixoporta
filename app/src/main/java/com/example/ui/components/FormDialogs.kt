package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.EcoGreenPrimary

@Composable
fun ReportIssueDialog(
    clientCode: String,
    onDismiss: () -> Unit,
    onSubmit: (issueType: String, description: String) -> Unit
) {
    val issueTypes = listOf(
        "Recolha Pendente",
        "Contentor Cheio",
        "Ausência do Coletor",
        "Lixo Não Coletado",
        "Outro"
    )

    var selectedType by remember { mutableStateOf(issueTypes[0]) }
    var description by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .testTag("report_issue_dialog"),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reportar Problema",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Text(
                    text = "Por favor, reporte o seu incidente para o cliente $clientCode. A nossa equipa de catadores irá verificar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Dropdown selector
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Tipo de Incidente",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedType,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("issue_type_dropdown")
                                .clickable { menuExpanded = true },
                            trailingIcon = {
                                IconButton(onClick = { menuExpanded = !menuExpanded }) {
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            issueTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        selectedType = type
                                        menuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Description Input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição do ocorrido") },
                    placeholder = { Text("Ex: O lixo está na porta desde ontem à noite, e os cães rasgaram o saco...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .testTag("issue_description_input"),
                    maxLines = 4,
                    minLines = 3
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (description.isNotBlank()) {
                                onSubmit(selectedType, description)
                            }
                        },
                        enabled = description.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                        modifier = Modifier.testTag("submit_issue_button")
                    ) {
                        Text("Registar")
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterClientDialog(
    nextSuggestedId: String,
    onDismiss: () -> Unit,
    onSubmit: (clientCode: String, name: String, phone: String, address: String, feeType: String, feeAmount: Double) -> Unit
) {
    var clientCode by remember { mutableStateOf(nextSuggestedId) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+244 ") }
    var address by remember { mutableStateOf("") }
    
    val feePlans = listOf("Diária", "Semanal", "Mensal")
    var selectedPlan by remember { mutableStateOf("Semanal") }
    
    // Auto preset values for plans in AOA
    val suggestedPrices = mapOf(
        "Diária" to 200.0,
        "Semanal" to 1500.0,
        "Mensal" to 5000.0
    )
    
    var feeAmount by remember { mutableStateOf(suggestedPrices["Semanal"]?.toString() ?: "1500") }

    // When plan changes, automatically propose the suggested price
    LaunchedEffect(selectedPlan) {
        feeAmount = suggestedPrices[selectedPlan]?.toInt()?.toString() ?: ""
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f)
                .padding(16.dp)
                .testTag("register_client_dialog"),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Aderir Novo Cliente",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Text(
                    text = "Insira os dados do agregado familiar para registar a sua morada na rota de recolha da Eco Beta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Client Code / Board Pass ID
                OutlinedTextField(
                    value = clientCode,
                    onValueChange = { clientCode = it.uppercase() },
                    label = { Text("Código de Adesão (ID)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("client_code_input"),
                    singleLine = true
                )

                // Owner Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do Responsável") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("client_name_input"),
                    singleLine = true
                )

                // Phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contacto Telefónico") },
                    placeholder = { Text("+244 9xx xxx xxx") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("client_phone_input"),
                    singleLine = true
                )

                // Address
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Endereço Completo") },
                    placeholder = { Text("Morada, Bairro, Zona/Município, Luanda") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("client_address_input"),
                    maxLines = 2
                )

                // Plan type Segmented Selector
                Column {
                    Text(
                        text = "Plano de Serviço (Taxa)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        feePlans.forEach { plan ->
                            val isSelected = selectedPlan == plan
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedPlan = plan },
                                label = { Text(plan) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chip_plan_$plan"),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EcoGreenPrimary.copy(alpha = 0.15f),
                                    selectedLabelColor = EcoGreenPrimary
                                )
                            )
                        }
                    }
                }

                // Custom Amount Input
                OutlinedTextField(
                    value = feeAmount,
                    onValueChange = { feeAmount = it },
                    label = { Text("Valor da Taxa (AOA)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("client_fee_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Regressar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val price = feeAmount.toDoubleOrNull() ?: suggestedPrices[selectedPlan] ?: 0.0
                            if (clientCode.isNotBlank() && name.isNotBlank() && phone.isNotBlank() && address.isNotBlank()) {
                                onSubmit(clientCode, name, phone, address, selectedPlan, price)
                            }
                        },
                        enabled = clientCode.isNotBlank() && name.isNotBlank() && phone.isNotBlank() && address.isNotBlank() && feeAmount.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("submit_client_button")
                    ) {
                        Text("Registar Cliente")
                    }
                }
            }
        }
    }
}

@Composable
fun ResolveIssueDialog(
    issueType: String,
    onDismiss: () -> Unit,
    onSubmit: (resolutionNotes: String) -> Unit
) {
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .testTag("resolve_issue_dialog"),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Resolver Ocorrência",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreenPrimary
                )

                Text(
                    text = "Escreva uma breve nota sobre a resolução deste incidente ($issueType):",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas de resolução") },
                    placeholder = { Text("Ex: A recolha em atraso foi concluída pelo coletor João.") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .testTag("resolution_notes_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSubmit(notes) },
                        enabled = notes.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                        modifier = Modifier.testTag("submit_resolution_button")
                    ) {
                        Text("Resolver")
                    }
                }
            }
        }
    }
}
