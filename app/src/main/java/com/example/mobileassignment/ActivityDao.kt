package com.example.myapplication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ActivityDao {

    @Insert
    suspend fun insertActivity(activity: ActivityEntity)

    @Query("SELECT * FROM activities")
    suspend fun getAllActivities(): List<ActivityEntity>

    @Query("SELECT * FROM activities WHERE date = :selectedDate")
    suspend fun getActivitiesByDate(selectedDate: String): List<ActivityEntity>

    @Query("SELECT SUM(duration) FROM activities WHERE date = :selectedDate")
    suspend fun getTotalDuration(selectedDate: String): Int?
}
