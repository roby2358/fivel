package yuwakisa.servel.mcp

import org.slf4j.{LoggerFactory, Logger as SLF4JLogger}

/**
 * Extension methods for getting loggers
 */
extension (clazz: Class[?])
  def getLogger: SLF4JLogger = LoggerFactory.getLogger(clazz)

extension (obj: Any)
  def getLogger: SLF4JLogger = LoggerFactory.getLogger(obj.getClass) 
  
extension (name: String)
  def getLogger: SLF4JLogger = LoggerFactory.getLogger(name)

/**
 * Extension method for tap
 */
extension[A] (a: A)
  def tap(f: A => Unit): A =
    f(a)
    a