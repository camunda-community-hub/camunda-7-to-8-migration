import {
    registerBpmnJSPlugin
  } from 'camunda-modeler-plugin-helpers';
import ConvertToCamundaCloudPlugin from './ConvertToCamundaCloudPlugin';

registerBpmnJSPlugin({
    __init__: [ 'convertToCamundaCloudPlugin' ],
    convertToCamundaCloudPlugin: [ 'type', ConvertToCamundaCloudPlugin ]
  });