import org.bouncycastle.asn1.sec.{SECNamedCurves, SECObjectIdentifiers}
import org.bouncycastle.crypto.params.{ECDomainParameters, ECNamedDomainParameters, ECPrivateKeyParameters, ECPublicKeyParameters}
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory

import java.math.BigInteger

object Secp256k1 {

  val curveName = SECObjectIdentifiers.secp256k1
  val curveParameters = SECNamedCurves.getByOID(curveName)
  val domainParameters = new ECDomainParameters(
    curveParameters.getCurve,
    curveParameters.getG,
    curveParameters.getN,
    curveParameters.getH
  )
  val namedDomainParameters = new ECNamedDomainParameters(curveName, domainParameters)

  def derivePublicKey(privateKey: Array[Byte]): Array[Byte] = {
    val privateKeyParams = new ECPrivateKeyParameters(
      new BigInteger(1, privateKey),
      domainParameters
    )
    val publicKeyParams = new ECPublicKeyParameters(
      curveParameters.getG.multiply(privateKeyParams.getD),
      namedDomainParameters
    )
    SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(publicKeyParams).getEncoded("DER")
  }

}
