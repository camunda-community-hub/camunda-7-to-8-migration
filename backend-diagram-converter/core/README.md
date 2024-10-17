# Diagram Converter Core

This is the core module of the diagram converter, containing the `BpmnConverter` plus its factory, the `BpmnConverterFactory`.

## How does it work?

Works in 2 phases:

* exploring the BPMN XML
  * this is done in a visitor pattern, the interface to use is `DomElementVisitor`
  * each element gets visited
  * convertibles are created for each process element
  * they can be decorated with aspects for the conversion
* the conversion is executed
  * decorated convertibles are processed
  * the registered aspects are adjusted in the BPMN XML
  * this is done in another visitor pattern, the interface to use is `Conversion`
  * the diagram becomes a Camunda 8 diagram

## How can I use it?

You can bootstrap the `BpmnConverter` in an easy way:

```java

BpmnConverter converter = BpmnConverterFactory.getInstance().get();
```

This will return a converter that is bootstrapped using SPI for `DomElementVisitor`, `Conversion` and `NotificationService`.

If you want to build a custom converter, you can add to the SPI resources.

If this is not sufficient, you can bootstrap the converter in a custom way:

```java

import java.util.ArrayList;
import java.util.List;

List<DomElementVisitor> visitors = loadDomElementVisitors();
List<Conversion> conversions = loadConversions();
NotificationService notificationService = laodNotificationService();

BpmnConverter converter = new BpmnConverter(visitors, conversions, notificationService);
```

Next, you need `ConverterProperties`. They control the way the converter will handle several aspects of the conversion.

You can bootstrap the properties in an easy way:

```java

ConverterProperties properties = ConverterPropertiesFactory.getInstance().get();
```

You can also customize the used converter properties:

```java

DefaultConverterProperties defaultConverterProperties = new DefaultConverterProperties();
// I want to migrate to an older platform version
defaultConverterProperties.setPlatformVersion("8.5");

ConverterProperties properties = ConverterPropertiesFactory
        .getInstance()
        .merge(defaultConverterProperties);
```

Now, you are able to convert process models or just receive a check result:

```java

BpmnConverter converter = BpmnConverterFactory
        .getInstance()
        .get();
// this comes from the camunda 7 bpmn model package
BpmnModelInstance modelInstance = loadModel();
ConverterProperties properties = ConverterPropertiesFactory.getInstance().get();

// the results will be in the provided modelInstance
converter.convert(modelInstance, properties);

// the results are returned, the model instance is also modified

BpmnDiagramCheckResult result = converter.check(modelInstance, properties);
```

## How can I extend it?

Beside the way of using custom bootstrapping mechanisms, the easiest way to extend is by using the capabilities of the underlying SPI.

You can find an example in the `/example` section of the root project.
