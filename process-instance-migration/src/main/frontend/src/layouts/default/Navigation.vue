<template>
  <v-navigation-drawer>
    <v-list v-if="tasks.length > 0">
      <v-list-item
        v-for="(task, index) in tasks"
        :key="index"
        :title="task.name"
        :value="task.key"
        @click="
          router.push({ name: 'task', params: { taskKey: task.key, key: task.key } })
        "
      >
      </v-list-item>
    </v-list>
    <v-alert v-else>No Tasks at the moment</v-alert>
  </v-navigation-drawer>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import router from "@/router";

const tasks = ref(
  [] as Array<{
    key: number;
    state: string;
    type: string;
    data: any;
    name: string;
  }>
);

const fetchTasks = async () => {
  const response = await fetch("/api/migration/tasks");
  tasks.value = await response.json();
};
fetchTasks();
setInterval(fetchTasks, 5000);
</script>
