# ChainTest Configuration

## Java

```properties
# chaintest configuration
chaintest.project.name=default

# storage
chaintest.storage.service.enabled=false
# [azure-blob, aws-s3]
chaintest.storage.service=azure-blob
# s3 bucket or azure container name
chaintest.storage.service.container-name=

# generators:
## chainlp
chaintest.generator.chainlp.enabled=false
chaintest.generator.chainlp.class-name=com.aventstack.chaintest.generator.ChainLPGenerator
chaintest.generator.chainlp.host.url=http://localhost/
chaintest.generator.chainlp.client.request-timeout-s=30
chaintest.generator.chainlp.client.expect-continue=false
chaintest.generator.chainlp.client.max-retries=3

## simple
chaintest.generator.simple.enabled=true
chaintest.generator.simple.document-title=chaintest
chaintest.generator.simple.class-name=com.aventstack.chaintest.generator.ChainTestSimpleGenerator
chaintest.generator.simple.output-file=target/chaintest/Index.html
chaintest.generator.simple.offline=false
chaintest.generator.simple.dark-theme=true
chaintest.generator.simple.datetime-format=yyyy-MM-dd hh:mm:ss a
chaintest.generator.simple.js=
chaintest.generator.simple.css=

## email
chaintest.generator.email.enabled=true
chaintest.generator.email.class-name=com.aventstack.chaintest.generator.ChainTestEmailGenerator
chaintest.generator.email.output-file=target/chaintest/Email.html
chaintest.generator.email.datetime-format=yyyy-MM-dd hh:mm:ss a
```