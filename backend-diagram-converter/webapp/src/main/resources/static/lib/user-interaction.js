const check = document.getElementById("check");
const convert = document.getElementById("convert");
const downloadCsv = document.getElementById("downloadCsv");

const fileUpload = document.getElementById("formFile");
const appendDocumentation = document.getElementById("appendDocumentation");

const checkContainer = document.getElementById("checkContainer");
const resultArea = document.getElementById("accordionExample");
const arrangedResultsArea = document.getElementById("arrangedResults");

const severityClasses = {
  "WARNING": "text-bg-warning",
  "TASK": "text-bg-warning",
  "REVIEW": "text-bg-info",
  "INFO": "text-bg-secondary"
}

const el = (tagName, attributes, children) => {
  const element = document.createElement(tagName);
  Object.keys(attributes).forEach(key => element[key] = attributes[key]);
  element.append(...children);
  return element;
};

const structureMessages = response => {
  response.results.forEach(result => {
    result.references.forEach(reference => {
      response.results.filter(r => r.elementId === reference).forEach(r => {
        result.messages.push(...r.messages);
      });
    });
  });
};

const createFormattedResult = result => {
  if (result.messages.length && result.referencedBy.length === 0) {
    return el("div", {className: "card m-3"}, [
      el("div", {className: "card-header"}, [result.elementType + " ", el("code", {}, result.elementId), ` ${result.elementName || ""}`]),
      createFormattedMessages(result.messages)
    ]);
  }
};

const createFormattedMessages = messages => {
  return el("ul", {className: "list-group list-group-flush"}, messages.map(createFormattedMessage));
};

const createFormattedMessage = message => {
  return el("li", {className: `list-group-item ${severityClasses[message.severity]}`}, `${message.severity}: ${message.message}`);
};

const createFormData = async () => {
  const formData = new FormData();
  if (fileUpload.files.length === 0) {
    alert("Please select a .bpmn file");
    return null;
  }
  formData.append("file", fileUpload.files[0]);
  formData.append("appendDocumentation", appendDocumentation.checked)
  return formData;
}
const createFormattedResultWrapper = file => {
  arrangedResultsArea.append(...createFormattedResultForFile(file));
}

const createFormattedResultForFile = file => {
  return  file.results.map(createFormattedResult).filter(e => e !== undefined);

}

const downloadResponse = async response => {
  const contentDisposition = response.headers.get("Content-Disposition");
  const targetFileName = contentDisposition.substring(contentDisposition.indexOf("\"") + 1, contentDisposition.lastIndexOf("\""));
  response.blob().then(data => {
    const a = document.createElement("a");
    a.href = window.URL.createObjectURL(data);
    a.download = targetFileName;
    a.click();
  });
};

check.addEventListener("click", async () => {
  const formData = await createFormData();
  if (!formData) {
    return;
  }
  fetch("/check", {
    body: formData,
    method: "POST",
    headers: {
      "Accept": "application/json"
    }
  }).then(response => {
    response.json().then(json => {
      checkContainer.innerHTML = JSON.stringify(json, null, 2);
      arrangedResultsArea.innerHTML = "";
      structureMessages(json);
      createFormattedResultWrapper(json);
      resultArea.hidden = false;
      addElementMarkers(json);
    })
  });
});

downloadCsv.addEventListener("click", async () => {
  const formData = await createFormData();
  if (!formData) {
    return;
  }
  fetch("/check", {
    body: formData,
    method: "POST",
    headers: {
      "Accept": "text/csv"
    }
  }).then(downloadResponse);
});

convert.addEventListener("click", async () => {
  const formData = await createFormData();
  if (!formData) {
    return;
  }
  fetch("/convert", {
    body: formData,
    method: "POST"
  }).then(downloadResponse);
});

async function showBpmn(bpmnXML) {
  const bpmnViewer = await getBpmnViewer();
  console.log('viewer loaded')
  openDiagram(bpmnXML, bpmnViewer);
  showPropertyInfos(bpmnViewer);
}

// load first diagram from uploaded files
const reader = new FileReader();
reader.onload = showBpmn;

if (fileUpload.files.length > 0) {
  reader.readAsText(fileUpload.files[0]);
}

fileUpload.addEventListener("change", () => {
  if (fileUpload.files.length !== 0) {
    reader.readAsText(fileUpload.files[0]);
  }
});

async function addElementMarkers(checkResult) {
  const bpmnViewer = await getBpmnViewer();
  var canvas = bpmnViewer.get('canvas');
  checkResult.results.forEach(result => {
    if (result.elementType !== 'process' && result.elementType !== 'message') {
      if (result.messages.length > 0) {
        const isWarning = result.messages.some(message => message.severity === 'WARNING');
        const isTask = result.messages.some(message => message.severity === 'TASK');
        const isReview = result.messages.some(message => message.severity === 'REVIEW');
        if (isWarning) {
          console.log('marker for ', result.elementId, result.elementName, 'WARNING');
          canvas.addMarker(result.elementId, 'conversion-warning')
        } else if (isTask) {
          console.log('marker for ', result.elementId, result.elementName, 'TASK');
          canvas.addMarker(result.elementId, 'conversion-task')
        } else if (isReview) {
          console.log('marker for ', result.elementId, result.elementName, 'REVIEW');
          canvas.addMarker(result.elementId, 'conversion-review')
        }
      }

    }
  })
}