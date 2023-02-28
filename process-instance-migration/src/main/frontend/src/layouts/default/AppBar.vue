<template>
  <v-app-bar>
  <v-app-bar-nav-icon @click.stop="$emit('navtoggle')"></v-app-bar-nav-icon>
    <v-app-bar-title @click="home" style="cursor: pointer;">Camunda Process Instance Migration</v-app-bar-title>
    <v-spacer></v-spacer>
    <overflow-menu></overflow-menu>
  </v-app-bar>
</template>

<script lang="ts" setup>
import router from '@/router';
import OverflowMenu from './OverflowMenu.vue';

const home = () => router.push('/');
</script>
