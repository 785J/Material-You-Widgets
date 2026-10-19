package com.material.you.widgets.core.shapes

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath

/**
 * Universal Shape Renderer
 * Renders shapes for both Compose UI and Glance widgets
 */
object ShapeRenderer {
    
    /**
     * Render a RoundedPolygon to a Bitmap
     * Used for Glance widgets (ImageProvider)
     * 
     * @param polygon The shape to render
     * @param widthPx Width in pixels
     * @param heightPx Height in pixels
     * @param color Fill color (ARGB)
     * @param stretchToFill If true, shape fills entire bounds; if false, maintains aspect ratio
     * @return Bitmap with rendered shape
     */
    fun renderToBitmap(
        polygon: RoundedPolygon,
        widthPx: Int,
        heightPx: Int,
        color: Int,
        stretchToFill: Boolean = false
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        val paint = Paint().apply {
            this.color = color
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        val path = polygon.toPath()
        val boundsArray = polygon.calculateBounds()
        val left = boundsArray[0]
        val top = boundsArray[1]
        val right = boundsArray[2]
        val bottom = boundsArray[3]
        
        val boundsWidth = right - left
        val boundsHeight = bottom - top
        val centerX = (left + right) / 2f
        val centerY = (top + bottom) / 2f
        
        val matrix = android.graphics.Matrix()
        matrix.postTranslate(-centerX, -centerY)
        
        if (stretchToFill) {
            // Stretch to fill entire bounds (non-uniform scaling)
            val scaleX = widthPx / boundsWidth
            val scaleY = heightPx / boundsHeight
            matrix.postScale(scaleX, scaleY)
            matrix.postTranslate(widthPx / 2f, heightPx / 2f)
        } else {
            // Maintain aspect ratio (uniform scaling)
            val maxDimension = maxOf(boundsWidth, boundsHeight)
            val scale = minOf(widthPx.toFloat(), heightPx.toFloat()) / maxDimension
            matrix.postScale(scale, scale)
            matrix.postTranslate(widthPx / 2f, heightPx / 2f)
        }
        
        path.transform(matrix)
        canvas.drawPath(path, paint)
        
        return bitmap
    }
    
    /**
     * Render a RoundedPolygon to an Android Path
     * Used for both Compose UI and custom drawing
     * 
     * @param polygon The shape to render
     * @param width Width in dp or pixels
     * @param height Height in dp or pixels
     * @param maintainAspectRatio If true, uses uniform scaling
     * @return Transformed Android Path
     */
    fun renderToPath(
        polygon: RoundedPolygon,
        width: Float,
        height: Float,
        maintainAspectRatio: Boolean = true
    ): Path {
        val path = polygon.toPath()
        
        val boundsArray = polygon.calculateBounds()
        val left = boundsArray[0]
        val top = boundsArray[1]
        val right = boundsArray[2]
        val bottom = boundsArray[3]
        
        val boundsWidth = right - left
        val boundsHeight = bottom - top
        val centerX = (left + right) / 2f
        val centerY = (top + bottom) / 2f
        
        val matrix = android.graphics.Matrix()
        matrix.postTranslate(-centerX, -centerY)
        
        if (maintainAspectRatio) {
            val maxDimension = maxOf(boundsWidth, boundsHeight)
            val scale = minOf(width, height) / maxDimension
            matrix.postScale(scale, scale)
        } else {
            val scaleX = width / boundsWidth
            val scaleY = height / boundsHeight
            matrix.postScale(scaleX, scaleY)
        }
        
        matrix.postTranslate(width / 2f, height / 2f)
        path.transform(matrix)
        
        return path
    }
}
