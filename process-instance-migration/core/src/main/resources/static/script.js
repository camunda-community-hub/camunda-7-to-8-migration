const loadTasks = async () => {
  const response = await fetch("/api/migration/tasks");
  const newTasks = await response.json();
  const oldTasks = JSON.parse(localStorage.getItem("tasks")) || [];
  localStorage.setItem("tasks", JSON.stringify(newTasks));
  merge(oldTasks, newTasks);
};
const el = (elementName, attributes, children, modifier) => {
  const element = document.createElement(elementName);
  if (attributes) {
    Object.keys(attributes).forEach(key => {
      element[key] = attributes[key];
    });
  }
  if (modifier) {
    modifier(element);
  }
  if (children) {
    element.append(...children);
  }
  return element;
};
const merge = (oldTasks, newTasks) => {
  const root = document.getElementById("tasklist");
  const remove = (task) => {
    const taskElement = document.getElementById(task.id);
    if (taskElement) {
      root.removeChild(taskElement);
    }
    localStorage.removeItem(task.id);
  };
  const add = (task) => {
    localStorage.setItem(task.id, localStorage.getItem(task.id) || JSON.stringify([]));
    const taskElement =
        el("div",
            {id: task.id},
            [
              el("h3",
                  {},
                  [task.bpmnProcessId]),
              el("div",
                  {},
                  task.availableProcessInstanceIds.map(pi => {
                    return el(
                        "div",
                        {id: pi},
                        [
                          el("input",
                              {
                                type: "checkbox",
                                value: pi,
                                checked: JSON.parse(localStorage.getItem(task.id)).includes(pi)
                              }, null, input => {
                                input.addEventListener("change", () => {
                                  const selectedItems = JSON.parse(localStorage.getItem(task.id));
                                  if (input.checked) {
                                    selectedItems.push(pi);
                                  } else {
                                    selectedItems.splice(selectedItems.indexOf(pi), 1);
                                  }
                                  localStorage.setItem(task.id, JSON.stringify(selectedItems));
                                })
                              }
                          ),
                          el("label",
                              {for: pi},
                              [pi])]
                    );
                  })
              ),
              el("button", {}, "Submit", button => {
                button.addEventListener("click", async () => {
                  const response = await fetch("/api/migration/tasks/" + task.id, {
                    method: "PUT",
                    body: localStorage.getItem(task.id),
                    headers: {
                      "Content-Type": "application/json"
                    }
                  });
                  if (response.status >= 200 && response.status < 400) {
                    await loadTasks();
                  }
                });
              })
            ]);
    root.appendChild(taskElement);
  };
  console.log(oldTasks);
  console.log(newTasks);
  const oldTaskIds = oldTasks.map(task => task.id);
  const newTaskIds = newTasks.map(task => task.id);
  // remove old tasks that do not appear in new tasks
  oldTasks.filter(oldTask => !newTaskIds.includes(oldTask.id)).forEach(remove);
  // add new tasks that did not appear in old tasks
  newTasks.filter(newTask => !oldTaskIds.includes(newTask.id)).forEach(add);
}

document.addEventListener("DOMContentLoaded", async () => {
      const startButton = document.getElementById("start");
      startButton.addEventListener("click", () => {
        const bpmnProcessId = prompt("Please enter the BPMN Process ID to migrate");
        if (bpmnProcessId) {
          fetch("api/migration/start", {
            method: "POST",
            body: JSON.stringify({
              bpmnProcessId
            }),
            headers: {
              "Content-Type": "application/json"
            }
          })
        }
      });
      localStorage.setItem("tasks", JSON.stringify([]));
      await loadTasks();
      setInterval(loadTasks, 10000);
    }
)