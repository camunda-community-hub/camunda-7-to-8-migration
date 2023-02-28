<template>
<span>Please create a process definition with <code>bpmnProcessId: {{ data.bpmnProcessId }}</code> and deploy it to your Camunda 8 instance.</span>
</template>
<script setup lang="ts">

defineProps(['data'])
defineExpose()
</script>