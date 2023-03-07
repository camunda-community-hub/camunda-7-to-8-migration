<template>
  <v-dialog activator="parent" v-model="dialog" width="500">
    <v-card>
      <v-card-title>
        <span class="text-h5">Start Process Instance Migration</span>
      </v-card-title>
      <v-card-text>
        <v-container :fluid="true">
          <v-expansion-panels>
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
        <v-container>
          <start-process-instance-form ref="taskData"></start-process-instance-form>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn @click="start">Start</v-btn>
        <v-spacer></v-spacer>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script setup lang="ts">
import StartProcessInstanceForm from "@/forms/StartProcessInstanceForm.vue";
import { ref } from "vue";
const taskData = ref();

const dialog = ref(false);

const start = async () => {
  await fetch("/api/migration/start", {
    method: "POST",
    body: JSON.stringify(taskData.value),
    headers: { "Content-Type": "application/json" },
  });
  dialog.value = false;
};

const formatJson = (jsonString: any) => {
  return JSON.stringify(jsonString, null, 2);
};
</script>
