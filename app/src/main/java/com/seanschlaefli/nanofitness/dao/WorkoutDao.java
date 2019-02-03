package com.seanschlaefli.nanofitness.dao;

import com.seanschlaefli.nanofitness.model.Workout;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface WorkoutDao {

    @Insert
    List<Long> insertAll(Workout... workouts);

    @Insert
    long insert(Workout workout);

    @Delete
    void delete(Workout workout);

    @Update
    void update(Workout... workouts);

    @Query("SELECT * FROM workout")
    List<Workout> getAll();

    @Query("SELECT * FROM workout WHERE id in (:workoutIds)")
    List<Workout> loadAllByIds(int[] workoutIds);

    @Query("SELECt * FROM workout where id = (:workoutId)")
    Workout loadById(final int workoutId);


}
