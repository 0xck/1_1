import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import ru.otus.sc.RoutingTable

import scala.io.StdIn

object Main {
  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem("system")

    import system.dispatcher

    val route   = RoutingTable.getRoute
    val binding = Http().newServerAt("localhost", 8080).bind(route)

    binding.foreach(b => println(s"Binding on ${b.localAddress}"))

    StdIn.readLine()

    binding.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }
}
