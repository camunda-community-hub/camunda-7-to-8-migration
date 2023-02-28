<template>
  <v-tooltip>
    <template v-slot:activator="{ props }">
      <v-btn
        icon="mdi-arrow-top-right-bold-box-outline"
        variant="plain"
        v-bind="props"
        :href="`${urls[systemType]}/${entityTypes[entityType].context[systemType]}/${id}`"
        target="blank_" density="compact"
      >
      </v-btn>
    </template>
    Go to {{ systemTypes[systemType] }} {{ entityTypes[entityType].name }}
  </v-tooltip>
</template>

<script setup lang="ts">
import { ref } from "vue";

const props = defineProps(["systemType", "entityType", "id"]);

const systemTypes = {
  C8: "Camunda 8",
  C7: "Camunda 7",
} as any;

const entityTypes = {
  "process-instance": {
    name: "Process Instance",
    context: {
      C7: "camunda/app/cockpit/default/#/process-instance",
      C8: "processes",
    },
  },
  "process-definition": {
    name: "Process Definition",
    context: {
      C7: "camunda/app/cockpit/default/#/process-definition",
      C8: "", // TODO figure out how this could work
    },
  },
} as any;

const urls = ref({} as any);

const fetchUrls = async () => {
  const response = await fetch("/api/migration/links");
  urls.value = await response.json();
};

fetchUrls();
</script>
