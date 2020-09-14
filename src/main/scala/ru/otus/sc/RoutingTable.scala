package ru.otus.sc

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ru.otus.sc.countdown.dao.impl.CountdownDaoImpl
import ru.otus.sc.countdown.route.CountdownRouter
import ru.otus.sc.countdown.service.impl.CountdownServiceImpl
import ru.otus.sc.counter.dao.impl.CounterDaoImpl
import ru.otus.sc.counter.route.CounterRouter
import ru.otus.sc.counter.service.impl.CounterServiceImpl
import ru.otus.sc.echo.dao.impl.EchoDaoImpl
import ru.otus.sc.echo.route.EchoRouter
import ru.otus.sc.echo.service.impl.EchoServiceImpl
import ru.otus.sc.greet.dao.impl.GreetingDaoImpl
import ru.otus.sc.greet.route.GreetUserRouter
import ru.otus.sc.greet.service.impl.GreetingServiceImpl
import ru.otus.sc.reverse.dao.impl.ReverseDaoImpl
import ru.otus.sc.reverse.route.ReverseRouter
import ru.otus.sc.reverse.service.impl.ReverseServiceImpl
import ru.otus.sc.storage.dao.impl.StorageDaoImpl
import ru.otus.sc.storage.route.SSStorageRouter
import ru.otus.sc.storage.service.impl.StorageServiceImpl
import ru.otus.sc.sum.dao.impl.SumDaoImpl
import ru.otus.sc.sum.route.SumRouter
import ru.otus.sc.sum.service.impl.SumServiceImpl
import ru.otus.sc.user.dao.impl.{UserDaoImpl, UserTagDaoImpl}
import ru.otus.sc.user.route.{UserRouter, UserTagRouter}
import ru.otus.sc.user.service.impl.{UserServiceImpl, UserTagServiceImpl}

object RoutingTable {
  /**
  * greet with provided object
  * panic if `isHuman` parameter is false
  * greeting request requires implicit parameter for extracting name from the object
  * in current implementation object is User and implicit can be borrowed from
  * `ru.otus.sc.user.implicits.UserNameImplicits._`
  */
  private val greetUserRouter = new GreetUserRouter(new GreetingServiceImpl(new GreetingDaoImpl))
  /**
  * reply on requested value with the same value
  * echo value can be multiplied up to 5 times with `repeatNum` value
  * no multiply answer by default (`repeatNum` is 1)
  * proper values return answer with `EchoResponse.Response`
  * bad values return error with `EchoResponse.Error`
  */
  private val echoRouter = new EchoRouter(new EchoServiceImpl(new EchoDaoImpl))
  /** reverse given string from end to begin */
  private val reverseRouter = new ReverseRouter(new ReverseServiceImpl(new ReverseDaoImpl))
  /**
  * sum 2 given values locally
  * sum can be performed externally with `external` parameter
  * in current implementation external computation is artificial delay
  * which was implemented with lazy value
  */
  private val sumRouter = new SumRouter(new SumServiceImpl(new SumDaoImpl))
  /**
  * manage users
  * user is compound from:
  *   id, which is DB related stuff like pk
  *   unique id, UUID, this item is unique for whole system
  *   user name, which is compound from: first, last, middle, patronymic names and title
  *   age
  *   set of tags
  * CRUD operations are available as well as search for first, last name and tag
  */
  private val userDao     = new UserDaoImpl
  private val userService = new UserServiceImpl(userDao)
  private val userRouter = new UserRouter(userService)
  /**
  * manage user tags
  * tags are bound with users and related only them
  * tag is compound from:
  *   id, which is DB related stuff like pk
  *   name, for human
  * CRUD operations are available as well as search for name
  * also associated with certain tag users can be obtained in search request
  * tags and users are totally independent
  * tagging and untagging users do not allow manage tags on users directly
  * and have to be performed as separate operations for users and tags
  * e.g. tagging user example,
  * imagine we already have app, user, tag and all operations are done successfully:
  *  val tagId = 42L
  *  val uniqueUserId = UUID.fromString("4a71a58b-4b39-44fc-ae52-76b9657be280")
  *  val tag = app.getUserTag(GetUserTagRequest(tagId))
  *  val user = app.getUser(GetUserRequest(uniqueUserId))
  *  val updatedUser = user.copy(tags = (user.tags + tag))
  *  app.tagUser(UpdateTagUserRequest(tagId, uniqueUserId))
  *  app.updateUser(UpdateUserRequest(updatedUser))
  */
  private val userTagDao = new UserTagDaoImpl
  private val userTagService = new UserTagServiceImpl(userTagDao)
  private val userTagRouter = new UserTagRouter(userTagService)
  /**
  * manage countdowns
  * countdown is auto-decrement item with initial `value`, default value is 1
  * update auto-decreases value by 1
  * when countdown reaches 0 it stops decrease value and swaps state from Tick to Done
  * Countdown.Done is composed from id, UUID, this item is unique for whole system
  * State above can not be updated anymore, also it can be created `CountdownDone`
  * Countdown.Tick is composed from:
  *   id, UUID, this item is unique for whole system
  *   updater, UUID this is id of something that create or update countdown
  *   value of countdown
  * CRUD operations are available as well as search for:
  *   values, updaters, Done and NonDone states
  * searching for values requires predicate (t: T, i: T) => Boolean for comparing items
  * where `t` is target and `i` is item in DB
  */
  private val countdownDao = new CountdownDaoImpl
  private val countdownService = new CountdownServiceImpl(countdownDao)
  private val countdownRouter = new CountdownRouter(countdownService)
  /**
  * manage counters
  * counter is auto-increment item with initial `value`, default value is 1
  * update increments counter by 1
  * counter value can not be updated itself, it can be only incremented
  * timestamp is updated on every counter update
  * counter is compound from:
  *   id, UUID, this item is unique for whole system
  *   timestamp of update
  *   value of counter
  * CRUD operations are available as well as search for values or timestamps
  * searching requires predicate (t: T, i: T) => Boolean for comparing items
  * where `t` is target and `i` is item in DB
  */
  private val counterDao = new CounterDaoImpl
  private val counterService = new CounterServiceImpl(counterDao)
  private val counterRouter = new CounterRouter(counterService)
  /**
  * manage storage
  * provide value by requested key
  * StorageEntry is composed from key and value of defined types
  * in current implementation keys and values are String
  * CRUD operations are available as well as search for value itself or predicate on value
  * searching with predicate requires predicate i: V => Boolean for comparing items
  * where `i` is item in DB
  */
  private val storageDao     = new StorageDaoImpl[String, String]
  private val storageService = new StorageServiceImpl(storageDao)
  private val storageRouter = new SSStorageRouter(storageService)

  private val apiVersion = "v1"
  private val route : Route = pathPrefix("api" / apiVersion) {

    greetUserRouter.route ~
    echoRouter.route ~
    reverseRouter.route ~
    sumRouter.route ~
    userRouter.route ~
    userTagRouter.route ~
    storageRouter.route ~
    countdownRouter.route ~
    counterRouter.route
  }
  def getRoute : Route = route
}
