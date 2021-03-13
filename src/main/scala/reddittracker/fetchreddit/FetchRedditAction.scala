package com.brighton1101.reddittracker.fetchreddit

import com.brighton1101.reddittracker.common.CliAction

class FetchRedditAction extends CliAction {
  def run(args: Seq[String]): Unit = {
    args.foreach(println(_))
  }
}
