# Camunda Modeler Plugin to convert BPMN models from Camunda Platform to Camunda Cloud

This modeler plugin can convert BPMN models that were created for Camunda Platform into models can be executed on Camunda Cloud.

## Migrating elements and attributes

All technical attributes and extension elements that can be migrated in a meaningful way are migrated. Whenever attributes are not supported in Camunda Cloud a warning is shown on the respective element in the model. Details on how elements are migrated are described in the [migration guide](https://docs.camunda.io/docs/guides/migrating-from-Camunda-Platform/).

**Important note:*** This plugin might not fully migrate your model, but it can give you a jump-start.

You can extended the [migration logic](client/ConvertToCamunda8Plugin.js#L230) easily to add your own custom migration rules.

## Migrating expressions

This plugin also migrates simple JUEL expressions of Camunda Platfom into FEEL expressions used in Camunda Cloud. You can have a look into [sample expressions in the test cases](client/JuelToFeelConverter.test.js) and might also want to extend the [expression converter](client/JuelToFeelConverter.test.js) to suite your needs.

# How to install and use

**Download the plugin** from the [release page](https://github.com/camunda-community-hub/camunda-7-to-8-migration/releases/latest). 

Pick the `modeler-plugin-7-to-8-converter.zip` file and extract it into

* ```{path_to_modeler}/plugins``` or
* ```C:\Users\{user_name}\AppData\Roaming\camunda-modeler\plugins``` (Windows) or
* ```/Users/{user_name}/Library/Application Support/camunda-modeler/plugins``` (Mac OS)

The latter ways preserve the plugins when the modeler installation is replaced with a new version and allows to use the plugins from several installed versions. See also [Camunda Desktop Modeler plugins docs](https://docs.camunda.io/docs/components/modeler/desktop-modeler/plugins/)

**Restart your Camunda Modeler**

![menu](menu.png)

You can now **convert a Camunda 7 BPMN Model** or **DMN model** by going to `Plugins -> Migration -> Convert Model from Camunda 7 to 8`. You need to have the model open that you want to convert.

The convertion adds hints to the model wherever appropriate:

![screenshot](screenshot.png)


# Building the plugin from the sources

If you want to build the plugin from the sources (e.g. because you extended the mirgation logic) simply do a:

```
npm update
npm run dev
```
