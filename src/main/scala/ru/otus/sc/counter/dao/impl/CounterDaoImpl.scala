package ru.otus.sc.counter.dao.impl

import java.time.LocalDateTime
import java.util.UUID

import ru.otus.sc.counter.dao.CounterDao
import ru.otus.sc.counter.model.Counter
import ru.otus.sc.counter.model.Counter.{CounterId, CounterTime, CounterValue}
import ru.otus.sc.counter.model.FindCountersRequest.{CompareTimes, CompareValues}

class CounterDaoImpl extends CounterDao {
  private var counters = Map[CounterId, Counter]()

  def createCounter(counter: Counter): Counter = {
    val newId      = UUID.randomUUID()
    val newTime    = LocalDateTime.now()
    val newCounter = counter.copy(id = Some(newId), timestamp = Some(newTime))
    counters += (newId -> newCounter)
    newCounter
  }

  def deleteCounter(id: CounterId): Option[Counter] =
    counters.get(id) match {
      case deletedCounter @ Some(_) =>
        counters -= id
        deletedCounter
      case None => None
    }

  def getCounter(id: CounterId): Option[Counter] = counters.get(id)

  def updateCounter(id: CounterId): Option[Counter] =
    counters.get(id) match {
      case Some(counter) =>
        val newTime    = LocalDateTime.now()
        val newCounter = counter.copy(timestamp = Some(newTime), value = counter.value + 1L)
        counters += (id -> newCounter)
        Some(newCounter)
      case None => None
    }

  def findCountersByValue(value: CounterValue, predicate: CompareValues): Seq[Counter] =
    counters.values.filter(counter => predicate(value, counter.value)).toVector

  def findCountersByTimestamp(timestamp: CounterTime, predicate: CompareTimes): Seq[Counter] =
    counters.values.collect { counter =>
      counter.timestamp match {
        case Some(time) if predicate(timestamp, time) => counter
      }
    }.toVector

  def getAll : Seq[Counter] = counters.values.toVector
}
