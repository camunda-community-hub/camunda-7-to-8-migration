import { registerBpmnJSPlugin } from 'camunda-modeler-plugin-helpers';
import ConvertToCamunda8Plugin from './ConvertToCamunda8Plugin';

/*
registerBpmnJSPlugin({
  __init__: [ 'convertToCamundaCloudPlugin' ],
  convertToCamundaCloudPlugin: [ 'type', ConvertToCamundaCloudPlugin ]
});
*/

export default {
  __init__: [ 'convertToCamunda8Plugin' ],
  convertToCamunda8Plugin: [ 'type', ConvertToCamunda8Plugin ]
};