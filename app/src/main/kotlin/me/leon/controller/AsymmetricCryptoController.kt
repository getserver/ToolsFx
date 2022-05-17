package me.leon.controller

import me.leon.ext.*
import me.leon.ext.crypto.*
import tornadofx.*

class AsymmetricCryptoController : Controller() {

    fun pubEncrypt(
        key: String,
        alg: String,
        data: String,
        isSingleLine: Boolean = false,
        reserved: Int = 11,
        inputEncode: String = "raw",
        outputEncode: String = "base64"
    ): String =
        catch({ "encrypt error: $it}" }) {
            println("encrypt $key  $alg $data")
            if (isSingleLine)
                data.lineAction2String {
                    it.decodeToByteArray(inputEncode)
                        .pubEncrypt(key, alg, reserved)
                        .encodeTo(outputEncode)
                }
            else
                data.decodeToByteArray(inputEncode)
                    .pubEncrypt(key, alg, reserved)
                    .encodeTo(outputEncode)
        }

    fun lengthFromPub(key: String): Int = key.toPublicKey("RSA")!!.bitLength()

    fun lengthFromPri(key: String): Int = key.toPrivateKey("RSA")!!.bitLength()

    fun priDecrypt(
        key: String,
        alg: String,
        data: String,
        isSingleLine: Boolean = false,
        inputEncode: String = "base64",
        outputEncode: String = "raw"
    ): String =
        catch({ "decrypt error: $it" }) {
            println("decrypt $key  $alg $data")
            if (isSingleLine)
                data.lineAction2String {
                    it.decodeToByteArray(inputEncode)
                        .privateDecrypt(key, alg)
                        .encodeTo(outputEncode)
                }
            else data.decodeToByteArray(inputEncode).privateDecrypt(key, alg).encodeTo(outputEncode)
        }

    fun priEncrypt(
        key: String,
        alg: String,
        data: String,
        isSingleLine: Boolean = false,
        reserved: Int = 11,
        inputEncode: String = "raw",
        outputEncode: String = "base64"
    ): String =
        catch({ "encrypt error: $it" }) {
            if (isSingleLine)
                data.lineAction2String {
                    it.decodeToByteArray(inputEncode)
                        .privateEncrypt(key, alg, reserved)
                        .encodeTo(outputEncode)
                }
            else
                data.decodeToByteArray(inputEncode)
                    .privateEncrypt(key, alg, reserved)
                    .encodeTo(outputEncode)
        }

    fun pubDecrypt(
        key: String,
        alg: String,
        data: String,
        isSingleLine: Boolean = false,
        inputEncode: String = "base64",
        outputEncode: String = "raw"
    ) =
        catch({ "decrypt error: $it" }) {
            println("decrypt $key  $alg $data")
            if (isSingleLine)
                data.lineAction2String {
                    it.decodeToByteArray(inputEncode).pubDecrypt(key, alg).encodeTo(outputEncode)
                }
            else data.decodeToByteArray(inputEncode).pubDecrypt(key, alg).encodeTo(outputEncode)
        }
}
