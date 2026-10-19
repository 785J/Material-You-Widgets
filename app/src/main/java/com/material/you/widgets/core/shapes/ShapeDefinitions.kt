package com.material.you.widgets.core.shapes

import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.CornerRounding as AndroidCornerRounding
import kotlin.math.*

/**
 * Universal Shape Definitions
 * Single source of truth for all shapes used across UI and widgets
 * 
 * Based on Flutter material_shapes library
 * https://github.com/ksokolovskyi/material_shapes
 */
object ShapeDefinitions {
    
    // ============ Basic Geometric Shapes ============
    
    /**
     * Perfect circle shape
     */
    val circle: RoundedPolygon by lazy {
        circle(numVertices = 64)
    }
    
    /**
     * Square shape with rounded corners
     */
    val square: RoundedPolygon by lazy {
        RoundedPolygon(
            numVertices = 4,
            rounding = AndroidCornerRounding(0.1f)
        )
    }
    
    /**
     * Circle shape with customizable vertex count
     */
    fun circle(numVertices: Int = 32): RoundedPolygon {
        val vertices = FloatArray(numVertices * 2)
        for (i in 0 until numVertices) {
            val angle = (i.toFloat() / numVertices) * 2f * PI.toFloat()
            vertices[i * 2] = cos(angle)
            vertices[i * 2 + 1] = sin(angle)
        }
        return RoundedPolygon(
            vertices = vertices,
            rounding = AndroidCornerRounding(0f)
        )
    }
    
    /**
     * Oval shape - ellipse with 2:1 width:height ratio
     */
    val oval: RoundedPolygon by lazy {
        val vertices = FloatArray(64)
        val numVertices = 32
        for (i in 0 until numVertices) {
            val angle = (i.toFloat() / numVertices) * 2f * PI.toFloat()
            vertices[i * 2] = cos(angle) * 1.0f
            vertices[i * 2 + 1] = sin(angle) * 0.64f // Scaled Y axis
        }
        RoundedPolygon(
            vertices = vertices,
            rounding = AndroidCornerRounding(0f)
        )
    }
    
    /**
     * Regular polygon with specified sides and rounding
     */
    fun regularPolygon(sides: Int, rounding: Float = 0.15f): RoundedPolygon {
        return RoundedPolygon(
            numVertices = sides,
            rounding = AndroidCornerRounding(rounding)
        )
    }
    
    // ============ Material Design Shapes ============
    
    /**
     * Pill/Stadium shape - rounded rectangle with semicircular ends
     */
    val pill: RoundedPolygon by lazy {
        customPolygon(
            listOf(
                PointNRound(ShapePoint(0.961f, 0.039f), AndroidCornerRounding(0.426f)),
                PointNRound(ShapePoint(1.001f, 0.428f)),
                PointNRound(ShapePoint(1f, 0.609f), AndroidCornerRounding(1f)),
            ),
            reps = 2,
            mirroring = true
        )
    }
    
    /**
     * Ghostish shape - ghost-like with wavy bottom
     */
    val ghostish: RoundedPolygon by lazy {
        customPolygon(
            listOf(
                PointNRound(ShapePoint(0.5f, 0f), AndroidCornerRounding(1f)),
                PointNRound(ShapePoint(1f, 0f), AndroidCornerRounding(1f)),
                PointNRound(ShapePoint(1f, 1.14f), AndroidCornerRounding(0.254f, 0.106f)),
                PointNRound(ShapePoint(0.575f, 0.906f), AndroidCornerRounding(0.253f)),
            ),
            reps = 1,
            mirroring = true
        )
    }
    
    /**
     * Pentagon shape
     */
    val pentagon: RoundedPolygon by lazy {
        customPolygon(
            listOf(
                PointNRound(ShapePoint(0.5f, -0.009f), AndroidCornerRounding(0.172f)),
                PointNRound(ShapePoint(1.03f, 0.365f), AndroidCornerRounding(0.164f)),
                PointNRound(ShapePoint(0.828f, 0.97f), AndroidCornerRounding(0.169f)),
            ),
            reps = 1,
            mirroring = true
        )
    }
    
