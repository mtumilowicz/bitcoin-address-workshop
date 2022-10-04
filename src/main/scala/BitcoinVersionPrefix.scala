import enumeratum.{Enum, EnumEntry}

sealed abstract class BitcoinVersionPrefix(val value: Byte) extends EnumEntry

object BitcoinVersionPrefix extends Enum[BitcoinVersionPrefix] {

  case object MainNetwork extends BitcoinVersionPrefix(0x00)

  case object TestNet extends BitcoinVersionPrefix(0x6f)

  override def values: IndexedSeq[BitcoinVersionPrefix] = findValues
}
