package ru.otus.sc.countdown.service.impl

import ru.otus.sc.countdown.dao.CountdownDao
import ru.otus.sc.countdown.model.{Countdown, CreateCountdownResponse, _}
import ru.otus.sc.countdown.service.CountdownService

class CountdownServiceImpl(dao: CountdownDao) extends CountdownService {
  def createCountdown(request: CreateCountdownRequest): CreateCountdownResponse =
    request.countdown match {
      case countdown @ Countdown.Done(_) =>
        CreateCountdownResponse.Created(dao.createCountdown(countdown))
      case countdown @ Countdown.Tick(_, _, value) =>
        /**
        * it can return CreateCountdownResponse.Error
        * but in current CountdownDao implementation there is not option for this
        * so emulate it with countdown.value set less than 1
        */
        if (value < 1L)
          CreateCountdownResponse.Error("Countdown initial value has to be more or equal 1")
        else
          CreateCountdownResponse.Created(dao.createCountdown(countdown))
    }
  def deleteCountdown(request: DeleteCountdownRequest): DeleteCountdownResponse =
    dao
      .deleteCountdown(request.id)
      .map(DeleteCountdownResponse.Deleted)
      .getOrElse(DeleteCountdownResponse.NotFound(request.id))

  def updateCountdown(request: UpdateCountdownRequest): UpdateCountdownResponse =
    request.countdown match {
      case Countdown.Tick(None, _, _) => UpdateCountdownResponse.ErrorWrongId
      case Countdown.Done(None) => UpdateCountdownResponse.ErrorWrongId
      case countdown @ Countdown.Tick(Some(id), _, _) =>
        dao
          .updateCountdown(countdown)
          .map(UpdateCountdownResponse.Updated)
          .getOrElse(UpdateCountdownResponse.NotFound(id))
      /** Done can not be updated anymore */
      case Countdown.Done(_) => UpdateCountdownResponse.CanNotUpdateDone
    }

  def getCountdown(request: GetCountdownRequest): GetCountdownResponse =
    dao
      .getCountdown(request.id)
      .map(GetCountdownResponse.Found)
      .getOrElse(GetCountdownResponse.NotFound(request.id))

  def findCountdowns(request: FindCountdownsRequest): FindCountdownsResponse = {
    val found = request match {
      case FindCountdownsRequest.ByValue(value, predicate) =>
        dao.findCountdownsByValue(value, predicate)
      case FindCountdownsRequest.ByUpdater(updater) =>
        dao.findCountdownsByUpdater(updater)
      case FindCountdownsRequest.AllDone =>
        dao.findAllDone
      case FindCountdownsRequest.AllNonDone =>
        dao.findAllNonDone
      case FindCountdownsRequest.GetAll =>
        dao.getAll
    }
    FindCountdownsResponse.Result(found)
  }
}
