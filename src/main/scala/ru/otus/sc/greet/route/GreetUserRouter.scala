// Using GreetUserRouter instead GreetRouter, due to Greeting requires types
// in current implementation it is User

package ru.otus.sc.greet.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ru.otus.sc.greet.model.GreetRequest
import ru.otus.sc.greet.service.GreetingService
import ru.otus.sc.route.BaseRouter
import ru.otus.sc.user.implicits.UserNameImplicits._
import ru.otus.sc.user.model.{User, UserName}

class GreetUserRouter(service: GreetingService[User]) extends BaseRouter {
  private val isHumanParam = parameter("isHuman".as[Boolean].?(true))
  private val PersonName   = Segment

  def route: Route =
    (get & path("greet" / PersonName) & isHumanParam) { (name, isHuman) =>
      val user = User(None, None, recognizeName(name), None, Set.empty)
      val resp = service.greet(GreetRequest[User](name = user, isHuman = isHuman))
      complete(resp.greeting)
    }

  private def recognizeName(name: String): UserName =
    name.split("""\s+""") match {
      /**
        * for simplifying let's:
        * exclude titles
        * use patronymics only if middle is defined, otherwise use middle only
        * ignore extra parts
        */
      case Array(f, m, p, l, _*) => UserName(f, Some(l), Some(m), Some(p), None)
      case Array(f, m, p, l)     => UserName(f, Some(l), Some(m), Some(p), None)
      case Array(f, m, l)        => UserName(f, Some(l), Some(m), None, None)
      case Array(f, l)           => UserName(f, Some(l), None, None, None)
      case Array(f)              => UserName(f, None, None, None, None)
    }
}
