package com.sample.currencyconverter.data.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.*
import retrofit2.Response

abstract class NetworkBoundResource<ResultType, RequestType> {

    fun asFlow() = flow<Resource<ResultType>>{
        //emit(Resource.Loading())

        val dbValue = loadFromDb().first()
        if (shouldFetch(dbValue)) {
            //emit(Resource.Loading())
            val apiResponse = fetchFromNetwork()
            if (apiResponse.isSuccessful) {
                saveNetworkResult(processResponse(apiResponse)!!)
                emitAll(loadFromDb().map {
                    Resource.Success(it)
                })
            } else {
                onFetchFailed()
                emit(Resource.Failed(apiResponse.message()))
            }
        } else {
            emitAll(loadFromDb().map { Resource.Success(it) })
        }
    }

    protected open fun onFetchFailed() {
        // Implement in sub-classes to handle errors
    }

    @WorkerThread
    protected open fun processResponse(response: Response<RequestType>) = response.body()

    @WorkerThread
    protected abstract suspend fun saveNetworkResult(item: RequestType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): Flow<ResultType>

    @MainThread
    protected abstract suspend fun fetchFromNetwork(): Response<RequestType>
}