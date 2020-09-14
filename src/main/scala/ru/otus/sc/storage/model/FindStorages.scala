package ru.otus.sc.storage.model

trait FindStoragesRequest[K, V]
object FindStoragesRequest {
  case class ByValue[K, V](value: V)                          extends FindStoragesRequest[K, V]
  case class ValuesByPredicate[K, V](predicate: V => Boolean) extends FindStoragesRequest[K, V]
  case class GetAll[K, V]()                                   extends FindStoragesRequest[K, V]
}

trait FindStoragesResponse[K, V]
object FindStoragesResponse {
  case class Result[K, V](entries: Seq[StorageEntry[K, V]]) extends FindStoragesResponse[K, V]
}
