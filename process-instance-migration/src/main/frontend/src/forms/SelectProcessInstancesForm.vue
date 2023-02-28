<template>
  <v-row>
    <span class="text-h6">{{ data.camunda7ProcessDefinitionId }}</span></v-row
  >
  <v-container
    v-for="(availableProcessInstance, index) in data.availableProcessInstances"
    v-bind:key="index" style="padding: 0"
  >
    <v-checkbox
      density="compact"
      v-model="camunda7ProcessInstanceIds"
      :value="availableProcessInstance.id"
    >
      <template v-slot:label>
        <span
          >{{ availableProcessInstance.id }} (Business Key:
          {{ availableProcessInstance.businessKey }})</span
        ><link-to-camunda-instance entity-type="process-instance" :id="availableProcessInstance.id" system-type="C7"/>
      </template>
    </v-checkbox>
    <v-alert
      v-for="(migrationHint, innerIndex) in availableProcessInstance.migrationHints"
      v-bind:key="innerIndex"
      density="compact"
      type="warning"
      title="Caution"
      :text="migrationHint"
    ></v-alert>
  </v-container>
</template>
<script setup lang="ts">
import { ref } from "vue";
import LinkToCamundaInstance from "@/components/LinkToCamundaInstance.vue";

defineProps(["data"]);
const camunda7ProcessInstanceIds = ref([] as Array<string>);
defineExpose({ camunda7ProcessInstanceIds });
</script>
