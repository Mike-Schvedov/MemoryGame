package com.mikeschvedov.memorygame.utils

import android.widget.Button
import androidx.constraintlayout.helper.widget.Flow

fun Flow.referencedViews(): List<Button> {
    val views = mutableListOf<Button>()
    for (id in this.referencedIds) {
        val view = this.rootView.findViewById<Button>(id)
        if (view != null) views.add(view)
    }
    return views
}
//TODO new extention ViewGroup.
// find only visible buttons / mapNotNull (Day18)