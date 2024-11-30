package com.application.data.entity.response

data class SampleResponse(
    val id: String,
    val attachmentId: String,
    val position: String,
    val createAt: Int,
    val projectOwnerId: String,
    val stageId: String,
    val answers: AnswerResponse,
    val dynamicFields: DynamicFieldResponse
)