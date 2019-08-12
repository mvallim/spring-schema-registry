# Schemas

1. **Register the schema**

    ```shell
    curl -s -k \
        -d "@people-schema.json" \
        -H "Content-Type: application/vnd.schemaregistry.v1+json" \
        -X POST \
        https://schema-registry-node01:8082/subjects/people-com.sample.avro.People/versions/ | jq
    ```

    The expected output looks like this:

    ```json
    {
      "id": 101
    }
    ```

2. **Check the subjects**

    ```shell
    curl -s -k \
        https://schema-registry-node01:8082/subjects/ | jq
    ```

    The expected output looks like this:

    ```json
    [
      "people-com.sample.avro.People"
    ]
    ```

3. **Check the versions**

    ```shell
    curl -s -k \
        https://schema-registry-node01:8082/subjects/people-com.sample.avro.People/versions | jq
    ```

    The expected output looks like this:

    ```json
    [
      1
    ]
    ```
