package com.example.healthconnectsample.presentation.screen.bloodpressure

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.permission.HealthPermission
import com.example.healthconnectsample.data.BloodPressureData
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

/**
 * Shows a week's worth of blood pressure data.
 */
@Composable
fun BloodPressureScreen(
    permissions: Set<HealthPermission>,
    permissionsGranted: Boolean,
    readingsList: List<BloodPressureData>,
    uiState: BloodPressureViewModel.UiState,
    onError: (Throwable?) -> Unit = {},
    onPermissionsResult: () -> Unit = {},
    onPermissionsLaunch: (Set<HealthPermission>) -> Unit = {}
) {
    // Remember the last error ID, such that it is possible to avoid re-launching the error
    // notification for the same error when the screen is recomposed, or configuration changes etc.
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }

    LaunchedEffect(uiState) {
        // If the initial data load has not taken place, attempt to load the data.
        if (uiState is BloodPressureViewModel.UiState.Uninitialized) {
            onPermissionsResult()
        }

        // The [BloodPressureViewModel.UiState] provides details of whether the last action
        // was a success or resulted in an error. Where an error occurred, for example in reading
        // and writing to Health Connect, the user is notified, and where the error is one that can
        // be recovered from, an attempt to do so is made.
        if (uiState is BloodPressureViewModel.UiState.Error && errorId.value != uiState.uuid) {
            onError(uiState.exception)
            errorId.value = uiState.uuid
        }
    }

    if (uiState != BloodPressureViewModel.UiState.Uninitialized) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (!permissionsGranted) {
                item {
                    Button(
                        onClick = {
//                            Log.i("1", permissions.map { it.toString() }.toString())
                            onPermissionsLaunch(permissions)
                        }
                    ) {
                        Text(text = stringResource(com.example.healthconnectsample.R.string.permissions_button_label))
                    }
                }
            } else {
                val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                items(readingsList) { reading ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .padding(2.dp, 2.dp)
                                .height(16.dp)
                                .width(16.dp),
                            painter = rememberDrawablePainter(drawable = reading.sourceAppInfo?.icon),
                            contentDescription = "App Icon"
                        )
                        Text(
                            text = "%.0f / %.0f"
                                .format(reading.systolic.inMillimetersOfMercury, reading.diastolic.inMillimetersOfMercury)
                        )
                        Text(text = formatter.format(reading.time))
                    }
                }
            }
        }
    }

}