package com.material.you.widgets.core.shapes

import kotlin.math.*

/**
 * Universal 2D point for shape calculations
 * Represents a point with x and y coordinates
 */
data class ShapePoint(val x: Float, val y: Float) {
    
    companion object {
        val ZERO = ShapePoint(0f, 0f)
    }
    
    // ============ Transformation Operations ============
    
    fun rotate90() = ShapePoint(-y, x)
    
    fun rotate(degrees: Float, center: ShapePoint = ZERO): ShapePoint {
        val radians = degrees * PI.toFloat() / 180f
        val off = this - center
        val cos = cos(radians)
        val sin = sin(radians)
        return ShapePoint(
            off.x * cos - off.y * sin,
            off.x * sin + off.y * cos
        ) + center
    }
    
    fun translate(dx: Float, dy: Float) = ShapePoint(x + dx, y + dy)
    
    fun scale(sx: Float, sy: Float) = ShapePoint(x * sx, y * sy)
    
    // ============ Vector Operations ============
    
    val angleDegrees: Float
        get() = angleRadians * 180f / PI.toFloat()
    
    val angleRadians: Float
        get() = atan2(y, x)
    
    fun getDistance() = sqrt(x * x + y * y)
    
    fun getDistanceSquared() = x * x + y * y
    
    fun dotProduct(other: ShapePoint) = x * other.x + y * other.y
    
    fun dotProductXY(otherX: Float, otherY: Float) = x * otherX + y * otherY
    
    fun clockwise(other: ShapePoint) = (x * other.y - y * other.x) > 0
    
    fun getDirection(): ShapePoint {
        val d = getDistance()
        require(d > 0) { "Can't get the direction of a 0-length vector" }
        return this / d
    }
    
    // ============ Operators ============
    
    operator fun unaryMinus() = ShapePoint(-x, -y)
    
    operator fun minus(operand: ShapePoint) = ShapePoint(x - operand.x, y - operand.y)
    
    operator fun plus(operand: ShapePoint) = ShapePoint(x + operand.x, y + operand.y)
    
    operator fun times(operand: Float) = ShapePoint(x * operand, y * operand)
    
    operator fun div(operand: Float) = ShapePoint(x / operand, y / operand)
    
    operator fun rem(operand: Float) = ShapePoint(x % operand, y % operand)
    
    override fun toString() = "ShapePoint($x, $y)"
}

/**
 * Linearly interpolate between two points
 */
fun interpolate(start: ShapePoint, stop: ShapePoint, fraction: Float): ShapePoint {
    return ShapePoint(
        lerp(start.x, stop.x, fraction),
        lerp(start.y, stop.y, fraction)
    )
}

fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start * (1 - fraction) + stop * fraction
}
