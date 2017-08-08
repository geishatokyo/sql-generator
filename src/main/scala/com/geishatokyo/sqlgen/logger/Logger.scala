package com.geishatokyo.sqlgen.logger

/**
 *
 * User: takeshita
 * Create: 12/01/24 23:50
 */

trait Logger {

  def log(message : String) : Unit
  def log(message : String , e : Exception) : Unit
  def replace( valueType : String , before : String , after : String) : Unit

}

object Logger extends Logger{
  val logger : Logger = new ConsoleLogger

  def log(message: String) {logger.log(message)}

  def log(message: String, e: Exception) {logger.log(message,e)}

  def logWarning(message: String){logger.log(message)}

  
  def replace( valueType : String , before : String , after : String){
    logger.replace(valueType,before,after)
  }
}

class ConsoleLogger extends Logger{
  def log(message: String) {
    println(message)
  }

  def log(message: String, e: Exception) {
    println(message + " -- " + e.getMessage +  e.printStackTrace())

  }

  def replace(valueType: String, before: String, after: String) {
    println("Replace %s: %s -> %s".format(valueType,before,after))
  }
}