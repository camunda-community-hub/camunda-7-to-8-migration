'use strict';

module.exports = function(electronApp, menuState) {
  return [{
    label: 'Convert model',
    enabled: function() {
      // only enabled for BPMN diagrams
      return menuState.bpmn;
    },
    action: function() {
      electronApp.emit('menu:action', 'convertToCamundaCloud');
    }
  }];
};
