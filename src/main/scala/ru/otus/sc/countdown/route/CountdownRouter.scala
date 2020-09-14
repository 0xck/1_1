package ru.otus.sc.countdown.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.Encoder
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import ru.otus.sc.countdown.model.Countdown.{Done, Tick}
import ru.otus.sc.countdown.model.{GetCountdownRequest, _}
import ru.otus.sc.countdown.service.CountdownService
import ru.otus.sc.route.BaseRouter

class CountdownRouter(service: CountdownService) extends BaseRouter {

  /**
    * manual sealed trait Countdown encoding,
    * due to escape extra case class name in JSON like {"Tick": {...}}
    */
  private implicit val encoder: Encoder[Countdown] = Encoder.instance {
    case tick @ Tick(_, _, _) => tick.asJson
    case done @ Done(_)       => done.asJson
  }
  private val GetCountdownId = JavaUUID.map(GetCountdownRequest)
  private val DeleteCountdownId = JavaUUID.map(DeleteCountdownRequest)

  def route: Route =
    pathPrefix("countdowns") {
      getCountdown ~ createCountdownTick ~ createCountdownDone ~
        updateCountdownTick ~ updateCountdownDone ~ deleteCountdown ~ getAll
    }

  private def getCountdown: Route =
    (get & path(GetCountdownId)) { countdownId =>
      service.getCountdown(countdownId) match {
        case GetCountdownResponse.Found(c) =>
          complete(c)
        case GetCountdownResponse.NotFound(_) =>
          complete(StatusCodes.NotFound)
      }
    }

  private def getAll: Route = {
    (get & path(PathEnd))
    service.findCountdowns(FindCountdownsRequest.GetAll) match {
      case FindCountdownsResponse.Result(c) => complete(c)
    }
  }

  private def createCountdownTick: Route =
    (post & entity(as[Countdown.Tick])) { countdown =>
      service.createCountdown(CreateCountdownRequest(countdown)) match {
        case CreateCountdownResponse.Created(c) =>
          complete(c)
        case CreateCountdownResponse.Error(_) =>
          complete(StatusCodes.UnprocessableEntity)
      }
    }

  private def createCountdownDone: Route =
    (post & entity(as[Countdown.Done])) { countdown =>
      service.createCountdown(CreateCountdownRequest(countdown)) match {
        case CreateCountdownResponse.Created(c) =>
          complete(c)
        case CreateCountdownResponse.Error(_) =>
          complete(StatusCodes.UnprocessableEntity)
      }
    }

  private def updateCountdownTick: Route =
    (put & entity(as[Countdown.Tick])) { countdown =>
      service.updateCountdown(UpdateCountdownRequest(countdown)) match {
        case UpdateCountdownResponse.Updated(c) =>
          complete(c)
        case UpdateCountdownResponse.NotFound(_) =>
          complete(StatusCodes.NotFound)
        case UpdateCountdownResponse.ErrorWrongId =>
          complete(StatusCodes.UnprocessableEntity)
        case _ => complete(StatusCodes.InternalServerError)
      }
    }

  private def updateCountdownDone: Route =
    (put & entity(as[Countdown.Done])) { countdown =>
      service.updateCountdown(UpdateCountdownRequest(countdown)) match {
        case UpdateCountdownResponse.CanNotUpdateDone =>
          complete(StatusCodes.UnprocessableEntity)
        case UpdateCountdownResponse.ErrorWrongId =>
          complete(StatusCodes.UnprocessableEntity)
        case _ => complete(StatusCodes.InternalServerError)
      }
    }

  private def deleteCountdown: Route =
    (delete & path(DeleteCountdownId)) { countdownId =>
      service.deleteCountdown(countdownId) match {
        case DeleteCountdownResponse.Deleted(c) =>
          complete(c)
        case DeleteCountdownResponse.NotFound(_) =>
          complete(StatusCodes.NotFound)
      }
    }
}
