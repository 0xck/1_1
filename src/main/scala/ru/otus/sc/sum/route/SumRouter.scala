package ru.otus.sc.sum.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ru.otus.sc.route.BaseRouter
import ru.otus.sc.sum.model
import ru.otus.sc.sum.model.SumResponse
import ru.otus.sc.sum.service.SumService

class SumRouter(service : SumService) extends BaseRouter {

  private val externalParam = parameter("external".as[Boolean].?(false))
  private val A = LongNumber
  private val B = LongNumber

  def route : Route =
    (get & path("sum" / A / B) & externalParam) { (a, b, e) =>
      service.sum(model.SumRequest(a, b, e)) match {
        case SumResponse(s) => complete(s)
      }
    }
}