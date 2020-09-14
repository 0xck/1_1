package ru.otus.sc.storage.dao

import ru.otus.sc.storage.model.StorageEntry

trait StorageDao[K, V] {
  def createStorage(entry: StorageEntry[K, V]): Option[StorageEntry[K, V]]
  def getStorage(key: K): Option[StorageEntry[K, V]]
  def deleteStorage(key: K): Option[StorageEntry[K, V]]
  def updateStorage(entry: StorageEntry[K, V]): Option[StorageEntry[K, V]]
  def findByValue(value: V): Seq[StorageEntry[K, V]]
  def findValuesByPredicate(predicate: V => Boolean): Seq[StorageEntry[K, V]]
  def getAll : Seq[StorageEntry[K, V]]
}
