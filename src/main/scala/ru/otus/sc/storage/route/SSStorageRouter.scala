package ru.otus.sc.storage.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import ru.otus.sc.route.BaseRouter
import ru.otus.sc.storage.model._
import ru.otus.sc.storage.service.StorageService


/** implementation for k: String -> v: String */
class SSStorageRouter(service: StorageService[String, String]) extends BaseRouter {

  def route: Route =
    pathPrefix("storage") {
      getStorage ~ createStorage ~ updateStorage ~ deleteStorage ~ getAll
    }

  private val GetStorageKey = Segment.map(GetStorageRequest[String, String])
  private def getStorage: Route =
    (get & path(GetStorageKey)) { key =>
      service.getStorage(key) match {
        case GetStorageResponse.Found(user) =>
          complete(user)
        case GetStorageResponse.NotFound(_) =>
          complete(StatusCodes.NotFound)
      }
    }

  private def getAll: Route = {
      (get & path(PathEnd))
      service.findStorages(FindStoragesRequest.GetAll()) match {
        case FindStoragesResponse.Result(s) => complete(s)}

    }

  private def createStorage: Route =
    (post & entity(as[StorageEntry[String, String]])) { entry =>

      service.createStorage(CreateStorageRequest(entry)) match {
        case CreateStorageResponse.Created(s) =>
          complete(s)
        case CreateStorageResponse.ErrorKeyExists(_) =>
          complete(StatusCodes.UnprocessableEntity)
      }
    }

  private def updateStorage: Route =
    (put & entity(as[StorageEntry[String, String]])) { entry =>

      service.updateStorage(UpdateStorageRequest(entry)) match {
        case UpdateStorageResponse.Updated(s) =>
          complete(s)
        case UpdateStorageResponse.NotFound(_) =>
          complete(StatusCodes.NotFound)
      }
    }

  private val DeleteStorageKey = Segment.map(DeleteStorageRequest[String, String])
  private def deleteStorage: Route =
    (delete & path(DeleteStorageKey)) { key =>
      service.deleteStorage(key) match {
        case DeleteStorageResponse.Deleted(s) =>
          complete(s)
        case DeleteStorageResponse.NotFound(_) =>
          complete(StatusCodes.NotFound)
      }
    }
}