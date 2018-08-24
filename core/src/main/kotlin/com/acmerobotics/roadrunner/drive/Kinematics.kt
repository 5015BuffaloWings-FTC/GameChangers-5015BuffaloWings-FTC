package com.acmerobotics.roadrunner.drive

import com.acmerobotics.roadrunner.Pose2d
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin

object Kinematics {
    @JvmStatic
    fun fieldToRobotPose(fieldPose: Pose2d) = Pose2d(fieldPose.pos().rotated(-fieldPose.heading), 0.0)

    @JvmStatic
    fun fieldToRobotPoseVelocity(fieldPose: Pose2d, fieldPoseVelocity: Pose2d) =
            Pose2d(fieldPoseVelocity.pos().rotated(-fieldPose.heading), fieldPoseVelocity.heading)

    @JvmStatic
    fun fieldToRobotPoseAcceleration(fieldPose: Pose2d, fieldPoseVelocity: Pose2d, fieldPoseAcceleration: Pose2d) =
            Pose2d(fieldPoseAcceleration.pos().rotated(-fieldPose.heading), fieldPoseAcceleration.heading) +
                    Pose2d(-fieldPoseVelocity.x * Math.sin(fieldPose.heading) + fieldPoseVelocity.y * Math.cos(fieldPose.heading),
                            -fieldPoseVelocity.x * Math.cos(fieldPose.heading) - fieldPoseVelocity.y * Math.sin(fieldPose.heading),
                            0.0
                    ) * fieldPoseVelocity.heading

    @JvmStatic
    fun calculateMotorFeedforward(velocities: List<Double>, accelerations: List<Double>, kV: Double, kA: Double, kStatic: Double) =
        velocities.zip(accelerations)
                .map { it.first * kV + it.second * kA }
                .map { if (abs(it) > 1e-4) it + sign(it) * kStatic else 0.0 }

    @JvmStatic
    fun relativeOdometryUpdate(fieldPose: Pose2d, robotPoseDelta: Pose2d): Pose2d {
        val fieldPoseDelta = if (abs(robotPoseDelta.heading) > 1e-6) {
            val finalHeading = fieldPose.heading + robotPoseDelta.heading
            val cosTerm = cos(finalHeading) - cos(fieldPose.heading)
            val sinTerm = sin(finalHeading) - sin(fieldPose.heading)

            Pose2d(
                    (robotPoseDelta.x * sinTerm + robotPoseDelta.y * cosTerm) / robotPoseDelta.heading,
                    (-robotPoseDelta.x * cosTerm + robotPoseDelta.y * sinTerm) / robotPoseDelta.heading,
                    robotPoseDelta.heading
            )
        } else {
            Pose2d(
                    robotPoseDelta.pos().rotated(fieldPose.heading + robotPoseDelta.heading / 2),
                    robotPoseDelta.heading
            )
        }

        return fieldPose + fieldPoseDelta
    }
}