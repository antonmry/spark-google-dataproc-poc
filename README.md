## Env

## Cloud

Create project and activate DataProc API: https://cloud.google.com/dataproc/docs/quickstarts/quickstart-gcloud

Create budget and DataProc cluster:

```
gcloud config set project testsparkdataproc
gcloud config set dataproc/region europe-west4 
gsutil mb -l europe-west4 gs://testsparkdataproc/
gcloud dataproc clusters create testsparkdataproc-cluster --region=europe-west4 --max-age=7h --single-node --bucket=testsparkdataproc
```

Upload the file to the bucket:

```
gsutil cp data/ibrd-statement-of-loans-latest-available-snapshot.csv gs://testsparkdataproc/
```

Compile the project:

```
./gradlew build
```

Submit the job:

```
gcloud dataproc jobs submit spark --cluster testsparkdataproc-cluster  \
--jars build/libs/dataprocJavaDemo-1.0-SNAPSHOT.jar \
--class org.example.dataproc.InternationalLoansAppDataproc \
-- gs://testsparkdataproc ibrd-statement-of-loans-latest-available-snapshot.csv results 
```

Check the result:

```
gsutil ls gs://testsparkdataproc/results/
```

## Local

Download Spark:

```
wget http://ftp.nluug.nl/internet/apache/spark/spark-2.4.4/spark-2.4.4-bin-hadoop2.7.tgz
tar -zxvf spark-2.4.4-bin-hadoop2.7.tgz
cp spark-2.4.4-bin-hadoop2.7/conf/log4j.properties.template spark-2.4.4-bin-hadoop2.7/conf/log4j.properties
```

Change the log level to WARN in log4.properties.

Compile the project:

```
./gradlew build
```

Submit the job to spark:

```
../spark-2.4.4-bin-hadoop2.7/bin/spark-submit \ 
--class "org.example.dataproc.InternationalLoansApp" \
--master local[4] \
build/libs/dataprocJavaDemo-1.0-SNAPSHOT.jar 
```
