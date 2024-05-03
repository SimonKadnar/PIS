<template>
    <div>
      <h1>Vítejte na homepage</h1>
      <router-link to="/Home/login">
        <button>LogIn</button>
        </router-link>
    </div>
  </template>
  
  <script>
  export default {
    mounted() {
      this.$router.push('/Home/Rooms');
    }
  }

  </script>
  
  <style scoped>
  @import '/src/main/vue/styles/style.css';
  </style>
  