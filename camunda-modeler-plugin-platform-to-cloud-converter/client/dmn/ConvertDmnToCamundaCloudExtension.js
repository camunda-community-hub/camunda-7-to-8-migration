/**
 * A client plugin is required to hook into the modeler lifecycles itself,
 * e.g. to save a file, see https://forum.bpmn.io/t/trigger-a-model-file-save-from-within-a-camunda-modeler-plugin/6423/2
 */

import { PureComponent } from 'camunda-modeler-plugin-helpers/react';

import ConvertToCamundaCloudPlugin from '../ConvertToCamundaCloudPluginIndex';


/**
 * An extension that shows how to hook into
 * editor events to accomplish the following:
 *
 * - hook into <bpmn.modeler.configure> to provide a bpmn.modeler extension
 * - hook into <bpmn.modeler.created> to register for bpmn.modeler events
 * - hook into <tab.saved> to perform a post-safe action
 *
 */
export default class ConvertDmnToCamundaCloudExtension extends PureComponent {

  constructor(props) {

    super(props);

    const {
      subscribe,
      triggerAction
    } = props;

    subscribe('dmn.modeler.configure', (event) => {

      const {
        tab,
        middlewares
      } = event;

      log('Creating editor for tab', tab);

      middlewares.push(addModule(ConvertToCamundaCloudPlugin));
    });


    subscribe('dmn.modeler.created', (event) => {

      const {
        tab,
        modeler,
      } = event;

      log('Modeler created for tab', tab);

      modeler.on('saveTab', (event) => {
        triggerAction('save')
          .then(tab => {
            if (!tab) {
              return this._displayNotification({ title: 'Failed to save' });
            } else {
              console.log("saved");
            }
          });
      });
      modeler.on('saveXML.start', (event) => {

        const {
          definitions
        } = event;

        log('Saving XML with definitions', definitions, tab);
      });

    });


    subscribe('tab.saved', (event) => {
      const {
        tab
      } = event;

      log('Tab saved', tab);
    });

  }

  render() {
    return null;
  }
}


// helpers //////////////

function log(...args) {
  console.log('[TestEditorEvents]', ...args);
}

/**
 * Returns a bpmn.modeler.configure middleware
 * that adds the specific module.
 *
 * @param {didi.Module} extensionModule
 *
 * @return {Function}
 */
function addModule(extensionModule) {

  return (config) => {

    const newConfig = { ...config };

      newConfig.drd = newConfig.drd || {};

      const additionalModules = (newConfig.drd.additionalModules) || [];

      newConfig.drd.additionalModules = [
        ...additionalModules,
        extensionModule
      ];

    return newConfig;
  };
}
