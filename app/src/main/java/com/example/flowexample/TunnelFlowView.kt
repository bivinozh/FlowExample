package com.example.flowexample

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.*

/**
 * Custom view that displays an L-shaped tunnel with flowing arrows inside
 * The tunnel has walls and arrows flow through the L-path
 */
class TunnelFlowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val outerWallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 60f
        color = Color.parseColor("#3A3A3A")
    }
    
    private val innerWallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 40f
        color = Color.parseColor("#1A1A1A")
    }
    
    private val centerLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.parseColor("#505050")
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }
    
    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#4CAF50")
    }
    
    private var animationProgress = 0f
    private var animator: ValueAnimator? = null
    var isAnimating = false
        private set
    
    // L-shape configuration
    private var cornerX = 0f
    private var cornerY = 0f
    private var totalPathLength = 0f
    private val cornerRadius = 100f
    private val numberOfArrows = 12
    
    // Flow direction
    private var isForwardDirection = true
    
    init {
        startAnimation()
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Calculate L-shape corner position
        cornerX = w * 0.25f
        cornerY = h * 0.65f
        
        // Calculate total path length
        val verticalLength = cornerY - cornerRadius
        val horizontalLength = w - cornerX - cornerRadius
        val cornerArcLength = (Math.PI.toFloat() / 2f) * cornerRadius
        totalPathLength = verticalLength + cornerArcLength + horizontalLength
    }
    
    /**
     * Start the tunnel flow animation
     */
    fun startAnimation() {
        if (isAnimating) return
        
        isAnimating = true
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 4000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            
            addUpdateListener { animation ->
                animationProgress = animation.animatedValue as Float
                invalidate()
            }
            
            start()
        }
    }
    
    /**
     * Stop the animation
     */
    fun stopAnimation() {
        isAnimating = false
        animator?.cancel()
        animator = null
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw L-shaped tunnel walls
        drawTunnelWalls(canvas)
        
        // Draw flowing arrows inside the tunnel
        for (i in 0 until numberOfArrows) {
            val baseProgress = (animationProgress + i.toFloat() / numberOfArrows) % 1f
            // Reverse progress for backward direction
            val progress = if (isForwardDirection) baseProgress else 1f - baseProgress
            drawArrowInTunnel(canvas, progress)
        }
    }
    
    /**
     * Draw L-shaped tunnel walls
     */
    private fun drawTunnelWalls(canvas: Canvas) {
        val path = Path()
        
        // Vertical segment
        path.moveTo(cornerX, 0f)
        path.lineTo(cornerX, cornerY - cornerRadius)
        
        // Corner arc
        val centerX = cornerX + cornerRadius
        val centerY = cornerY - cornerRadius
        val cornerLeft = centerX - cornerRadius
        val cornerTop = centerY - cornerRadius
        val cornerRight = centerX + cornerRadius
        val cornerBottom = centerY + cornerRadius
        path.arcTo(cornerLeft, cornerTop, cornerRight, cornerBottom, 180f, -90f, false)
        
        // Horizontal segment
        path.lineTo(width.toFloat(), cornerY)
        
        // Draw outer tunnel wall (thick stroke)
        canvas.drawPath(path, outerWallPaint)
        
        // Draw inner tunnel wall (thinner stroke on top)
        canvas.drawPath(path, innerWallPaint)
        
        // Draw center line (dashed)
        canvas.drawPath(path, centerLinePaint)
    }
    
    /**
     * Draw arrow flowing inside the tunnel
     */
    private fun drawArrowInTunnel(canvas: Canvas, progress: Float) {
        val position = getPositionOnPath(progress) ?: return
        val baseAngle = getAngleAtPosition(progress)
        
        // Flip arrow direction for backward flow
        val angle = if (isForwardDirection) baseAngle else baseAngle + 180f
        
        // Fixed colors based on direction
        // Forward: Blue, Backward: Green
        val arrowColor = if (isForwardDirection) {
            Color.parseColor("#2196F3") // Blue for forward
        } else {
            Color.parseColor("#4CAF50") // Green for backward
        }
        
        // Alpha variation for fade effect (fade at edges)
        val alpha = ((1f - abs(progress - 0.5f) * 2f) * 255).toInt().coerceIn(150, 255)
        arrowPaint.color = arrowColor
        arrowPaint.alpha = alpha
        
        // Draw arrow
        canvas.save()
        canvas.translate(position.x, position.y)
        canvas.rotate(angle)
        
        val arrowSize = 25f
        val arrowPath = Path().apply {
            moveTo(-arrowSize, -arrowSize * 0.6f)
            lineTo(arrowSize * 0.8f, 0f)
            lineTo(-arrowSize, arrowSize * 0.6f)
            lineTo(-arrowSize * 0.4f, 0f)
            close()
        }
        
        canvas.drawPath(arrowPath, arrowPaint)
        canvas.restore()
    }
    
    /**
     * Get position along the L-shape path
     */
    private fun getPositionOnPath(progress: Float): PointF? {
        if (width == 0 || height == 0) return null
        
        val distance = progress * totalPathLength
        val verticalLength = cornerY - cornerRadius
        val cornerArcLength = (Math.PI.toFloat() / 2f) * cornerRadius
        
        return when {
            distance <= verticalLength -> {
                PointF(cornerX, distance)
            }
            distance <= verticalLength + cornerArcLength -> {
                val arcDistance = distance - verticalLength
                val arcProgress = arcDistance / cornerArcLength
                val centerX = cornerX + cornerRadius
                val centerY = cornerY - cornerRadius
                val startAngle = Math.PI.toFloat()
                val endAngle = Math.PI.toFloat() / 2f
                val currentAngle = startAngle - (arcProgress * (startAngle - endAngle))
                val x = centerX + cornerRadius * cos(currentAngle)
                val y = centerY + cornerRadius * sin(currentAngle)
                PointF(x, y)
            }
            else -> {
                val horizontalDistance = distance - verticalLength - cornerArcLength
                PointF(cornerX + cornerRadius + horizontalDistance, cornerY)
            }
        }
    }
    
    /**
     * Get rotation angle at position
     */
    private fun getAngleAtPosition(progress: Float): Float {
        val distance = progress * totalPathLength
        val verticalLength = cornerY - cornerRadius
        val cornerArcLength = (Math.PI.toFloat() / 2f) * cornerRadius
        
        return when {
            distance <= verticalLength -> 90f
            distance <= verticalLength + cornerArcLength -> {
                val arcProgress = (distance - verticalLength) / cornerArcLength
                90f * (1f - arcProgress)
            }
            else -> 0f
        }
    }
    
    /**
     * Set flow direction to forward (top to right)
     */
    fun setForwardDirection() {
        isForwardDirection = true
        invalidate()
    }
    
    /**
     * Set flow direction to backward (right to top)
     */
    fun setBackwardDirection() {
        isForwardDirection = false
        invalidate()
    }
    
    /**
     * Get current flow direction
     */
    fun isFlowingForward(): Boolean = isForwardDirection
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }
}

