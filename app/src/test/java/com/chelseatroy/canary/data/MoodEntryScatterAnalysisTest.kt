package com.chelseatroy.canary.data

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MoodEntryScatterAnalysisTest {
    lateinit var systemUnderTest: MoodEntryScatterAnalysis

    @Before
    fun setUp() {
        systemUnderTest = MoodEntryScatterAnalysis()
    }

    @Test
    fun arrangesMoodsVertically_basedOnMoodHappiness() {
        assertEquals(1f, systemUnderTest.getYPositionFor(MoodEntry(Mood.UPSET)))
        assertEquals(2f, systemUnderTest.getYPositionFor(MoodEntry(Mood.DOWN)))
        assertEquals(3f, systemUnderTest.getYPositionFor(MoodEntry(Mood.NEUTRAL)))
        assertEquals(4f, systemUnderTest.getYPositionFor(MoodEntry(Mood.COPING)))
        assertEquals(5f, systemUnderTest.getYPositionFor(MoodEntry(Mood.ELATED)))
    }

    @Test
    fun arrangesMoodsHorizontally_proportionalToWhenTheyWereLogged() {
        val regularlySpacedMoodEntries = arrayListOf<MoodEntry>(
            MoodEntry(Mood.ELATED, 1L, "Note", "EATING"),
            MoodEntry(Mood.ELATED, 2L, "Note", "EATING"),
            MoodEntry(Mood.ELATED, 3L, "Note", "EATING"),
            MoodEntry(Mood.ELATED, 4L, "Note", "EATING"),
            MoodEntry(Mood.ELATED, 5L, "Note", "EATING")
        )

        assertEquals(arrayListOf(0.0f, 0.25f, 0.5f, 0.75f, 1.0f), systemUnderTest.getXPositionsFor(regularlySpacedMoodEntries))
    }

    @Test
    fun comments_whenThereArentManyMoodEntries_encourageMoreMoodLogging() {
        val lessThanFiveMoodEntries = arrayListOf<MoodEntry>(
            MoodEntry(Mood.ELATED, 1L, "Note", "EATING"),
            MoodEntry(Mood.ELATED, 2L, "Note", "EATING"),
            MoodEntry(Mood.ELATED, 3L, "Note", "EATING"),
            MoodEntry(Mood.ELATED, 4L, "Note", "EATING")
        )
        val expectedEncouragingComment = "It's hard to glean much from just a few mood entries. Log more throughout the coming week to learn more about yourself!"
        assertEquals(expectedEncouragingComment, systemUnderTest.commentOn(lessThanFiveMoodEntries))

        val atLeastFiveMoodEntries = arrayListOf<MoodEntry>(
            MoodEntry(Mood.ELATED, 1L, "Note", "EATING"),
            MoodEntry(Mood.ELATED, 2L, "Note", "EATING"),
            MoodEntry(Mood.ELATED, 3L, "Note", "EATING"),
            MoodEntry(Mood.ELATED, 4L, "Note", "EATING"),
            MoodEntry(Mood.ELATED, 5L, "Note", "EATING")
        )
        val expectedStableComment = "It looks like your mood has been pretty stable. Do you want that to change, or are you happy with how things are going right now?"
        assertEquals(expectedStableComment, systemUnderTest.commentOn(atLeastFiveMoodEntries))
    }

    @Test
    fun comments_whenMoodIsImproving_mentionThatMoodIsImproving() {
        val improvingMoodEntries = arrayListOf<MoodEntry>(
            MoodEntry(Mood.UPSET, 1L, "Note", "EATING"),
            MoodEntry(Mood.DOWN, 2L, "Note", "EATING"),
            MoodEntry(Mood.COPING, 3L, "Note", "EATING"),
            MoodEntry(Mood.NEUTRAL, 4L, "Note", "EATING"),
            MoodEntry(Mood.COPING, 5L, "Note", "EATING")
        )
        val expectedImprovingComment = "Looks like your mood has been improving throughout the week! Take stock of what made you feel better, and stay smiling :)"
        assertEquals(expectedImprovingComment, systemUnderTest.commentOn(improvingMoodEntries))
    }

    @Test
    fun comments_whenMoodIsDeclining_mentionThatMoodIsDeclining() {
        val decliningMoodEntries = arrayListOf<MoodEntry>(
            MoodEntry(Mood.ELATED, 1L, "Note", "EATING"),
            MoodEntry(Mood.ELATED, 2L, "Note", "EATING"),
            MoodEntry(Mood.ELATED, 3L, "Note", "EATING"),
            MoodEntry(Mood.UPSET, 4L, "Note", "EATING"),
            MoodEntry(Mood.DOWN, 5L, "Note", "EATING")
        )
        val expectedDecliningComment = "Your mood has been on a bit of a downturn this week. It's a good time to stay mindful of the things you enjoy and the things you don't."
        assertEquals(expectedDecliningComment, systemUnderTest.commentOn(decliningMoodEntries))
    }

    @Test
    fun comments_whenThereIsOneOutlierMood_mentionAnyNotesFromThatMood() {
        val outlierMoodEntries = arrayListOf<MoodEntry>(
            MoodEntry(Mood.ELATED, 1L, "Note", "EATING"),
            MoodEntry(Mood.ELATED, 2L, "Note", "EATING"),
            MoodEntry(Mood.UPSET, 3L, "Note", "EATING"),
            MoodEntry(Mood.ELATED, 4L, "Note", "EATING"),
            MoodEntry(Mood.ELATED, 5L, "Note", "EATING")
        )
        val expectedOutlierComment = "Seems like you felt UPSET on a particular day this week. What happened that made you feel differently from the rest of the week?"
        assertEquals(expectedOutlierComment, systemUnderTest.commentOn(outlierMoodEntries))
    }


}