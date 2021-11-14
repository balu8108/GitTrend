package com.bala.gittrend.models

import com.google.gson.annotations.SerializedName

data class ProjectInfoParsedList(
    @SerializedName("items")
    val projectInfos: List<ProjectInfoParsed>
)
