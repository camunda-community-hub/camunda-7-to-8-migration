<template>
  <v-app>
    <v-layout>
      <v-app-bar>
        <v-app-bar-title>Camunda Process Instance Migration</v-app-bar-title>
        <v-menu location="bottom">
          <template v-slot:activator="{ props }">
            <v-btn icon v-bind="props">
              <v-icon>mdi-dots-vertical</v-icon>
            </v-btn>
          </template>

          <v-list>
            <v-list-item
              v-for="(item, index) in items"
              :key="index"
              :prepend-icon="item.icon"
              :title="item.title"
              :value="item.value"
            >
            </v-list-item>
          </v-list>
        </v-menu>
      </v-app-bar>
      <v-navigation-drawer></v-navigation-drawer>
      <v-main>
        <router-view />
      </v-main>
    </v-layout>
  </v-app>
</template>

<script lang="ts" setup>
//
const items = [
  {
    title: "Start Process Instance Migration",
    icon: "mdi-play",
    value: "start-migration",
  },
];
</script>
