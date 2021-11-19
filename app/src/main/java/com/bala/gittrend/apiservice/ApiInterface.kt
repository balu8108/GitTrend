package com.bala.gittrend.apiservice

import com.bala.gittrend.models.ProjectInfoParsedList
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {
    @GET("repositories?q=android&per_page=50&sort=stars&page=1&order=desc")
    suspend fun getProjectInfoParsed(): Response<ProjectInfoParsedList?>
}