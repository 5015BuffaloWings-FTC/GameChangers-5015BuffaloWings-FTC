package com.acmerobotics.roadrunner.path

import com.acmerobotics.roadrunner.Vector2d
import com.acmerobotics.roadrunner.util.DoubleProgression

/**
 * Parametric curve with two components (x and y).
 */
abstract class ParametricCurve {

    /**
     * Returns the vector [s] units along the curve.
     */
    @JvmOverloads
    operator fun get(s: Double, t: Double = reparam(s)) = internalGet(t)

    /**
     * Returns the derivative [s] units along the curve.
     */
    @JvmOverloads
    fun deriv(s: Double, t: Double = reparam(s)) = internalDeriv(t) * paramDeriv(t)

    /**
     * Returns the second derivative [s] units along the curve.
     */
    @JvmOverloads
    fun secondDeriv(s: Double, t: Double = reparam(s)): Vector2d {
        val deriv = internalDeriv(t)
        val secondDeriv = internalSecondDeriv(t)
        val splineParameterDeriv = paramDeriv(t)
        val splineParameterSecondDeriv = paramSecondDeriv(t)
        return secondDeriv * splineParameterDeriv * splineParameterDeriv +
                deriv * splineParameterSecondDeriv
    }

    /**
     * Returns the third derivative [s] units along the curve.
     */
    @JvmOverloads
    fun thirdDeriv(s: Double, t: Double = reparam(s)): Vector2d {
        val deriv = internalDeriv(t)
        val secondDeriv = internalSecondDeriv(t)
        val thirdDeriv = internalThirdDeriv(t)
        val splineParameterDeriv = paramDeriv(t)
        val splineParameterSecondDeriv = paramSecondDeriv(t)
        val splineParameterThirdDeriv = paramThirdDeriv(t)
        return thirdDeriv * splineParameterDeriv * splineParameterDeriv * splineParameterDeriv +
                secondDeriv * splineParameterSecondDeriv * splineParameterDeriv * 3.0 +
                deriv * splineParameterThirdDeriv
    }

    /**
     * Returns the start vector.
     */
    fun start() = get(0.0, 0.0)

    /**
     * Returns the start derivative.
     */
    fun startDeriv() = deriv(0.0, 0.0)

    /**
     * Returns the start second derivative.
     */
    fun startSecondDeriv() = secondDeriv(0.0, 0.0)

    /**
     * Returns the start third derivative.
     */
    fun startThirdDeriv() = thirdDeriv(0.0, 0.0)

    /**
     * Returns the end vector.
     */
    fun end() = get(length(), 1.0)

    /**
     * Returns the end derivative.
     */
    fun endDeriv() = deriv(length(), 1.0)

    /**
     * Returns the end second derivative.
     */
    fun endSecondDeriv() = secondDeriv(length(), 1.0)

    /**
     * Returns the end third derivative.
     */
    fun endThirdDeriv() = thirdDeriv(length(), 1.0)

    /**
     * Returns the angle of the tangent line [s] units along the curve.
     */
    @JvmOverloads
    fun tangentAngle(s: Double, t: Double = reparam(s)) = deriv(s, t).angle()

    /**
     * Returns the derivative of the tangent angle [s] units along the curve.
     */
    @JvmOverloads
    fun tangentAngleDeriv(s: Double, t: Double = reparam(s)): Double {
        val deriv = deriv(s, t)
        val secondDeriv = secondDeriv(s, t)
        return deriv.x * secondDeriv.y - deriv.y * secondDeriv.x
    }

    /**
     * Returns the second derivative of the tangent angle [s] units along the curve.
     */
    @JvmOverloads
    fun tangentAngleSecondDeriv(s: Double, t: Double = reparam(s)): Double {
        val deriv = deriv(s, t)
        val thirdDeriv = thirdDeriv(s, t)
        return deriv.x * thirdDeriv.y - deriv.y * thirdDeriv.x
    }

    /**
     * Returns the length of the curve.
     */
    abstract fun length(): Double

    internal abstract fun reparam(s: Double): Double
    internal fun reparam(s: DoubleProgression): DoubleArray = s.map(::reparam).toDoubleArray()

    internal abstract fun internalGet(t: Double): Vector2d
    internal abstract fun internalDeriv(t: Double): Vector2d
    internal abstract fun internalSecondDeriv(t: Double): Vector2d
    internal abstract fun internalThirdDeriv(t: Double): Vector2d

    internal abstract fun paramDeriv(t: Double): Double
    internal abstract fun paramSecondDeriv(t: Double): Double
    internal abstract fun paramThirdDeriv(t: Double): Double
}