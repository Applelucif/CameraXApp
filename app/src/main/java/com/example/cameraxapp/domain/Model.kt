package com.example.cameraxapp.domain

data class BodyPartCoord(
    val bodyPart: BodyPart,
    val x: Float,
    val y: Float
)

enum class BodyPart {
    LEFT_SHOULDER,
    LEFT_WRIST,
    RIGHT_WRIST,
    LEFT_ELBOW,
    RIGHT_ELBOW
}
