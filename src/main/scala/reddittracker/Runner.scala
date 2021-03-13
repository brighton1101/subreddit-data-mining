package com.brighton1101.reddittracker

import com.brighton1101.reddittracker.common.CliAction
import com.brighton1101.reddittracker.fetchreddit.FetchRedditAction

object Runner {

  lazy val commands = Map[String, CliAction](
    "fetch" -> new FetchRedditAction()
  )

  val helpMsg =
f"""Reddit ETL Pipeline:
    [cmd][...subcommands]
    Available commands: ${commands.map { case (k, _) => k }.mkString(", ")}
"""

  def displayHelp: Unit = println(helpMsg)

  def help: Unit = println(helpMsg)
  def main(args: Array[String]): Unit = {
    if (args.isEmpty)
      displayHelp
    else
      commands.get(args(0)) match {
        case Some(s) => s.run(args.toSeq.drop(1))
        case None => displayHelp
      }
  }
}
