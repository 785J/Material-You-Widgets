package com.material.you.widgets.core.shapes

/**
 * Universal Corner Rounding Definition
 * Defines the amount and quality of rounding around a given vertex of a shape
 * 
 * @param radius The radius of the circle which forms the basis of the rounding. Must be >= 0.
 * @param smoothing The amount by which the curve is extended from the circular arc to the edge. Must be in range [0, 1].
 */
data class ShapeRounding(
    val radius: Float = 0f,
    val smoothing: Float = 0f
) {
    init {
        require(radius >= 0) { "radius has to be greater than or equal to zero" }
        require(smoothing in 0f..1f) { "smoothing has to be in range [0, 1]" }
    }
    
    companion object {
        val UNROUNDED = ShapeRounding()
    }
}
