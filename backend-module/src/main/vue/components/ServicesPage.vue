<template>
  <div class="table-container">
    <table>
      <thead>
        <tr>
          <th style="width: 150px;">Name*</th>
          <th style="width: 50px;">Cost*</th>
          <th style="width: 130px;">Availability*</th>
          <th>Description</th>
          <th style="width: 80px;">Edit</th>
        </tr>
      </thead>
      <tbody>

        <!-- Add new -->
        <tr v-if="addingNew">
          <td><input type="text" style="width: 150px;" v-model="newService.Name" placeholder="Name of service" :class="{ 'required-field-empty': newService.Name === '' }" required></td>
          <td><input type="number" min="0.5" step="0.5" style="width: 50px;" v-model="newService.Cost" :class="{ 'required-field-empty': newService.Cost === '' }" required></td>
          <td> 
            <select v-model="newService.Availability" style="width: 130px;" :class="{ 'required-field-empty': newService.Availability === '' }" required>
              <option value="" disabled selected>Select Availability</option>
              <option value="Available">Available</option>
              <option value="Closed">Closed</option>
            </select>
          </td>
          <td><input type="text" style="width: 95%;" v-model="newService.Description" placeholder="Description of service"></td>
          <td>
            <button @click="addNewService" class="edit-button" >OK</button>
            <button @click="cancelNewService" class="delete-button" >Cancel</button>
          </td>
        </tr>
        <tr v-else>
          <td colspan="5" style="text-align: center;">
            <button @click="toggleAddNew" class="edit-button">Add New</button>
          </td>
        </tr>


        <!-- Filter row -->
        <tr>
          <td><input type="text" style="width: 150px;" v-model="filters.Name"></td>
          <td><input type=number step="0.5" style="width: 50px;" v-model="filters.Cost"></td>
          <td> 
            <select v-model="filters.Availability" style="width: 130px;">
              <option value="Available">Available</option>
              <option value="Closed">Closed</option>
              <option value=""> </option>
            </select>
          </td>
          <td><input type="text" style="width: 95%;" v-model="filters.Description"></td>
          <td></td> <!-- Empty cell for buttons -->
        </tr>

        <!-- Data rows -->
        <tr v-for="service in filteredServices" :key="service.ServiceID">
          <td v-if="!service.editable">{{ service.Name }}</td>
          <td v-else><input type="text" style="width: 150px;" v-model="service.Name" :class="{ 'required-field-empty': service.Name === '' }" required></td>
          <td v-if="!service.editable">{{ service.Cost }}</td>
          <td v-else><input type=number step="0.5" style="width: 50px;" v-model="service.Cost" :style="{ width: '50px' }" :class="{ 'required-field-empty': service.Cost === '' }" required></td>
          <td v-if="!service.editable">{{ service.Availability }}</td>
          <td v-else> 
            <select v-model="service.Availability" style="width: 130px;" :class="{ 'required-field-empty': service.Availability === '' }" required>
              <option value="Available">Available</option>
              <option value="Closed">Closed</option>
            </select>
          </td>
          <td v-if="!service.editable">{{ service.Description }}</td>
          <td v-else><input type="text" style="width: 95%;" v-model="service.Description"></td>
          <td>
            <button v-if="!service.editable" class="edit-button" @click="toggleEdit(service)">Edit</button>
            <button v-else class="ok-button" @click="updateService(service)">OK</button>
            <button v-if="service.editable && RezeravationForService(service.ServiceID)" class="delete-button" @click="deleteService(service)">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
  
<script>
import Parent from './Parent.vue';

export default {
  data() {
    return {
      reservations: [],
      services: [],
      filters: {
        Name: '',
        Cost: '',
        Availability: '',
        Description: ''
      },
      addingNew: false,
      newService: {
        Name: '',
        Cost: '1.0',
        Availability: 'Available',
        Description: '',
        ServiceID: ''
      }
    };
  },
  computed: {
    filteredServices() {
      // filter services if some value is not set filter will ignore that value
      return this.uniqueServices.filter(service => {
        return (
          service.Name.toLowerCase().includes(this.filters.Name.toLowerCase()) &&
          service.Cost.toString().includes(this.filters.Cost) &&
          service.Availability.toLowerCase().includes(this.filters.Availability.toLowerCase()) &&
          service.Description.replace(/\s/g, '').toLowerCase().includes(this.filters.Description.replace(/\s/g, '').toLowerCase())
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
  mounted() {
    this.fetchServices(); // load services
    this.fetchReservationServices(); // load ResevationServices
  },
  methods: {
    // requesting for service data
    async fetchServices() {
      fetch('/Home/Services/GetServices') 
        .then(response => response.json())
        .then(data => {
          this.services = data.map(service => ({ ...service, editable: false }));
        })
        .catch(error => {
          console.error('Error fetching services:', error);
        });
    },

    async fetchReservationServices() 
    {
    try {
      const response = await fetch('/Home/Reservations/GetReservationServices');
      this.reservations = await response.json();
      } catch (error) {
        console.error('Error fetching reservations:', error);
      }
    },

    toggleEdit(service) {
      service.editable = !service.editable; // change edit value
    },

    RezeravationForService(serviceID){

      for (const reservation of this.reservations) 
      {
        if (serviceID === reservation.ServiceID) 
        {
          return false;
        }
      }
      return true;
    },

    updateService(service) {
      if (service.Name && service.Cost && service.Availability) 
      {
        console.log('Updating service:', service);
        service.editable = false; // clsoe edit window

        // send data to server
        fetch('/Home/Services/UpdateService', {
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
      }
        else 
      {
        alert('Fill in all red fields in for Update Service.');
      }
    },
    
    deleteService(service) {
      console.log('Deleting service:', service);
      service.editable = false; // close edit window

      const index = this.services.indexOf(service);
      if (index !== -1) 
      {
        this.services.splice(index, 1);
      }
      //send service data to delete
      fetch('/Home/Services/DeleteService', {
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

    toggleAddNew() {
      this.addingNew = true;
    },
    addNewService() {

      //check if some input is missing
      if (this.newService.Name && this.newService.Cost && this.newService.Availability) 
      {
        //create new id from max ServiceID
        let maxServiceID = Math.max(...this.services.map(service => service.ServiceID));
        this.newService.ServiceID = maxServiceID + 1;
        this.services.push({ ...this.newService, editable: false });

        //send data for new service
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
          Cost: '1.0',
          Availability: 'Available',
          Description: '',
          ServiceID: ''
        };
        this.addingNew = false;
      }
        else 
      {
        alert('Fill in all red fields in for new Service.');
      }
    },
    //cancel add
    cancelNewService() {
      this.newService = {
        Name: '',
        Cost: '1.0',
        Availability: 'Available',
        Description: '',
        ServiceID: ''
      };
      this.addingNew = false;
    }
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
input[type="text"] {
  padding: 8px; 
  border: none; 
  border-radius: 4px; 
  font-size: 16px; 
  border: 1px solid #2196F3;
}
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

.required-field-empty {
    border-color: #ff0000 !important;
}
</style>
  