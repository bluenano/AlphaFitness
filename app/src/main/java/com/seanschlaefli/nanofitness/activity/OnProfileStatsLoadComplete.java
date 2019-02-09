package com.seanschlaefli.nanofitness.activity;

import com.seanschlaefli.nanofitness.activity.model.ProfileStats;

public interface OnProfileStatsLoadComplete {
    void onProfileStatsLoadComplete(ProfileStats allTime, ProfileStats avgWeekly);
}
