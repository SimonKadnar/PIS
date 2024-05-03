<template>
  <div class="reservation-form">
    <h2>Create a Reservation</h2>
    <form @submit.prevent="submitForm">
      <div>
        <label for="customer-id">Customer Name*</label>
        <select id="customer-id" v-model="reservation.CustomerID" required>
          <option value="" disabled>Select a customer</option>
          <option v-for="customer in customers" :key="customer.customerId" :value="customer.customerId">
            {{ customer.person.email }}
          </option>
        </select>
        <button v-if="!showNewCustomerForm" @click.prevent="addNewCustomer" :disabled="!isCreateButtonEnabled">New Customer</button>
        <button v-else @click.prevent="addNewCustomer">Hide Creation</button>
      </div>
      <div>
        <label for="room-id">Rooms*</label>
        <select id="room-id" v-model="reservation.RoomID" required>
          <option value="" disabled>Select a room</option>
          <option v-for="room in filteredRooms" :value="room.RoomID">{{ room.RoomID }} (Beds: {{ room.Beds }})</option>
        </select>
      </div>
      <div>
        <label for="start-date">Start Date*</label>
        <input id="start-date" type="date" v-model="reservation.Start" required>
      </div>
      <div>
        <label for="end-date">End Date*</label>
        <input id="end-date" type="date" v-model="reservation.End" required>
        <span v-if="!isCreateButtonEnabled" class="warning-icon" title="End datum musí být po Start datum">&#9888;</span>
      </div>
      <div>
        <label for="coming-time">Check-in</label>
        <input id="coming-time" type="time" v-model="reservation.ComingTime" required>
      </div>
      <div>
        <label for="leaving-time">Check-out</label>
        <input id="leaving-time" type="time" v-model="reservation.LeavingTime" required>
      </div>
      <div>
        <label for="business-guest">Business Guest</label>
        <input id="business-guest" type="checkbox" v-model="reservation.BusinessGuest">
      </div>
      <div>
        <label for="parking">Parking</label>
        <input id="parking" type="checkbox" v-model="reservation.Parking">
      </div>
      <div>
  <label for="cost">Cost</label>
  <input id="cost" type="number" min="0" step="0.01" v-model="totalCost" readonly>
</div>
      <button type="submit" :disabled="!isCreateButtonEnabled">Create Reservation</button>
    </form>

     <div v-if="reservationCreated">
      <h2>Reservation Details</h2>
      <p><strong>Customer Name:</strong> {{ selectedCustomerName }}</p>
      <p><strong>Room:</strong> {{ selectedRoom }}</p>
      <p><strong>Start Date:</strong> {{ reservation.Start }}</p>
      <p><strong>End Date:</strong> {{ reservation.End }}</p>
      <p><strong>Cost:</strong> {{ reservation.Cost }}</p>
      <p><strong>Coming Time:</strong> {{ reservation.ComingTime }}</p>
      <p><strong>Leaving Time:</strong> {{ reservation.LeavingTime }}</p>
      <p><strong>Business Guest:</strong> {{ reservation.BusinessGuest }}</p>
      <p><strong>Parking:</strong> {{ reservation.Parking }}</p>

 <h3>Add Services</h3>
 <button @click="showModal = true">Select Services</button>
 <router-link to="/Home/Reservations">
    <button>Continue without services</button>
  </router-link>

 <div v-if="showModal" class="modal">
    <div class="modal-content">
      <span class="close" @click="showModal = false">&times;</span>
      <h4>Select Services</h4>
      <div v-for="service in services" :key="service.ServiceID">
        <input type="checkbox" :id="service.ServiceID" :value="service.ServiceID" v-model="selectedServices">
        <label :for="service.ServiceID">{{ service.Name }}</label>
      </div>
      <button @click="addReservationServices">Add Services</button>
    </div>
  </div>

  <ul>
    <li v-for="serviceId in reservation.Services" :key="serviceId">{{ getServiceName(serviceId) }}</li>
  </ul>
