<template>
  <v-menu location="bottom" open-on-hover>
    <template v-slot:activator="{ props }">
      <v-btn icon v-bind="props">
        <v-icon>mdi-dots-vertical</v-icon>
      </v-btn>
    </template>

    <v-list>
      <v-list-item
        prepend-icon="mdi-play"
        title="Start Process Instance Migration"
        value="start-migration"
        ><start-process-instance-dialog></start-process-instance-dialog>
      </v-list-item>
    </v-list>
  </v-menu>
</template>

<script setup lang="ts">
import StartProcessInstanceDialog from "@/components/StartProcessInstanceDialog.vue";
</script>
