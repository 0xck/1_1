package ru.otus.sc.user.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import ru.otus.sc.route.BaseRouter
import ru.otus.sc.user.model._
import ru.otus.sc.user.service.UserService

class UserRouter(service: UserService) extends BaseRouter {

  private val GetUserId = JavaUUID.map(GetUserRequest)

  /*
   * CRUD:
   *
   * POST - without ID
   * PUT - with ID
   *
   * /users - add new - POST
   * /users/{ID} - add/update - PUT
   * Create - /users - POST
   * Read - /users GET - all
   * Read - /users/{ID} GET - one
   * Update /users/{ID} PUT
   * Delete - DELETE
   *
   *
   * */
  private val DeleteUserId = JavaUUID.map(DeleteUserRequest)

  def route: Route =
    pathPrefix("users") {
      getUser ~ createUser ~ updateUser ~ deleteUser ~ getAll
    }

  private def getUser: Route =
    (get & path(GetUserId)) { userId =>
      service.getUser(userId) match {
        case GetUserResponse.Found(user) =>
          complete(user)
        case GetUserResponse.NotFound(_) =>
          complete(StatusCodes.NotFound)
      }
    }

  private def getAll: Route = {
    (get & path(PathEnd))
    service.findUsers(FindUsersRequest.GetAll) match {
      case FindUsersResponse.Result(u) => complete(u)
    }
  }

  private def createUser: Route =
    (post & entity(as[User])) { user =>
      service.createUser(CreateUserRequest(user)) match {
        case CreateUserResponse.Created(u) =>
          complete(u)
        case CreateUserResponse.Error(_) =>
          complete(StatusCodes.UnprocessableEntity)
      }
    }

  private def updateUser: Route =
    (put & entity(as[User])) { user =>
      service.updateUser(UpdateUserRequest(user)) match {
        case UpdateUserResponse.Updated(u) =>
          complete(u)
        case UpdateUserResponse.NotFound(_) =>
          complete(StatusCodes.NotFound)
        case UpdateUserResponse.ErrorNoUniqueId =>
          complete(StatusCodes.UnprocessableEntity)
        case UpdateUserResponse.ErrorWrongId =>
          complete(StatusCodes.UnprocessableEntity)
      }
    }

  private def deleteUser: Route =
    (delete & path(DeleteUserId)) { userId =>
      service.deleteUser(userId) match {
        case DeleteUserResponse.Deleted(u) =>
          complete(u)
        case DeleteUserResponse.NotFound(_) =>
          complete(StatusCodes.NotFound)
      }
    }
}
