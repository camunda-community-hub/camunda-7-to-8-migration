/*
 * Load modeler instance with Camunda-Moddle extension and show all properties
 */
async function getCamundaModdle() {
  try {
    const response = await fetch(
      "https://unpkg.com/camunda-bpmn-moddle@7.0.1/resources/camunda.json"
    );
    if (!response.ok) {
      throw new Error(`HTTP Error: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error(`could not get Camunda moddle: ${error}`);
  }
}

let bpmnViewer = null;

async function getBpmnViewer() {
  if (!bpmnViewer) {
    const camundaModdle = await getCamundaModdle();

    console.log(camundaModdle);

    // viewer instance
    bpmnViewer = new BpmnJS({
      container: "#canvas",
      moddleExtensions: {
        camunda: camundaModdle,
      },
    });
  }

  return bpmnViewer;
}

/**
 * Open diagram in our viewer instance.
 *
 * @param {String} bpmnXML diagram to display
 */
async function openDiagram(bpmnXML, bpmnViewer) {
  // import diagram
  try {
    await bpmnViewer.importXML(bpmnXML.target.result);

    // access viewer components
    var canvas = bpmnViewer.get("canvas");
    // zoom to fit full viewport
    canvas.zoom("fit-viewport");
  } catch (err) {
    console.error("could not import BPMN 2.0 diagram", err);
  }
}

async function showPropertyInfos() {
  bpmnViewer = await getBpmnViewer();
  var overlays = bpmnViewer.get("overlays");
  var elementRegistry = bpmnViewer.get("elementRegistry");

  var elements = elementRegistry.getAll();
  for (var elementCount in elements) {
    var elementObject = elements[elementCount];
    if (
      elementObject.businessObject.$instanceOf("bpmn:FlowNode") ||
      elementObject.businessObject.$instanceOf("bpmn:Participant")
    ) {
      addStyle(elementObject, overlays);
    }
  }
}

function addStyle(element, overlays) {
  var elementOverlays = [];

  // if (elementOverlays[element.id] !== undefined && elementOverlays[element.id].length !== 0) {
  //     for (var overlay in elementOverlays[element.id]) {
  //         overlays.remove(elementOverlays[element.id][overlay]);
  //     }
  // }

  elementOverlays[element.id] = [];

  if (
    element.businessObject.documentation !== undefined &&
    element.businessObject.documentation.length > 0 &&
    element.businessObject.documentation[0].text.trim() !== "" &&
    element.type !== "label"
  ) {
    var text = element.businessObject.documentation[0].text;
    text = text.replace(/(?:\r\n|\r|\n)/g, "<br />");

    elementOverlays[element.id].push(
      overlays.add(element, "badge", {
        position: {
          top: 4,
          right: 4,
        },
        html:
          '<div class="doc-val-true" data-badge="D"></div><div class="doc-val-hover" data-badge="D">' +
          text +
          "</div>",
      })
    );
  }

  if (
    element.businessObject.extensionElements === undefined &&
    element.businessObject.$instanceOf("bpmn:FlowNode")
  ) {
    return;
  }

  //Do not process the label of an element
  if (element.type === "label") {
    return;
  }

  var badges = [];

  if (element.businessObject.$instanceOf("bpmn:Participant")) {
    var extensionElements = element.businessObject.processRef.extensionElements;
    var extensions =
      extensionElements === undefined ? [] : extensionElements.values;

    var type = "&#9654;";
    var background = "prop-info-green";
    if (element.businessObject.processRef.isExecutable === false) {
      type = "&#10074;&#10074;";
      background = "prop-info-red";
    }
    badges.push({
      badgeKey: "isExecutable",
      badgeSort: 0,
      badgeType: type,
      badgeBackground: background,
      badgeLocation: "left",
    });
  } else {
    var extensions = element.businessObject.extensionElements.values;
  }

  for (var extension in extensions) {
    var type = "";
    var background = "";
    var location = "right";
    var sort = 0;
    var key = "";

    console.log("Extension: ", extensions[extension]);

    switch (extensions[extension].$type) {
      case "camunda:ExecutionListener":
        if (extensions[extension].event === "start") {
          location = "left";
          key = "camunda:ExecutionListener-start";
          sort = 20;
        } else {
          location = "right";
          key = "camunda:ExecutionListener-end";
          sort = 70;
        }
        type = "L";
        background = "prop-info-green";
        break;
      case "camunda:Properties":
        key = "camunda:Properties";
        sort = 80;
        type = "E";
        background = "prop-info-violet";
        break;
      case "camunda:TaskListener":
        if (
          extensions[extension].event === "create" ||
          extensions[extension].event === "assignment"
        ) {
          location = "left";
          key = "camunda:TaskListener-start";
          sort = 21;
        } else {
          location = "right";
          key = "camunda:TaskListener-end";
          sort = 71;
        }
        type = "T";
        background = "prop-info-green";
        break;
      case "camunda:InputOutput":
        background = "prop-info-blue";
        break;
      case "camunda:In":
        type = "V";
        key = "camunda:In";
        location = "left";
        sort = 10;
        background = "prop-info-blue";
        break;
      case "camunda:Out":
        type = "V";
        key = "camunda:Out";
        location = "right";
        sort = 60;
        background = "prop-info-blue";
        break;
      case "camunda:field":
        key = "camunda:Field";
        sort = 90;
        type = "F";
        background = "prop-info-red";
        break;
    }

    if (extensions[extension].$type === "camunda:InputOutput") {
      if (
        extensions[extension].hasOwnProperty("inputParameters") &&
        extensions[extension].inputParameters.length > 0
      ) {
        location = "left";
        type = "I";
        key = "camunda:InputOutput-input";
        sort = 10;

        badges.push({
          badgeKey: key,
          badgeSort: sort,
          badgeType: type,
          badgeBackground: background,
          badgeLocation: location,
        });
      }

      if (
        extensions[extension].hasOwnProperty("outputParameters") &&
        extensions[extension].outputParameters.length > 0
      ) {
        location = "right";
        type = "O";
        key = "camunda:InputOutput-output";
        sort = 60;

        badges.push({
          badgeKey: key,
          badgeSort: sort,
          badgeType: type,
          badgeBackground: background,
          badgeLocation: location,
        });
      }
    } else {
      if (key !== "") {
        badges.push({
          badgeKey: key,
          badgeSort: sort,
          badgeType: type,
          badgeBackground: background,
          badgeLocation: location,
        });
      }
    }
  }

  addOverlays(badges, element, overlays, elementOverlays);
}

function addOverlays(badgeList, element, overlays, elementOverlays) {
  var badges = [];

  var leftCounter = 0;
  var rightCounter = 0;

  var sortedBadgeList = uniqBy(badgeList, function (item) {
    return item.badgeKey;
  });

  sortedBadgeList.sort(function (a, b) {
    return a.badgeSort - b.badgeSort;
  });

  for (var overlayCounter in sortedBadgeList) {
    var overlayObject = sortedBadgeList[overlayCounter];

    if (overlayObject.badgeLocation === "left") {
      badges.push(
        overlays.add(element, "badge", {
          position: {
            bottom: 0,
            left: leftCounter,
          },
          html:
            '<div class="prop-info ' +
            overlayObject.badgeBackground +
            '" data-badge="' +
            overlayObject.badgeType +
            '"></div>',
        })
      );
      leftCounter = leftCounter + 16;
    } else {
      badges.push(
        overlays.add(element, "badge", {
          position: {
            bottom: 0,
            right: rightCounter,
          },
          html:
            '<div class="prop-info ' +
            overlayObject.badgeBackground +
            '" data-badge="' +
            overlayObject.badgeType +
            '"></div>',
        })
      );
      rightCounter = rightCounter + 16;
    }
  }

  pushArray(elementOverlays[element.id], badges);
}

function uniqBy(a, key) {
  var seen = {};
  return a.filter(function (item) {
    var k = key(item);
    return seen.hasOwnProperty(k) ? false : (seen[k] = true);
  });
}

function pushArray(list, other) {
  var len = other.length;
  var start = list.length;
  list.length = start + len;
  for (var i = 0; i < len; i++, start++) {
    list[start] = other[i];
  }
}
