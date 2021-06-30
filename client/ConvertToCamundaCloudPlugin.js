'use strict';

var removeDiacritics = require('diacritics').remove;
var domify = require('min-dom/lib/domify'),
    domEvent = require('min-dom/lib/event'),
    domClasses = require('min-dom/lib/classes'),
    domQuery = require('min-dom/lib/query'),
    clear = require('min-dom/lib/clear'),
    BpmnJS = require('bpmn-js/lib/Modeler');

function ConvertToCamundaCloudPlugin(elementRegistry, editorActions, canvas, modeling) {
  this._elementRegistry = elementRegistry;
  this._modeling = modeling;

  var self = this;

  this.state = {
    open: false
  };

  editorActions.register({
    convertToCamundaCloud: function() {
      self.convertToCamundaCloud();
    }
  });
}

ConvertToCamundaCloudPlugin.prototype.convertToCamundaCloud = function(name, type) {
  var self = this;
  console.log("YEAH");
  self.generateIDs();
}

ConvertToCamundaCloudPlugin.prototype.generateIDs = function() {
  var self = this;
  var bpmnJS = new BpmnJS();

  var moddle = bpmnJS.get('moddle');

  var elements = this._elementRegistry._elements;
  this.technicalIds = {};
  Object.keys(elements).forEach(function(key) {
    var element = elements[key].element;
//    console.log(key);
    console.log(element);

    if (element.type == "bpmn:ServiceTask") {
      if (element.businessObject.topic) { // External Tasks
        var taskDef = moddle.create("zeebe:taskDefinition");
        taskDef.type = element.businessObject.topic;

        var extensions  = element.businessObject.extensionElements;
        if (!extensions) {
          extensions = moddle.create('bpmn:ExtensionElements', {});          
        }
        extensions.append(taskDef);
      }
      console.log(element);

    }

  });
};

/**
ConvertToCamundaCloudPlugin.prototype._getTechnicalID = function(name, type) {
  var name = removeDiacritics(name); // remove diacritics
  name = name.replace(/[^\w\s]/gi, ''); // now replace special characters
  name = this._getCamelCase(name);; // get camelcase
  
  if ( !isNaN(name.charAt(0)) ) { // mask leading numbers
     name = 'N' + name;
  }
  
  if ( type === 'bpmn:Process' ) {
    return name + 'Process';
  } else if ( type === 'bpmn:IntermediateCatchEvent' || type === 'bpmn:IntermediateThrowEvent' ) {
    return name + 'Event';
  } else if ( type === 'bpmn:UserTask' || type === 'bpmn:ServiceTask' || type === 'bpmn:ReceiveTask' || type === 'bpmn:SendTask' 
                || type === 'bpmn:ManualTask' || type === 'bpmn:BusinessRuleTask' || type === 'bpmn:ScriptTask' ) {
    return name + 'Task';
  } else if ( type === 'bpmn:ExclusiveGateway' || type === 'bpmn:ParallelGateway' || type === 'bpmn:ComplexGateway' 
                || type === 'bpmn:EventBasedGateway' ) {
    return name + 'Gateway';
  } else {
    return name + type.replace('bpmn:','');
  }
};
 */


ConvertToCamundaCloudPlugin.$inject = [ 'elementRegistry', 'editorActions', 'canvas', 'modeling' ];

module.exports = {
    __init__: ['convertToCamundaCloudPlugin'],
    convertToCamundaCloudPlugin: ['type', ConvertToCamundaCloudPlugin]
};
