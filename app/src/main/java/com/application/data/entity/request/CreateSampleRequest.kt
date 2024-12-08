package com.application.data.entity.request

data class CreateSampleRequest(
    val attachmentId: String? = null,
    val position: String? = null,
    val stageId: String,
    val answers: List<UpsertAnswerRequest>,
    val dynamicFields: List<CreateDynamicFieldRequest>
)