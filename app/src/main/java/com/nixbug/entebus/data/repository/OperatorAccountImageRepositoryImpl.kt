package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.manager.TokenManager
import com.nixbug.entebus.data.remote.operator.api.OperatorAccountImageApi
import com.nixbug.entebus.domain.repository.OperatorAccountImageRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject

class OperatorAccountImageRepositoryImpl @Inject constructor(
    private val accountImageApi: OperatorAccountImageApi,
    private val tokenManager: TokenManager,
) : OperatorAccountImageRepository {
    override suspend fun fetchOperatorAccountImage(): Resource<String> {
        val operatorId = tokenManager.operatorId
        if (operatorId == null) {
            println("Session expired. Please login again.")
            return Resource.Error("No operatorId")
        }

        val listResponse = accountImageApi.fetchOperatorAccountImage(operatorId = operatorId)

        if (listResponse.isSuccessful) {
            val images = listResponse.body()

            if (!images.isNullOrEmpty()) {
                val firstImage = images[0]

                println("Operator Image ID: ${firstImage.id}, File Name: ${firstImage.fileName}")
                try {
                    val imageResponse =
                        accountImageApi.downloadOperatorImageForOperatorCompanyAccountPictureIdGet(
                            firstImage.id
                        )
                    if (imageResponse.isSuccessful) {
//                    val body = imageResponse.body()
                        val body = imageResponse.body()
                        println("Downloaded Image size: ${body?.contentLength()}")
                    } else {
                        println("Download API Error: ${imageResponse.code()}")
                    }
                } catch (e: Exception) {
                    println("Error downloading image: ${e.message}")


                }

            } else {
                println("No images found")
            }
        } else {
            println("List API Error: ${listResponse.code()}")
        }

        return Resource.Success("done")
    }
}