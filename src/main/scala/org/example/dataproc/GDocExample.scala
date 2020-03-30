package org.example.dataproc

import java.io.File

import com.github.potix2.spark.google.spreadsheets.SparkSpreadsheetService
import org.apache.spark.sql.types.{DateType, IntegerType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}

object GDocExample {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder
      .master("local[*]")
      .appName("Simple Application")
      .getOrCreate()

    val someData = Seq(
      Row(1, 0, java.sql.Date.valueOf("2016-11-01")),
      Row(2, 1, java.sql.Date.valueOf("2016-11-02")),
      Row(3, 2, java.sql.Date.valueOf("2016-11-03"))
    )

    val someSchema = List(
      StructField("apples", IntegerType, true),
      StructField("oranges", IntegerType, true),
      StructField("date", DateType, true)
    )

    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(someData),
      StructType(someSchema)
    )

    // Basic stats
    println("Rows of data:%d%n", df.count)
    println("Schema:")
    df.printSchema()

    // Google Spreadsheet upload

    val serviceAccount = "231211187532-compute@developer.gserviceaccount.com"
    val credentialFile = "credential.p12"
    val spreadsheetId = "1pvUORlpofFGDdMHBufL2vFh4-_9zVz0DO9qgWOrytBM"
    val sheet = "Sheet1"

    val sc = SparkSpreadsheetService.SparkSpreadsheetContext(
      Option(serviceAccount),
      new File(credentialFile))

    val s = sc.findSpreadsheet(spreadsheetId)

    s.deleteWorksheet(sheet)

    df.write.
      format("com.github.potix2.spark.google.spreadsheets").
      option("serviceAccountId", serviceAccount).
      option("credentialPath", credentialFile).
      save(spreadsheetId + "/" + sheet)
  }
}

