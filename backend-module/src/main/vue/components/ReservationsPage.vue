<template>
  <div>
    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>Customer*</th>
            <th>Room*</th>
            <th>Services</th>
            <th>Start Date*</th>
            <th>End Date*</th>
            <th>Cost</th>
            <th>Comming Time</th>
            <th>Leaving Time</th>
            <th>Business Guest</th>
            <th>Parking</th>
            <th>Edit</th>
            <th>Check-In/-Out</th>
          </tr>
        </thead>
        <tbody>

        <tr>
          <td><input type=text v-model="filtersR.CustomerName"></td>
          <td><input type=number v-model="filtersR.RoomID"></td>
          <td></td>
          <td><input type=date v-model="filtersR.Start"></td>
          <td><input type=date v-model="filtersR.End"></td>

          <td><input type=number min="0" v-model="filtersR.Cost"></td>
          <td><input type=time v-model="filtersR.CommingTime"></td>
          <td><input type=time v-model="filtersR.LeavingTime"></td>
          <td> 
            <select v-model="filtersR.BusinessGuest" :style="{ width: '140px' }">
              <option value="false">No</option>
              <option value="true">Yes</option>
              <option value="">Do Not Index</option>
            </select>
          </td>
          <td> 
            <select v-model="filtersR.Parking" :style="{ width: '140px' }">
              <option value="false">No</option>
              <option value="true">Yes</option>
              <option value="">Do Not Index</option>
            </select>
          </td>
          <td></td>
          <td> 
            <select v-model="filtersR.State" :style="{ width: '140px' }">
              <option value="Pending">Check-In</option>
              <option value="Confirmed">Check-Out</option>
              <option value="Paid">Paid</option>
              <option value="">Do Not Index</option>
            </select>
          </td>
        </tr>

          <tr v-for="reservation in filteredReservations()" :key="reservation.ReservationID">
            <td>
              <span>{{ reservation.CustomerName }}</span>
            </td>
            <td>
              <span v-if="!reservation.editable">{{ reservation.RoomID }}</span>
              <select id="room-id" v-model="reservation.RoomID" v-if="reservation.editable">
                <option value="" disabled>Select a room</option>
                <option v-for="room in filteredRooms(reservation)" :value="room.RoomID">{{ room.RoomID }} (Beds: {{ room.Beds }})</option>
              </select>
              <!-- <input v-if="reservation.editable" type="text" v-model="reservation.RoomID"> -->
            </td>
            <td>
              <span v-if="!reservation.editable">
                {{ reservation.ServiceIDs ? reservation.ServiceIDs.map(id => services_available.find(service => service.ID === id).Name).join(', ') : '' }}
              </span>
              <button v-else @click="showModalFunc(reservation)">Edit Services</button>
            </td>
            <td>
              <span v-if="!reservation.editable">{{ formatDate(reservation.Start) }}</span>
              <input v-if="reservation.editable" type="date" 
                    :style="{ 'border': invalidStartDate ? '2px solid red' : '' }"
                    :value="formatDate(reservation.Start)" 
                    @input="updateStartDate($event.target.value, reservation)" />
            </td>
            <td>
              <span v-if="!reservation.editable">{{ formatDate(reservation.End) }}</span>
              <input v-if="reservation.editable" type="date" 
                    :style="{ 'border': invalidEndDate ? '2px solid red' : '' }"
                    :value="formatDate(reservation.End)" 
                    @input="updateEndDate($event.target.value, reservation)" />
            </td>

            <td>
              <span>{{ reservation.Cost }}</span>
            </td>
            <td>
              <span v-if="!reservation.editable">{{ reservation.CommingTime }}</span>
              <input v-if="reservation.editable" type="time" v-model="reservation.CommingTime">
            </td>
            <td>
              <span v-if="!reservation.editable">{{ reservation.LeavingTime }}</span>
              <input v-if="reservation.editable" type="time" v-model="reservation.LeavingTime">
            </td>
            <td>
              <span v-if="!reservation.editable">{{ reservation.BusinessGuest ? 'Yes' : 'No' }}</span>
              <input v-if="reservation.editable" type="checkbox" v-model="reservation.BusinessGuest">
            </td>
            <td>
              <span v-if="!reservation.editable">{{ reservation.Parking ? 'Yes' : 'No' }}</span>
              <input v-if="reservation.editable" type="checkbox" v-model="reservation.Parking">
            </td>
            <td>
              <button v-if="!reservation.editable" :disabled="!isDateValid" @click="toggleEdit(reservation)">Edit</button>
              <button v-else @click="updateReservation(reservation)" :disabled="!isDateValid">OK</button>
              <button v-if="reservation.editable" class="delete-button" @click="deleteReservation(reservation)">Delete</button>
            </td>
            <td>
              <button v-if="reservation.State==='Pending'" class="edit-button" @click="toggleCheckIn(reservation)">Check-In</button>
              <button v-else-if="reservation.State==='Confirmed'" class="edit-button" @click="toggleCheckIn(reservation)">Check-Out</button>
              <p v-else-if="reservation.State==='Paid'">Paid</p>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="isLoading">
    </div>
  
    <div v-if="popup" class="modal">
      <div class="modal-content">
          <div>
            <div v-for="reservation in reservations" :key="reservationCheckIn.ReservationID" v-if="reservation.ReservationID === reservationCheckIn.ReservationID">
              <p>First Name: {{ reservation.CustomerFirstName }}</p>
              <p>Last Name: {{ reservation.CustomerLastName}}</p>
              <p>Email: {{ reservation.CustomerName}}</p>
              <p>Start Date: {{ formatDate(reservation.Start) }}</p>
              <p> End Date: {{ formatDate(reservation.End) }}</p>
              <p> Room: {{ reservation.RoomID }}</p>
              <p> Cost: {{ reservation.Cost }}</p>
              <p> Bussines Guest: {{ trueFalseFormat(reservation.BusinessGuest) }}</p>
              <p> Parking: {{ trueFalseFormat(reservation.Parking) }}</p>
              <p> Services: </p>
              <div v-for="service in reservation.ServiceIDs" :key="reservation.ID">
                <ul>
                  <li>{{ getServiceName(service) }}</li> 
                </ul>
              </div>

              <button v-if="reservation.State==='Pending'" class="button" @click="closePopupCheckIn(reservation)">Check-In</button> 
              <button v-else-if="reservation.State==='Confirmed'" class="button" @click="closePopupCheckIn(reservation)">Check-Out</button> 
              </div>
          </div>
        <button class="button" @click="closePopupCancel()">Cancel</button> 
      </div>
    </div>

    <div v-if="showModal" class="modal">
      <div class="modal-content">
        <span class="close" @click="showModal = false">&times;</span>
        <h4>Select Services</h4>
          <div v-for="reservation in reservations" :key="selectedServices.ReservationID" v-if="reservation.ReservationID === selectedServices.ReservationID">
            <div v-for="service in services_available" :key="service.ID">
              <input type="checkbox" :id="service.ID" :value="service.ID" v-model="reservation.ServiceIDs">
              <label :for="service.ID">{{ getServiceName(service.ID) }}</label>
            </div>
          </div>
        <button @click="showModal = false">Confirm</button>
      </div>
    </div>

    
  </div>
