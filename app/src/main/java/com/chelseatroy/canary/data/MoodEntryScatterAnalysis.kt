package com.chelseatroy.canary.data

class MoodEntryScatterAnalysis() {
    fun getYPositionFor(moodEntry: MoodEntry): Float {
        var height = 0f
        when (moodEntry.mood) {
            Mood.UPSET -> height = 1f
            Mood.DOWN -> height = 2f
            Mood.NEUTRAL -> height = 3f
            Mood.COPING -> height = 4f
            Mood.ELATED -> height = 5f
        }
        return height
    }

    fun getXPositionsFor(moodEntries: List<MoodEntry>): List<Float> {
        val latestPoint = moodEntries.last().loggedAt
        val earliestPoint = moodEntries.first().loggedAt
        val diff = latestPoint - earliestPoint

        if (diff == 0L) {
            return arrayListOf(0f) // If we don't do this, a singleton list with 0L in it becomes [NaN] for some reason
        } else {
            return moodEntries.map { (it.loggedAt.toFloat() - earliestPoint) / diff } as ArrayList<Float>
        }
    }

    fun commentOn(moodEntries: List<MoodEntry>): String {
        val averageMoodValues = getAverageMoodValues(moodEntries)
        val singleOutlier = findSingleOutlier(moodEntries)

            // Encouragement check
        if (notEnoughEntries(moodEntries)) {
            return "It's hard to glean much from just a few mood entries. Log more throughout the coming week to learn more about yourself!"
        }

        // Improving check
        if (averageMoodValues.first < averageMoodValues.second && singleOutlier == null) {
            return "Looks like your mood has been improving throughout the week! Take stock of what made you feel better, and stay smiling :)"
        }

        // Declining check
        if (averageMoodValues.first > averageMoodValues.second && singleOutlier == null) {
            return "Your mood has been on a bit of a downturn this week. It's a good time to stay mindful of the things you enjoy and the things you don't."
        }

        // Outlier check
        if (singleOutlier != null) {
            return "Seems like you felt ${singleOutlier} on a particular day this week. What happened that made you feel differently from the rest of the week?"
        }

        // Stable check
        return "It looks like your mood has been pretty stable. Do you want that to change, or are you happy with how things are going right now?"
    }

    private fun notEnoughEntries(moodEntries: List<MoodEntry>): Boolean {
        return moodEntries.size < 5
    }

    private fun getAverageMoodValues(moodEntries: List<MoodEntry>): Pair<Double, Double> {
        val numEntries = moodEntries.size
        val moodValues = moodEntries.map { moodEntry: MoodEntry -> getYPositionFor(moodEntry) }
        val firstHalfAverage = moodValues.subList(0, numEntries/2 + 1).average()
        val secondHalfAverage = moodValues.subList(numEntries/2, numEntries).average()
        return Pair(firstHalfAverage, secondHalfAverage)
    }

    private fun findSingleOutlier(moodEntries: List<MoodEntry>): Mood? {
        val moodValues = moodEntries.map { moodEntry: MoodEntry -> getYPositionFor(moodEntry) }
        val average = moodValues.average()

        val nonOutliersFromAverage =
            moodValues.filter { it >= average - 2 && it <= average + 2 }
        val outliersFromAverage =
            moodValues.filter { it < average - 2 || it > average + 2 }

        if (outliersFromAverage.size == 1) {
            nonOutliersFromAverage.forEach {
                if (outliersFromAverage[0] >= it - 1 && outliersFromAverage[0] <= it + 1) {
                    return null
                }
            }
            return getMoodFor(outliersFromAverage[0])
        } else {
            return null
        }
    }

    private fun getMoodFor(yPosition: Float): Mood {
        var mood: Mood = Mood.NEUTRAL
        when (yPosition) {
            1f -> mood = Mood.UPSET
            2f -> mood = Mood.DOWN
            3f -> mood = Mood.NEUTRAL
            4f -> mood = Mood.COPING
            5f -> mood = Mood.ELATED
        }
        return mood
    }
}