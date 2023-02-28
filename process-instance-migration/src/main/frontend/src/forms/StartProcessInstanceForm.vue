<template>
  <v-row>
    <v-text-field
      label="BPMN Process ID*"
      required
      v-model="bpmnProcessId"
    ></v-text-field>
  </v-row>
  <v-row>
    <v-select
      label="Migration Type"
      v-model="selection"
      :hint="selection.hint"
      :items="migrationTypes"
      return-object
      persistent-hint
    ></v-select>
  </v-row>
</template>
<script setup lang="ts">
import { ref, computed } from "vue";

const migrationTypes = [
  {
    title: "Simple",
    value: "simple",
    hint: "Simple point-in-time migration",
  },
  {
    title: "Router",
    value: "router",
    hint: "Router migration that will run until it is interrupted",
  },
];
const selection = ref(migrationTypes[0]);
const bpmnProcessId = ref();

const migrationType = computed(() => selection.value.value);

defineExpose({
  bpmnProcessId: bpmnProcessId,
  migrationType: migrationType,
});
</script>