</template>



<script>


export default {
  data() {
    return {
      reservations: [], 
      services: [], 
      services_available: [], 
      popup: false,
      reservationCheckIn: [],
      showModal: false,
      selectedServices: [],
      isLoading: true,
      invalidEndDate: false,
      invalidStartDate: false,
      rooms: [],
      customers: [],
      filtersR: {
        CustomerName: '',
        RoomID: '',
        Start: null,
        End: null,
        State: '',
        Cost: 0,
        CommingTime: null,      
        LeavingTime: null,      
        BusinessGuest: '',
        Parking: ''
      },
    };
  },
  async mounted() {
    await this.fetchServices();
    await this.fetchReservationsAndServices();
    await this.fetchReservationServices();
    await this.fetchCustomerNames();
    this.isLoading = false;
    this.fetchRooms();
  },
  computed: {
    isDateValid() {
      console.log('Invalid start date:', this.invalidStartDate);
      return !this.invalidStartDate && !this.invalidEndDate;
    },
  },
  methods: {
    async fetchServices() {
      try {
        const response = await fetch('/Home/Services/GetServices');
        const data = await response.json();
        this.services_available = data.map(service => ({ ID: service.ServiceID, Name: service.Name, ...service, editable: false }));
      } catch (error) {
        console.error('Error fetching services:', error);
      }
    },
    async fetchReservationsAndServices() {
      try {
        const response = await fetch('/Home/Reservations/GetReservations');
        const data = await response.json();
        this.reservations = data.map(reservation => ({ ...reservation, editable: false }));
      } catch (error) {
        console.error('Error fetching reservations:', error);
      }
    },
    async fetchReservationServices() {
      try {
        const response = await fetch('/Home/Reservations/GetReservationServices');
        const data = await response.json();
        const serviceMap = {};

        data.forEach(service => {
          const serviceID = service.ServiceID;
          if (!serviceMap[serviceID]) {
            serviceMap[serviceID] = {
              ServiceID: serviceID,
              reservations: []
            };
          }
          serviceMap[serviceID].reservations.push(service.ReservationID);
        });

        this.services = Object.values(serviceMap);

        console.log('Services:', this.services);

        this.reservations.forEach(reservation => {
          const serviceIDs = data
            .filter(service => service.ReservationID === reservation.ReservationID)
            .map(service => service.ServiceID);
          reservation.ServiceIDs = serviceIDs;
        });
        console.log('Reservations with services:', this.reservations);
      } catch (error) {
        console.error('Error fetching reservation services:', error);
      }
    },


    async fetchCustomerNames() {
      try {
        const customerIDs = [...new Set(this.reservations.map(reservation => reservation.CustomerID))];

        const response = await fetch(`/Home/Customer/GetCustomers`);
        const data = await response.json();
        this.customers = data;
        customerIDs.forEach(customerID => {
          const matchingReservations = this.reservations.filter(reservation => reservation.CustomerID === customerID);
          if (matchingReservations.length > 0) {
            matchingReservations.forEach(reservation => {
              data.forEach(customer => {
                if (customer.customerId === customerID) {
                  reservation.CustomerName = customer.person.email; 
                  reservation.CustomerFirstName = customer.person.firstName; 
                  reservation.CustomerLastName = customer.person.lastName; 
                }
              });
            });
          }
        });
        console.log('Reservations:', this.reservations);
      } catch (error) {
        console.error('Error fetching customer names:', error);
      }
    },



    toggleEdit(reservation) {
      reservation.editable = !reservation.editable; 
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
    totalCost(reservation) {
      console.log('Calculating total cost:', reservation)
      if (reservation.Start && reservation.End && reservation.RoomID) {
        const startDate = new Date(reservation.Start);
        const endDate = new Date(reservation.End);
        const days = Math.ceil((endDate - startDate) / (1000 * 60 * 60 * 24));
        const room = this.rooms.find(room => room.RoomID === reservation.RoomID);
        let cost = days * room.Cost;

        if (reservation.BusinessGuest) {
          cost += 50;
        }

        if (reservation.Parking) {
          cost += 10;
        }

        if (reservation.ServiceIDs) {
          reservation.ServiceIDs.forEach(serviceID => {
            const service = this.services_available.find(service => service.ID === serviceID);
            cost += service.Cost;
          });
        }

        return cost.toFixed(2);

      }

      return 0;
    },
    updateReservation(reservation) {
  console.log('Updating reservation:', reservation);
  reservation.editable = false; // Close the editing mode

  reservation.Start = this.formatDate(reservation.Start);
  reservation.End = this.formatDate(reservation.End);
  
  reservation.Parking = reservation.Parking ? 1 : 0;
  reservation.BusinessGuest = reservation.BusinessGuest ? 1 : 0;

  reservation.Cost = this.totalCost(reservation);
  console.log('Updated reservation:', reservation);
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

    deleteReservation(reservation) {
      console.log('Deleting reservation:', reservation);
      reservation.editable = false; 

      const index = this.reservations.indexOf(reservation);
      if (index !== -1) {
        this.reservations.splice(index, 1);
      }
      fetch('/Home/Reservations/DeleteReservation', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(reservation),
      })
      .then(response => {
        if (response.ok) {
          console.log('Reservation deleted successfully');
        } else {
          throw new Error('Failed to delete reservation');
        }
      })
      .catch(error => {
        console.error('Error deleting reservation:', error);
      });
    },
    trueFalseFormat(value) {
      if(value === "true")
      {
        return "Yes";
      }
      else
      {
        return "No";
      }
    },
    closePopupCheckIn(reservation){
      if(reservation.State === "Pending")
        reservation.State = "Confirmed";
      else if(reservation.State === "Confirmed")
        reservation.State = "Paid";
      this.updateReservation(reservation);
      this.editTable = !this.editTable;
      this.popup = !this.popup;
    },
    closePopupCancel(){
      this.popup = !this.popup;
    },
    toggleCheckIn(Reservation){
      this.popup = !this.popup;
      this.reservationCheckIn = Reservation;
      console.log(this.reservations);
      console.log('Check-In:', this.reservationCheckIn);
    },
    showModalFunc(reservation) {
      this.showModal = true;
      this.selectedServices = reservation;
      console.log('Selected services:', this.selectedServices);
    },
    getServiceName(serviceId) {
      console.log('Service ID:', serviceId);
      console.log('Services:', this.services_available);
      const service_name = this.services_available.find(service => service.ID === serviceId).Name;
      console.log('Service name:', service_name);
      return service_name;
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
    updateStartDate(newValue, reservation) {
    if (!newValue) {
      reservation.Start = null;
      return;
    }

    const parts = newValue.split('-');
    const year = parseInt(parts[0], 10);
    const month = parseInt(parts[1], 10) - 1; 
    const day = parseInt(parts[2], 10);
    const date = new Date(year, month, day).getTime();
    console.log('Date:', date);
    if (typeof reservation.End === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(reservation.End)) {
      const endParts = reservation.End.split('-');
      const endYear = parseInt(endParts[0], 10);
      const endMonth = parseInt(endParts[1], 10) - 1;
      const endDay = parseInt(endParts[2], 10);
      reservation.End = new Date(endYear, endMonth, endDay).getTime();
    }
    console.log('Reservation end:', reservation.End);
    if (date >= reservation.End) {
      this.invalidStartDate = true;
    }
    else {
      this.invalidStartDate = false;
      this.invalidEndDate = false;
    }
    reservation.Start = new Date(year, month, day).getTime();
  },
  updateEndDate(newValue, reservation) {
    if (!newValue) {
      reservation.End = null;
      return;
    }

    const parts = newValue.split('-');
    const year = parseInt(parts[0], 10);
    const month = parseInt(parts[1], 10) - 1; 
    const day = parseInt(parts[2], 10);
    const date = new Date(year, month, day).getTime();
    console.log('Date:', date);
    if (typeof reservation.Start === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(reservation.End)) {
      const endParts = reservation.End.split('-');
      const endYear = parseInt(endParts[0], 10);
      const endMonth = parseInt(endParts[1], 10) - 1;
      const endDay = parseInt(endParts[2], 10);
      reservation.Start = new Date(endYear, endMonth, endDay).getTime();
    }
    console.log('Reservation start:', reservation.Start);
    if (date <= reservation.Start) {
      this.invalidEndDate = true;
    }
    else {
      this.invalidEndDate = false;
      this.invalidStartDate = false;
    }
    reservation.End = new Date(year, month, day).getTime(); 
  },
  filterDateFormat(oldDate) {
    console.log(oldDate);
    const parts = oldDate.split('-');
    const year = parseInt(parts[0], 10);
    const month = parseInt(parts[1], 10) - 1; 
    const day = parseInt(parts[2], 10);
    const date = new Date(year, month, day).getTime();
    return date;
  },
  BPformat(BP){
    if (typeof BP === 'string') {
      if (BP === '')
        return null;
      else if (BP === 'true') {
        return true;
      }
      else {
        return false;
      }
    }
    if (typeof BP === 'boolean') {
      if (BP === true) {
        return true;
      }
      else {
        return false;
      }
    }
  },
  checkRoomAvailable(roomID, reservations, startDate, endDate) {
      // const parts = startDate.split('-');
      // const year = parseInt(parts[0], 10);
      // const month = parseInt(parts[1], 10) - 1; // Months are in JavaScript zero-indexed
      // const day = parseInt(parts[2], 10);
      // const Startdate = new Date(year, month, day).getTime();

      // const parts1 = endDate.split('-');
      // const year1 = parseInt(parts1[0], 10);
      // const month1 = parseInt(parts1[1], 10) - 1;
      // const day1 = parseInt(parts1[2], 10);
      // const EndDate = new Date(year1, month1, day1).getTime();

      for (const reservation of reservations) 
      {
        if (
          roomID === reservation.RoomID &&
          endDate >= reservation.Start && 
          startDate <= reservation.End) 
        {
          return false;
        }
      }
      return true;
    },
    filteredRooms(reservation) {
      return this.rooms.filter(room => {
        if (room.RoomID === reservation.RoomID) {
            return true;
        }
        if (/^\d{4}-\d{2}-\d{2}$/.test(reservation.End)) {
          const endParts = reservation.End.split('-');
          const endYear = parseInt(endParts[0], 10);
          const endMonth = parseInt(endParts[1], 10) - 1;
          const endDay = parseInt(endParts[2], 10);
          reservation.End = new Date(endYear, endMonth, endDay).getTime();
        }
        if (/^\d{4}-\d{2}-\d{2}$/.test(reservation.Start)) {
          const endParts = reservation.Start.split('-');
          const endYear = parseInt(endParts[0], 10);
          const endMonth = parseInt(endParts[1], 10) - 1;
          const endDay = parseInt(endParts[2], 10);
          reservation.Start = new Date(endYear, endMonth, endDay).getTime();
        }
        console.log('Checking room availability:', room.RoomID, reservation.Start, reservation.End);
        return (
          ((reservation.Start && reservation.End) ? this.checkRoomAvailable(room.RoomID, this.reservations, reservation.Start, reservation.End) : true )
        );
      });
    },
    filteredReservations() {
      let filterStart = null;
      let filterEnd = null;
      if(this.filtersR.Start != null)
        filterStart = this.filterDateFormat(this.filtersR.Start);
      
      if(this.filtersR.End != null)
        filterEnd = this.filterDateFormat(this.filtersR.End);
      return this.reservations.filter(reservation => {
        while (reservation.CustomerName === undefined) {
          return true;
        }
        return (
          reservation.CustomerName.includes(this.filtersR.CustomerName) &&
          reservation.RoomID.toString().includes(this.filtersR.RoomID) &&
          (!this.filtersR.Start || reservation.Start >= filterStart) &&
          (!this.filtersR.End || reservation.End <= filterEnd) &&
          reservation.State.toLowerCase().includes(this.filtersR.State.toLowerCase()) &&
          reservation.Cost >= this.filtersR.Cost &&
          (!this.filtersR.CommingTime || reservation.CommingTime >= this.filtersR.CommingTime) &&
          (!this.filtersR.LeavingTime || reservation.LeavingTime <= this.filtersR.LeavingTime) &&
          (this.BPformat(this.filtersR.BusinessGuest) === null || reservation.BusinessGuest === this.BPformat(this.filtersR.BusinessGuest)) &&
          (this.BPformat(this.filtersR.Parking )=== null || reservation.Parking === this.BPformat(this.filtersR.Parking))
        );
      });
    },
  }
};

</script>

<style scoped>
.edit-button{
  padding: 10px 20px;
  margin-bottom: 10px;
  border: none;
  border-radius: 5px;
  background-color: #2196f3;
  color: white;
  font-size: 16px;
  cursor: pointer;
}
.edit-button:hover {
  background-color: #13568e;
}
.ok-button{
  padding: 10px 10px;
  margin-bottom: 10px;
  border: none;
  border-radius: 5px;
  background-color: #2196f3;
  color: white;
  font-size: 16px;
  cursor: pointer;
}
.ok-button:hover {
  background-color: #13568e;
}
.delete-button{
  padding: 10px 10px;
  margin-bottom: 10px;
  border: none;
  border-radius: 5px;
  background-color: #f32f21;
  color: white;
  font-size: 16px;
  cursor: pointer;
}
.delete-button:hover {
  background-color: #951e16;
}
input[type="text"],
input[type="email"],
input[type="number"],
input[type="date"],
input[type="time"] {
  padding: 8px;
  border: none; 
  border-radius: 4px;
  font-size: 16px; 
  border: 1px solid #2196F3;
}
input[type="number"] {
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
/* .modal {
  display: flex;
  align-items: center;
  justify-content: center;
  position: fixed;
  z-index: 1000;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgba(0, 0, 0, 0.4);
}

.modal-content {
  background-color: white;
  margin: 15% auto;
  padding: 20px;
  border: 1px solid #888;
  width: 80%;
} */
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