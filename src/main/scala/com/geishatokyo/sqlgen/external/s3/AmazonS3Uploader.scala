package com.geishatokyo.sqlgen.external.s3

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth.BasicAWSCredentials
import java.io.ByteArrayInputStream
import com.amazonaws.services.s3.model.{ObjectMetadata, CannedAccessControlList, PutObjectRequest}
import com.geishatokyo.sqlgen.external.FileUploader
import com.geishatokyo.sqlgen.logger.Logger
import com.geishatokyo.sqlgen.util.FileUtil

/**
 *
 * User: takeshita
 * Create: 12/01/31 14:44
 */

class AmazonS3Uploader(bucketName: String, accessKey: String, secretKey: String) extends FileUploader {

  var s3: AmazonS3Client = null

  connect()

  def connect() = {
    s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey))
    Logger.log("Connect to amazon s3 (bucketname = %s)".format(bucketName))
    if (!s3.doesBucketExist(bucketName)) {
      Logger.log("Bucket:%s is not found!".format(bucketName))
      throw new Exception("Bucket:%s is not found!".format(bucketName))
    }
  }

  def exist_?(filename: String) = {
    try {
      val s3Obj = s3.getObject(bucketName, filename)
      if (s3Obj != null) s3Obj.getObjectContent.close()

      s3Obj != null
    } catch {
      case e: Exception => {
        Logger.log("file:%s is not found".format(filename))
        false
      }
    }
  }

  def upload(key: String, data: Array[Byte]): Boolean = {
    if (key == null) {
      Logger.log("key is null")
      return false
    }

    if (data == null || data.length == 0) {
      Logger.log("Data is empty")
      return false
    }

    val keyOnS3 = key
    Logger.log("Upload to s3 /%s/%s".format(bucketName, keyOnS3))
    val req = new PutObjectRequest(bucketName, keyOnS3,
      new ByteArrayInputStream(data), generateObjectMetadata(key, data))
    val result = s3.putObject(req)
    Logger.log("Success to upload : " + keyOnS3)
    addPermissions(keyOnS3)
    true
  }


  def getContentType(key: String) = try {
    val (dir, name, ext) = FileUtil.splitPathAndNameAndExt(key)
    ext.substring(1) match {
      case "svg" => "image/svg+xml"
      case "png" => "image/png"
      case "jpg" | "jpeg" => "image/jpeg"
      case "zip" => "application/zip"
      case "xml" => "application/xml"
      case _ => "image/svg+xml"
    }
  } catch {
    case e : Throwable => "image/svg+xml"
  }

  def generateObjectMetadata(key: String, data: Array[Byte]) = {

    val metadata = new ObjectMetadata()
    metadata.setContentLength(data.length)
    metadata.setContentType(getContentType(key))

    metadata
  }


  private def addPermissions(key: String) = {
    s3.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead)
  }


}