    /**
     * 6-sided Cookie shape
     */
    val cookie6Sided: RoundedPolygon by lazy {
        customPolygon(
            listOf(
                PointNRound(ShapePoint(0.723f, 0.884f), AndroidCornerRounding(0.394f)),
                PointNRound(ShapePoint(0.5f, 1.099f), AndroidCornerRounding(0.398f)),
            ),
            reps = 6
        )
    }
    
    /**
     * 7-sided Cookie shape
     */
    val cookie7Sided: RoundedPolygon by lazy {
        customPolygon(
            listOf(
                PointNRound(ShapePoint(0.707f, 0.879f), AndroidCornerRounding(0.391f)),
                PointNRound(ShapePoint(0.5f, 1.087f), AndroidCornerRounding(0.394f)),
            ),
            reps = 7
        )
    }
    
    /**
     * 9-sided Cookie shape
     */
    val cookie9Sided: RoundedPolygon by lazy {
        customPolygon(
            listOf(
                PointNRound(ShapePoint(0.684f, 0.866f), AndroidCornerRounding(0.381f)),
                PointNRound(ShapePoint(0.5f, 1.062f), AndroidCornerRounding(0.384f)),
            ),
            reps = 9
        )
    }
    
    /**
     * Bun shape - soft rounded shape
     */
    val bun: RoundedPolygon by lazy {
        customPolygon(
            listOf(
                PointNRound(ShapePoint(0.796f, 0.5f)),
                PointNRound(ShapePoint(0.853f, 0.518f), AndroidCornerRounding(1f)),
                PointNRound(ShapePoint(0.992f, 0.631f), AndroidCornerRounding(1f)),
                PointNRound(ShapePoint(0.968f, 1f), AndroidCornerRounding(1f)),
            ),
            reps = 2,
            mirroring = true
        )
    }
    
    /**
     * Heart shape
     */
    val heart: RoundedPolygon by lazy {
        customPolygon(
            listOf(
                PointNRound(ShapePoint(0.5f, 0.268f), AndroidCornerRounding(0.016f)),
                PointNRound(ShapePoint(0.792f, -0.066f), AndroidCornerRounding(0.958f)),
                PointNRound(ShapePoint(1.064f, 0.276f), AndroidCornerRounding(1f)),
                PointNRound(ShapePoint(0.501f, 0.946f), AndroidCornerRounding(0.129f)),
            ),
            reps = 1,
            mirroring = true
        )
    }
    
    /**
     * Diamond shape
     */
    val diamond: RoundedPolygon by lazy {
        customPolygon(
            listOf(
                PointNRound(ShapePoint(0.5f, 1.096f), AndroidCornerRounding(0.151f, 0.524f)),
                PointNRound(ShapePoint(0.04f, 0.5f), AndroidCornerRounding(0.159f)),
            ),
            reps = 2
        )
    }
    
    /**
     * Clamshell shape - hexagon-like rounded shape
     */
    val clamshell: RoundedPolygon by lazy {
        customPolygon(
            listOf(
                PointNRound(ShapePoint(0.75f, 0.067f), AndroidCornerRounding(0.194f)),
                PointNRound(ShapePoint(0.983f, 0.5f), AndroidCornerRounding(0.194f)),
                PointNRound(ShapePoint(0.75f, 0.933f), AndroidCornerRounding(0.194f)),
            ),
            reps = 1,
            mirroring = true
        )
    }
    
    /**
     * Arch/Semicircle shape - half circle with flat bottom
     */
    val arch: RoundedPolygon by lazy {
        val numVertices = 16
        val vertices = FloatArray((numVertices + 2) * 2)
        
        // Bottom left corner
        vertices[0] = 0f
        vertices[1] = 1f
        
        // Arc from left to right (180 degrees)
        for (i in 0 until numVertices) {
            val angle = PI.toFloat() * (1f - i.toFloat() / (numVertices - 1))
            vertices[(i + 1) * 2] = 0.5f + cos(angle) * 0.5f
            vertices[(i + 1) * 2 + 1] = 0.5f + sin(angle) * 0.5f
        }
        
        // Bottom right corner
        vertices[(numVertices + 1) * 2] = 1f
        vertices[(numVertices + 1) * 2 + 1] = 1f
        
        RoundedPolygon(
            vertices = vertices,
            rounding = AndroidCornerRounding(0.08f)
        )
    }
    
