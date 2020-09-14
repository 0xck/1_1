package ru.otus.sc.reverse.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ru.otus.sc.reverse.model
import ru.otus.sc.reverse.model.ReverseResponse
import ru.otus.sc.reverse.service.ReverseService
import ru.otus.sc.route.BaseRouter

class ReverseRouter(service: ReverseService) extends BaseRouter {

  private val ReversedString = Segment

  def route: Route =
    (get & path("reverse" / ReversedString)) { string =>
      service.reverse(model.ReverseRequest(string)) match {
        case ReverseResponse(s) => complete(s)
      }
    }
}
