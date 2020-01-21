package com.example.trivia.util;

import android.app.Activity;
import android.content.SharedPreferences;

public class Prefs {
    private SharedPreferences preferences;

    public Prefs(Activity activity) {
        this.preferences = activity.getPreferences(activity.MODE_PRIVATE);
    }

    public void saveHighScore(int score)
    {
        int currentscore = score;

        int last_score = preferences.getInt("high_score",0);

        if (currentscore>last_score)
        {
            //we have a new high score
            preferences.edit().putInt("high_score",currentscore).apply();
        }
    }
    public int getHighScore()
    {
        return preferences.getInt("high_score",0);
    }
    public void setState(int index)
    {
        preferences.edit().putInt("index_state",index).apply();
    }

    public int getState() {
        return preferences.getInt("index_state",0);

    }


}
