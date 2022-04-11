import { registerClientExtension } from 'camunda-modeler-plugin-helpers';

import ConvertBpmnToCamunda8Extension from './bpmn/ConvertBpmnToCamunda8Extension';
import ConvertDmnToCamunda8Extension from './dmn/ConvertDmnToCamunda8Extension';

registerClientExtension(ConvertBpmnToCamunda8Extension);
registerClientExtension(ConvertDmnToCamunda8Extension);