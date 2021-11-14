package com.bala.gittrend.apiservice

import com.bala.gittrend.models.ProjectInfoParsedList
import com.bala.gittrend.utils.ErrorUtils
import retrofit2.Retrofit
import javax.inject.Inject

class ApiService @Inject constructor(private val retrofit: Retrofit) {

    suspend fun fetchTrendingGitRepos(): Result<ProjectInfoParsedList?> {
        val apiInterface = retrofit.create(ApiInterface::class.java);
        val response = apiInterface.getProjectInfoParsed()

        return try {
            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                val errorResponse = ErrorUtils.parseError(response, retrofit)
                if (errorResponse != null) {
                    Result.failure(errorResponse)
                } else {
                    Result.failure(Error("Unknown Error"))
                }
            }
        } catch (e: Throwable) {
            Result.failure(Error("Unknown Error"))
        }

    }

}