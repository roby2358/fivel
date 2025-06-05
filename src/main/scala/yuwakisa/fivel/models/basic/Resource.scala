package yuwakisa.fivel.models.basic

import yuwakisa.fivel.Logging
import java.io.{BufferedReader, File, InputStreamReader}
import java.util.jar.JarFile
import scala.collection.mutable
import scala.language.postfixOps
import scala.util.chaining.*
import scala.util.matching.Regex

class Resource(path: String) extends Logging:

  /**
   * Open a buffered reader for the resource
   *
   * @return option of the buffered reader
   */
  def reader: Option[BufferedReader] =
    val in = Option(this.getClass.getResourceAsStream(path))
    in.map(s => new BufferedReader(InputStreamReader(s)))

  /**
   * Read a reader to get all the contents into a sequence
   *
   * @param r buffered reader
   * @return the sequence of strings
   */
  def readAll(r: BufferedReader): Seq[String] =
    Iterator.continually(r.readLine)
      .takeWhile(_ != null)
      .toSeq
      .tap(_ => r.close())

  /**
   * Returns a seq of the newline-separated lines in the resource, or the
   * resources contained if it's a directory
   *
   * @return a seq of the lines in the resource
   */
  def toSeq: Seq[String] =
    reader.map(readAll).getOrElse(Seq[String]())

  /**
   * On the file system, getResource as stream lists the files
   * @return a sequence of file names
   */
  def fileStreamPaths: Seq[String] =
    val p = path + (if path.last == '/' then "" else "/")
    toSeq.map(p + _)

  val pathRegex: Regex = """jar:file:(.*)!.*""".r
  val fileRegex: Regex = Regex(s"""${path.substring(1)}.*[^/]""")

  /**
   * getResourceAsStream doesn't list anything inside a jar. Have to traverse it ourselves
   * @param jarFilePath path to the jar file
   * @return a sequence of file names
   */
  def jarFilePaths(jarFilePath: String): Seq[String] =
    Option(JarFile(File(jarFilePath))) match
      case None =>
        logger.debug(s"No $jarFilePath")
        Seq()
      case Some(jar) =>
        logger.debug(s"Some(jar) $jarFilePath")
        val buffer = mutable.ArrayBuffer[String]()
        val e = jar.entries
        while
          e.hasMoreElements
        do
          val ee = e.nextElement.getRealName
          if fileRegex.matches(ee) then
            buffer.addOne("/" + ee)
        buffer.toSeq

  /**
   * @return a seq of paths to resources inside this resource (if it's a directory)
   */
  def paths: Seq[String] =
    logger.debug(path)
    val uri = this.getClass.getResource(path).toURI.toString
    pathRegex.findFirstMatchIn(uri).map(_.group(1)) match
      case None =>
        fileStreamPaths
      case Some(jarFilePath) =>
        jarFilePaths(jarFilePath)
