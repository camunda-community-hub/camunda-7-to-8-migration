var registerBpmnJSPlugin = require('camunda-modeler-plugin-helpers').registerBpmnJSPlugin;

var ConvertToCamundaCloudPlugin = require('./ConvertToCamundaCloudPlugin');
registerBpmnJSPlugin(ConvertToCamundaCloudPlugin);