</div>


  <div v-if="showNewCustomerForm && !reservationCreated">
      <h2>Create a New Customer</h2>
      <form @submit.prevent="createCustomer">
        <div>
            <label for="new-customer-firstname">First Name*</label>
            <input id="new-customer-firstname" v-model="newCustomer.FirstName" required>
          </div>
          <div>
            <label for="new-customer-lastname">Last Name*</label>
            <input id="new-customer-lastname" v-model="newCustomer.LastName" required>
          </div>
          <div>
            <label for="new-customer-email">Email*</label>
            <input id="new-customer-email" v-model="newCustomer.Email" required type="email">
            <span v-if="!isEmailUnique" class="warning-icon" title="Zákazník s daným emailem již existuje">&#9888;</span>
          </div>
          <div>
            <label for="new-customer-phone">Phone*</label>
            <input id="new-customer-phone" v-model="newCustomer.PhoneNumber" required type="tel">
            <span v-if="!isPhoneNumberUnique" class="warning-icon" title="Zákazník s tímto číslem již existuje">&#9888;</span>
          </div>
          <div>
            <label for="new-customer-document">Document Number*</label>
            <input id="new-customer-document" v-model="newCustomer.DocumentNumber" required type="number">
          </div>
          <div>
            <label for="new-customer-dob">Date of Birth*</label>
            <input id="new-customer-dob" type="date" v-model="newCustomer.DateOfBirth" required :max="getFormattedDateYears(18)">
          </div>
          <div>
            <label for="new-customer-phone-preselection">Phone Preselection</label>
            <input id="new-customer-phone-preselection" v-model="newCustomer.PhonePreselection" required pattern="\+\d{2,3}">
          </div>
          <div>
            <label for="new-customer-allergy">Allergy*</label>
            <input id="new-customer-allergy" v-model="newCustomer.Allergy" required>
          </div>
          <div>
            <label for="new-customer-handicap">Handicap</label>
            <input id="new-customer-handicap" type="checkbox" v-model="newCustomer.Handicap">
          </div>
          <div>
            <label for="new-customer-address">Address*</label>
            <input id="new-customer-address" v-model="newCustomer.Address" required>
          </div>
          <div>
            <label for="new-customer-subscription">Subscription</label>
            <input id="new-customer-subscription" type="checkbox" v-model="newCustomer.Subscription">
          </div>
        
          <button type="submit" :disabled="!isCustomerUnique">Create Customer</button>
        </form>
      </div>
    </div>
</template>

