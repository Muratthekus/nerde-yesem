package com.thekusch.nerdeyesem

import android.graphics.Bitmap
import android.view.View

object TakeScreenshot {
    private fun takeScreenshot(v: View): Bitmap {
        v.setDrawingCacheEnabled(true)
        v.buildDrawingCache(true)
        val bitmap = Bitmap.createBitmap(v.getDrawingCache())
        v.setDrawingCacheEnabled(false)
        return bitmap
    }
    fun takeScreenshotOfRootView(v:View):Bitmap{
        return takeScreenshot(v.rootView)
    }

}