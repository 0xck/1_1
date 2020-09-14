package ru.otus.sc.storage.dao.impl

import ru.otus.sc.storage.dao.StorageDao
import ru.otus.sc.storage.model.StorageEntry

class StorageDaoImpl[K, V] extends StorageDao[K, V] {
  private var storages: Map[K, V] = Map.empty

  def createStorage(entry: StorageEntry[K, V]): Option[StorageEntry[K, V]] =
    storages.get(entry.key) match {
      case Some(_) => None
      case None =>
        storages += (entry.key -> entry.value)
        Some(entry)
    }

  def getStorage(key: K): Option[StorageEntry[K, V]] =
    storages.get(key).map(value => StorageEntry(key, value))

  def deleteStorage(key: K): Option[StorageEntry[K, V]] =
    for {
      value <- storages.get(key)
    } yield {
      storages -= key
      StorageEntry(key, value)
    }

  def updateStorage(entry: StorageEntry[K, V]): Option[StorageEntry[K, V]] =
    for {
      _ <- storages.get(entry.key)
    } yield {
      storages += (entry.key -> entry.value)
      entry
    }

  def findByValue(value: V): Seq[StorageEntry[K, V]] =
    storages.collect {
      case (k, v) if v == value => StorageEntry[K, V](k, v)
    }.toVector

  def findValuesByPredicate(predicate: V => Boolean): Seq[StorageEntry[K, V]] =
    storages.collect {
      case (k, v) if predicate(v) => StorageEntry[K, V](k, v)
    }.toVector

  def getAll: Seq[StorageEntry[K, V]] = storages.map(x => StorageEntry[K, V](x._1, x._2)).toVector
}
