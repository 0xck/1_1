package ru.otus.sc.user.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import ru.otus.sc.route.BaseRouter
import ru.otus.sc.user.model._
import ru.otus.sc.user.service.UserTagService

class UserTagRouter(service: UserTagService) extends BaseRouter {

  private val GetUserTagId = LongNumber.map(GetUserTagRequest)
  private val DeleteUserTagId = LongNumber.map(DeleteUserTagRequest)
  // update tagged and untagged
  private val UserTagId = LongNumber
  private val UserId    = JavaUUID

  def route: Route =
    pathPrefix("usertags") {
      getUserTag ~ createUserTag ~ updateUserTag ~ tagUserWithTag ~ deleteUserTag ~ untagUserWithTag ~ getAll
    }

  private def getUserTag: Route =
    (get & path(GetUserTagId)) { userTagId =>
      service.getUserTag(userTagId) match {
        case GetUserTagResponse.Found(userTag) =>
          complete(userTag)
        case GetUserTagResponse.NotFound(_) =>
          complete(StatusCodes.NotFound)
      }
    }

  private def getAll: Route = {
    (get & path(PathEnd))
    service.findUserTags(FindUserTagsRequest.GetAll) match {
      case FindUserTagsResponse.TagsResult(tags) => complete(tags)
      case _                                     => complete(StatusCodes.InternalServerError)
    }

  }

  private def createUserTag: Route =
    (post & entity(as[UserTag])) { userTag =>
      service.createUserTag(CreateUserTagRequest(userTag)) match {
        case CreateUserTagResponse.Created(u) =>
          complete(u)
        case CreateUserTagResponse.Error(_) =>
          complete(StatusCodes.UnprocessableEntity)
      }
    }

  private def updateUserTag: Route =
    (put & entity(as[UserTag])) { userTag =>
      service.updateUserTag(UpdateUserTagRequest(userTag)) match {
        case UpdateUserTagResponse.Updated(u) =>
          complete(u)
        case UpdateUserTagResponse.NotFound(_) =>
          complete(StatusCodes.NotFound)
        case UpdateUserTagResponse.ErrorNoTagId =>
          complete(StatusCodes.UnprocessableEntity)
      }
    }

  private def deleteUserTag: Route =
    (delete & path(DeleteUserTagId)) { userTagId =>
      service.deleteUserTag(userTagId) match {
        case DeleteUserTagResponse.Deleted(u) =>
          complete(u)
        case DeleteUserTagResponse.NotFound(_) =>
          complete(StatusCodes.NotFound)
      }
    }

  private def tagUserWithTag: Route =
    (put & path(UserTagId / UserId)) { (tag, user) =>
      service.tagUser(UpdateTagUserRequest(tag, user)) match {
        case UpdateTagUserResponse.TaggedUser(u, t) =>
          complete((u, t))
        case UpdateTagUserResponse.NotFoundTag(_) =>
          complete(StatusCodes.NotFound)
      }
    }

  private def untagUserWithTag: Route =
    (delete & path(UserTagId / UserId)) { (tag, user) =>
      service.untagUser(UpdateUntagUserRequest(tag, user)) match {
        case UpdateUntagUserResponse.UntaggedUser(u, t) =>
          complete((u, t))
        case UpdateUntagUserResponse.NotFoundTag(_) =>
          complete(StatusCodes.NotFound)
      }
    }
}
