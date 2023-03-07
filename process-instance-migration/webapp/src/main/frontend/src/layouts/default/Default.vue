<template>
  <v-app>
    <default-bar @navtoggle="nav = !nav" />
    <default-navigation v-model="nav"/>
    <default-view />
  </v-app>
</template>

<script lang="ts" setup>
import DefaultBar from "./AppBar.vue";
import DefaultView from "./View.vue";
import DefaultNavigation from "./Navigation.vue";
import { ref } from "vue";

const nav = ref(false);
</script>
