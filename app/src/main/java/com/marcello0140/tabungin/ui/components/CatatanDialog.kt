package com.marcello0140.tabungin.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marcello0140.tabungin.model.TabunganHistory
import com.marcello0140.tabungin.ui.screen.formatRupiah
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun DialogTambahWishlist(
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Wishlist Baru") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Wishlist") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() }) targetAmount = it
                    },
                    label = { Text("Target Tabungan") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amountInt = targetAmount.toIntOrNull() ?: 0
                if (name.isNotBlank() && amountInt > 0) {
                    onConfirm(name, amountInt)
                }
            }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}


@Composable
fun DialogEditWishlist(
    initialName: String,
    initialTargetAmount: String,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    var targetAmount by remember(initialTargetAmount) { mutableStateOf(initialTargetAmount) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Wishlist") },
        text = {
            Column {
                // Input nama wishlist
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Wishlist") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Input target amount (angka)
                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() }) targetAmount = it
                    },
                    label = { Text("Target Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val targetValue = targetAmount.toIntOrNull() ?: 0
                if (name.isNotBlank() && targetValue > 0) {
                    onConfirm(name, targetValue)
                    onDismiss()
                }
            }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}


@Composable
fun DialogDeleteWishlist(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hapus Wishlist") },
        text = { Text("Apakah kamu yakin ingin menghapus wishlist ini? Tindakan ini tidak dapat dibatalkan.") },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text("Ya, Hapus", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun DialogTambahRiwayat(
    onDismiss: () -> Unit,
    onConfirm: (Int, Boolean) -> Unit,
    currentAmount: Int,
    initialNominal: String = "",
    initialIsPenambahan: Boolean = true
) {
    var nominal by remember { mutableStateOf(initialNominal) }
    var isPenambahan by remember { mutableStateOf(initialIsPenambahan) }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah / Edit Catatan Tabungan") },
        text = {
            Column {
                // Input nominal angka
                OutlinedTextField(
                    value = nominal,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() }) nominal = it
                    },
                    label = { Text("Nominal") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pilihan penambahan/pengurangan (segmented button)
                SegmentedButton(
                    selected = isPenambahan,
                    onSelectedChange = { isPenambahan = it }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val nominalValue = nominal.toIntOrNull() ?: 0
                when {
                    nominalValue <= 0 -> {
                        Toast.makeText(context, "Nominal harus lebih besar dari 0", Toast.LENGTH_SHORT).show()
                    }
                    !isPenambahan && nominalValue > currentAmount -> {
                        Toast.makeText(context, "Pengurangan tidak boleh lebih besar dari jumlah tabungan", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        onConfirm(nominalValue, isPenambahan)
                        onDismiss()
                    }
                }
            }) {
                Icon(Icons.Default.Save, contentDescription = "Simpan")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun DialogRiwayat(
    historyItem: TabunganHistory,
    onDismiss: () -> Unit,
    onEdit: (historyId: Int) -> Unit,
    onDelete: (historyId: Int) -> Unit
) {
    val isPenambahan = historyItem.isPenambahan
    val nominalColor = if (isPenambahan) Color(0xFF2E7D32) else Color(0xFFC62828)
    val jenisText = if (isPenambahan) "Penambahan" else "Pengurangan"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Detail Catatan", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = jenisText,
                    color = nominalColor,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = formatRupiah(historyItem.nominal),
                    color = nominalColor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tanggal: ${formatDateToReadable(historyItem.tanggal)}",
                    fontStyle = FontStyle.Italic
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { onEdit(historyItem.id.toInt()) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
                TextButton(onClick = { onDelete(historyItem.id.toInt()) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

fun formatDateToReadable(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yy - HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}


@Composable
fun PreviewDialogTambahCatatan() {
    var showDialog by remember { mutableStateOf(true) }
    val currentAmount by remember { mutableIntStateOf(100000) } // Contoh currentAmount

    if (showDialog) {
        DialogTambahRiwayat(
            onDismiss = { showDialog = false },
            onConfirm = { nominal, isPenambahan ->
                // Handle confirm logic here
                // Misalnya, update tabungan history atau state
                println("Nominal: $nominal, Penambahan: $isPenambahan")
                showDialog = false // Menutup dialog setelah konfirmasi
            },
            currentAmount = currentAmount
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    PreviewDialogTambahCatatan()
}


//@Preview(showBackground = true)
//@Composable
//fun PreviewDialogCatatan() {
//    MaterialTheme {
//        DialogRiwayat (
//            historyItem = TabunganHistory(
//                id = 1,
//                nominal = 50000,
//                isPenambahan = true,
//                tanggal = "2025-05-08 10:45:00"
//            ),
//            onDismiss = {},
//            onEdit = { println("Edit clicked") },
//            onDelete = { println("Delete clicked") }
//        )
//    }
//}
