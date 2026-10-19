package com.material.you.widgets.core.shapes

import kotlin.math.*

/**
 * Universal Cubic Bézier Curve
 * Holds anchor and control point data for a single cubic Bézier curve
 */
data class ShapeCubic(
    val anchor0X: Float,
    val anchor0Y: Float,
    val control0X: Float,
    val control0Y: Float,
    val control1X: Float,
    val control1Y: Float,
    val anchor1X: Float,
    val anchor1Y: Float
) {
    
    val points: FloatArray
        get() = floatArrayOf(
            anchor0X, anchor0Y,
            control0X, control0Y,
            control1X, control1Y,
            anchor1X, anchor1Y
        )
    
    companion object {
        private const val DISTANCE_EPSILON = 1e-5f
        
        /**
         * Generates a bezier curve that is a straight line between the given anchor points
         */
        fun straightLine(x0: Float, y0: Float, x1: Float, y1: Float): ShapeCubic {
            return ShapeCubic(
                x0, y0,
                lerp(x0, x1, 1f / 3f),
                lerp(y0, y1, 1f / 3f),
                lerp(x0, x1, 2f / 3f),
                lerp(y0, y1, 2f / 3f),
                x1, y1
            )
        }
        
        /**
         * Generates a bezier curve that approximates a circular arc
         */
        fun circularArc(
            centerX: Float,
            centerY: Float,
            x0: Float,
            y0: Float,
            x1: Float,
            y1: Float
        ): ShapeCubic {
            val p0d = directionVector(x0 - centerX, y0 - centerY)
            val p1d = directionVector(x1 - centerX, y1 - centerY)
            val rotatedP0 = p0d.rotate90()
            val rotatedP1 = p1d.rotate90()
            val clockwise = rotatedP0.dotProductXY(x1 - centerX, y1 - centerY) >= 0
            val cosa = p0d.dotProduct(p1d)
            
            // p0 ~= p1
            if (cosa > 0.999) {
                return straightLine(x0, y0, x1, y1)
            }
            
            val k = distance(x0 - centerX, y0 - centerY) *
                    4f / 3f *
                    (sqrt(2 * (1 - cosa)) - sqrt(1 - cosa * cosa)) /
                    (1 - cosa) *
                    (if (clockwise) 1 else -1)
            
            return ShapeCubic(
                x0, y0,
                x0 + rotatedP0.x * k,
                y0 + rotatedP0.y * k,
                x1 - rotatedP1.x * k,
                y1 - rotatedP1.y * k,
                x1, y1
            )
        }
        
        fun empty(x0: Float, y0: Float) = ShapeCubic(x0, y0, x0, y0, x0, y0, x0, y0)
        
        // Helper functions
        private fun distance(x: Float, y: Float) = sqrt(x * x + y * y)
        
        private fun directionVector(x: Float, y: Float): ShapePoint {
            val d = distance(x, y)
            require(d > 0) { "Required distance greater than zero." }
            return ShapePoint(x / d, y / d)
        }
    }
    
    /**
     * Returns a point on the curve for parameter t
     */
    fun pointOnCurve(t: Float): ShapePoint {
        val u = 1 - t
        return ShapePoint(
            anchor0X * (u * u * u) +
                    control0X * (3 * t * u * u) +
                    control1X * (3 * t * t * u) +
                    anchor1X * (t * t * t),
            anchor0Y * (u * u * u) +
                    control0Y * (3 * t * u * u) +
                    control1Y * (3 * t * t * u) +
                    anchor1Y * (t * t * t)
        )
    }
    
    fun zeroLength(): Boolean =
        abs(anchor0X - anchor1X) < DISTANCE_EPSILON &&
        abs(anchor0Y - anchor1Y) < DISTANCE_EPSILON
    
    /**
     * Returns two Cubics created by splitting this curve at the given distance t
     */
    fun split(t: Float): Pair<ShapeCubic, ShapeCubic> {
        val u = 1 - t
        val point = pointOnCurve(t)
        
        return Pair(
            ShapeCubic(
                anchor0X, anchor0Y,
                anchor0X * u + control0X * t,
                anchor0Y * u + control0Y * t,
                anchor0X * (u * u) + control0X * (2 * u * t) + control1X * (t * t),
                anchor0Y * (u * u) + control0Y * (2 * u * t) + control1Y * (t * t),
                point.x, point.y
            ),
            ShapeCubic(
                point.x, point.y,
                control0X * (u * u) + control1X * (2 * u * t) + anchor1X * (t * t),
                control0Y * (u * u) + control1Y * (2 * u * t) + anchor1Y * (t * t),
                control1X * u + anchor1X * t,
                control1Y * u + anchor1Y * t,
                anchor1X, anchor1Y
            )
        )
    }
    
    /**
     * Utility function to reverse the control/anchor points for this curve
     */
    fun reverse() = ShapeCubic(
        anchor1X, anchor1Y,
        control1X, control1Y,
        control0X, control0Y,
        anchor0X, anchor0Y
    )
    
    override fun toString(): String {
        return "anchor0: ($anchor0X, $anchor0Y) " +
                "control0: ($control0X, $control0Y), " +
                "control1: ($control1X, $control1Y), " +
                "anchor1: ($anchor1X, $anchor1Y)"
    }
}
