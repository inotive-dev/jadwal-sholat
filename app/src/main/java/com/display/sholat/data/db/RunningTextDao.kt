package com.display.sholat.data.db

import androidx.room.*
import com.display.sholat.data.entity.RunningText

/**
 * Created by Jumadi Janjaya date on 01/10/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

@Dao
sealed interface RunningTextDao {

    @Query("SELECT * FROM runningText")
    suspend fun getAll(): List<RunningText>

    @Query("SELECT * FROM runningText where isShowInIqomah=0")
    suspend fun getAllWith(): List<RunningText>

    @Query("SELECT * FROM runningText where isShowInIqomah=1")
    suspend fun getAllWithIqomah(): List<RunningText>

    @Query("DELETE FROM runningText where id in (:list)")
    suspend fun deletes(list: Array<Int>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserts(list: List<RunningText>)

    @Update
    suspend fun updates(list: Array<RunningText>)

    @Query("SELECT * FROM runningText WHERE id=:id")
    suspend fun get(id: Int): RunningText?

    @Query("SELECT * FROM runningText WHERE current=1")
    suspend fun getCurrent(): RunningText?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(runningText: RunningText) : Long

    @Update
    suspend fun update(runningText: RunningText)

    @Delete
    suspend fun delete(runningText: RunningText)

    @Query("DELETE FROM runningText WHERE id=:id")
    suspend fun delete(id: Int)
}