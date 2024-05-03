<template>
  <div class="Services">
      <div class="header">
          <h1>Hotel Management System</h1>
          <p>{{ username }}</p>
          <div class="logout">
              <button class="button" @click="logout">Log out</button>
          </div>
      </div>
      <div class="content">
          <div class="toolbar">
              <button class="button" @click="viewRooms">View Rooms</button>
              <button class="button" @click="manageHotelServices">Hotel Services</button>
              <button class="button" @click="createReservation">Create Reservation</button>
              <button class="button" @click="viewReservations">View Reservations</button>
              <button class="button" @click="viewCustomers">View Customers</button>
              <button class="button" @click="accommodatedCustomers">Accommodated customers</button>
              <button v-if="isAdmin()" class="button" @click="viewEmployees">View Employees</button>
          </div>
          <router-view />
      </div>
  </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        username: '',
        role: ''
      };
    },
    mounted() {
      this.fetchUserInformation();
      window.addEventListener('scroll', this.handleScroll);
    },
    methods: {
      fetchUserInformation() {
        fetch('/Home/UserInformation', {
            headers: {
              'Authorization': localStorage.getItem('token'),
            }
        })
        .then(response => {
          if (!response.ok) {
            this.$router.push('/Home/login');
          }
          return response.json();
        }) 
        .then(data => {
          this.username = data.username; 
          this.role = data.role;
        })
        .catch(error => {
          console.error('Error:', error);
          this.$router.push('/Home/login');
        });
      },
      logout() {
        console.log('Logout');
        localStorage.removeItem('token');
        this.$router.push('/Home/login');
      },
      isAdmin() {
      return this.role === 'Admin';
    },
      viewRooms() {
          this.$router.push('/Home/Rooms');
      },
      manageHotelServices() {
        this.$router.push('/Home/Services');
      },
      createReservation() {
        this.$router.push('/Home/CreateReservation');
      },
      viewReservations() {
        this.$router.push('/Home/Reservations');
      },
      viewCustomers() {
        this.$router.push('/Home/Customers');
      }, 
      accommodatedCustomers() {
        this.$router.push('/Home/AccommodatedCustomers');
      },
      viewEmployees() {
        this.$router.push('/Home/Employees');
      }
    },
  };
  </script>
  
  <style >
  .hotel-management {
    margin: 0;
    font-family: Arial, sans-serif;
  }
  .header {
    background-color: transparent;
    border-bottom: 2px solid #2196F3;
    color: #2196F3;
    padding: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .logout button {
    padding: 10px;
    border: none;
    border-radius: 5px;
    background-color: #f44336;
    color: white;
    font-size: 16px;
    cursor: pointer;
  }
  .logout button:hover {
    background-color: #d32f2f;
  }
  .toolbar {
    display: flex;
    flex-direction: column;
    width: calc(100vw / 6);
    margin-right: 20px;
    position: sticky;
    top: 0;
    z-index: 1000;
    padding-top: 12px;
  }
  .button {
    padding: 10px 20px;
    margin-bottom: 10px;
    border: none;
    border-radius: 5px;
    background-color: #2196f3;
    color: white;
    font-size: 16px;
    cursor: pointer;
  }
  .button:hover {
    background-color: #13568e;
  }
  button:disabled {
      background-color: light-dark(rgba(239, 239, 239, 0.3), rgba(19, 1, 1, 0.3));
      color: light-dark(rgba(16, 16, 16, 0.3), rgba(255, 255, 255, 0.3));
      border-color: light-dark(rgb(0, 0, 0), rgba(8, 8, 8, 0.918));
      border: 1px solid;
      cursor: not-allowed;
  }
  .content {
    display: flex;
    justify-content: flex-start;
    align-items: flex-start;
    padding: 20px;
  }
  .table-container {
    flex: 1;
    padding: 10px;
    border-radius: 10px;
    overflow: auto;
  }
  table {
    width: 100%;
    border-collapse: collapse;
  }
  th, td {
    border: 1px solid #ddd;
    padding: 8px;
    text-align: left;
  }
  th {
    background-color: #2196F3;
    color: white;
    position: sticky;
    top: 0;
  }
  </style>