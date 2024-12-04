package com.application.data.entity.response

data class SampleResponse(
    val id: String,
    val attachmentId: String,
    val position: String,
    val createdAt: Long,
    val projectOwnerId: String,
    val stageId: String,
    val answers: List<AnswerResponse>,
    val dynamicFields: List<DynamicFieldResponse>
)