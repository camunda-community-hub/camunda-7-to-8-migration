'use strict';

module.exports = function(electronApp, menuState) {
  return [{
    label: 'Convert model',
    accelerator: 'CommandOrControl+Shift+C', 
    enabled: function() {
      // only enabled for BPMN diagrams
      return menuState.bpmn || menuState.dmn;
    },
    action: function() {
      if (menuState.bpmn) {
        electronApp.emit('menu:action', 'convertBpmnToCamundaCloud');
      } else {
        electronApp.emit('menu:action', 'convertDmnToCamundaCloud');
      }
    }
  }];
};