    /**
     * Slanted shape - rectangle with angled corners (parallelogram-like)
     */
    val slanted: RoundedPolygon by lazy {
        customPolygon(
            listOf(
                PointNRound(ShapePoint(0.177f, 0.046f), AndroidCornerRounding(0.156f)),
                PointNRound(ShapePoint(0.823f, 0.046f), AndroidCornerRounding(0.156f)),
                PointNRound(ShapePoint(0.954f, 0.954f), AndroidCornerRounding(0.156f)),
                PointNRound(ShapePoint(0.308f, 0.954f), AndroidCornerRounding(0.156f)),
            ),
            reps = 1
        )
    }
    
    // ============ Google Material3 Shapes ============
    
    /**
     * Semicircle shape - flat diameter base, smooth dome top
     */
    val semiCircle: RoundedPolygon by lazy {
        val numVertices = 16
        val vertices = FloatArray(numVertices * 2)
        for (i in 0 until numVertices) {
            val angle = PI.toFloat() * (i.toFloat() / (numVertices - 1))
            vertices[i * 2] = -cos(angle)
            vertices[i * 2 + 1] = -sin(angle)
        }
        RoundedPolygon(
            vertices = vertices,
            rounding = AndroidCornerRounding(0.1f)
        )
    }
    
    /**
     * Rounded triangle shape
     */
    val triangle: RoundedPolygon by lazy {
        RoundedPolygon(numVertices = 3, rounding = AndroidCornerRounding(0.22f))
    }
    
    /**
     * Arrow / arrowhead shape with a concave notch at the back
     */
    val arrow: RoundedPolygon by lazy {
        customPolygon(
            listOf(
                PointNRound(ShapePoint(0.5f, 0f), AndroidCornerRounding(0.5f)),
                PointNRound(ShapePoint(1f, 0.95f), AndroidCornerRounding(0.35f)),
                PointNRound(ShapePoint(0.5f, 0.65f), AndroidCornerRounding(0.35f)),
                PointNRound(ShapePoint(0f, 0.95f), AndroidCornerRounding(0.35f)),
            ),
            reps = 1
        )
    }
    
    /**
     * Fan / quarter-circle wedge shape
     */
    val fan: RoundedPolygon by lazy {
        val numArcPoints = 10
        val points = mutableListOf(ShapePoint(0f, 0f))
        for (i in 0..numArcPoints) {
            val angle = (PI.toFloat() / 2f) * (i.toFloat() / numArcPoints)
            points.add(ShapePoint(cos(angle), sin(angle)))
        }
        val vertices = FloatArray(points.size * 2)
        points.forEachIndexed { i, p -> vertices[i * 2] = p.x; vertices[i * 2 + 1] = p.y }
        RoundedPolygon(vertices = vertices, rounding = AndroidCornerRounding(0.15f))
    }
    
    /**
     * Gem shape - 6-point faceted cut
     */
    val gem: RoundedPolygon by lazy {
        RoundedPolygon.star(
            numVerticesPerRadius = 3,
            innerRadius = 0.65f,
            rounding = AndroidCornerRounding(0.18f)
        )
    }
    
    /**
     * 4-sided Cookie shape
     */
    val cookie4Sided: RoundedPolygon by lazy {
        customPolygon(
            listOf(
                PointNRound(ShapePoint(0.751f, 0.896f), AndroidCornerRounding(0.401f)),
                PointNRound(ShapePoint(0.5f, 1.125f), AndroidCornerRounding(0.407f)),
            ),
            reps = 4
        )
    }
    
    /**
     * 12-sided Cookie shape
     */
    val cookie12Sided: RoundedPolygon by lazy {
        customPolygon(
            listOf(
                PointNRound(ShapePoint(0.649f, 0.847f), AndroidCornerRounding(0.366f)),
                PointNRound(ShapePoint(0.5f, 1.025f), AndroidCornerRounding(0.369f)),
            ),
            reps = 12
        )
    }
    
