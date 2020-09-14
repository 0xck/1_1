package ru.otus.sc.user.service.impl

import ru.otus.sc.user.dao.UserTagDao
import ru.otus.sc.user.model._
import ru.otus.sc.user.service.UserTagService

class UserTagServiceImpl(dao: UserTagDao) extends UserTagService {
  def createUserTag(request: CreateUserTagRequest): CreateUserTagResponse = {

    /**
      * it can return CreateUserTagResponse.Error
      * but in current UserTagDao implementation there is not option for this
      * so emulate it with palindrome or empty tag name
      */
    val tagName = request.tag.tagName
    if (tagName == tagName.reverse)
      CreateUserTagResponse.Error(
        "palindrome or empty tag name is not allowed in current DB version"
      )
    else
      CreateUserTagResponse.Created(dao.createTag(request.tag))
  }

  def getUserTag(request: GetUserTagRequest): GetUserTagResponse =
    dao.getTag(request.tagId) match {
      case Some(user) => GetUserTagResponse.Found(user)
      case None       => GetUserTagResponse.NotFound(request.tagId)
    }

  def updateUserTag(request: UpdateUserTagRequest): UpdateUserTagResponse =
    request.tag.id match {
      case Some(tagId) =>
        dao.updateTag(request.tag) match {
          case Some(tag) => UpdateUserTagResponse.Updated(tag)
          case None      => UpdateUserTagResponse.NotFound(tagId)
        }
      case None => UpdateUserTagResponse.ErrorNoTagId
    }

  def deleteUserTag(request: DeleteUserTagRequest): DeleteUserTagResponse =
    dao
      .deleteTag(request.tagId)
      .map(DeleteUserTagResponse.Deleted)
      .getOrElse(DeleteUserTagResponse.NotFound(request.tagId))

  def findUserTags(request: FindUserTagsRequest): FindUserTagsResponse =
    request match {
      case FindUserTagsRequest.ByName(tagName) =>
        FindUserTagsResponse.TagsResult(dao.findByName(tagName))
      case FindUserTagsRequest.UsersByTag(tagId) =>
        FindUserTagsResponse.UsersResult(dao.getUsersByTag(tagId))
      case FindUserTagsRequest.GetAll =>
        FindUserTagsResponse.TagsResult(dao.getAllTags)
    }

  override def tagUser(request: UpdateTagUserRequest): UpdateTagUserResponse =
    dao
      .tagUser(request.tagId, request.uniqueUserId)
      .map(result => UpdateTagUserResponse.TaggedUser(result._1, result._2))
      .getOrElse(UpdateTagUserResponse.NotFoundTag(request.tagId))

  def untagUser(request: UpdateUntagUserRequest): UpdateUntagUserResponse =
    dao
      .untagUser(request.tagId, request.uniqueUserId)
      .map(result => UpdateUntagUserResponse.UntaggedUser(result._1, result._2))
      .getOrElse(UpdateUntagUserResponse.NotFoundTag(request.tagId))
}
