package com.brighton1101.reddittracker.common

trait CliAction {
  def run(args: Seq[String]): Unit
}

