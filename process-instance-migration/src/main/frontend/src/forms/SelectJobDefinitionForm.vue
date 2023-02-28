<template>
  <v-row>
    <span class="text-h6">{{ data.camunda7ProcessDefinitionId }}</span><link-to-camunda-instance :id="data.camunda7ProcessDefinitionId" entity-type="process-definition" system-type="C7"/></v-row
  >
  <v-row
    v-for="(activityId, jobDefinition) in data.camunda7JobDefinitions"
    v-bind:key="jobDefinition"
  >
    <v-checkbox
      v-model="selection"
      :value="jobDefinition"
      :label="`${jobDefinition} (${activityId})`"
    ></v-checkbox>
  </v-row>
</template>
<script setup lang="ts">
import LinkToCamundaInstance from "@/components/LinkToCamundaInstance.vue";
import { computed, ref } from "vue";

const props = defineProps(["data"]);
const selection = ref([] as Array<string>);

const selectedJobDefinitions = computed(() => {
  const o = {} as any;
  selection.value.forEach((v) => (o[v] = props.data.camunda7JobDefinitions[v]));
  return o;
});

defineExpose({
  selectedJobDefinitions,
});
</script>
