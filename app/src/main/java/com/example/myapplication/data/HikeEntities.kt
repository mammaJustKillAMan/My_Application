package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

/**
 * Represents a completed hiking session.
 *
 * Stores aggregated statistics and metadata describing
 * a single hike.
 *
 * @property id Auto-generated primary key.
 * @property date Timestamp representing when the hike occurred.
 * @property durationSeconds Total duration of the hike in seconds.
 * @property maxAltitude Highest altitude reached during the hike.
 * @property avgAscentRate Average ascent rate in meters per minute.
 * @property avgDescentRate Average descent rate in meters per minute.
 */
@Entity(tableName = "hikes")
data class Hike(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long, // Timestamp
    val durationSeconds: Long,
    val maxAltitude: Double,
    val avgAscentRate: Double, // m/min
    val avgDescentRate: Double // m/min
)

/**
 * Represents a single recorded point along a hiking route.
 *
 * Each route point belongs to a parent [Hike] and captures
 * location, altitude, and altitude risk at a specific moment in time.
 *
 * Route points are automatically deleted when their parent hike
 * is removed.
 *
 * @property pointId Auto-generated primary key.
 * @property hikeId Foreign key referencing the parent [Hike].
 * @property latitude Latitude coordinate.
 * @property longitude Longitude coordinate.
 * @property altitude Altitude in meters at this point.
 * @property timestamp Timestamp when the point was recorded.
 * @property riskLevelName Name of the [RiskLevel] at this point
 * (e.g. "LOW", "MODERATE", "HIGH").
 */
@Entity(
    tableName = "route_points",
    foreignKeys = [ForeignKey(
        entity = Hike::class,
        parentColumns = ["id"],
        childColumns = ["hikeId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class RoutePoint(
    @PrimaryKey(autoGenerate = true) val pointId: Long = 0,
    val hikeId: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val timestamp: Long,
    val riskLevelName: String
)