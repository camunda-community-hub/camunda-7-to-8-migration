<template>
  <v-row>
    <span class="text-h6">Camunda 7 Process Definition: {{ data.camunda7ProcessDefinitionId }}</span>
    <link-to-camunda-instance entity-type="process-definition" :id="data.camunda7ProcessDefinitionId" system-type="C7"/>
    </v-row
  >
  <v-row class="text-subtitle-1"> Routing job definitions: </v-row>
  <v-row>
    <v-list density="compact">
      <v-list-item
        v-for="(activityId, jobDefinition) in data.camunda7JobDefinitions"
        :key="jobDefinition"
        :title="`${jobDefinition} (${activityId})`"
        prepend-icon="mdi-arrow-right-bold"
      ></v-list-item> </v-list
  ></v-row>
  <v-row>
    <v-alert
      density="compact"
      type="warning"
      title="Caution"
      text="By completing this task, you will interrupt the router"
    ></v-alert>
  </v-row>
</template>
<script setup lang="ts">
import LinkToCamundaInstance from '@/components/LinkToCamundaInstance.vue';

defineProps(["data"]);
defineExpose({});
</script>
