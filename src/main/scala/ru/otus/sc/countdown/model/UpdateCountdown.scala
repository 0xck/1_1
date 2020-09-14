package ru.otus.sc.countdown.model

import ru.otus.sc.countdown.model.Countdown.CountdownId

case class UpdateCountdownRequest(countdown: Countdown)

sealed trait UpdateCountdownResponse
object UpdateCountdownResponse {
  case class Updated(countdown: Countdown) extends UpdateCountdownResponse
  case class NotFound(id: CountdownId)     extends UpdateCountdownResponse
  case object CanNotUpdateDone             extends UpdateCountdownResponse
  // when countdownId is None
  case object ErrorWrongId extends UpdateCountdownResponse
}
