## Env

Create project and activate DataProc API: https://cloud.google.com/dataproc/docs/quickstarts/quickstart-gcloud

Create budget and DataProc cluster:

```
gcloud config set project itamarsparkdataproc
gsutil mb -l europe-west4 gs://itamarsparkdataproc/
gcloud dataproc clusters create itamarsparkdataproc-cluster --region=europe-west4 --max-age=7h --single-node --bucket=itamarsparkdataproc

```


