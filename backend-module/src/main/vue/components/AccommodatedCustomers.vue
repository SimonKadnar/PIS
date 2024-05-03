<template>
    <div class="table-container">
        <table>
        <thead>
            <tr>
            <th>Last Name*</th>
            <th>First Name*</th>
            <th>Email*</th>
            <th>Phone Number*</th>
            <th>Room*</th>
            <th>Start Date*</th>
            <th>End Date*</th>
            <th></th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="customer in customers" :key="customer.PersonID">
            <td>{{ customer.lastName }}</td>
            <td>{{ customer.firstName }}</td>
            <td>{{ customer.email }}</td>
            <td>{{ customer.phoneNumber }}</td>
            <td>{{ customer.roomNumber }}</td>
            <td>{{ customer.startDate }}</td>
            <td>{{ customer.endDate }}</td>
            <td>
                <button class="button" @click="getDetail(customer.personID)">View details</button>
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
    };
},
mounted() {
    this.fetchCustomers(); 
},
methods: {
    fetchCustomers() {
    fetch('/Home/AccommodatedCustomers/GetAccommodatedCustomers')
        .then(response => response.json())
        .then(data => {
        this.customers = data; 
        console.log(this.customers);
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
      