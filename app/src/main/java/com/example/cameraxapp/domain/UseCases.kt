package com.example.cameraxapp.domain

import com.example.cameraxapp.data.LocalDataSource
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot

class UseCases @Inject constructor(
    private val dataSource: LocalDataSource
) {

    fun getRecognitionResultFlow(): Flowable<String> {
        return dataSource
            .getCoordsFlow()
            .onBackpressureDrop()
            .throttleFirst(250, TimeUnit.MILLISECONDS)
            .map { bodyPartCoords ->
                detectPose(bodyPartCoords)
            }
    }

    private fun detectPose(array: List<BodyPartCoord>): String {

        val leftShoulderY = array[0].y
        val leftElbowAX = array[1].x
        val leftElbowAY = array[1].y
        val rightElbowBX = array[2].x
        val rightElbowBY = array[2].y
        val leftWristCX = array[3].x
        val leftWristCY = array[3].y
        val rightWristDX = array[4].x
        val rightWristDY = array[4].y


        var angle = Math.toDegrees(
            (atan2(
                leftElbowAY - leftWristCY,
                leftElbowAX - leftWristCX
            )
                    - atan2(
                rightElbowBY - rightWristDY,
                rightElbowBX - rightWristDX
            )).toDouble()
        )
        angle = abs(angle)

        val distanceAB = hypot(
            (leftElbowAY - rightElbowBY).toDouble(),
            (leftElbowAX - rightElbowBX).toDouble()
        )

        val distanceAC = hypot(
            (leftElbowAY - leftWristCY).toDouble(),
            (leftElbowAX - leftWristCX).toDouble()
        )

        val distanceAD = hypot(
            (leftElbowAY - rightWristDY).toDouble(),
            (leftElbowAX - rightWristDX).toDouble()
        )
        val isBelowShoulder =
            leftShoulderY < leftElbowAY && leftShoulderY < leftWristCY
        val isLeftHandPositionHorizontal =
            (abs(leftElbowAY - leftWristCY) / leftElbowAY) * 100 <= 15

        val isLeftHandPositionVertical =
            (abs(leftElbowAX - leftWristCX) / leftElbowAX) * 100 <= 15

        val isHandsOnSameHeight =
            (abs(leftElbowAY - rightElbowBY) / leftElbowAY) * 100 <= 15

        val isRightHandPositionHorizontal =
            (abs(rightElbowBY - rightWristDY) / rightElbowBY) * 100 <= 15

        val isHandsOnSameX =
            (abs(leftElbowAX - rightWristDX) / leftElbowAX) * 100 <= 15


        if ((angle in 170.0..190.0) && distanceAD <= 100 && isBelowShoulder && isLeftHandPositionHorizontal) {
            return "Ni"
        }

        if ((angle in 0.0..20.0) && (leftElbowAY - leftWristCY >= 0.7 * distanceAC) && isLeftHandPositionVertical && (distanceAB >= 0.5 * distanceAC) && isHandsOnSameHeight) {
            return "Hu"
        }

        if ((angle in 160.0..190.0) && isLeftHandPositionHorizontal && isRightHandPositionHorizontal && (distanceAD >= 0.5 * distanceAC) && isHandsOnSameX) {
            return "Ja"
        }
        return "nothing"
    }
}