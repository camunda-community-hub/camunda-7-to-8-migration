import { registerClientExtension } from 'camunda-modeler-plugin-helpers';

import ConvertBpmnToCamundaCloudExtension from './bpmn/ConvertBpmnToCamundaCloudExtension';
import ConvertDmnToCamundaCloudExtension from './dmn/ConvertDmnToCamundaCloudExtension';

registerClientExtension(ConvertBpmnToCamundaCloudExtension);
registerClientExtension(ConvertDmnToCamundaCloudExtension);