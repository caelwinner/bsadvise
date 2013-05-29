package ar.com.caeldev.bsacore.serializer

import org.json4s.{ NoTypeHints, native }
import native.Serialization.{ read, write => swrite }
import org.json4s.ext.JodaTimeSerializers

class JsonSerializer[T <: AnyRef](implicit mot: Manifest[T]) extends Serializer[T, String] {

  implicit val formats = native.Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all

  def serialize(entity: T): String = {
    swrite(entity)
  }

  def deserialize(target: String): T = {
    read[T](target)
  }
}
