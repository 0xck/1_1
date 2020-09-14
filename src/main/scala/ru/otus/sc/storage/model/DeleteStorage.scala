package ru.otus.sc.storage.model

case class DeleteStorageRequest[K, V](key: K)

trait DeleteStorageResponse[K, V]
object DeleteStorageResponse {
  case class Deleted[K, V](entry: StorageEntry[K, V]) extends DeleteStorageResponse[K, V]
  case class NotFound[K, V](key: K)                   extends DeleteStorageResponse[K, V]
}
