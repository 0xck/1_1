package ru.otus.sc.user.service.impl

import ru.otus.sc.user.dao.UserDao
import ru.otus.sc.user.model._
import ru.otus.sc.user.service.UserService

class UserServiceImpl(dao: UserDao) extends UserService {
  def createUser(request: CreateUserRequest): CreateUserResponse = {
    /**
    * it can return CreateUserResponse.Error
    * but in current UserDao implementation there is not option for this
    * so emulate it with palindrome or empty name
    */
    val userName = request.user.name.firstName
    if (userName == userName.reverse)
      CreateUserResponse.Error("palindrome or empty name is not allowed in current DB version")
    else
      CreateUserResponse.Created(dao.createUser(request.user))
  }

  def getUser(request: GetUserRequest): GetUserResponse =
    dao.getUser(request.uniqueId) match {
      case Some(user) => GetUserResponse.Found(user)
      case None       => GetUserResponse.NotFound(request.uniqueId)
    }

  def updateUser(request: UpdateUserRequest): UpdateUserResponse =
    request.user.uniqueId match {
      case Some(uniqueId) =>
        dao.updateUser(request.user) match {
          case Some(user) => UpdateUserResponse.Updated(user)
          case None       => UpdateUserResponse.NotFound(uniqueId)
        }
      case None =>
        if (request.user.id.isEmpty)
          UpdateUserResponse.ErrorWrongId
        else
          UpdateUserResponse.ErrorNoUniqueId
    }

  def deleteUser(request: DeleteUserRequest): DeleteUserResponse =
    dao
      .deleteUser(request.uniqueId)
      .map(DeleteUserResponse.Deleted)
      .getOrElse(DeleteUserResponse.NotFound(request.uniqueId))

  def findUsers(request: FindUsersRequest): FindUsersResponse =
    request match {
      case FindUsersRequest.ByFirstName(firstName) =>
        FindUsersResponse.Result(dao.findByFirstName(firstName))
      case FindUsersRequest.ByLastName(lastName) =>
        FindUsersResponse.Result(dao.findByLastName(lastName))
      case FindUsersRequest.ByTag(tagId) =>
        FindUsersResponse.Result(dao.findByTag(tagId))
      case FindUsersRequest.GetAll =>
        FindUsersResponse.Result(dao.getAllUsers)
    }
}
