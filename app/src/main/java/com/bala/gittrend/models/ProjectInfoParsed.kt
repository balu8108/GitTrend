package com.bala.gittrend.models

import com.google.gson.annotations.SerializedName

data class ProjectInfoParsed(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("language")
    val language: String?,
    @SerializedName("description")
    val description: String,
    @SerializedName("html_url")
    val projectUrl: String,
    @SerializedName("forks_count")
    val forksCount: Long,
    @SerializedName("stargazers_count")
    val starGazersCount: Long,
    @SerializedName("owner")
    val projectOwnerDetails: ProjectOwnerDetails
)
