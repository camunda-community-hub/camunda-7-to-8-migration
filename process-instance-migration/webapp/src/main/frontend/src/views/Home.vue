<template>
  <v-responsive class="d-flex align-center text-center fill-height">
    <v-btn prepend-icon="mdi-play"
      ><start-process-instance-dialog></start-process-instance-dialog>Start Process
      Instance Migration</v-btn
    >
  </v-responsive>
</template>

<script lang="ts" setup>
import StartProcessInstanceDialog from "@/components/StartProcessInstanceDialog.vue";
</script>
