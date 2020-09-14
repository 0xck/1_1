package ru.otus.sc.counter.dao

import ru.otus.sc.counter.model.Counter
import ru.otus.sc.counter.model.Counter.{CounterId, CounterTime, CounterValue}
import ru.otus.sc.counter.model.FindCountersRequest.{CompareTimes, CompareValues}

trait CounterDao {
  def createCounter(counter: Counter): Counter
  def deleteCounter(id: CounterId): Option[Counter]
  def getCounter(id: CounterId): Option[Counter]
  def updateCounter(id: CounterId): Option[Counter]
  def findCountersByValue(value: CounterValue, predicate: CompareValues): Seq[Counter]
  def findCountersByTimestamp(timestamp: CounterTime, predicate: CompareTimes): Seq[Counter]
  def getAll: Seq[Counter]
}
