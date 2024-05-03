<template>
  <div class="table-container">
    <table>
      <thead>
        <tr>
          <th>Last Name*</th>
          <th>First Name*</th>
          <th>Email*</th>
          <th>Phone Preselection</th>
          <th>Phone Number*</th>
          <th></th>
        </tr>
      </thead>
      <tbody>

        <tr>
          <td><input type=text v-model="filters.lastName"></td>
          <td><input type=text v-model="filters.firstName"></td>
          <td><input type=text v-model="filters.email"></td>
          <td><input type=number v-model="filters.phonePreselection"></td>
          <td><input type=number v-model="filters.phoneNumber"></td>
          <td></td>
        </tr>

        <tr v-for="customer in filteredCustomers()" :key="customer.PersonID">
          <td>{{ customer.person.lastName }}</td>
          <td>{{ customer.person.firstName }}</td>
          <td>{{ customer.person.email }}</td>
          <td>{{ customer.person.phonePreselection }}</td>
          <td>{{ customer.person.phoneNumber }}</td>
          <td>
            <button class="button" @click="getDetail(customer.person.personID)">View details</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  data() {
    return {
      customers: [],
      filters: {
        lastName: '',
        firstName: '',
        email: '',
        phonePreselection: '',
        phoneNumber: ''
      },
    };
  },
  mounted() {
    this.fetchCustomers();
  },
  methods: {
    fetchCustomers() {
      fetch('/Home/Customer/GetCustomers') 
        .then(response => response.json())
        .then(data => {
          this.customers = data;
        })
        .catch(error => {
          console.error('Error fetching customers:', error);
        });
    },
    getDetail(personID) {
      this.$router.push(`/Home/Customers/detail/${personID}`);
    },
    logout() {
      console.log('Logout');
    },
    manageHotelServices() {
      console.log('Manage Hotel Services');
    },
    createReservation() {
      console.log('Create Reservation');
    },
    viewReservations() {
      console.log('View Reservations');
    },
    viewCustomers() {
      console.log('View Customers');
    },
    filteredCustomers() {
      return this.customers.filter(customer => {
        console.log(customer);
        return (
          customer.person.firstName.toLowerCase().includes(this.filters.firstName.toLowerCase()) &&
          customer.person.lastName.toLowerCase().includes(this.filters.lastName.toLowerCase()) &&
          customer.person.email.toLowerCase().includes(this.filters.email.toLowerCase()) &&
          customer.person.phonePreselection.toLowerCase().includes(this.filters.phonePreselection.toLowerCase()) &&
          customer.person.phoneNumber.toLowerCase().includes(this.filters.phoneNumber.toLowerCase())
        );
      });
    },
  },
};
</script>
    
    <style scoped>
    input[type="text"],
    input[type=number] {
      padding: 8px;
      border: none;
      border-radius: 4px;
      font-size: 16px;
      border: 1px solid #2196F3;
    }

    input[type="number"]::-webkit-inner-spin-button,
    input[type="number"]::-webkit-outer-spin-button {
      -webkit-appearance: inner-spin-button; 
      appearance: inner-spin-button;
      color: #2196F3;
      font-size: 16px; 
    }
    select{
      padding: 8px; 
      border: none; 
      border-radius: 4px; 
      font-size: 16px; 
      border: 1px solid #2196F3;
    }
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
    .content {
      display: flex;
      justify-content: center;
      align-items: center;
      padding: 20px;
    }
    .table-container {
      flex: 1;
      padding: 10px;
      border-radius: 10px;
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
    }
    table td:last-child {
      text-align: center;
    }
    </style>
    