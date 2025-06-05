package yuwakisa.fivel

import org.slf4j.{LoggerFactory, Logger as SLF4JLogger}

/**
 * A trait that provides logging functionality to any class that mixes it in.
 * Usage:
 * class MyClass extends Logging {
 *   logger.info("Hello world!")
 * }
 */
trait Logging:
  protected val logger: SLF4JLogger = LoggerFactory.getLogger(this.getClass)

/**
 * Extension methods for getting loggers
 */
extension (clazz: Class[?])
  def getLogger: SLF4JLogger = LoggerFactory.getLogger(clazz)

extension (obj: Any)
  def getLogger: SLF4JLogger = LoggerFactory.getLogger(obj.getClass) 
  
extension (name: String)
  def getLogger: SLF4JLogger = LoggerFactory.getLogger(name) 