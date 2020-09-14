package ru.otus.sc.storage.service.impl

import ru.otus.sc.storage.dao.StorageDao
import ru.otus.sc.storage.model._
import ru.otus.sc.storage.service.StorageService

class StorageServiceImpl[K, V](dao: StorageDao[K, V]) extends StorageService[K, V] {
  def createStorage(request: CreateStorageRequest[K, V]): CreateStorageResponse[K, V] =
    dao
      .createStorage(request.entry)
      .map(CreateStorageResponse.Created[K, V])
      .getOrElse(CreateStorageResponse.ErrorKeyExists[K, V](request.entry.key))

  def getStorage(request: GetStorageRequest[K, V]): GetStorageResponse[K, V] =
    dao
      .getStorage(request.key)
      .map(GetStorageResponse.Found[K, V])
      .getOrElse(GetStorageResponse.NotFound[K, V](request.key))

  def deleteStorage(request: DeleteStorageRequest[K, V]): DeleteStorageResponse[K, V] =
    dao
      .deleteStorage(request.key)
      .map(DeleteStorageResponse.Deleted[K, V])
      .getOrElse(DeleteStorageResponse.NotFound[K, V](request.key))

  def updateStorage(request: UpdateStorageRequest[K, V]): UpdateStorageResponse[K, V] =
    dao
      .updateStorage(request.entry)
      .map(UpdateStorageResponse.Updated[K, V])
      .getOrElse(UpdateStorageResponse.NotFound[K, V](request.entry.key))

  def findStorages(request: FindStoragesRequest[K, V]): FindStoragesResponse[K, V] = {
    val found = request match {
      case FindStoragesRequest.ByValue(value)               => dao.findByValue(value)
      case FindStoragesRequest.ValuesByPredicate(predicate) => dao.findValuesByPredicate(predicate)
      case FindStoragesRequest.GetAll()                     => dao.getAll
    }
    FindStoragesResponse.Result[K, V](found)
  }
}
