package com.example.healthconnectsample.data

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.units.Pressure
import java.time.ZonedDateTime

/**
 * Represents data for a given [BloodPressureRecord].
 */
data class BloodPressureData(
    val id: String,
    val diastolic: Pressure,
    val systolic: Pressure,
    val time: ZonedDateTime,
    val sourceAppInfo: HealthConnectAppInfo?
)