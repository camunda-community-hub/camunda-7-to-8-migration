# Process Application for Camunda 7 using External Service Tasks

## The process

![Payment process using external tasks](docs/payment_process.png)

## Deployment

The Spring Boot process application deploys the process [on startup](src/main/java/org/camunda/community/migration_example/ExternalTaskWorkerApplication.java).

## Create instances

you can create process instances with zbctl:

```
zbctl create instance PaymentProcess --variables '{"orderTotal":49.99, "customerId":"cust30","cardNumber":"1234 5678", "CVC":"156","expiryDate":"10/23"}' --insecure
```