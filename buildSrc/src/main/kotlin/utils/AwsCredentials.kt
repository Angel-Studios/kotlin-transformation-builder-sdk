package utils

import software.amazon.awssdk.auth.credentials.AwsCredentials

fun awsCredentials(): AwsCredentials = object : AwsCredentials {
    override fun accessKeyId(): String = System.getenv()["AWS_ACCESS_KEY_ID"].orEmpty()
    override fun secretAccessKey(): String = System.getenv()["AWS_SECRET_ACCESS_KEY"].orEmpty()
}