<script>
export default {
  data() {
    return {
      customers: [],
      rooms: [],
      services: [],
      reservation: {
        CustomerID: '',
        RoomID: '',
        Start: this.getFormattedDate(),
        End: this.getFormattedDate(7),
        Cost: 0,
        ComingTime: '14:00',
        LeavingTime: '10:00',
        BusinessGuest: false,
        Parking: false,
        reservationId: 0,
        ReservationID: 0,
      },
      newCustomer: {
        LastName: '',
        FirstName: '',
        Email: 'konza.hulich@example.com',
        PhonePreselection: '+420',
        PhoneNumber: '777777777',
        DocumentNumber: '123456789',
        DateOfBirth: '1998-01-01',
        Allergy: 'None',
        Handicap: false,
        Address: 'Bozetechova 1/2, 612 00 Brno',
        Subscription: false
    
      },
      ReservationID : 0,
      showNewCustomerForm: false,
      reservationCreated: false,
      showAddServicesButton: true,
      selectedCustomerName: '',
      selectedRoom: '',
      newServiceName: '',
      services: [],
      selectedServices: [],
      isCustomerUnique: true,
      isEmailUnique: true,
      isPhoneNumberUnique: true,
      showModal: false,
      reservations: [],
    };
  },
  mounted() {
    this.fetchCustomers(); 
    this.fetchRooms();
    this.fetchServices();
    this.fetchReservations();
  },
  computed: {
  totalCost() {
    if (this.reservation.Start && this.reservation.End && this.reservation.RoomID) {
      const startDate = new Date(this.reservation.Start);
      const endDate = new Date(this.reservation.End);
      const days = Math.ceil((endDate - startDate) / (1000 * 60 * 60 * 24));
      const room = this.rooms.find(room => room.RoomID === this.reservation.RoomID);
      let cost = days * room.Cost;

      if (this.reservation.BusinessGuest) {
        cost += 50;
      }

      if (this.reservation.Parking) {
        cost += 10;
      }

      return cost.toFixed(2);
    }

    return 0;
  },
  isCreateButtonEnabled() {
    const startDate = new Date(this.reservation.Start);
    const endDate = new Date(this.reservation.End);
    return (endDate > startDate);
  },
  filteredRooms() {
      return this.rooms.filter(room => {
        return (
          ((this.reservation.Start && this.reservation.End) ? this.checkRoomAvailable(room.RoomID, this.reservations, this.reservation.Start, this.reservation.End) : true )
        );
      });
    },
  },
  methods: {
    async submitForm() {
      this.reservation.Cost = this.totalCost;
      try {
        const response = await fetch('/Home/Reservations/AddReservation', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.reservation)
        });

        if (response.ok) {
          const data = await response.json();
          console.log('Reservation created successfully');

            console.log('Reservation ID:', data.reservationId);

          this.ReservationID = data.reservationId;
          console.log('Reservation ID:', this.ReservationID);

          this.reservationCreated = true;
          this.selectedCustomerName = this.customers.find(customer => customer.customerId === this.reservation.CustomerID).person.firstName + ' ' + this.customers.find(customer => customer.customerId === this.reservation.CustomerID).person.lastName;
          this.selectedRoom = this.rooms.find(room => room.RoomID === this.reservation.RoomID).RoomID;
        } else {
          console.error('Failed to create reservation');
        }
      } catch (error) {
        console.error('An error occurred:', error);
      }
    },

    getServiceName(serviceId) {
      return this.services.find(service => service.ServiceID === serviceId).Name;
    },

    updateReservation(reservation) {
  reservation.reservationId = this.ReservationID;
  reservation.ReservationID = this.ReservationID;
  reservation.State = 'Pending';
  reservation.CommingTime = reservation.ComingTime;
  console.log('Updating reservation:', reservation);
  reservation.editable = false; // Close the editing mode

  reservation.Start = this.formatDate(reservation.Start);
  reservation.End = this.formatDate(reservation.End);
  
  reservation.Parking = reservation.Parking ? 1 : 0;
  reservation.BusinessGuest = reservation.BusinessGuest ? 1 : 0;

  fetch('/Home/Reservations/UpdateReservation', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(reservation),
  })
  .then(response => {
    if (response.ok) {
      console.log('Reservation updated successfully');
    } else {
      throw new Error('Failed to update reservation');
    }
  })
  .catch(error => {
    console.error('Error updating reservation:', error);
  });
},

    fetchCustomers() {
  fetch('/Home/Customer/GetCustomers') 
    .then(response => response.json())
    .then(data => {
      this.customers = data.map(customer => ({ ...customer, editable: false }));
      this.isUniqueEmail(this.newCustomer.Email);
      this.isUniquePhoneNumber(this.newCustomer.PhoneNumber);
    })
    .catch(error => {
      console.error('Error fetching customers:', error);
    });
},
fetchRooms() {
      fetch('/Home/Rooms/GetRooms') 
        .then(response => response.json())
        .then(data => {
          this.rooms = data.map(room => ({ ...room, editable: false }));
        })
        .catch(error => {
          console.error('Error fetching rooms:', error);
        });
    },


  fetchServices() {
      fetch('/Home/Services/GetServices') 
        .then(response => response.json())
        .then(data => {
          this.services = data.map(service => ({ ...service, editable: false })); 
        })
        .catch(error => {
          console.error('Error fetching services:', error);
        });
    },

