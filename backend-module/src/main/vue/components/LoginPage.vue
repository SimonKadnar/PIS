<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" />

<template>
  <div class="login-container">
    <h2 class="login-title">Login</h2>
    <form @submit.prevent="login" class="login-form">
      <div class="form-field">
        <label for="username" class="form-label">Username:</label>
        <input type="email" id="username" v-model="email" required class="form-input" placeholder="example@example.com">
      </div>
      <div class="form-field" style="margin-left: 4px;">
        <label for="password" class="form-label">Password:</label>
        <input type="password" id="password" :type="passwordFieldType" v-model="password" required class="form-input">
        <span class="toggle-password" @click="togglePasswordVisibility">
          <font-awesome-icon :icon="passwordFieldType === 'password' ? 'eye-slash' : 'eye'" />
        </span>
      </div>
      <button type="submit" class="login-button">Login</button>
    </form>
    <p v-if="loginMessage">{{ loginMessage }}</p>
  </div>
</template>

<style scoped> 
  @import '/src/main/vue/styles/style.css';
</style> 

<script>
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';

export default {
  components: {
    FontAwesomeIcon,
  },
data() {
  return {
    email: '',
    password: '',
    loginMessage: '',
    passwordFieldType: 'password'
  };
},
methods: {
  switchVisibility(){
    this.passwordFieldType = this.passwordFieldType === 'password' ? 'text' : 'password';
  },
  hide(){
    this.passwordFieldType = this.passwordFieldType === 'text' ? 'password' : 'text';
  },
  login() {
    fetch('/Home/loginVerify', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: this.email,
        password: this.password
      })
    })
    .then(response => {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('Login failed');
      }
    })
    .then(data => {
      localStorage.setItem('token', data.token);
      console.log('Login successful');
      this.$router.push('/Home/Rooms');
    })
    .catch(error => {
      console.error('Error during login:', error);
      this.loginMessage = 'Špatný email nebo heslo'; 
    });
  },
  togglePasswordVisibility() {
    this.passwordFieldType = this.passwordFieldType === 'password' ? 'text' : 'password';
  }
}
};
</script>
