package com.rk.vegetableshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*

data class Party(
    val name: String,
    var balance: Double
)

data class Entry(
    val id: Long,
    val date: Long,
    val party: String,
    val amount: Double,
    val paid: Double,
    val isPurchase: Boolean
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppScreen()
                }
            }
        }
    }
}

@Composable
fun AppScreen() {

    var parties by remember {
        mutableStateOf(
            mutableListOf(
                Party("Default Supplier", 0.0),
                Party("Local Market Supplier", 0.0)
            )
        )
    }

    var entries by remember { mutableStateOf(mutableListOf<Entry>()) }

    var showForm by remember { mutableStateOf(false) }
    var selectedParty by remember { mutableStateOf(parties.first().name) }
    var amount by remember { mutableStateOf("") }
    var paidNow by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {

        Text("RK Vegetable Shop", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(10.dp))

        Card(elevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {

                val totalOutstanding = parties.sumOf { it.balance }
                val totalSales = entries.filter { !it.isPurchase }.sumOf { it.amount }

                Text("Total Outstanding: ₹${String.format("%.2f", totalOutstanding)}")
                Text("Total Sales Recorded: ₹${String.format("%.2f", totalSales)}")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { showForm = !showForm }) {
            Text(if (showForm) "Hide Entry Form" else "Add Daily Entry")
        }

        if (showForm) {

            Spacer(modifier = Modifier.height(10.dp))

            Text("Select Party")
            Spacer(modifier = Modifier.height(4.dp))

            var dropDownExpanded by remember { mutableStateOf(false) }

            Box {
                Button(onClick = { dropDownExpanded = true }) {
                    Text(selectedParty)
                }

                DropdownMenu(
                    expanded = dropDownExpanded,
                    onDismissRequest = { dropDownExpanded = false }
                ) {
                    parties.forEach { p ->
                        DropdownMenuItem(onClick = {
                            selectedParty = p.name
                            dropDownExpanded = false
                        }) {
                            Text(p.name)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount (₹)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = paidNow,
                onValueChange = { paidNow = it },
                label = { Text("Paid Now (₹)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row {
                Button(onClick = {

                    val a = amount.toDoubleOrNull() ?: 0.0
                    val p = paidNow.toDoubleOrNull() ?: 0.0
                    val id = entries.size + 1L

                    entries.add(
                        0,
                        Entry(id, System.currentTimeMillis(), selectedParty, a, p, true)
                    )

                    parties.find { it.name == selectedParty }?.balance = parties
                        .find { it.name == selectedParty }!!.balance + (a - p)

                    amount = ""
                    paidNow = ""

                }) {
                    Text("Save Purchase")
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(onClick = {

                    val a = amount.toDoubleOrNull() ?: 0.0
                    val p = paidNow.toDoubleOrNull() ?: 0.0
                    val id = entries.size + 1L

                    entries.add(
                        0,
                        Entry(id, System.currentTimeMillis(), selectedParty, a, p, false)
                    )

                    parties.find { it.name == selectedParty }?.balance =
                        parties.find { it.name == selectedParty }!!.balance - p

                    amount = ""
                    paidNow = ""

                }) {
                    Text("Save Payment")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Parties", style = MaterialTheme.typography.h6)

        LazyColumn {
            items(parties) { p ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp), elevation = 2.dp) {

                    Row(modifier = Modifier.padding(10.dp)) {
                        Column {
                            Text(p.name)
                            Text("Balance: ₹${String.format("%.2f", p.balance)}")
                        }
                    }
                }
            }
        }
    }
}
