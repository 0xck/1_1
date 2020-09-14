package ru.otus.sc.counter.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import ru.otus.sc.counter.model.{FindCountersRequest, GetCounterRequest, GetCounterResponse, _}
import ru.otus.sc.counter.service.CounterService
import ru.otus.sc.route.BaseRouter

class CounterRouter(service: CounterService) extends BaseRouter {

  private val GetCounterId = JavaUUID.map(GetCounterRequest)
  private val UpdateCounterId = JavaUUID.map(UpdateCounterRequest)
  private val DeleteCounterId = JavaUUID.map(DeleteCounterRequest)

  def route: Route =
    pathPrefix("counters") {
      getCounter ~ createCounter ~ updateCounter ~ deleteCounter ~ getAll
    }

  private def getCounter: Route =
    (get & path(GetCounterId)) { counterId =>
      service.getCounter(counterId) match {
        case GetCounterResponse.Found(c) =>
          complete(c)
        case GetCounterResponse.NotFound(_) =>
          complete(StatusCodes.NotFound)
      }
    }

  private def getAll: Route = {
    (get & path(PathEnd))
    service.findCounters(FindCountersRequest.GetAll) match {
      case FindCountersResponse.Result(c) => complete(c)
    }
  }

  private def createCounter: Route =
    (post & entity(as[Counter])) { counter =>
      service.createCounter(CreateCounterRequest(counter)) match {
        case CreateCounterResponse.Created(c) =>
          complete(c)
        case CreateCounterResponse.Error(_) =>
          complete(StatusCodes.UnprocessableEntity)
      }
    }

  private def updateCounter: Route =
    (put & path(UpdateCounterId)) { counterId =>
      service.updateCounter(counterId) match {
        case UpdateCounterResponse.Updated(c) =>
          complete(c)
        case UpdateCounterResponse.NotFound(_) =>
          complete(StatusCodes.NotFound)
      }
    }

  private def deleteCounter: Route =
    (delete & path(DeleteCounterId)) { counterId =>
      service.deleteCounter(counterId) match {
        case DeleteCounterResponse.Deleted(c) =>
          complete(c)
        case DeleteCounterResponse.NotFound(_) =>
          complete(StatusCodes.NotFound)
      }
    }
}
