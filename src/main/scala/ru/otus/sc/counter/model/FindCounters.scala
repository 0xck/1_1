package ru.otus.sc.counter.model

import ru.otus.sc.counter.model.Counter.{CounterTime, CounterValue}

sealed trait FindCountersRequest
object FindCountersRequest {
  type CompareValues = (CounterValue, CounterValue) => Boolean
  type CompareTimes  = (CounterTime, CounterTime) => Boolean
  case class ByValue(value: CounterValue, predicate: CompareValues) extends FindCountersRequest
  case class ByTimestamp(timestamp: CounterTime, predicate: CompareTimes)
      extends FindCountersRequest
  case object GetAll extends FindCountersRequest
}

sealed trait FindCountersResponse
object FindCountersResponse {
  case class Result(counters: Seq[Counter]) extends FindCountersResponse
}
