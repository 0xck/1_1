package ru.otus.sc.echo.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ru.otus.sc.echo.model.{EchoRequest, EchoResponse}
import ru.otus.sc.echo.service.EchoService
import ru.otus.sc.route.BaseRouter

class EchoRouter(service: EchoService) extends BaseRouter {

  private val repeatNumParam = parameter("repeatNum".as[Int].?(1))
  private val EchoString     = Segment

  def route: Route =
    (get & path("echo" / EchoString) & repeatNumParam) { (string, num) =>
      service.echo(EchoRequest(string, num)) match {
        case EchoResponse.Response(r) => complete(r)
        case EchoResponse.Error(_)    => complete(StatusCodes.UnprocessableEntity)
      }
    }
}
