<template>
  <div class="table-container">
    <button class="button" @click="ReturnToAllCustomers">Zpět na seznam</button>
    <button class="button" @click="ToggleTable()">{{ buttonLabel }}</button> 
    
    <div v-if="popup" class="modal">
      <div class="modal-content">
        <p>Unsaved changes will be lost</p>
        <button class="button" @click="closePopupLeave">Leave</button> 
        <button class="button" @click="closePopupStay">Stay</button> 
      </div>
    </div>
    
    <p v-if="error">error</p>
    <div class="tableWrapper">
      <div class="tableCustomers">
        <table v-if="editTable" class="threadTable">
          <tbody v-if="customers && customers.length > 0">
            <tr v-for="(customer, index) in customers" :key="index">
              <th>Last Name:</th><td>{{ customer.LastName }}</td>
            </tr>
            <tr v-for="(customer, index) in customers" :key="'first_name_' + index">
              <th>First Name:</th><td>{{ customer.FirstName }}</td>
            </tr>
            <tr v-for="(customer, index) in customers" :key="'email_' + index">
              <th>Email:</th><td>{{ customer.Email }}</td>
            </tr>
            <tr v-for="(customer, index) in customers" :key="'phone_number_' + index">
              <th>Phone Number:</th><td>{{ formatPhoneNumber(customer.PhonePreselection, customer.PhoneNumber) }}</td>
            </tr>
            <tr v-for="(customer, index) in customers" :key="'document_number_' + index">
              <th>Document Number:</th><td>{{ customer.DocumentNumber }}</td>
            </tr>
            <tr v-for="(customer, index) in customers" :key="'date_of_birth_' + index">
              <th>Date of Birth:</th><td>{{ formatDate(customer.dateOfBirth) }}</td>
            </tr>
            <tr v-for="(customer, index) in customers" :key="'allergy_' + index">
              <th>Allergy:</th><td>{{ customer.Allergy }}</td>
            </tr>
            <tr v-for="(customer, index) in customers" :key="'handicap_' + index">
              <th>Handicap:</th><td>{{ formatHandicap(customer.Handicap) }}</td>
            </tr>
            <tr v-for="(customer, index) in customers" :key="'address_' + index">
              <th>Address:</th><td>{{ customer.Address }}</td>
            </tr>
            <tr v-for="(customer, index) in customers" :key="'subscription_' + index">
              <th>Subscription:</th><td>{{ formatSubscription(customer.Subscription) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <table v-if="editTable">
      <thead>
        <tr>
          <th>Room *</th>
          <th>Services</th>
          <th>Start Date*</th>
          <th>End Date*</th>
          <th>Cost</th>
          <th>Comming Time</th>
          <th>Leaving Time</th>
          <th>Business Guest</th>
          <th>Parking</th>
          <th>Check-In/-Out</th>
        </tr>
      </thead>
      <tbody>

        <tr>
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
          <td v-if="!reservation.editable">
            <span>{{ reservation.RoomID }}</span>
          </td>
          <td v-else>
            <input type="text" v-model="reservation.RoomID">
          </td>

          <td v-if="!reservation.editable">
            <span>
              {{ reservation.ServiceIDs ? reservation.ServiceIDs.map(id => services_available.find(service => service.ID === id).Name).join(', ') : '' }}
            </span>
          </td>
          <td v-else>
            <select v-model="reservation.ServiceIDs" multiple>
              <option v-for="service in services" :key="service.ID" :value="service.ID">{{ service.Name }}</option>
            </select>
          </td>

          <td v-if="!reservation.editable">
            <span>{{ formatDate(reservation.Start) }}</span>
          </td>
          <td v-else>
            <input type="date" v-model="reservation.Start">
          </td>

          <td v-if="!reservation.editable">
            <span>{{ formatDate(reservation.End) }}</span>
          </td>
          <td v-else>
            <input type="date" v-model="reservation.End">
          </td>
          <td>
            <span v-if="!reservation.editable">{{ reservation.Cost }}</span>
            <input v-else type="number" min="0" v-model="reservation.Cost">
          </td>
          <td>
            <span v-if="!reservation.editable">{{ reservation.CommingTime }}</span>
            <input v-else type="time" v-model="reservation.CommingTime">
          </td>
          <td>
            <span v-if="!reservation.editable">{{ reservation.LeavingTime }}</span>
            <input v-else type="time" v-model="reservation.LeavingTime">
          </td>
          <td>
            <span v-if="!reservation.editable">{{ reservation.BusinessGuest ? 'Yes' : 'No' }}</span>
            <input v-else type="checkbox" v-model="reservation.BusinessGuest">
          </td>
          <td>
            <span v-if="!reservation.editable">{{ reservation.Parking ? 'Yes' : 'No' }}</span>
            <input v-else type="checkbox" v-model="reservation.Parking">
          </td>
          <td>
            <button v-if="reservation.State==='Pending'" class="edit-button" @click="toggleCheckIn(reservation)">Check-In</button>
            <button v-else-if="reservation.State==='Confirmed'" class="edit-button" @click="toggleCheckIn(reservation)">Check-Out</button>
            <p v-else-if="reservation.State==='Paid'">Paid</p>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="popupCheck" class="modal">
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

    <div v-if="isLoading">
    </div>

    <div class="tableCustomers">
      <table v-if="!editTable" class="threadTable">
        <tbody v-if="customers && customers.length > 0">
          <tr v-for="(customer, index) in customers" :key="index">
            <th>Last Name:</th><td><input type="text" v-model="customer.LastName"/></td>
          </tr>
          <tr v-for="(customer, index) in customers" :key="'first_name_' + index">
            <th>First Name:</th><td><input type="text" v-model="customer.FirstName"/></td>
          </tr>
          <tr v-for="(customer, index) in customers" :key="'email_' + index">
            <th>Email:</th><td><input type="email" v-model="customer.Email"/></td>
          </tr>
          <tr v-for="(customer, index) in customers" :key="'phone_number_' + index">
            <th>Phone Number:</th><td><select v-model="customer.PhonePreselection">
              <option value='+420'>+420</option>
              <option value='+421'>+421</option>
              <option value='+69'>+49</option>
            </select>
            <input type="text" v-model="customer.PhoneNumber"/></td>
          </tr>
          <tr v-for="(customer, index) in customers" :key="'document_number_' + index">
            <th>Document Number:</th><td><input type="text" v-model="customer.DocumentNumber"/></td>
          </tr>
          
          <tr v-for="(customer, index) in customers" :key="'date_of_birth_' + index">
            <th>Date of Birth:</th>
            <td>
              <input type="date" 
                    :value="formatDate(customer.dateOfBirth)" 
                    @input="updateDateOfBirth($event.target.value, customer)" />
            </td>
          </tr>
          <tr v-for="(customer, index) in customers" :key="'allergy_' + index">
            <th>Allergy:</th><td><input type="text" v-model="customer.Allergy"/></td>
          </tr>
          <tr v-for="(customer, index) in customers" :key="'handicap_' + index">
            <th>Handicap:</th><td><select v-model="customer.Handicap">
              <option value=true>Ano</option>
              <option value=false>Ne</option>
            </select></td>
          </tr>
          <tr v-for="(customer, index) in customers" :key="'address_' + index">
            <th>Address:</th><td><input type="text" v-model="customer.Address" /></td>
          </tr>
          <tr v-for="(customer, index) in customers" :key="'subscription_' + index">
            <th>Subscription:</th><td><select v-model="customer.Subscription">
              <option value=true>Přihlášen</option>
              <option value=false>Odhlášen</option>
            </select></td>
          </tr>
          <tr v-for="(customer, index) in customers" :key="'button_' + index">
            <td colspan="2">
              <button class="ok-button" @click="updateToggleTable(customer)">Uložit</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

</div>

</template>

<script>
export default {
data() {
  return {
    editTable: true,
    customers: [],
    customers_resevations: [],
    buttonLabel: 'Upravit informace',
    ID: null,
    error: false,
    addingNew: false,
    reservationCheckIn: [],
    addingNewReservation: false,
    popupCheck: false,
    popup: false,
    filters: {
      Name: '',
      Cost: '',
      Description: '',
      Extra: ''
    },
    filtersR: {
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
    newService: {
      Name: '',
      Cost: '10',
      Availability: 'Available',
      Description: '',
      ServiceID: ''
    },
    newReservation: {
      Start: '2024-04-09',
      End: '2024-04-09',
      State: 'Pending',
      Cost: '600',
      CommingTime: '2024-04-09',
      LeavingTime: '2024-04-09',
      BusinessGuest: 'No',
      Parking: 'No',
      ReservationID: ''
    },
    reservations: [], 
      services: [], 
    services_available: [],
    isLoading: true,
    reservationsRdy: false,
    customersRdy: false,
    personID: null,
  };
},
mounted() {
  this.personID = this.$route.params.personID;
  console.log('Person ID:', this.personID);
  this.fetchCustomers(this.personID); 
  this.fetchServices(); 
  this.fetchReservations();
  this.fetchReservationServices();
  this.fetchCustomerNames();
},
computed: {
  filteredServices() {
    return this.services.filter(service => {
      return (
        service.Name.toLowerCase().includes(this.filters.Name.toLowerCase()) &&
        service.Cost.toString().includes(this.filters.Cost) &&
        service.Description.replace(/\s/g, '').toLowerCase().includes(this.filters.Description.replace(/\s/g, '').toLowerCase()) &&
        service.Extra.replace(/\s/g, '').toLowerCase().includes(this.filters.Extra.replace(/\s/g, '').toLowerCase())
      );
    });
  },
  uniqueServices() {
    const uniqueNames = [];
    const uniqueServices = [];
    this.services.forEach(service => {
      if (!uniqueNames.includes(service.Name)) {
        uniqueNames.push(service.Name);
        uniqueServices.push(service);
      }
    });
    return uniqueServices;
  }
},

methods: {
    async fetchCustomerNames() {
      console.log('Fetching customer names');
      while(!this.reservationsRdy || !this.customersRdy) {
        await new Promise(resolve => setTimeout(resolve, 1000));
      }
      try {
        const customerIDs = [...new Set(this.reservations.map(reservation => reservation.CustomerID))];

        customerIDs.forEach(customerID => {
          const matchingReservations = this.reservations.filter(reservation => reservation.CustomerID === customerID);
          if (matchingReservations.length > 0) {
            matchingReservations.forEach(reservation => {
              this.customers.forEach(customer => {
                console.log('Customer:', customer);
                console.log('Customer ID:', customerID);
                if (customer.CustomerID === customerID) {
                  reservation.CustomerName = customer.Email; 
                  reservation.CustomerFirstName = customer.FirstName; 
                  reservation.CustomerLastName = customer.LastName; 
                }
              });
            });
          }
          this.isLoading = false;
        });
        console.log('Reservations:', this.reservations);
      } catch (error) {
        console.error('Error fetching customer names:', error);
      }
    },

  // ------------------------------------------------------------------------------------------------
  ToggleTable() {
    if(this.editTable == false)
    {
      this.popup = !this.popup;
    }
    else{
      this.buttonLabel = 'Zrušit změny';
      this.editTable = !this.editTable;
    }
  },
  closePopupLeave(){
    this.buttonLabel = 'Upravit informace';
    this.editTable = !this.editTable;
    this.popup = !this.popup;
  },
  closePopupStay(){
    this.popup = !this.popup;
  },

  updateToggleTable(customer) {        
    this.validatePhoneNumber(customer.PhoneNumber);
    this.validateEmail(customer.Email);
    if(!customer.dateOfBirth){
      customer.dateOfBirth = null;
    }

    console.log('Updating customer:', customer);
    fetch('/Home/Customer/UpdateCustomer', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(customer),
    })
    .then(response => {
      if (response.ok) {
        console.log('Customer updated successfully');
      } else {
        throw new Error('Failed to update customer');
      }
    })
    .catch(error => {
      console.error('Error updating customer:', error);
    });
    this.buttonLabel = 'Upravit informace';
    this.editTable = !this.editTable;
  },
  closePopupCheckIn(reservation){
      if(reservation.State === "Pending")
        reservation.State = "Confirmed";
      else if(reservation.State === "Confirmed")
        reservation.State = "Paid";
      this.updateReservation(reservation);
      this.popupCheck = !this.popupCheck;
    },
    closePopupCancel(){
      this.popupCheck = !this.popupCheck;
    },
    toggleCheckIn(Reservation){
      this.popupCheck = !this.popupCheck;
      this.reservationCheckIn = Reservation;
      console.log(this.reservations);
      console.log('Check-In:', this.reservationCheckIn);
    },
  // ---------------------------------------- FETCH --------------------------------------------------------
  getFormattedDate(days = 0) {
      const today = new Date();
      today.setDate(today.getDate() + days);
      const day = today.getDate().toString().padStart(2, '0');
      const month = (today.getMonth() + 1).toString().padStart(2, '0');
      const year = today.getFullYear();
      return `${year}-${month}-${day}`;
    },
  fetchServices() {
    console.log('Fetching services');
    fetch('/Home/Customer/GetServices') 
      .then(response => response.json())
      .then(data => {
        this.services_available = data.map(service => ({ ID: service.ServiceID, Name: service.Name, ...service, editable: false }));
        console.log('All Services:', this.services_available);
      })
      .catch(error => {
        console.error('Error fetching services:', error);
      });
  },
  fetchReservations() {
    console.log('Fetching reservations');
    fetch('/Home/Customer/GetReservations') 
      .then(response => response.json())
      .then(data => {
        this.reservations = data.map(reservation => ({ ...reservation, editable: false }));
      })
      .catch(error => {
        console.error('Error fetching reservations:', error);
      });
  },
  
  fetchReservationServices() {
    console.log('Fetching reservation services');
  fetch('/Home/Reservations/GetReservationServices')
    .then(response => response.json())
    .then(data => {
      const serviceMap = {};

      this.fetchServices(); 
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
      this.reservationsRdy = true;
    })
    .catch(error => {
      console.error('Error fetching services:', error);
    });
},


  fetchCustomers(personID) {
    console.log('Fetching customers');
    fetch('/Home/Customer/GetCustomer', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(personID),
    })
    .then(response => response.json())
    .then(data => {
      this.customers = data;
      console.log('Customers:', this.customers);
      this.customersRdy = true;
    })
    .catch(error => {
      console.error('Error fetching customers:', error);
    });
  },
  // ---------------------------------------- FETCH --------------------------------------------------------
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
  // ---------------------------------------------- FORMATS -------------------------------------------------
  formatPhoneNumber(PhonePreselection, phoneNumber)
  {
    return `${PhonePreselection} ${phoneNumber}`;
  },
  formatDate(dateOfBirth)
  {
    if (!dateOfBirth) return ''; 

    const dateObj = new Date(dateOfBirth);
    const month = dateObj.getMonth() + 1;
    const day = dateObj.getDate();
    const year = dateObj.getFullYear();

    const formattedMonth = month < 10 ? `0${month}` : `${month}`;
    const formattedDay = day < 10 ? `0${day}` : `${day}`;

    return `${year}-${formattedMonth}-${formattedDay}`;
  },
  updateDateOfBirth(newValue, customer) {
    if (!newValue) {
      customer.dateOfBirth = null;
      return;
    }

    const parts = newValue.split('-');
    const year = parseInt(parts[0], 10);
    const month = parseInt(parts[1], 10) - 1;
    const day = parseInt(parts[2], 10);

    customer.dateOfBirth = new Date(year, month, day).getTime();
  },
  formatHandicap(Handicap){
    if (typeof Handicap === 'string') {
      if (Handicap === 'true') {
        return 'Ano';
      }
      else {
        return 'Ne';
      }
    }
    if (typeof Handicap === 'boolean') {
      if (Handicap === true) {
        return 'Ano';
      }
      else {
        return 'Ne';
      }
    }
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
  formatSubscription(Subscription){
    if (typeof Subscription === 'string') {
      if (Subscription === 'true') {
        return 'Přihlášen';
      }
      else {
        return 'Odhlášen';
      }
    }
    if (typeof Subscription === 'boolean') {
      if (Subscription === true) {
        return 'Přihlášen';
      }
      else {
        return 'Odhlášen';
      }
    }
  },
  // ---------------------------------------------- FORMATS -------------------------------------------------
  filterDateFormat(oldDate) {
    console.log(oldDate);
    
    if (!isNaN(oldDate) && !isNaN(parseFloat(oldDate))) {
        oldDate = this.formatDate(oldDate);
    }
    const parts = oldDate.split('-');
    const year = parseInt(parts[0], 10);
    const month = parseInt(parts[1], 10) - 1; 
    const day = parseInt(parts[2], 10);
    const date = new Date(year, month, day).getTime();
    return date;
  },
  // ---------------------------------------------- CUSTOMER -------------------------------------------------
  ReturnToAllCustomers() {
    this.$router.push('/Home/Customers');
  },
  validatePhoneNumber(phoneNumber){
    // +420 xxx xxx xxx - length + 3 9
    if(phoneNumber.length!=9)
      console.log('wrong number format');
    if(!(phoneNumber.split('').every(char => /^\d$/.test(char))))
      console.log('wrong number format');
  },
  validateEmail(email) {
    if (!email) {
      console.log('Email is required');
      return; 
    }

    if (!/\S+@\S+\.\S+/.test(email)) {
      console.log('Invalid email format');
      if (!/@/.test(email)) {
        console.log('Missing @ symbol');
      } else if (!/\.\S+/.test(email)) {
        console.log('Missing domain part (e.g., .com)');
      } else {
        console.log('Email is incomplete');
      }
    }
  },
  cancelEdit() {
    fetchCustomers();
  },
  removeChanges(){
    this.editTable = !this.editTable;
  },
  // -------------------------------------------- SERVICES ---------------------------------------------------
  toggleAddNew() {
      this.addingNew = true;
     },
 
  addNewService() {
    if (this.newService.Name && this.newService.Cost && this.newService.Availability) 
    {

      let maxServiceID = Math.max(...this.services.map(service => service.ServiceID));
      this.newService.ServiceID = maxServiceID + 1;

      this.services.push({ ...this.newService, editable: false });

      fetch('/Home/Services/AddService', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(this.newService),
      })
      .then(response => {
        if (response.ok) {
          console.log('Service added successfully');
        } else {
          throw new Error('Failed to add service');
        }
      })
      .catch(error => {
        console.error('Error adding service:', error);
      });

      this.newService = {
        Name: '',
        Cost: '10',
        Availability: 'Available',
        Description: '',
        ServiceID: ''
      };
      this.addingNew = false;
      }
        else 
      {
        alert('Fill in all fields in for new Service.');
      }
    },
    cancelNewService() {
    this.newService = {
      Name: '',
      Cost: '10',
      Availability: 'Available',
      Description: '',
      ServiceID: ''
    };
    this.addingNew = false;
  },
  toggleEdit(service) {
    console.log('Toggling edit mode for service: ', service);
    service.editable = !service.editable;
    console.log('Service in edit mode: ', service.editable);
  },
  updateService(service) {
    console.log('Updating service:', service);
    service.editable = false;
    fetch('/Home/Customer/UpdateService', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(service),
    })
    .then(response => {
      if (response.ok) {
        console.log('Service updated successfully');
      } else {
        throw new Error('Failed to update service');
      }
    })
    .catch(error => {
      console.error('Error updating service:', error);
    });
  },

  deleteService(service) {
    console.log('Deleting service:', service);
    service.editable = false; 

    const index = this.services.indexOf(service);
    if (index !== -1) 
    {
      this.services.splice(index, 1);
    }
    fetch('/Home/Customer/DeleteService', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(service),
    })
    .then(response => {
      if (response.ok) {
        console.log('Service deleted successfully');
      } else {
        throw new Error('Failed to delete Service');
      }
    })
    .catch(error => {
      console.error('Error deleting Service:', error);
    });
  },
  getServiceInputWidth(text) {
      return text ? `${text.length * 12}px` : '100px'; 
  },
  // --------------------- RESERVATION ------------------------
  toggleAddNewReservation() {
  this.addingNewReservation = true;
  },
  addNewReservation() {
  if (this.newReservation.Name && this.newReservation.Cost && this.newReservation.Availability) 
  {

    let maxReservationID = Math.max(...this.reservations.map(reservation => reservation.ReservationID));
    this.newReservation.ReservationID = maxReservationID + 1;

    this.reservations.push({ ...this.newReservation, editable: false });

    fetch('/Home/Reservations/AddReservation', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(this.newReservation),
    })
    .then(response => {
      if (response.ok) {
        console.log('Reservation added successfully');
      } else {
        throw new Error('Failed to add reservation');
      }
    })
    .catch(error => {
      console.error('Error adding reservation:', error);
    });

    this.newReservation = {
      Name: '',
      Cost: '10',
      Availability: 'Pending',
      Description: '',
      ReservationID: ''
    };
    this.addingNewReservation = false;
    }
      else 
    {
      alert('Fill in all fields in for new Reservation.');
    }
  },
  cancelNewReservation() {
  this.newReservation = {
    Name: '',
    Cost: '10',
    Availability: 'Available',
    Description: '',
    ReservationID: ''
  };
  this.addingNewReservation = false;
  },
  toggleEditReservation(reservation) {
    reservation.editable = !reservation.editable; 
  },
  updateReservation(reservation) {
    console.log('Updating reservation:', reservation);
    reservation.Start = this.formatDate(reservation.Start);
    reservation.End = this.formatDate(reservation.End);
    fetch('/Home/Customer/UpdateReservations', {
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
  deleteReservation(reservation) {
    console.log('Deleting reservation:', reservation);
    reservation.editable = false; 

    const index = this.reservations.indexOf(reservation);
    if (index !== -1) 
    {
      this.reservations.splice(index, 1);
    }
    fetch('/Home/Customer/DeleteReservation', {
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
        throw new Error('Failed to delete Reservation');
      }
    })
    .catch(error => {
      console.error('Error deleting Reservation:', error);
    });
  },
  getReservationInputWidth(text) {
      return text ? `${text.length * 12}px` : '100px';
    },
  getServiceName(serviceId) {
    console.log('Service ID:', serviceId);
    console.log('Services:', this.services_available);
    const service_name = this.services_available.find(service => service.ID === serviceId).Name;
    console.log('Service name:', service_name);
    return service_name;
  },
  filteredReservations() {
    let filterStart = null;
    let filterEnd = null;
    if(this.filtersR.Start != null)
      filterStart = this.filterDateFormat(this.filtersR.Start);
    
    if(this.filtersR.End != null)
      filterEnd = this.filterDateFormat(this.filtersR.End);
    return this.reservations.filter(reservation => {
      return (
        reservation.CustomerID == this.personID &&
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
.tableWrapper {
    display: flex;
    justify-content: flex-start;
    margin-top: 20px;
  }

.tableCustomers {
  display: flex;
  flex-direction: row;
  align-items: stretch;
}

.tableData {
  margin-left: 20px;
  width: 150%;
}

.tableData table {
  width: 150%;
  border-collapse: collapse;
}

.tableData tr {
  padding: 8px;
  text-align: left;
  height: 35px;

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

.buttonR {
display: inline-block;
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
margin-bottom: 15px;
}
th, td {
text-align: left;
}
th {
background-color: #2196F3;
color: white;
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

input[type="date"],
input[type="time"],
input[type="email"],
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

.modal {
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
}

.close {
  color: #aaa;
  float: right;
  font-size: 28px;
  font-weight: bold;
}

.close:hover,
.close:focus {
  color: black;
  text-decoration: none;
  cursor: pointer;
}

</style>