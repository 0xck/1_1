package ru.otus.sc.user.dao.impl

import java.util.UUID

import ru.otus.sc.user.dao.UserDao
import ru.otus.sc.user.model.User
import ru.otus.sc.user.model.User.UniqueUserId
import ru.otus.sc.user.model.UserTag.UserTagId

class UserDaoImpl extends UserDao {

  private var users: Map[UniqueUserId, User] = Map.empty

  /** emulate pk for users */
  private var id: Long = 1

  def createUser(user: User): User = {
    val newUniqueId = UUID.randomUUID
    val newUser     = user.copy(id = Some(id), uniqueId = Some(newUniqueId))
    id += 1
    users += (newUniqueId -> newUser)
    newUser
  }

  def getUser(uniqueUserId: UniqueUserId): Option[User] = users.get(uniqueUserId)

  def updateUser(user: User): Option[User] =
    for {
      uniqueId  <- user.uniqueId
      existUser <- users.get(uniqueId)
      /**
        * only uniqueId is required for user, id might not exist
        * if id exists then it has to match existing user id
        */
      if user.id.isEmpty || existUser.id == user.id
    } yield {
      val newUser = if (user.id.isEmpty) user.copy(id = existUser.id) else user
      users += (uniqueId -> newUser)
      newUser
    }

  def deleteUser(uniqueUserId: UniqueUserId): Option[User] =
    users.get(uniqueUserId) match {
      case deletedUser @ Some(_) =>
        users -= uniqueUserId
        deletedUser
      case _ => None
    }

  def findByFirstName(firstName: String): Seq[User] =
    users.values.filter(_.name.firstName == firstName).toVector

  def findByLastName(lastName: String): Seq[User] =
    users.values
      .collect(user =>
        user.name.lastName match {
          case Some(userLastName) if userLastName == lastName => user
        }
      )
      .toVector

  def findByTag(tag: UserTagId): Seq[User] =
    users.values.filter(_.tags.contains(tag)).toVector

  def getAllUsers: Seq[User] = users.values.toVector
}