    /**
     * Sunny shape - rounded 8-point burst
     */
    val sunny: RoundedPolygon by lazy {
        RoundedPolygon.star(
            numVerticesPerRadius = 8,
            innerRadius = 0.8f,
            rounding = AndroidCornerRounding(0.15f)
        )
    }
    
    /**
     * Very Sunny shape - rounded 12-point burst
     */
    val verySunny: RoundedPolygon by lazy {
        RoundedPolygon.star(
            numVerticesPerRadius = 12,
            innerRadius = 0.85f,
            rounding = AndroidCornerRounding(0.12f)
        )
    }
    
    /**
     * 4-leaf clover shape
     */
    val clover4Leaf: RoundedPolygon by lazy {
        RoundedPolygon.star(
            numVerticesPerRadius = 4,
            innerRadius = 0.32f,
            rounding = AndroidCornerRounding(0.35f),
            innerRounding = AndroidCornerRounding(0.32f)
        )
    }
    
    /**
     * 8-leaf clover shape
     */
    val clover8Leaf: RoundedPolygon by lazy {
        RoundedPolygon.star(
            numVerticesPerRadius = 8,
            innerRadius = 0.42f,
            rounding = AndroidCornerRounding(0.25f),
            innerRounding = AndroidCornerRounding(0.3f)
        )
    }
    
    /**
     * Burst shape - sharp 9-point star
     */
    val burst: RoundedPolygon by lazy {
        RoundedPolygon.star(
            numVerticesPerRadius = 9,
            innerRadius = 0.65f,
            rounding = AndroidCornerRounding(0f)
        )
    }
    
    /**
     * Soft burst shape - rounded 9-point star
     */
    val softBurst: RoundedPolygon by lazy {
        RoundedPolygon.star(
            numVerticesPerRadius = 9,
            innerRadius = 0.7f,
            rounding = AndroidCornerRounding(0.1f),
            innerRounding = AndroidCornerRounding(0.1f)
        )
    }
    
    /**
     * Boom shape - dense sharp starburst
     */
    val boom: RoundedPolygon by lazy {
        RoundedPolygon.star(
            numVerticesPerRadius = 16,
            innerRadius = 0.45f,
            rounding = AndroidCornerRounding(0f)
        )
    }
    
    /**
     * Soft boom shape - dense rounded starburst
     */
    val softBoom: RoundedPolygon by lazy {
        RoundedPolygon.star(
            numVerticesPerRadius = 16,
            innerRadius = 0.65f,
            rounding = AndroidCornerRounding(0.15f),
            innerRounding = AndroidCornerRounding(0.2f)
        )
    }
    
    /**
     * Flower shape - rounded 8-petal daisy
     */
    val flower: RoundedPolygon by lazy {
        RoundedPolygon.star(
            numVerticesPerRadius = 8,
            innerRadius = 0.575f,
            rounding = AndroidCornerRounding(0.32f),
            innerRounding = AndroidCornerRounding(0.05f)
        )
    }
    
    /**
     * Puffy shape - inflated 4-lobe blob
     */
    val puffy: RoundedPolygon by lazy {
        RoundedPolygon.star(
            numVerticesPerRadius = 4,
            innerRadius = 0.75f,
            rounding = AndroidCornerRounding(0.9f),
            innerRounding = AndroidCornerRounding(0.9f)
        )
    }
    
    /**
     * Puffy diamond shape
     */
    val puffyDiamond: RoundedPolygon by lazy {
        RoundedPolygon.star(
            numVerticesPerRadius = 4,
            innerRadius = 0.85f,
            rounding = AndroidCornerRounding(0.4f),
            innerRounding = AndroidCornerRounding(0.1f)
        )
    }
    
