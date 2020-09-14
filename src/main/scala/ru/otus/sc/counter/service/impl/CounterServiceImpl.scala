package ru.otus.sc.counter.service.impl

import ru.otus.sc.counter.dao.CounterDao
import ru.otus.sc.counter.model._
import ru.otus.sc.counter.service.CounterService

class CounterServiceImpl(dao: CounterDao) extends CounterService {
  def createCounter(request: CreateCounterRequest): CreateCounterResponse = {

    /** it can return CreateCounterResponse.Error
      * but in current CounterDao implementation there is not option for this
      * so emulate it with counter.value set less than 1
      */
    if (request.counter.value < 1L)
      CreateCounterResponse.Error("Counter initial value has to be more or equal 1")
    CreateCounterResponse.Created(dao.createCounter(request.counter))
  }
  def deleteCounter(request: DeleteCounterRequest): DeleteCounterResponse =
    dao
      .deleteCounter(request.id)
      .map(DeleteCounterResponse.Deleted)
      .getOrElse(DeleteCounterResponse.NotFound(request.id))

  def updateCounter(request: UpdateCounterRequest): UpdateCounterResponse =
    dao
      .updateCounter(request.id)
      .map(UpdateCounterResponse.Updated)
      .getOrElse(UpdateCounterResponse.NotFound(request.id))

  def getCounter(request: GetCounterRequest): GetCounterResponse =
    dao
      .getCounter(request.id)
      .map(GetCounterResponse.Found)
      .getOrElse(GetCounterResponse.NotFound(request.id))

  def findCounters(request: FindCountersRequest): FindCountersResponse = {
    val found = request match {
      case FindCountersRequest.ByValue(value, predicate) =>
        dao.findCountersByValue(value, predicate)
      case FindCountersRequest.ByTimestamp(value, predicate) =>
        dao.findCountersByTimestamp(value, predicate)
      case FindCountersRequest.GetAll =>
        dao.getAll
    }
    FindCountersResponse.Result(found)
  }
}
