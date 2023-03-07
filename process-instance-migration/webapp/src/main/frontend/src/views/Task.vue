<template>
  <v-card v-if="task">
    <v-card-title>
      <span class="text-h5">{{ task.name }}</span
      ><link-to-camunda-instance
        system-type="C8"
        entity-type="process-instance"
        :id="task.processInstanceKey"
      />
    </v-card-title>
    <v-card-text>
      <v-container :fluid="true">
        <v-expansion-panels>
          <v-expansion-panel>
            <v-expansion-panel-title>Task Input</v-expansion-panel-title>
            <v-expansion-panel-text
              ><code>
                <pre>{{ formatJson(task.data) }}</pre>
              </code></v-expansion-panel-text
            >
          </v-expansion-panel>
          <v-expansion-panel>
            <v-expansion-panel-title>Task Output</v-expansion-panel-title>
            <v-expansion-panel-text>
              <code>
                <pre>{{ formatJson(taskData) }}</pre>
              </code></v-expansion-panel-text
            >
          </v-expansion-panel>
        </v-expansion-panels>
      </v-container>
      <v-container :fluid="true">
        <component :is="forms[task.type]" :data="task.data" ref="taskData"></component
      ></v-container>
    </v-card-text>
    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn @click="complete">Complete</v-btn>
      <v-spacer></v-spacer>
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { ref } from "vue";
import router from "@/router";
import SelectProcessInstancesForm from "@/forms/SelectProcessInstancesForm.vue";
import CreateAndDeployConversionForm from "@/forms/CreateAndDeployConversionForm.vue";
import SelectJobDefinitionForm from "@/forms/SelectJobDefinitionForm.vue";
import CancelRouteExecutionForm from "@/forms/CancelRouteExecutionForm.vue";
import LinkToCamundaInstance from "@/components/LinkToCamundaInstance.vue";

const props = defineProps(["taskKey"]);
const task = ref();
const taskData = ref();

const forms = {
  "select-process-instances": SelectProcessInstancesForm,
  "create-and-deploy-conversion": CreateAndDeployConversionForm,
  "select-job-definition": SelectJobDefinitionForm,
  "cancel-route-execution": CancelRouteExecutionForm,
} as any;

const formatJson = (jsonString: any) => {
  return JSON.stringify(jsonString, null, 2);
};

const fetchTask = async () => {
  const response = await fetch(`/api/migration/tasks/${props.taskKey}`);
  task.value = await response.json();
};

const complete = async () => {
  await fetch(`/api/migration/tasks/${props.taskKey}`, {
    method: "PUT",
    body: JSON.stringify(taskData.value),
    headers: { "Content-Type": "application/json" },
  });
  router.push("/");
};

fetchTask();
</script>
