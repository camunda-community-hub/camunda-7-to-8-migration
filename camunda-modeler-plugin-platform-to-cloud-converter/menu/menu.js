'use strict';

module.exports = function(electronApp, menuState) {
  return [{
    label: 'Convert model',
    accelerator: 'CommandOrControl+Shift+C', 
    enabled: function() {
      // only enabled for BPMN diagrams
      return menuState.bpmn;
    },
    action: function() {
      electronApp.emit('menu:action', 'convertToCamundaCloud');
    }
  }];
};
