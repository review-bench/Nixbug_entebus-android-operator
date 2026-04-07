//package com.nixbug.entebus.data.retro
//
//import com.nixbug.entebus.data.remote.operator.model.OperatorSchema
//import okhttp3.RequestBody
//import retrofit2.Response
//import retrofit2.http.Body
//import retrofit2.http.PATCH
//import retrofit2.http.Path
//
//interface CustomAccountApi {
//
//    @PATCH("company/account/{id}")
//    suspend fun updateAccount(
//        @Path("id") id: Int,
//        @Body body: RequestBody
//    ): Response<OperatorSchema>
//
//}
