package com.seanschlaefli.nanofitness.dao;

import com.seanschlaefli.nanofitness.model.Workout;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface WorkoutDao {

    @Insert
    long[] insertAll(Workout... workouts);

    @Insert
    long insert(Workout workout);

    @Delete
    void delete(Workout workout);

    @Update
    void update(Workout... workouts);

    @Query("SELECT * FROM workout")
    LiveData<List<Workout>> getAll();

    @Query("SELECT * FROM workout")
    List<Workout>getAllSync();

    @Query("SELECT * FROM workout WHERE id in (:workoutIds)")
    LiveData<List<Workout>> loadAllByIds(int[] workoutIds);

    @Query("SELECT * FROM workout where id = (:workoutId)")
    LiveData<Workout> loadById(final int workoutId);

    @Query("SELECT * FROM workout where id = (:workoutId)")
    Workout loadByIdSync(final int workoutId);


}