    /**
     * Pixel circle shape - blocky staircase approximation of a circle
     */
    val pixelCircle: RoundedPolygon by lazy {
        val stepsIn = floatArrayOf(0f, 0.35f, 0.35f, 0.65f, 0.65f, 0.85f, 0.85f, 1f)
        val stepsOut = floatArrayOf(1f, 1f, 0.85f, 0.85f, 0.65f, 0.65f, 0.35f, 0.35f)
        val quarter = stepsIn.indices.map { ShapePoint(stepsIn[it], -stepsOut[it]) }
        val allPoints = mutableListOf<ShapePoint>()
        for (q in 0 until 4) {
            for (p in quarter) {
                allPoints.add(p.rotate(90f * q))
            }
        }
        val vertices = FloatArray(allPoints.size * 2)
        allPoints.forEachIndexed { i, p -> vertices[i * 2] = p.x; vertices[i * 2 + 1] = p.y }
        RoundedPolygon(vertices = vertices, rounding = AndroidCornerRounding(0f))
    }
    
    /**
     * Pixel triangle shape - blocky staircase approximation of a triangle
     */
    val pixelTriangle: RoundedPolygon by lazy {
        val points = listOf(
            ShapePoint(-1f, -1f), ShapePoint(-1f, 1f),
            ShapePoint(0.2f, 1f), ShapePoint(0.2f, 0.6f),
            ShapePoint(0.5f, 0.6f), ShapePoint(0.5f, 0.2f),
            ShapePoint(0.8f, 0.2f), ShapePoint(0.8f, -0.2f),
            ShapePoint(0.5f, -0.2f), ShapePoint(0.5f, -0.6f),
            ShapePoint(0.2f, -0.6f), ShapePoint(0.2f, -1f),
        )
        val vertices = FloatArray(points.size * 2)
        points.forEachIndexed { i, p -> vertices[i * 2] = p.x; vertices[i * 2 + 1] = p.y }
        RoundedPolygon(vertices = vertices, rounding = AndroidCornerRounding(0f))
    }
    
    // ============ Private Helper Functions ============
    
    /**
     * Helper to create custom polygon with repetition and mirroring
     */
    private fun customPolygon(
        points: List<PointNRound>,
        reps: Int,
        center: ShapePoint = ShapePoint(0.5f, 0.5f),
        mirroring: Boolean = false
    ): RoundedPolygon {
        val actualPoints = doRepeat(points, reps, center, mirroring)
        
        val vertices = FloatArray(actualPoints.size * 2)
        val perVertexRounding = mutableListOf<AndroidCornerRounding>()
        
        for (i in actualPoints.indices) {
            val ap = actualPoints[i]
            perVertexRounding.add(ap.r)
            
            val j = i * 2
            vertices[j] = ap.p.x
            vertices[j + 1] = ap.p.y
        }
        
        return RoundedPolygon(
            vertices = vertices,
            perVertexRounding = perVertexRounding,
            centerX = center.x,
            centerY = center.y
        )
    }
    
    /**
     * Repeat and mirror points to create symmetric shapes
     */
    private fun doRepeat(
        points: List<PointNRound>,
        reps: Int,
        center: ShapePoint,
        mirroring: Boolean
    ): List<PointNRound> {
        val result = mutableListOf<PointNRound>()
        
        if (mirroring) {
            val measures = points.map { point ->
                val off = point.p - center
                Pair(
                    atan2(off.y, off.x),
                    off.getDistance()
                )
            }
            val actualReps = reps * 2
            val sectionAngle = PI.toFloat() * 2f / actualReps
            
            for (r in 0 until actualReps) {
                for (index in points.indices) {
                    val i = if (r % 2 == 0) index else points.size - 1 - index
                    if (i > 0 || r % 2 == 0) {
                        val a = sectionAngle * r +
                                (if (r % 2 == 0) measures[i].first
                                else sectionAngle - measures[i].first + 2 * measures[0].first)
                        
                        val finalPoint = ShapePoint(
                            cos(a),
                            sin(a)
                        ) * measures[i].second + center
                        
                        result.add(PointNRound(finalPoint, points[i].r))
                    }
                }
            }
        } else {
            val np = points.size
            for (i in 0 until (np * reps)) {
                val point = points[i % np].p.rotate(
                    (i / np) * 360f / reps,
                    center = center
                )
                result.add(PointNRound(point, points[i % np].r))
            }
        }
        
        return result
    }
    
    /**
     * Data class holding a point and its corner rounding
     */
    private data class PointNRound(
        val p: ShapePoint,
        val r: AndroidCornerRounding = AndroidCornerRounding.Unrounded
    )
}