addNewCustomer() {
      this.showNewCustomerForm = !this.showNewCustomerForm;
    },

    createCustomer() {
      fetch('/Home/Customer/AddCustomer', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(this.newCustomer),
      })
        .then((response) => response.json())
        .then((data) => {
          this.customers.push(data);
          
          this.showNewCustomerForm = false;

          this.reservation.CustomerID = data.customerId;
          this.isUniqueEmail(this.newCustomer.Email);
          this.isUniquePhoneNumber(this.newCustomer.PhoneNumber);
        })
        .catch((error) => {
          console.error('Error creating customer:', error);
        });
    },

    addReservationServices(){
      console.log('Selected services:', this.selectedServices);
      console.log('Reservation:', this.reservation);
      if (this.selectedServices.length > 0) {
        this.selectedServices.forEach(service => { 
          let serviceCost = parseFloat(this.services.find(s => s.ServiceID === service).Cost);
          this.reservation.Cost = parseFloat(this.reservation.Cost) + serviceCost;
        });
      }
      console.log('New reservation: ', this.reservation);
      console.log('Total cost:', this.reservation.Cost);
      this.updateReservation(this.reservation);
      fetch('/Home/Reservations/AddReservationServices', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ reservationId: this.ReservationID, serviceIds: this.selectedServices }),
      })
        .then((response) => {
          if (response.ok) {
            this.showModal = false;
            this.$router.push('/Home/Reservations');
          } else {
            throw new Error('Adding new reservation failed');
          }
        })
        .then((data) => {
          this.newServiceName = '';

          this.showAddServicesButton = false;
        })
        .catch((error) => {
          console.error('Error adding service:', error);
        });

    },
    getFormattedDate(days = 0) {
      const today = new Date();
      today.setDate(today.getDate() + days);
      const day = today.getDate().toString().padStart(2, '0');
      const month = (today.getMonth() + 1).toString().padStart(2, '0');
      const year = today.getFullYear();
      return `${year}-${month}-${day}`;
    },
    getFormattedDateYears(years = 0) {
      const today = new Date();
      today.setDate(today.getDate());
      const day = today.getDate().toString().padStart(2, '0');
      const month = (today.getMonth() + 1).toString().padStart(2, '0');
      const year = (today.getFullYear()-years);
      return `${year}-${month}-${day}`;
    },
    isUniqueEmail(email) {
      const existingCustomer = this.customers.find(customer =>
        customer.person.email === email);
      if (existingCustomer) {
        this.isCustomerUnique = false;
        this.isEmailUnique = false;
        return false; 
      } else {
        return true;
      }
    },
    isUniquePhoneNumber(phoneNumber) {
      const existingCustomer = this.customers.find(customer =>
        customer.person.phoneNumber === phoneNumber
      );
      if (existingCustomer) {
        this.isCustomerUnique = false;
        this.isPhoneNumberUnique = false;
        return false; 
      } else {
        return true; 
      }
    },
    formatDate(ReservDate) {
      if (!ReservDate) return ''; 

      const dateObj = new Date(ReservDate);
      const month = dateObj.getMonth() + 1;
      const day = dateObj.getDate();
      const year = dateObj.getFullYear();

      const formattedMonth = month < 10 ? `0${month}` : `${month}`;
      const formattedDay = day < 10 ? `0${day}` : `${day}`;

      return `${year}-${formattedMonth}-${formattedDay}`;
    },
    async fetchReservations() {
      try {
        const response = await fetch('/Home/Reservations/GetReservations');
        this.reservations = await response.json();
      } catch (error) {
        console.error('Error fetching reservations:', error);
      }
    },
    checkRoomAvailable(roomID, reservations, startDate, endDate) {
      console.log("CHECK DATE FOR ROOM");
      console.log(startDate);
      console.log(endDate);
      const parts = startDate.split('-');
      const year = parseInt(parts[0], 10);
      const month = parseInt(parts[1], 10) - 1;
      const day = parseInt(parts[2], 10);
      const Startdate = new Date(year, month, day).getTime();

      const parts1 = endDate.split('-');
      const year1 = parseInt(parts1[0], 10);
      const month1 = parseInt(parts1[1], 10) - 1;
      const day1 = parseInt(parts1[2], 10);
      const EndDate = new Date(year1, month1, day1).getTime();

      for (const reservation of reservations) 
      {
        if (
          roomID === reservation.RoomID &&
          EndDate >= reservation.Start && 
          Startdate <= reservation.End) 
        {
          return false;
        }
      }
      return true;
    },

  },
  watch: {
    'reservation.End'(newVal, oldVal) {
    if (new Date(this.reservation.End) <= new Date(this.reservation.Start)) {
      this.showNewCustomerForm = false;
    }
  },
  'reservation.Start'(newVal, oldVal) {
    if (new Date(this.reservation.End) <= new Date(this.reservation.Start)) {
      this.showNewCustomerForm = false;
    }
  },
   'newCustomer.Email'(newVal, oldVal) {
      this.isEmailUnique = this.isUniqueEmail(this.newCustomer.Email);
      if (this.isEmailUnique && this.isPhoneNumberUnique) {
        this.isCustomerUnique = true;
      } else {
        this.isCustomerUnique = false;
      }
    },
    'newCustomer.PhoneNumber'(newVal, oldVal) {
      this.isPhoneNumberUnique = this.isUniquePhoneNumber(this.newCustomer.PhoneNumber);
      if (this.isEmailUnique && this.isPhoneNumberUnique) {
        this.isCustomerUnique = true;
      } else {
        this.isCustomerUnique = false;
      }
    },
  }
};
</script>

<style>
.reservation-form {
  max-width: 500px;
  margin: 0 auto;
}

.reservation-form h2 {
  text-align: center;
}

.reservation-form form {
  display: flex;
  flex-direction: column;
}

.reservation-form label {
  margin-bottom: 0.5rem;
}

.reservation-form input,
.reservation-form select {
  margin-bottom: 1rem;
}
.warning-icon {
  color: red; 
  margin-left: 5px; 
  font-size: 18px;
}

.modal {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 5px;
  width: 300px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.close {
  float: right;
  font-size: 28px;
  cursor: pointer;
}
</style>