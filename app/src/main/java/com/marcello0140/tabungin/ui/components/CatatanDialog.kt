package com.marcello0140.tabungin.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
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
fun DialogTambahCatatan(
    onDismiss: () -> Unit,
    onConfirm: (Int, Boolean) -> Unit,
    currentAmount: Int // Menambahkan currentAmount untuk validasi pengurangan
) {
    var nominal by remember { mutableStateOf("") }
    var isPenambahan by remember { mutableStateOf(true) }

    // Mengambil context menggunakan LocalContext
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Catatan Tabungan") },
        text = {
            Column {
                OutlinedTextField(
                    value = nominal,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            nominal = it
                        }
                    },
                    label = { Text("Nominal") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Segmented button untuk Penambahan/Pengurangan
                SegmentedButton(
                    selected = isPenambahan,
                    onSelectedChange = { isPenambahan = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Validasi input
                    val nominalValue = nominal.toIntOrNull() ?: 0

                    // Sanity Check
                    when {
                        nominalValue <= 0 -> {
                            Toast.makeText(context, "Nominal harus lebih besar dari 0", Toast.LENGTH_SHORT).show()
                        }
                        !isPenambahan && nominalValue > currentAmount -> {
                            Toast.makeText(context, "Pengurangan tidak boleh lebih besar dari jumlah tabungan", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            // Validasi lolos, kirim data ke onConfirm
                            onConfirm(nominalValue, isPenambahan)
                            onDismiss() // Tutup dialog setelah konfirmasi
                        }
                    }
                },
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save")
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
fun DialogEditCatatan(
    historyItem: TabunganHistory,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isPenambahan = historyItem.isPenambahan
    val nominalColor = if (isPenambahan) Color(0xFF2E7D32) else Color(0xFFC62828)
    val jenisText = if (isPenambahan) "Penambahan" else "Pengurangan"

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = {
            Text(
                text = "Detail Catatan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Jenis
                Text(
                    text = jenisText,
                    color = nominalColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Nominal Besar di Tengah
                Text(
                    text = formatRupiah(historyItem.nominal),
                    color = nominalColor,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tanggal
                Text(
                    text = "Tanggal: ${formatDateToReadable(historyItem.tanggal)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tombol Aksi
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Tombol Edit
                    TextButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit")
                    }

                    // Tombol Hapus
                    TextButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hapus", color = Color.Red)
                    }
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

@Composable
fun SegmentedButton(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(8.dp)
            )
    ) {
        val selectedColor = MaterialTheme.colorScheme.primary
        val unselectedColor = MaterialTheme.colorScheme.surface

        // Tombol "+"
        Box(
            modifier = Modifier
                .weight(1f)
                .background(if (selected) selectedColor else unselectedColor)
                .clickable { onSelectedChange(true) }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }

        // Tombol "–"
        Box(
            modifier = Modifier
                .weight(1f)
                .background(if (!selected) selectedColor else unselectedColor)
                .clickable { onSelectedChange(false) }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "–",
                color = if (!selected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
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
        DialogTambahCatatan(
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


@Preview(showBackground = true)
@Composable
fun PreviewDialogEditCatatan() {
    MaterialTheme {
        DialogEditCatatan(
            historyItem = TabunganHistory(
                nominal = 50000,
                isPenambahan = true,
                tanggal = "2025-05-08 10:45:00"
            ),
            onDismiss = {},
            onEdit = { println("Edit clicked") },
            onDelete = { println("Delete clicked") }
        )
    }
}
