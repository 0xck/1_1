package ru.otus.sc.echo.service.impl

import ru.otus.sc.echo.dao.EchoDao
import ru.otus.sc.echo.model.{EchoRequest, EchoResponse}
import ru.otus.sc.echo.service.EchoService

class EchoServiceImpl(dao: EchoDao) extends EchoService {

  def echo(request: EchoRequest): EchoResponse =
    if (request.repeatNum > 0 & request.repeatNum <= 5)
      EchoResponse.Response(s"${dao.getResponse(request.echoRequest, request.repeatNum)}")
    else EchoResponse.Error("Number of replies has to be from 1 to 5")
}
