'use strict';

module.exports = function(electronApp, menuState) {
  return [{
    label: 'Convert Model from Camunda 7 to 8',
    accelerator: 'CommandOrControl+Shift+C', 
    enabled: function() {
      // enabled for BPMN and DMN diagrams
      return menuState.bpmn || menuState.dmn;
    },
    action: function() {
      if (menuState.bpmn) {
        electronApp.emit('menu:action', 'convertBpmnToCamunda8');
      } else {
        electronApp.emit('menu:action', 'convertDmnToCamunda8');
      }
    }
  }];
};
