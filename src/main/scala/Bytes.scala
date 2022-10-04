import java.security.SecureRandom

object Bytes {

  def generate(seed: Array[Byte]): Array[Byte] = {
    val secureRandom: SecureRandom = new SecureRandom(seed) // not safe - seed in the memory
    val output = Array[Byte](32)
    secureRandom.nextBytes(output)
    output
  }

  def deriveChecksum(bytes: Array[Byte]): Array[Byte] =
    Codec.sha256(Codec.sha256(bytes)).take(4)

  def appendChecksum(bytes: Array[Byte]): Array[Byte] =
    bytes ++ deriveChecksum(bytes)


}
