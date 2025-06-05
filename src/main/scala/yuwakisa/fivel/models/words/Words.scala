package yuwakisa.fivel.models.words

import yuwakisa.fivel.Logging

import scala.util.Random
import yuwakisa.fivel.models.basic.Resource

object Words extends Logging:

  val random: Random = Random()

  /**
   * @param path the path to the resource
   * @return the name of the resource without the path or extension
   */
  def name(path: String): String =
    path.split("/").last.replace(".txt", "").toLowerCase

  /**
   * Parse a resource into an associator
   * @param path
   * @param random
   * @return the name of the resource and associator for it
   */
  def parseOne(path: String)(implicit random: Random = Random()): (String, Associator) =
    val input = Resource(path).toSeq
    (name(path), Associator(3, input).build())

  /**
   * Load all the tables from a list of sources
   * @param sources list of resource paths
   * @return tables from all the sources
   */
  def load(sources: Seq[String])(implicit random: Random = Random()): Words =
    val wordsByName = sources.map(parseOne).toMap
    Words(wordsByName)

class Words(val wordsByName: Map[String, Associator]) :

  def toSeq: Seq[String] = wordsByName.keys.toSeq.sorted

  def uniq(name: String)(implicit random: Random = Words.random): Either[String, String] =
    wordsByName.get(name.toLowerCase) match
      case None =>
        Left(s"No associator for $name")
      case Some(t) =>
        Right(t.uniq())